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

import static dk.martinu.kofi.KofiUtil.isDigit;
import static dk.martinu.kofi.KofiUtil.isHexDigit;

public class KofiCodec implements DocumentFileReader, DocumentFileWriter, DocumentStringReader, DocumentStringWriter {

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static KofiCodec provider() {
        return new KofiCodec();
    }

    @Contract(pure = true)
    @Override
    public boolean canRead(@NotNull final Path filePath) {
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
    public boolean canWrite(@NotNull final Path filePath, @Nullable final Document document) {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
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

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    @Override
    public Document readFile(@NotNull final Path filePath, @Nullable final Charset cs) throws IOException {
        if (Files.exists(filePath))
            return read(() -> Files.newBufferedReader(filePath, cs != null ? cs : StandardCharsets.UTF_8));
        else
            return new Document();
    }

    @Contract(pure = true)
    @Override
    @NotNull
    public Document readString(final @NotNull String string) throws IOException {
        Objects.requireNonNull(string, "string is null");
        return read(() -> new BufferedReader(new CharArrayReader(string.toCharArray())));
    }

    @Contract(pure = true)
    @Override
    public void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs)
            throws IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
        write(() -> Files.newBufferedWriter(filePath, cs != null ? cs : StandardCharsets.UTF_8), document);
    }

