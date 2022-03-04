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

import dk.martinu.kofi.*;
import dk.martinu.kofi.properties.*;
import dk.martinu.kofi.spi.*;

import static dk.martinu.kofi.KofiUtil.*;

/**
 * Codec for reading and writing text that conforms to the KoFi Text Syntax
 * specification.
 * <p>
 * For information on the textual representations of documents and their
 * contents, and how they are parsed back to documents, see the KoFi Syntax
 * specification.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class KofiCodec implements DocumentFileReader, DocumentFileWriter, DocumentStringReader, DocumentStringWriter {

    // list returned by getExtensions
    private static final List<String> EXTENSIONS = List.of("kofi");
    // constants used for KofiUtil.equalsIgnoreCase
    private static final char[] NULL = {'N', 'U', 'L', 'L'};
    private static final char[] TRUE = {'T', 'R', 'U', 'E'};
    private static final char[] FALSE = {'F', 'A', 'L', 'S', 'E'};
    private static final char[] NAN = {'N', 'A', 'N'};
    private static final char[] INFINITY = {'I', 'N', 'F', 'I', 'N', 'I', 'T', 'Y'};

    /**
     * Returns a new instance of this service provider.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public static KofiCodec provider() {
        return new KofiCodec();
    }

    /**
     * Returns a list of file extensions supported by this codec. The returned
     * list is immutable and only contains the {@code "kofi"} file extension.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    /**
     * {@inheritDoc}
     */
    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    @Override
    public Document readFile(@NotNull final Path filePath, @Nullable final Charset cs) throws IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        if (Files.isRegularFile(filePath))
            return read(() -> Files.newBufferedReader(filePath, cs != null ? cs : StandardCharsets.UTF_8));
        else
            return new Document();
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    @NotNull
    public Document readString(final @NotNull String string) throws IOException {
        Objects.requireNonNull(string, "string is null");
        if (!string.isEmpty())
            return read(() -> new BufferedReader(new CharArrayReader(string.toCharArray())));
        else
            return new Document();
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    public void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs)
            throws IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
        write(() -> Files.newBufferedWriter(filePath, cs != null ? cs : StandardCharsets.UTF_8), document);
    }

    /**
     * {@inheritDoc}
     */
    @Contract(pure = true)
    @Override
    @NotNull
    public String writeString(final @NotNull Document document) throws IOException {
        Objects.requireNonNull(document, "document is null");
        final CharArrayWriter writer = new CharArrayWriter();
        write(() -> new BufferedWriter(writer), document);
        return writer.toString();
    }

    /**
     * Parses the specified characters to an {@link Element} according to the
     * KoFi Text Syntax.
     * <p>
     * See {@link KofiCodec} for details on parsing.
     *
     * @param chars the line to parse
     * @param line  the line number that is being parsed
     * @return a new {@code Element} parsed from the specified line
     * @throws ParseException if an exception occurred when parsing the line
     */
    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    protected Element parseLine(final char[] chars, final int line) throws ParseException {
        // line is whitespace
        if (chars.length == 0)
            return new Whitespace();
        switch (chars[0]) {
            // comment
            case ';' -> {
                return new Comment(new String(chars, 1, chars.length - 1));
            }
            // section
            case '[' -> {
                if (chars[chars.length - 1] == ']')
                    return new Section(new String(chars, 1, chars.length - 2));
                else
                    throw KofiLog.exception(KofiCodec.class, "parseLine(String)",
                            new ParseException(line, 1, "section missing closing bracket"));
            }
            // property
            default -> {
                final Property<?> property = parseProperty(chars, line);
                if (property != null)
                    return property;
                else
                    throw KofiLog.exception(KofiCodec.class, "parseLine(String)",
                            new ParseException(line, 1, "invalid element"));
            }
        }
    }

    /**
     * Parses the specified array of characters as a {@link Property} and
     * returns it. The {@code line} parameter is only used for exception
     * messages and is not required. This method calls
     * {@link #parseValue(char[], int, int, int)}, and the type of the returned
     * property is determined by the type of the parsed value.
     *
     * @param chars the characters to parse
     * @param line  the line number of the characters to parse
     * @return a new property
     * @throws ParseException if an error occurs while parsing
     * @see dk.martinu.kofi.properties
     */
    @Contract(value = "null, _ -> fail", pure = true)
    @Nullable
    protected Property<?> parseProperty(final char[] chars, final int line) throws ParseException {
        // get index of delimiter
        final int delimiter = indexOf('=', chars, 0, chars.length);
        if (delimiter == -1)
            return null;

        // get parsable value
        final Parsable<?> parsable = parseValue(chars, delimiter + 1, -1, line);

        // return property
        if (parsable != null) {
            if (parsable.length == chars.length) {
                final String key = new String(trim(unescape(chars, 0, delimiter)));
                final Object value = parsable.getValue();
                return switch (parsable.getType()) {
                    case NULL -> new NullProperty(key);
                    case STRING -> new StringProperty(key, (String) value);
                    case DOUBLE -> new DoubleProperty(key, (Double) value);
                    case FLOAT -> new FloatProperty(key, (Float) value);
                    case LONG -> new LongProperty(key, (Long) value);
                    case INT -> new IntProperty(key, (Integer) value);
                    case CHAR -> new CharProperty(key, (Character) value);
                    case BOOLEAN -> new BooleanProperty(key, (Boolean) value);
                    case ARRAY -> new ArrayProperty(key, (KofiArray) value);
                    case OBJECT -> new ObjectProperty(key, (KofiObject) value);
                };
            }
            else
                throw KofiLog.exception(KofiCodec.class, "parseProperty(char[])",
                        new ParseException(line, parsable.length + 1, "property value has trailing data"));
        }
        else
            throw KofiLog.exception(KofiCodec.class, "parseProperty(char[])",
                    new ParseException(line, delimiter + 2, "property value is empty"));
    }

    /**
     * Parses the specified array of characters as a {@link Parsable} and
     * returns it, starting at the specified offset and reading no further than
     * the specified length (exclusive). The {@code line} parameter is only used
     * for exception messages and is not required.
     *
     * @param chars  the characters to parse
     * @param offset the starting offset
     * @param length the maximum length to read into {@code chars} (exclusive),
     *               or {@code -1}
     * @param line   the line number of the characters to parse
     * @return a new property
     * @throws ParseException if an error occurs while parsing
     */
    @Contract(value = "null, _, _, _ -> fail", pure = true)
    @Nullable
    protected KofiCodec.Parsable<?> parseValue(final char[] chars, final int offset, final int length,
            final int line) throws ParseException {
        assert chars != null : "chars is null";
        final KofiLog.Source src = new KofiLog.Source(KofiCodec.class, "parseValue(char[], int, int)");

        // max length of value to iterate
        final int l = length != -1 ? length : chars.length;


        for (int start = offset; start < l; start++) {
            char c = chars[start];
            if (isWhitespace(c)) {
                continue;
            }

            // null and NaN
            else if (c == 'n' || c == 'N') {
                if (equalsIgnoreCase(chars, start, l, NULL))
                    return new ParsableNull(chars, start, l);
                else if (equalsIgnoreCase(chars, start, l, NAN))
                    return ParsableFloat.getNan(chars, start, l);
            }

            // String
            else if (c == '"') {
                final int end = indexOf('"', chars, start + 1, l);
                if (end == -1)
                    throw KofiLog.exception(src, new ParseException(line, start + 1, "string is not enclosed"));
                return new ParsableString(chars, start, end + 1, l);
            }

            // char
            else if (c == '\'') {
                // TODO can char check be implemented better?
                final int remainder = l - start;
                final int end;
                if (remainder >= 8 && chars[start + 1] == '\\'
                        && (chars[start + 2] == 'u' || chars[start + 2] == 'U')
                        && isHexDigit(chars[start + 3]) && isHexDigit(chars[start + 4])
                        && isHexDigit(chars[start + 5]) && isHexDigit(chars[start + 6])
                        && chars[start + 7] == '\'') {
                    end = start + 8;
                    return new ParsableChar(chars, start, end, l);
                }
                else if (remainder >= 4 && chars[start + 1] == '\\' && chars[start + 3] == '\'') {
                    end = start + 4;
                    return new ParsableChar(chars, start, end, l);
                }
                else if (remainder >= 3 && chars[start + 2] == '\'') {
                    end = start + 3;
                    return new ParsableChar(chars, start, end, l);
                }
            }

            // boolean true
            else if (c == 't' || c == 'T') {
                if (equalsIgnoreCase(chars, start, l, TRUE))
                    return ParsableBoolean.getTrue(chars, start, l);
            }
            // boolean false
            else if (c == 'f' || c == 'F') {
                if (equalsIgnoreCase(chars, start, l, FALSE))
                    return ParsableBoolean.getFalse(chars, start, l);
            }

            // unsigned infinity
            else if (c == 'i' || c == 'I') {
                if (equalsIgnoreCase(chars, start, l, INFINITY))
                    return ParsableFloat.getInfinity(chars, start, l);
            }

            // Number and signed infinity
            else number:if (isDigit(c) || c == '-' || c == '+' || c == '.') {
                    boolean hasDigits = false, positive = true;
                    // flags for fraction, exponent and bit precision (set with type specifiers)
                    NumFlag fraction = null;
                    NumFlag exponent = null;
                    NumFlag precision = null;
                    int end = start;
                    for (; end < l; end++) {
                        c = chars[end];
                        // ws or separator - not part of number
                        if (isWhitespace(c) || c == ',') {
                            break;
                        }
                        // digits 0-9
                        else if (isDigit(c)) {
                            if (exponent != null)
                                exponent = NumFlag.EXP_NUM;
                            else if (fraction != null)
                                fraction = NumFlag.FRAC_NUM;
                            hasDigits = true;
                            continue;
                        }
                        // decimal separator
                        else if (c == '.') {
                            if (exponent == null && fraction == null) {
                                fraction = NumFlag.FRAC_SEP;
                                continue;
                            }
                        }
                        // signs
                        else if (c == '-' || c == '+') {
                            if (exponent == null) {
                                if (end == start) {
                                    positive = c == '+';
                                    continue;
                                }
                            }
                            else if (exponent == NumFlag.EXP_PREFIX) {
                                exponent = NumFlag.EXP_SIGN;
                                positive = c == '+';
                                continue;
                            }
                        }
                        // exponent
                        else if (c == 'e' || c == 'E') {
                            if (hasDigits && exponent == null) {
                                exponent = NumFlag.EXP_PREFIX;
                                continue;
                            }
                        }
                        // signed infinity
                        else if (c == 'i' || c == 'I') {
                            if (!hasDigits && fraction == null && equalsIgnoreCase(chars, end, l, INFINITY))
                                if (positive)
                                    return ParsableFloat.getPositiveInfinity(chars, start, l);
                                else
                                    return ParsableFloat.getNegativeInfinity(chars, start, l);
                        }
                        // long
                        else if (c == 'L' || c == 'l') {
                            if (hasDigits && fraction == null && exponent == null && precision == null) {
                                precision = NumFlag.P64;
                                continue;
                            }
                        }
                        // double
                        else if (c == 'd' || c == 'D') {
                            if (hasDigits && precision == null) {
                                precision = NumFlag.P64;
                                continue;
                            }
                        }
                        // float
                        else if (c == 'F' || c == 'f') {
                            if (hasDigits && precision == null) {
                                precision = NumFlag.P32;
                                continue;
                            }
                        }
                        // invalid number character
                        break number;
                    }

                    if (hasDigits && (exponent == null || exponent == NumFlag.EXP_NUM)) {
                        // double and float
                        if (fraction != null || exponent != null) {
                            if (precision != null)
                                return precision == NumFlag.P64 ?
                                        new ParsableDouble(chars, start, end, l) :
                                        new ParsableFloat(chars, start, end, l);
                            else
                                return new ParsableFloat(chars, start, end, l);
                        }
                        // long and int
                        else {
                            if (precision != null)
                                return precision == NumFlag.P64 ?
                                        new ParsableLong(chars, start, end, l) :
                                        new ParsableInt(chars, start, end, l);
                            else
                                return new ParsableInt(chars, start, end, l);
                        }
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
                        throw KofiLog.exception(src, new ParseException(line, start + 1, "array missing closing bracket"));

                    // list of parsable values in the array
                    final ArrayList<Parsable<?>> values = new ArrayList<>();
                    boolean parse = true;
                    for (int i = start + 1; i < end - 1; ) {
                        if (parse) {
                            final Parsable<?> pv = parseValue(chars, i, end - 1, line);
                            // add non-empty values to list
                            if (pv != null) {
                                values.add(pv);
                                i = pv.length;
                                parse = false;
                            }
                            // allow empty arrays, but not empty values in populated arrays
                            else if (values.isEmpty())
                                break;
                            else
                                throw KofiLog.exception(src, new ParseException(line, i + 1, "expecting array value"));
                        }
                        else if (chars[i] == ',') {
                            parse = true;
                            i++;
                        }
                        else
                            throw KofiLog.exception(src,
                                    new ParseException(line, i + 1, "array values must be separated by a comma"));
                    }
                    return new ParsableKofiArray(chars, start, end, l, values);
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
                        throw KofiLog.exception(src, new ParseException(line, start + 1, "object missing closing bracket"));

                    // list of parsable key/value pairs in the object
                    final ArrayList<Parsable<?>> properties = new ArrayList<>();
                    boolean parse = true;
                    for (int i = start + 1; i < end - 1; ) {
                        if (parse) {
                            // get property key
                            final Parsable<?> key = parseValue(chars, i, end - 1, line);
                            if (key != null && key.getType() != Parsable.Type.STRING)
                                throw KofiLog.exception(src, new ParseException(line, i + 1, "invalid entry key"));
                            if (key != null) {
                                if (key.length == end - 1 || chars[key.length] != ':') {
                                    throw KofiLog.exception(src,
                                            new ParseException(line, key.length + 1, "expecting separator"));
                                }
                                final Parsable<?> value =
                                        parseValue(chars, key.length + 1, end - 1, line);
                                if (value == null) {
                                    throw KofiLog.exception(src,
                                            new ParseException(line, key.length + 2, "expecting entry value"));
                                }
                                properties.add(key);
                                properties.add(value);
                                i = value.length;
                                parse = false;
                            }
                            // allow empty objects, but not null entries in populated objects
                            else if (properties.isEmpty())
                                break;
                            else
                                throw KofiLog.exception(src, new ParseException(line, i + 1, "expecting object entry"));
                        }
                        else if (chars[i] == ',') {
                            parse = true;
                            i++;
                        }
                        else
                            throw KofiLog.exception(src,
                                    new ParseException(line, i + 1, "object entries must be separated by a comma"));
                    }

                    return new ParsableKofiObject(chars, start, end, l, properties);
                }

            // unknown value type
            throw KofiLog.exception(src, new ParseException(line, start + 1, "invalid value"));
        }
        // if this statement is reached then the value is empty or contains only whitespace
        return null;
    }

    /**
     * Reads a new {@link Document} from the supplied reader and returns it.
     *
     * @param supplier the {@link BufferedReader} supplier
     * @return a new document
     * @throws NullPointerException if {@code supplier} is {@code null}
     * @throws IOException          if an error occurs is while reading
     */
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    protected Document read(@NotNull final Supplier<BufferedReader> supplier) throws IOException {
        Objects.requireNonNull(supplier, "supplier is null");
        // executor to parse lines in parallel
        final ExecutorService executor = Executors.newCachedThreadPool();
        try {
            // list to hold futures of parse tasks
            final ArrayList<Future<Element>> futures = new ArrayList<>();
            // add parse task for each line, in line order
            try (final BufferedReader reader = supplier.get()) {
                reader.lines().forEachOrdered(line -> futures.add(executor.submit(
                        new ParseTask(line.toCharArray(), futures.size() + 1))));
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

    /**
     * Writes the {@link Document} to the supplied writer.
     *
     * @param supplier the {@link BufferedWriter} supplier
     * @param document the document to write
     * @throws NullPointerException if {@code supplier} or {@code document} is
     *                              {@code null}
     * @throws IOException          if an error occurs while writing
     */
    protected void write(@NotNull final Supplier<BufferedWriter> supplier, @NotNull final Document document) throws
            IOException {
        Objects.requireNonNull(supplier, "supplier is null");
        Objects.requireNonNull(document, "document is null");
        try (BufferedWriter writer = supplier.get()) {
            for (int i = 0; i < document.size(); i++) {
                final String s;
                try {
                    s = document.getElement(i).getString();
                }
                catch (Exception e) {
                    throw KofiLog.exception(KofiCodec.class, "write(KofiCodec.Supplier, Document",
                            new IOException("could not get string representation {" + document.getElement(i) + "}", e));
                }
                writer.write(s);
                if (i < document.size() - 1)
                    writer.write('\n');
            }
            writer.flush();
        }
    }

    /**
     * Flags used to retain information about a number while it is being parsed.
     */
    protected enum NumFlag {
        /**
         * Fraction separator.
         */
        FRAC_SEP,
        /**
         * Fraction number.
         */
        FRAC_NUM,
        /**
         * Exponent prefix.
         */
        EXP_PREFIX,
        /**
         * Exponent sign.
         */
        EXP_SIGN,
        /**
         * Exponent number
         */
        EXP_NUM,
        /**
         * 32 bit precision flag. Used for int and float.
         */
        P32,
        /**
         * 64 bit precision flag. Used for long and double.
         */
        P64
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

    /**
     * A task that parses a line of characters into an {@link Element}.
     */
    protected class ParseTask implements Callable<Element> {

        public final char[] chars;
        public final int line;

        @Contract(pure = true)
        public ParseTask(final char[] chars, final int line) {
            this.chars = chars;
            this.line = line;
        }

        @Contract(value = "-> new", pure = true)
        @NotNull
        @Override
        public Element call() throws ParseException {
            return parseLine(KofiUtil.trim(chars), line);
        }
    }

    /**
     * A subarray of a {@code char} array source representing a value of type
     * {@code T}. The value can be retrieved with {@link #getValue()}.
     *
     * @param <T> the runtime type of the value
     * @see #parseValue(char[], int, int, int)
     */
    protected abstract static class Parsable<T> {

        /**
         * {@code char} array source.
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

        /**
         * Constructs a new parsable with the specified source, start, end and
         * length. {@code len} is not the length of the subarray, but the length
         * into the array source from index {@code 0}, including any
         * insignificant such as whitespace. Any character at or after
         * {@code len} is not part of this parsable.
         *
         * @param chars the array source
         * @param start the start of this parsable, inclusive
         * @param end   the end of this parsable, exclusive
         * @param len   the last index of this parsable in the array source,
         *              exclusive
         */
        public Parsable(final char[] chars, final int start, final int end, final int len) {
            this.chars = chars;
            this.start = start;
            this.end = end;

            int n = end;
            while (n < len && isWhitespace(chars[n]))
                n++;
            length = n;
        }

        /**
         * Returns the value type of this parsable.
         */
        @NotNull
        public abstract Type getType();

        /**
         * Parses the characters of this instance into a value and returns it.
         */
        public abstract T getValue();

        /**
         * An enumeration of the different value types that can be parsed.
         *
         * @see #getType()
         */
        public enum Type {
            NULL, STRING, INT, LONG, FLOAT, DOUBLE, CHAR, BOOLEAN, ARRAY, OBJECT
        }

    }

    protected static class ParsableBoolean extends Parsable<Boolean> {

        @NotNull
        public static ParsableBoolean getFalse(final char[] chars, final int start, final int len) {
            return new ParsableBoolean(chars, start, start + 5, len, Boolean.FALSE);
        }

        @NotNull
        public static ParsableBoolean getTrue(final char[] chars, final int start, final int len) {
            return new ParsableBoolean(chars, start, start + 4, len, Boolean.TRUE);
        }

        @NotNull
        protected Boolean value;

        private ParsableBoolean(final char[] chars, final int start, final int end, final int len,
                @NotNull final Boolean value) {
            super(chars, start, end, len);
            this.value = value;
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.BOOLEAN;
        }

        @NotNull
        @Override
        public Boolean getValue() {
            return value;
        }
    }

    protected static class ParsableChar extends Parsable<Character> {

        public ParsableChar(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.CHAR;
        }

        @NotNull
        @Override
        public Character getValue() {
            final int len = end - start;
            return switch (len) {
                case 3 -> chars[start + 1];
                case 4 -> switch (chars[start + 2]) {
                    case 't' -> '\t';
                    case 'b' -> '\b';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 'f' -> '\f';
                    case '0' -> '\0';
                    // character is escaped but does not require special handling
                    default -> chars[start + 2];
                };
                case 8 -> (char) Integer.parseInt(new String(chars, start + 3, 4), 16);
                default -> throw new RuntimeException("unexpected length {" + len + "}");
            };
        }
    }

    protected static class ParsableDouble extends Parsable<Double> {

        public ParsableDouble(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.DOUBLE;
        }

        @NotNull
        @Override
        public Double getValue() {
            // do not include specifier if present, e.g. 1.5d -> 1.5d
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Double.parseDouble(new String(chars, start, count));
        }
    }

    protected static class ParsableFloat extends Parsable<Float> {

        @NotNull
        public static ParsableFloat getInfinity(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + 8, len);
            p.value = Float.POSITIVE_INFINITY;
            return p;
        }

        @NotNull
        public static ParsableFloat getNan(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + 3, len);
            p.value = Float.NaN;
            return p;
        }

        @NotNull
        public static ParsableFloat getNegativeInfinity(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + 9, len);
            p.value = Float.NEGATIVE_INFINITY;
            return p;
        }

        @NotNull
        public static ParsableFloat getPositiveInfinity(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + 9, len);
            p.value = Float.POSITIVE_INFINITY;
            return p;
        }

        @Nullable
        protected Float value = null;

        public ParsableFloat(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.FLOAT;
        }

        @NotNull
        @Override
        public Float getValue() {
            if (value != null)
                return value;
            else {
                // do not include specifier if present, e.g. 1.5F -> 1.5
                final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
                return Float.parseFloat(new String(chars, start, count));
            }
        }
    }

    protected static class ParsableInt extends Parsable<Integer> {

        public ParsableInt(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.INT;
        }

        @NotNull
        @Override
        public Integer getValue() {
            return Integer.parseInt(new String(chars, start, end - start));
        }
    }

    protected static class ParsableKofiArray extends Parsable<KofiArray> {

        @NotNull
        private final List<Parsable<?>> values;

        public ParsableKofiArray(final char[] chars, final int start, final int end, final int len,
                @NotNull final ArrayList<Parsable<?>> values) {
            super(chars, start, end, len);
            this.values = values;
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.ARRAY;
        }

        @NotNull
        @Override
        public KofiArray getValue() {
            final Object[] array = new Object[values.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = values.get(i).getValue();
            return new KofiArray(array);
        }
    }

    protected static class ParsableKofiObject extends Parsable<KofiObject> {

        @NotNull
        private final ArrayList<Parsable<?>> properties;

        public ParsableKofiObject(final char[] chars, final int start, final int end, final int len,
                @NotNull final ArrayList<Parsable<?>> properties) {
            super(chars, start, end, len);
            this.properties = properties;
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.OBJECT;
        }

        @NotNull
        @Override
        public KofiObject getValue() {
            final KofiObject.Builder builder = new KofiObject.Builder();
            for (int i = 0; i < properties.size(); i += 2)
                builder.put((String) properties.get(i).getValue(),
                        properties.get(i + 1).getValue());
            return builder.build();
        }
    }

    protected static class ParsableLong extends Parsable<Long> {

        public ParsableLong(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.LONG;
        }

        @NotNull
        @Override
        public Long getValue() {
            // do not include specifier if present, e.g. 22L -> 22
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Long.parseLong(new String(chars, start, count));
        }
    }

    protected static class ParsableNull extends Parsable<Object> {

        public ParsableNull(final char[] chars, final int start, final int len) {
            super(chars, start, start + 4, len);
        }

        @Override
        @NotNull
        public Type getType() {
            return Type.NULL;
        }

        @Override
        @Nullable
        public Object getValue() {
            return null;
        }
    }

    protected static class ParsableString extends Parsable<String> {

        public ParsableString(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        @NotNull
        @Override
        public Type getType() {
            return Type.STRING;
        }

        @Contract(value = "-> new", pure = true) // inferred
        @NotNull
        @Override
        public String getValue() {
            // do not include quotation marks
            return new String(unescape(chars, start + 1, end - 1));
        }
    }
}
