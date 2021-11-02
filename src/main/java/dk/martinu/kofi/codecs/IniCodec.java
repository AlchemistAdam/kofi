/*
 * Copyright (c) 2021, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package dk.martinu.kofi.codecs;

import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.IntUnaryOperator;

import dk.martinu.kofi.*;
import dk.martinu.kofi.properties.*;
import dk.martinu.kofi.spi.*;

public class IniCodec
        implements DocumentFileReader, DocumentFileWriter, DocumentStringReader, DocumentStringWriter {

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static IniCodec provider() {
        return new IniCodec();
    }

    @Contract(pure = true)
    protected static boolean isDigit(final char c) {
        return c >= '0' && c <= '9';
    }

    @Contract(pure = true)
    protected static boolean isHexDigit(final char c) {
        if (isDigit(c))
            return true;
        else if (c >= 'A' && c <= 'F')
            return true;
        else
            return c >= 'a' && c <= 'f';
    }

    @Contract(pure = true)
    @Override
    public boolean canRead(@NotNull final Path filePath) throws NullPointerException {
        Objects.requireNonNull(filePath, "filePath is null");
        if (!Files.isDirectory(filePath)) {
            final String fileName = filePath.getFileName().toString();
            final int index = fileName.lastIndexOf('.');
            if (index != -1) {
                final String extension = fileName.substring(index);
                for (Extension ext : Extension.values())
                    if (ext.getExtension().equals(extension))
                        return true;
            }
        }
        return false;
    }

    @Contract(pure = true)
    @Override
    public boolean canWrite(@NotNull final Path filePath, @Nullable final Document document) throws
            NullPointerException {
        Objects.requireNonNull(filePath, "filePath is null");
        if (document != null && !Files.isDirectory(filePath)) {
            final String fileName = filePath.getFileName().toString();
            final int index = fileName.lastIndexOf('.');
            if (index != -1) {
                final String extension = fileName.substring(index);
                for (Extension ext : Extension.values())
                    if (ext.getExtension().equals(extension))
                        return true;
            }
        }
        return false;
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    @Override
    public Document readFile(@NotNull final Path filePath, @Nullable final Charset cs) throws IOException {
        if (Files.exists(filePath))
            return read(() -> Files.newBufferedReader(filePath, cs != null ? cs : StandardCharsets.UTF_8));
        else
            return new Document();
    }

    @Contract(value = "_ -> new", pure = true)
    @Override
    @NotNull
    public Document readString(final @NotNull String string) throws NullPointerException, IOException {
        Objects.requireNonNull(string, "string is null");
        return read(() -> new BufferedReader(new CharArrayReader(string.toCharArray())));
    }

    @Contract(pure = true)
    @Override
    public void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs)
            throws NullPointerException, IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
        write(() -> Files.newBufferedWriter(filePath, cs != null ? cs : StandardCharsets.UTF_8), document);
    }

    @Contract(value = "_, _ -> new", pure = true)
    @Override
    @NotNull
    public String writeString(final @NotNull String string, final @NotNull Document document) throws
            NullPointerException, IOException {
        Objects.requireNonNull(string, "string is null");
        Objects.requireNonNull(document, "document is null");
        final CharArrayWriter writer = new CharArrayWriter();
        write(() -> new BufferedWriter(writer), document);
        return writer.toString();
    }

    @Contract(pure = true)
    @NotNull
    protected Element parseLine(@NotNull String line) throws ParseException {
        // ignore leading/trailing ws
        line = line.trim();
        // line is whitespace
        if (line.isEmpty())
            return new Whitespace();
        // line is a comment
        if (line.charAt(0) == ';') {
            return new Comment(line);
        }
        // line is a section
        else if (line.charAt(0) == '[' && line.charAt(line.length() - 1) == ']') {
            return new Section(line.substring(1, line.length() - 1));
        }
        // attempt to parse line as a property
        else {
            final Property<?> property = parseProperty(line);
            if (property != null)
                return property;
            else
                throw new ParseException("line is not a comment, section, property or whitespace");
        }
    }

    @Contract(pure = true)
    @Nullable
    protected Property<?> parseProperty(@NotNull final String line) throws ParseException {
        // all properties require a = delimiter
        final int delimiter = line.indexOf('=');
        if (delimiter == -1)
            return null;
        // get and validate key
        final String key = line.substring(0, delimiter).trim();
        if (key.isEmpty())
            throw new ParseException("property key is empty");
        // get parsable value
        final char[] rawValue = line.substring(delimiter + 1).toCharArray();
        final Parsable<?> parsableValue = parseValue(rawValue, 0, -1, false);
        // return property
        if (parsableValue != null) {
            if (parsableValue.length == rawValue.length) {
                final Object value = parsableValue.parse();
                return switch (parsableValue.getType()) {
                    case NULL -> new NullProperty(key);
                    case STRING -> new StringProperty(key, (String) value);
                    case DOUBLE -> new DoubleProperty(key, (Double) value);
                    case FLOAT -> new FloatProperty(key, (Float) value);
                    case LONG -> new LongProperty(key, (Long) value);
                    case INT -> new IntProperty(key, (Integer) value);
                    case CHAR -> new CharProperty(key, (Character) value);
                    case BOOLEAN -> new BooleanProperty(key, (Boolean) value);
                    case ARRAY -> new ArrayProperty(key, (JsonArray) value);
                    case OBJECT -> new ObjectProperty(key, (JsonObject) value);
                };
            }
            else
                throw new ParseException("property value has trailing data");
        }
        else
            throw new ParseException("property value is empty");
    }

    @Contract(value = "null, _, _, _ -> fail", pure = true)
    @Nullable
    protected IniCodec.Parsable<?> parseValue(final char[] chars, final int offset, final int length,
            final boolean json) throws ParseException {
        assert chars != null;
        char c;
        // max length of value to iterate
        final int l = length != -1 ? length : chars.length;
        // lambda expression used to get the length of a parsable
        final IntUnaryOperator from = len -> {
            while (len < l && Character.isWhitespace(chars[len]))
                len++;
            return len;
        };
        for (int start = offset; start < l; start++) {
            // peek first character to determine type of parsable object
            c = chars[start];
            if (!Character.isWhitespace(c)) {
                // null
                if (c == 'n' || c == 'N') {
                    final int remainder = l - start;
                    final int end;
                    if (remainder >= 4 && String.copyValueOf(chars, start, 4).equalsIgnoreCase("null"))
                        end = start + 4;
                    else
                        throw new ParseException("invalid null value");

                    final int len = from.applyAsInt(end);
                    return new ParsableNull(chars, start, end, len);
                }
                // String
                else if (c == '"') {
                    boolean hasDelimiter = false;
                    int end = start + 1;
                    char c2;
                    while (end < l) {
                        c2 = chars[end - 1];
                        c = chars[end++];
                        if (c == '\"' && c2 != '\\') {
                            hasDelimiter = true;
                            break;
                        }
                    }

                    if (!hasDelimiter)
                        throw new ParseException("string values must be enclosed in \" quotes \"");

                    final int len = from.applyAsInt(end);
                    return new ParsableString(chars, start, end, len);
                }
                // char - not specified in JSON
                else if (c == '\'' && !json) {
                    final int remainder = l - start;
                    final int end;
                    if (remainder >= 4 && chars[start + 1] == '\\' && chars[start + 3] == '\'')
                        end = start + 4;
                    else if (remainder >= 3 && chars[start + 2] == '\'')
                        end = start + 3;
                    else
                        throw new ParseException("invalid char value");

                    final int len = from.applyAsInt(end);
                    return new ParsableChar(chars, start, end, len);
                }
                // codepoint (char) - not specified in JSON
                else if (c == '\\' && !json) {
                    final int remainder = l - start;
                    final int end;
                    if (remainder >= 6 && chars[start + 1] == 'u'
                            && isHexDigit(chars[start + 2]) && isHexDigit(chars[start + 3])
                            && isHexDigit(chars[start + 4]) && isHexDigit(chars[start + 5]))
                        end = start + 6;
                    else
                        throw new ParseException("invalid codepoint");

                    final int len = from.applyAsInt(end);
                    return new ParsableCodepoint(chars, start, end, len);
                }
                // boolean
                else if (c == 't' || c == 'T' || c == 'f' || c == 'F') {
                    final int remainder = l - start;
                    final int end;
                    if (remainder >= 4 && String.copyValueOf(chars, start, 4)
                            .equalsIgnoreCase(String.valueOf(true)))
                        end = start + 4;
                    else if (remainder >= 5 && String.copyValueOf(chars, start, 5)
                            .equalsIgnoreCase(String.valueOf(false)))
                        end = start + 5;
                    else
                        throw new ParseException("invalid boolean value");

                    final int len = from.applyAsInt(end);
                    return new ParsableBoolean(chars, start, end, len);
                }
                // TODO implement exponent (scientific notation) when parsing numbers
                // TODO ensure JSON grammar is correct
                // Number
                else if (isDigit(c) || c == '+' || c == '-' || c == '.') {
                    boolean hasDigits = false, hasFraction = false, isLong = false, isDouble = false;
                    int end = start;
                    while (end < l) {
                        c = chars[end++];
                        // digits are always allowed
                        if (isDigit(c)) {
                            hasDigits = true;
                            continue;
                        }
                        // only allow decimal separator if unique
                        else if (c == '.') {
                            if (!hasFraction) {
                                hasFraction = true;
                                continue;
                            }
                            else
                                throw new ParseException("number values can only have one decimal separator");
                        }
                        // only allow sign at index 0
                        else if (c == '+' || c == '-') {
                            if (end == start)
                                continue;
                            else
                                throw new ParseException("number values can only have a sign character at the beginning");
                        }
                        // allow type specifiers for double, float and long - not specified in JSON
                        else if (hasDigits && !json) {
                            if (c == 'd' || c == 'D') {
                                isDouble = true;
                                break;
                            }
                            else if ((c == 'F' || c == 'f'))
                                break;
                            else if (c == 'L' || c == 'l') {
                                if (hasFraction)
                                    throw new ParseException("number values of type long must not have fractions");
                                isLong = true;
                                break;
                            }
                        }
                        // unknown number character - not part of number
                        end--;
                        break;
                    }
                    if (!hasDigits)
                        throw new ParseException("number values must have at least one digit");
                    final int len = from.applyAsInt(end);
                    if (hasFraction)
                        if (json)
                            return new ParsableDouble(chars, start, end, len);
                        else
                            return isDouble ?
                                    new ParsableDouble(chars, start, end, len) :
                                    new ParsableFloat(chars, start, end, len);
                    else if (json)
                        return new ParsableLong(chars, start, end, len);
                    else
                        return isLong ?
                                new ParsableLong(chars, start, end, len) :
                                new ParsableInt(chars, start, end, len);
                }
                // array
                else if (c == '[') {
                    // find index of closing array bracket
                    int end = -1;
                    for (int i = l - 1; i > start; i--) {
                        c = chars[i];
                        if (!Character.isWhitespace(c)) {
                            if (c == ']')
                                end = i + 1;
                            break;
                        }
                    }
                    if (end == -1)
                        throw new ParseException("array values must be enclosed in brackets");
                    // list of parsable values in the array - they are parsed when the array itself is parsed
                    final ArrayList<Parsable<?>> values = new ArrayList<>();
                    boolean parse = true;
                    for (int i = start + 1; i < end - 1; ) {
                        if (parse) {
                            final Parsable<?> pv = parseValue(chars, i, end - 1, true);
                            // add value to list
                            if (pv != null) {
                                values.add(pv);
                                i = pv.length;
                                parse = false;
                            }
                            // allow empty arrays, but not empty values in populated arrays
                            else if (values.isEmpty()) {
                                break;
                            }
                            else
                                throw new ParseException("arrays must not contain empty values");
                        }
                        else if (chars[i] == ',') {
                            parse = true;
                            i++;
                        }
                        else
                            throw new ParseException("array values must be separated by a comma");
                    }
                    final int len = from.applyAsInt(end);
                    return new ParsableJsonArray(chars, start, end, len, values);
                }
                // object
                else if (c == '{') {
                    // find index of closing object bracket
                    int end = -1;
                    for (int i = l - 1; i > start; i--) {
                        c = chars[i];
                        if (!Character.isWhitespace(c)) {
                            if (c == '}')
                                end = i + 1;
                            break;
                        }
                    }
                    if (end == -1)
                        throw new ParseException("object properties must be enclosed in brackets");
                    // list of parsable key/value pairs in the object - they are parsed when the object itself is parsed
                    final ArrayList<Parsable<?>> properties = new ArrayList<>();
                    boolean parse = true;
                    for (int i = start + 1; i < end - 1; ) {
                        if (parse) {
                            // get property key
                            final Parsable<?> key = parseValue(chars, i, end - 1, true);
                            if (key != null && key.getType() != Parsable.Type.STRING)
                                throw new ParseException("object properties must begin with a string key");
                            if (key != null) {
                                if (key.length >= end - 2)
                                    throw new ParseException("object properties must contain a value");
                                if (chars[key.length] != ':')
                                    throw new ParseException("object properties must contain a : delimiter");
                                // get property value
                                final Parsable<?> value = parseValue(chars, key.length + 1, end - 1, true);
                                if (value == null)
                                    throw new ParseException("object property values must not be empty");
                                // add property to map
                                properties.add(key);
                                properties.add(value);
                                i = value.length;
                                parse = false;
                            }
                            // allow empty objects, but not null properties in populated objects
                            else if (properties.isEmpty()) {
                                break;
                            }
                            else
                                throw new ParseException("objects must not contain empty properties");
                        }
                        else if (chars[i] == ',') {
                            parse = true;
                            i++;
                        }
                        else
                            throw new ParseException("object properties must be separated by a comma");
                    }
                    final int len = from.applyAsInt(end);
                    return new ParsableJsonObject(chars, start, end, len, properties);
                }
                // unknown value type
                else {
                    throw new ParseException("could not parse property value {" + c + "}");
                }
            }
        }
        // if this statement is reached then the value is empty or contains only whitespace
        return null;
    }

    @NotNull
    protected Document read(@NotNull final Supplier<BufferedReader> readerSupplier) throws IOException {
        // executor to parse lines in parallel
        final ExecutorService executor = Executors.newCachedThreadPool();
        try {
            // list to hold futures of parse tasks
            final ArrayList<Future<Element>> futures = new ArrayList<>();
            // add parse task for each line, in line order
            try (final BufferedReader reader = readerSupplier.get()) {
                reader.lines().forEachOrdered(line ->
                        futures.add(executor.submit(new ParseTask(line, futures.size() + 1))));
            }
            // create and populate document, in line order
            final Document document = new Document(futures.size());
            for (Future<Element> future : futures) {
                while (!future.isDone())
                    Thread.onSpinWait();
                if (!future.isCancelled())
                    try {
                        document.addElement(future.get());
                    }
                    catch (InterruptedException | ExecutionException e) {
                        throw new IOException("an exception occurred while reading", e);
                    }
            }
            return document;
        }
        finally {
            executor.shutdown();
        }
    }

    protected void write(@NotNull final Supplier<BufferedWriter> supplier, @NotNull final Document document) throws
            IOException {
        try (BufferedWriter writer = supplier.get()) {
            for (int i = 0; i < document.size(); i++) {
                writer.write(document.getElement(i).getString());
                if (i < document.size() - 1)
                    writer.write('\n');
            }
            writer.flush();
        }
    }

    public enum Extension {

        INI(".ini"), CF(".cf"), CFG(".cfg"), CNF(".cnf"), CONF(".conf");

        @NotNull
        private final String extension;

        Extension(@NotNull final String extension) {
            this.extension = extension;
        }

        @Contract(pure = true)
        @NotNull
        public String getExtension() {
            return extension;
        }
    }

    /**
     * Specialized supplier that can throw {@link <code>IOException</code>}
     */
    @FunctionalInterface
    protected interface Supplier<T> {

        T get() throws IOException;
    }

    protected class ParseTask implements Callable<Element> {

        @NotNull
        public final String line;
        public final int lineNumber;

        @Contract(pure = true)
        public ParseTask(@NotNull final String line, final int lineNumber) {
            this.line = line;
            this.lineNumber = lineNumber;
        }

        @Contract(pure = true)
        @NotNull
        @Override
        public Element call() throws ParseException {
            try {
                return parseLine(line);
            }
            catch (ParseException e) {
                throw new ParseException(lineNumber, line, e);
            }
        }
    }

    protected abstract static class Parsable<T> {

        /**
         * <code>char</code> array source.
         */
        public final char[] chars;
        /**
         * First index in the {@link #chars} array, inclusive.
         */
        public final int start;
        /**
         * Last index in the {@link #chars} array, exclusive.
         */
        public final int end;
        /**
         * The length into the {@link #chars} array. More specifically, the
         * last index in the array (including whitespace), exclusive. This is
         * <b>not</b> the length of this parsable itself. Any character at or
         * after this index in the array is not part of this parsable value.
         */
        public final int length;

        public Parsable(final char[] chars, final int start, final int end, final int length) {
            this.chars = chars;
            this.start = start;
            this.end = end;
            this.length = length;
        }

        @NotNull
        public abstract Type getType();

        public abstract T parse();

        public enum Type {
            NULL, STRING, INT, LONG, FLOAT, DOUBLE, CHAR, BOOLEAN, ARRAY, OBJECT
        }

    }

    protected static class ParsableBoolean extends Parsable<Boolean> {

        public ParsableBoolean(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.BOOLEAN;
        }

        @NotNull
        @Override
        public Boolean parse() {
            return Boolean.valueOf(String.copyValueOf(chars, start, end - start));
        }
    }

    protected static class ParsableChar extends Parsable<Character> {

        public ParsableChar(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.CHAR;
        }

        @NotNull
        @Override
        public Character parse() {
            if (end - start == 3)
                return chars[start + 1];
            else
                return switch (chars[start + 2]) {
                    case 't' -> '\t';
                    case 'b' -> '\b';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 'f' -> '\f';
                    case '0' -> '\0';
                    // character is escaped but does not require special handling
                    default -> chars[start + 2];
                };
        }
    }

    protected static class ParsableCodepoint extends Parsable<Character> {

        public ParsableCodepoint(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.CHAR;
        }

        @NotNull
        @Override
        public Character parse() {
            return (char) Integer.parseInt(String.copyValueOf(
                    chars, start + 2, 4), 16);
        }
    }

    protected static class ParsableDouble extends Parsable<Double> {

        public ParsableDouble(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.DOUBLE;
        }

        @NotNull
        @Override
        public Double parse() {
            // do not include specifier is present, e.g. 1.5d
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Double.parseDouble(String.copyValueOf(chars, start, count));
        }
    }

    protected static class ParsableFloat extends Parsable<Float> {

        public ParsableFloat(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.FLOAT;
        }

        @NotNull
        @Override
        public Float parse() {
            // do not include specifier is present, e.g. 1.5F
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Float.parseFloat(String.copyValueOf(chars, start, count));
        }
    }

    protected static class ParsableInt extends Parsable<Integer> {

        public ParsableInt(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.INT;
        }

        @NotNull
        @Override
        public Integer parse() {
            return Integer.parseInt(String.copyValueOf(
                    chars, start, end - start));
        }
    }

    protected static class ParsableJsonArray extends Parsable<JsonArray> {

        @NotNull
        private final List<Parsable<?>> values;

        public ParsableJsonArray(final char[] chars, final int start, final int end, final int length,
                @NotNull final ArrayList<Parsable<?>> values) {
            super(chars, start, end, length);
            this.values = values;
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.ARRAY;
        }

        @NotNull
        @Override
        public JsonArray parse() {
            final Object[] array = new Object[values.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = values.get(i).parse();
            return new JsonArray(array);
        }
    }

    protected static class ParsableJsonObject extends Parsable<JsonObject> {

        @NotNull
        private final ArrayList<Parsable<?>> properties;

        public ParsableJsonObject(final char[] chars, final int start, final int end, final int length,
                @NotNull final ArrayList<Parsable<?>> properties) {
            super(chars, start, end, length);
            this.properties = properties;
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.OBJECT;
        }

        @NotNull
        @Override
        public JsonObject parse() {
            final JsonObject.Builder builder = new JsonObject.Builder();
            for (int i = 0; i < properties.size(); i += 2)
                builder.put((String) properties.get(i).parse(),
                        properties.get(i + 1).parse());
            return builder.build();
        }
    }

    protected static class ParsableLong extends Parsable<Long> {

        public ParsableLong(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.LONG;
        }

        @NotNull
        @Override
        public Long parse() {
            // do not include specifier is present, e.g. 22L
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Long.parseLong(String.copyValueOf(chars, start, count));
        }
    }

    // TODO maybe Object can be used as parameter type instead?
    protected static class ParsableNull extends Parsable<Void> {

        public ParsableNull(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @Override
        @NotNull
        public Type getType() {
            return Type.NULL;
        }

        @Override
        @Nullable
        public Void parse() {
            return null;
        }
    }

    protected static class ParsableString extends Parsable<String> {

        public ParsableString(final char[] chars, final int start, final int end, final int length) {
            super(chars, start, end, length);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.STRING;
        }

        // TODO needs testing
        // TODO unicode escape sequences (\\uXXXX) must be preserved
        @NotNull
        @Override
        public String parse() {
            final StringBuilder sb = new StringBuilder(end - start - 2);
            for (int i = start + 1; i < end - 1; ) {
                if (i < end - 2 && chars[i] == '\\') {
                    switch (chars[i + 1]) {
                        case 't' -> sb.append('\t');
                        case 'b' -> sb.append('\b');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 'f' -> sb.append('\f');
                        case '0' -> sb.append('\0');
                        default -> {
                            // escape sequence is unknown - do not unescape
                            sb.append('\\');
                            sb.append(chars[i + 1]);
                        }
                    }
                    i += 2;
                }
                else
                    sb.append(chars[i++]);
            }
            return sb.toString();
        }
    }
}