    @Contract(pure = true)
    @Override
    @NotNull
    public String writeString(final @NotNull Document document) throws IOException {
        Objects.requireNonNull(document, "document is null");
        final CharArrayWriter writer = new CharArrayWriter();
        write(() -> new BufferedWriter(writer), document);
        return writer.toString();
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    protected Element parseLine(@NotNull String line) throws ParseException {
        // ignore leading/trailing ws
        line = line.trim();
        // line is whitespace
        if (line.isEmpty())
            return new Whitespace();
        // line is a comment
        if (line.charAt(0) == ';') {
            return new Comment(line.substring(1));
        }
        // line is a section
        else if (line.charAt(0) == '[') {
            if (line.charAt(line.length() - 1) == ']')
                return new Section(line.substring(1, line.length() - 1));
            else
                throw new ParseException("section must be enclosed in [ brackets ]");
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
        final char[] chars = line.substring(delimiter + 1).toCharArray();
        final Parsable<?> parsableValue = parseValue(chars, 0, -1, false);
        // return property
        if (parsableValue != null) {
            if (parsableValue.length == chars.length) {
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

    // TODO improve the way error information is propagated up to read() such
    //  that useful exception messages can be constructed
    @Contract(value = "null, _, _, _ -> fail", pure = true)
    @Nullable
    protected KofiCodec.Parsable<?> parseValue(final char[] chars, final int offset, final int length,
            final boolean json) throws ParseException {
        assert chars != null;
        char c;
        // max length of value to iterate
        final int l = length != -1 ? length : chars.length;
        // lambda expression used to get the length of a parsable
        final IntUnaryOperator from = json ?
                len -> {
                    // count whitespace and add to len
                    while (len < l && Json.isWhitespace(chars[len]))
                        len++;
                    return len;
                } :
                len -> {
                    // count whitespace and add to len
                    while (len < l && chars[len] < 33)
                        len++;
                    return len;
                };
        for (int start = offset; start < l; start++) {
            // peek first character to determine type of parsable object, whitespace is ignored
            c = chars[start];
            if (c > 32) {
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
                    int end = -1;
                    {
                        char prev;
                        for (int i = start + 1; i < l; i++) {
                            prev = c;
                            c = chars[i];
                            if (c == '\"')
                                if (prev != '\\') {
                                    end = i + 1;
                                    break;
                                }
                                // the second last character is a backslash
                                else {
                                    // count joined backslashes
                                    int n = 1, k = i - 2;
                                    while (k > start && chars[k--] == '\\')
                                        n++;
                                    // if n is even then the string is enclosed
                                    if ((n & 0x1) == 0)
                                        end = i + 1;
                                    else
                                        break;
                                }
                        }
                    }
                    if (end == -1)
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
                // Number
                else if (isDigit(c) || c == '-' || c == '+' || c == '.') {
                    boolean hasDigits = false;
                    // flags for fraction, exponent and bit precision (set with type specifiers)
                    NumFlag fraction = null;
                    NumFlag exponent = null;
                    NumFlag precision = null;
                    int end = start;
                    for (; end < l; end++) {
                        c = chars[end];
                        // ws or separator - not part of number
                        if (c < 33 && (!json || Json.isWhitespace(c)) || c == ',') {
                            break;
                        }
                        // characters not allowed after type specifier
                        else if (precision != null) {
                            throw new ParseException("significant characters not allowed after type specifier");
                        }
                        // digits 0-9
                        else if (isDigit(c)) {
                            if (exponent != null)
                                exponent = NumFlag.EXP_NUM;
                            else if (fraction != null)
                                fraction = NumFlag.FRAC_NUM;
                            else if (c == '0' && !hasDigits && end < l - 1 && isDigit(chars[end + 1]) && json)
                                throw new ParseException("JSON numbers cannot have leading zeros");
                            hasDigits = true;
                        }
                        // decimal separator
                        else if (c == '.') {
                            if (!hasDigits && json)
                                throw new ParseException("JSON numbers must have at least one digit before a decimal separator");
                            else if (exponent != null)
                                throw new ParseException("exponents cannot have decimal separators");
                            else if (fraction != null)
                                throw new ParseException("numbers can only have one decimal separator");
                            else if ((end == l - 1 || !isDigit(chars[end + 1])) && json)
                                throw new ParseException("JSON numbers must have at least one digit after a decimal separator");
                            else
                                fraction = NumFlag.FRAC_SEP;
                        }
                        // signs
                        else if (c == '-' || c == '+') {
                            if (exponent == null) {
                                if (end != start)
                                    throw new ParseException("sign characters must be at the start of a number or after an exponent prefix");
                                else if (c == '+' && json)
                                    throw new ParseException("JSON numbers cannot have a plus sign");
                            }
                            else if (exponent != NumFlag.EXP_PREFIX)
                                throw new ParseException("exponents can only have a sign right after the exponent prefix");
                            else
                                exponent = NumFlag.EXP_SIGN;
                        }
                        // exponent
                        else if (c == 'e' || c == 'E') {
                            if (!hasDigits)
                                throw new ParseException("numbers must have at least one digit before an exponent");
                            else if (exponent != null)
                                throw new ParseException("numbers can only have one exponent");
                            else if (end == l - 1 || (!isDigit(chars[end + 1]) && chars[end + 1] != '-' && chars[end + 1] != '+'))
                                throw new ParseException("numbers must have at least one digit after an exponent");
                            else
                                exponent = NumFlag.EXP_PREFIX;
                        }// Java/KOFI number logic
                        else if (!json) {
                            if (c == 'L' || c == 'l') {
                                if (fraction != null)
                                    throw new ParseException("numbers of type long cannot have fractions");
                                else if (exponent != null)
                                    throw new ParseException("numbers of type long cannot have exponents");
                                else if (!hasDigits)
                                    throw new ParseException("numbers must have at least one digit before a type specifier");
                                precision = NumFlag.P64;
                            }
                            else if (c == 'd' || c == 'D') {
                                if (!hasDigits)
                                    throw new ParseException("numbers must have at least one digit before a type specifier");
                                precision = NumFlag.P64;
                            }
                            else if (c == 'F' || c == 'f') {
                                if (!hasDigits)
                                    throw new ParseException("numbers must have at least one digit before a type specifier");
                                precision = NumFlag.P32;
                            }
                            else {
                                throw new ParseException("invalid number character");
                            }
                        }
                        else {
                            throw new ParseException("invalid number character");
                        }
                    }
                    if (!hasDigits)
                        throw new ParseException("numbers must have at least one digit");
                    else if (exponent != null && exponent != NumFlag.EXP_NUM)
                        throw new ParseException("exponents must have at least one digit");

                    final int len = from.applyAsInt(end);

                    // double and float
                    if (fraction != null || exponent != null) {
                        if (json)
                            return new ParsableDouble(chars, start, end, len);
                        else if (precision != null)
                            return precision == NumFlag.P64 ?
                                    new ParsableDouble(chars, start, end, len) :
                                    new ParsableFloat(chars, start, end, len);
                        else
                            return new ParsableFloat(chars, start, end, len);
                    }
                    // long and int
                    else {
                        if (json)
                            return new ParsableLong(chars, start, end, len);
                        else if (precision != null)
                            return precision == NumFlag.P64 ?
                                    new ParsableLong(chars, start, end, len) :
                                    new ParsableInt(chars, start, end, len);
                        else
                            return new ParsableInt(chars, start, end, len);
                    }
                }
                // array
                else if (c == '[') {
                    // scan for index of closing array bracket
                    int end = -1;
                    {
                        int depth = 0, string = -1;
                        char prev;
                        for (int i = start + 1; i < l; i++) {
                            prev = c;
                            c = chars[i];
                            if (string == -1) {
                                if (c == '[')
                                    depth++;
                                else if (c == ']')
                                    if (depth == 0) {
                                        end = i + 1;
                                        break;
                                    }
                                    else
                                        depth--;
                                else if (c == '\"')
                                    string = i;
                            }
                            else if (c == '\"')
                                if (prev != '\\')
                                    string = -1;
                                else {
                                    // count joined backslashes
                                    int n = 1, k = i - 2;
                                    while (k > string && chars[k--] == '\\')
                                        n++;
                                    // if n is even then the string is enclosed
                                    if ((n & 0x1) == 0)
                                        string = -1;
                                    else
                                        break;
                                }
                        }
                    }
                    if (end == -1)
                        throw new ParseException("arrays must be enclosed in [ brackets ]");
                    // list of parsable values in the array
                    final ArrayList<Parsable<?>> values = new ArrayList<>();
                    boolean parse = true;
                    for (int i = start + 1; i < end - 1; ) {
                        if (parse) {
                            final Parsable<?> pv = parseValue(chars, i, end - 1, true);
                            // add non-empty values to list
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
                            throw new ParseException("array values must be separated by a , comma");
                    }
                    final int len = from.applyAsInt(end);
                    return new ParsableJsonArray(chars, start, end, len, values);
                }
                // object
                else if (c == '{') {
                    // find index of closing object bracket
                    int end = -1;
                    {
                        int depth = 0, string = -1;
                        char prev;
                        for (int i = start + 1; i < l; i++) {
                            prev = c;
                            c = chars[i];
                            if (string == -1) {
                                if (c == '{')
                                    depth++;
                                else if (c == '}')
                                    if (depth == 0) {
                                        end = i + 1;
                                        break;
                                    }
                                    else
                                        depth--;
                                else if (c == '\"')
                                    string = i;
                            }
                            else if (c == '\"')
                                if (prev != '\\')
                                    string = -1;
                                else {
                                    // count joined backslashes
                                    int n = 1, k = i - 2;
                                    while (k > string && chars[k--] == '\\')
                                        n++;
                                    // if n is even then the string is enclosed
                                    if ((n & 0x1) == 0)
                                        string = -1;
                                    else
                                        break;
                                }
                        }
                    }
                    if (end == -1)
                        throw new ParseException("objects must be enclosed in { brackets }");
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
                                    throw new ParseException("object properties must contain a : separator");
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

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    protected Document read(@NotNull final Supplier<BufferedReader> readerSupplier) throws IOException {
        // executor to parse lines in parallel
        final ExecutorService executor = Executors.newCachedThreadPool();
        try {
            // list to hold futures of parse tasks
            final ArrayList<Future<Element>> futures = new ArrayList<>();
            // add parse task for each line, in line order
            try (final BufferedReader reader = readerSupplier.get()) {
                reader.lines().forEachOrdered(
                        line -> futures.add(executor.submit(new ParseTask(line, futures.size() + 1))));
            }
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

        KOFI(".kofi");

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

    protected enum NumFlag {
        FRAC_SEP, FRAC_NUM, EXP_PREFIX, EXP_SIGN, EXP_NUM, P32, P64
    }

    /**
     * Specialized supplier that can throw {@link IOException}.
     */
    @FunctionalInterface
    protected interface Supplier<T> {

        @Contract(value = "-> new", pure = true)
        @NotNull
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

    protected static class ParsableNull extends Parsable<Object> {

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
        public Object parse() {
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

        @NotNull
        @Override
        public String parse() {
            return KofiUtil.unescapeString(chars, start, end);
        }
    }
}
