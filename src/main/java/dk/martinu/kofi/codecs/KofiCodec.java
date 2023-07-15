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
 * Codec for reading and writing text that conforms to the KoFi Text Syntax.
 * <p>
 * For information on the textual representations of documents and their
 * contents, see the KoFi Technical Specification.
 *
 * @author Adam Martinu
 * @since 1.0
 */
public class KofiCodec implements DocumentFileReader, DocumentFileWriter, DocumentStringReader, DocumentStringWriter {


    /**
     * List returned by {@link #getExtensions()}.
     */
    private static final List<String> EXTENSIONS = List.of("kofi");
    /**
     * Constant for matching a string with {@code "null"}.
     */
    private static final char[] NULL = {'N', 'U', 'L', 'L'};
    /**
     * Constant for matching a string with {@code "true"}.
     */
    private static final char[] TRUE = {'T', 'R', 'U', 'E'};
    /**
     * Constant for matching a string with {@code "false"}.
     */
    private static final char[] FALSE = {'F', 'A', 'L', 'S', 'E'};
    /**
     * Constant for matching a string with {@code "NaN"}.
     */
    private static final char[] NAN = {'N', 'A', 'N'};
    /**
     * Constant for matching a string with {@code "infinity"}.
     */
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
     * Parses an entry name from the specified region of characters and returns
     * a {@link Parsable Parsable} object to get the name.
     *
     * @param chars  the characters to parse
     * @param offset start index in {@code chars}, inclusive
     * @param length end index in {@code chars}, exclusive
     * @return a new {@code Parsable} entry name
     */
    @Contract(value = "!null, _, _ -> new; null, _, _ -> fail", pure = true)
    @NotNull
    protected ParsableEntryName parseEntryName(final char[] chars, final int offset, final int length) {
        assert chars != null : "chars is null";
        // skip leading whitespace
        int start = offset;
        while (start < length && isWhitespace(chars[start]))
            start++;

        // last significant index of entry name, exclusive
        int end = start;
        // scan remaining chars for name
        // stops when exhausted or separator ':' is found
        for (int i = start; i < length; i++) {
            if (chars[i] == ':') {
                if (isEscaped(chars, i, start - 1))
                    end = i + 1;
                else
                    break;
            }
            else if (!isWhitespace(chars[i]) || (i > start && isEscaped(chars, i, start - 1)))
                end = i + 1;
        }

        return new ParsableEntryName(chars, start, end, length);
    }

    /**
     * Parses an {@link Element} from the specified character array and returns
     * it.
     *
     * @param chars the line to parse
     * @param line  the line number that is being parsed
     * @return a new {@code Element}
     * @throws ParseException if an error occurs while parsing
     */
    @SuppressWarnings("Contract")
    @Contract(value = "!null, _ -> new; null, _ -> fail", pure = true)
    @NotNull
    protected Element parseLine(char[] chars, final int line) throws ParseException {
        assert chars != null : "chars is null";
        final KofiLog.Source src = new KofiLog.Source(KofiCodec.class, "parseLine(char[], int)");

        // ignore leading and trailing whitespace in chars
        int start = 0, end = chars.length;
        for (; start < end; start++) {
            if (!isWhitespace(chars[start]))
                break;
        }
        for (; end > start; end--) {
            if (!isWhitespace(chars[end - 1]))
                break;
        }
        final int len = end - start;

        // line is whitespace
        if (len == 0)
            return new Whitespace();

        switch (chars[start]) {

            // comment
            case ';' -> {
                return new Comment(new String(chars, start + 1, len - 1));
            }

            // section
            case '[' -> {
                if (chars[end - 1] == ']')
                    return new Section(new String(KofiUtil.unescape(chars, start + 1, end - 1)));
                else
                    throw KofiLog.exception(src, new ParseException(line, len - 1,
                            "section closing bracket ']' expected"));
            }

            // property
            default -> {
                final Property<?> property = parseProperty(chars, start, end, line);
                if (property != null)
                    return property;
                else
                    throw KofiLog.exception(src, new ParseException(line, 0, "invalid element"));
            }
        }
    }

    /**
     * Parses a {@link Property} from the specified character array and returns
     * it, or {@code null} if it does not represent a property (no '=' delimiter).
     *
     * @param chars  the characters to parse
     * @param offset start index in {@code chars}, inclusive
     * @param end    end index in {@code chars}, exclusive
     * @param line   the line number that is being parsed
     * @return a new property, or {@code null}
     * @throws ParseException if an error occurs while parsing
     * @see dk.martinu.kofi.properties
     */
    @Contract(value = "null, _, _, _ -> fail", pure = true)
    @Nullable
    protected Property<?> parseProperty(final char[] chars, final int offset, final int end, final int line) throws ParseException {
        assert chars != null : "chars is null";
        final KofiLog.Source src = new KofiLog.Source(KofiCodec.class, "parseProperty(char[], int)");

        // get index of delimiter
        final int delimiter = indexOf('=', chars, offset, end);
        if (delimiter == -1)
            return null;

        // get parsable value
        final Parsable<?> parsable = parseValue(chars, delimiter + 1, end, line);

        // return property
        if (parsable != null) {
            if (parsable.length == end) {
                final String key = new String(unescape(trim(chars, 0, delimiter)));
                final Object value = parsable.getValue();
                return switch (parsable.getValueType()) {
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
                throw KofiLog.exception(src, new ParseException(line, parsable.length,
                        "property value has trailing characters"));
        }
        else
            throw KofiLog.exception(src, new ParseException(line, delimiter + 1, "property value expected"));
    }

    /**
     * Parses a {@link Property} value from the specified region of characters
     * and returns it, or {@code null} if the region is empty or contains only
     * whitespace.
     *
     * @param chars  the characters to parse
     * @param offset start index in {@code chars}, inclusive
     * @param length end index in {@code chars}, exclusive
     * @param line   the line number that is being parsed
     * @return a new property, or {@code null}
     * @throws ParseException if an error occurs while parsing
     */
    @Contract(value = "null, _, _, _ -> fail", pure = true)
    @Nullable
    protected KofiCodec.Parsable<?> parseValue(final char[] chars, final int offset, final int length,
            final int line) throws ParseException {
        assert chars != null : "chars is null";
        final KofiLog.Source src = new KofiLog.Source(KofiCodec.class, "parseValue(char[], int, int, int)");

        for (int start = offset; start < length; start++) {
            char c = chars[start];
            if (isWhitespace(c)) {
                continue;
            }

            // null and NaN
            else if (c == 'n' || c == 'N') {
                if (matchesCI(chars, start, length, NULL))
                    return new ParsableNull(chars, start, length);
                else if (matchesCI(chars, start, length, NAN))
                    return ParsableFloat.getNan(chars, start, length);
            }

            // String
            else if (c == '"') {
                final int end = indexOf('"', chars, start + 1, length);
                if (end == -1)
                    throw KofiLog.exception(src, new ParseException(line, start + 1, "string is not enclosed"));
                return new ParsableString(chars, start, end + 1, length);
            }

            // Character
            else if (c == '\'') {
                final int remainder = length - start;
                final int end;
                // six-character unicode escape sequence
                // '\UXXXX'
                if (remainder >= 8 && chars[start + 1] == '\\'
                        && (chars[start + 2] == 'u' || chars[start + 2] == 'U')
                        && isHexDigit(chars[start + 3]) && isHexDigit(chars[start + 4])
                        && isHexDigit(chars[start + 5]) && isHexDigit(chars[start + 6])
                        && chars[start + 7] == '\'') {
                    end = start + 8;
                    return new ParsableChar(chars, start, end, length);
                }
                // two-character escape sequence
                // '\X'
                else if (remainder >= 4 && chars[start + 1] == '\\' && chars[start + 3] == '\'') {
                    end = start + 4;
                    return new ParsableChar(chars, start, end, length);
                }
                // single character
                // 'X'
                else if (remainder >= 3 && chars[start + 2] == '\'') {
                    end = start + 3;
                    return new ParsableChar(chars, start, end, length);
                }
            }

            // Boolean
            else if (c == 't' || c == 'T') {
                if (matchesCI(chars, start, length, TRUE))
                    return ParsableBoolean.getTrue(chars, start, length);
            }
            else if (c == 'f' || c == 'F') {
                if (matchesCI(chars, start, length, FALSE))
                    return ParsableBoolean.getFalse(chars, start, length);
            }

            // unsigned infinity
            else if (c == 'i' || c == 'I') {
                if (matchesCI(chars, start, length, INFINITY))
                    return ParsableFloat.getInfinity(chars, start, length);
            }

            // Number and signed infinity
            else if (isDigit(c) || c == '-' || c == '+' || c == '.') {
                boolean hasDigits = false, positive = true;
                NumFlag fraction = null;
                NumFlag exponent = null;
                NumFlag precision = null;
                int end = start;
                for (; end < length; end++) {
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
                        if (!hasDigits && fraction == null && matchesCI(chars, end, length, INFINITY))
                            if (positive)
                                return ParsableFloat.getPositiveInfinity(chars, start, length);
                            else
                                return ParsableFloat.getNegativeInfinity(chars, start, length);
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
                    hasDigits = false;
                    break;
                }

                if (hasDigits && (exponent == null || exponent == NumFlag.EXP_NUM)) {
                    // double and float
                    if (fraction != null || exponent != null) {
                        if (precision != null)
                            return precision == NumFlag.P64 ?
                                    new ParsableDouble(chars, start, end, length) :
                                    new ParsableFloat(chars, start, end, length);
                        else
                            return new ParsableDouble(chars, start, end, length);
                    }
                    // long and int
                    else {
                        if (precision != null)
                            return precision == NumFlag.P64 ?
                                    new ParsableLong(chars, start, end, length) :
                                    new ParsableInt(chars, start, end, length);
                        else
                            return new ParsableInt(chars, start, end, length);
                    }
                }
            }

            // KofiArray
            else if (c == '[') {
                // index of closing object bracket
                int end = -1;
                {
                    // nested object depth
                    int depth = 0;
                    // index of opening string quote, or -1
                    int string = -1;
                    // find index of closing array bracket
                    for (int i = start + 1; i < length; i++) {
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
                        else if (c == '\"' && !isEscaped(chars, i, start))
                            string = -1;
                    }
                }
                if (end == -1)
                    throw KofiLog.exception(src, new ParseException(line, start, "array is not enclosed"));
                // index for array characters
                int i = start + 1;
                // skip whitespace until first significant character
                while (i < end - 1) {
                    if (isWhitespace(chars[i]))
                        i++;
                    else
                        break;
                }
                // parsable component type of array, if any
                final ParsableTypeSpecifier typeSpecifier = parseTypeSpecifier(chars, start + 1, end - 1, line);
                // list of parsable values in the array
                final ArrayList<Parsable<?>> values = new ArrayList<>();
                // true if parsing a value, false if expecting an array value separator
                boolean parse = true;
                // update i and parse if type specifier is present
                if (typeSpecifier != null) {
                    i = typeSpecifier.length;
                    parse = false;
                }
                // parse array values
                while (i < end - 1) {
                    if (parse) {
                        final Parsable<?> value = parseValue(chars, i, end - 1, line);
                        // add non-empty values to list
                        if (value != null) {
                            values.add(value);
                            i = value.length;
                            parse = false;
                        }
                        // allow empty arrays, but not empty values in populated arrays
                        else if (values.isEmpty())
                            break;
                        else
                            throw KofiLog.exception(src, new ParseException(line, i + 1, "array value expected"));
                    }
                    else if (chars[i] == ',') {
                        parse = true;
                        i++;
                    }
                    else
                        throw KofiLog.exception(src,
                                new ParseException(line, i + 1, "array value separator ',' expected"));
                }
                // TODO add type specifier to array
                return new ParsableKofiArray(chars, start, end, length, values);
            }

            // KofiObject
            else if (c == '{') {
                // index of closing object bracket
                int end = -1;
                {
                    // nested object depth
                    int depth = 0;
                    // index of opening string quote, or -1
                    int string = -1;
                    // find index of closing object bracket
                    for (int i = start + 1; i < length; i++) {
                        c = chars[i];
                        if (string == -1) {
                            if (c == '{') {
                                if (!isEscaped(chars, i, start))
                                    depth++;
                            }
                            else if (c == '}') {
                                if (!isEscaped(chars, i, start))
                                    if (depth == 0) {
                                        end = i + 1;
                                        break;
                                    }
                                    else
                                        depth--;
                            }
                            else if (c == '\"' && !isEscaped(chars, i, start))
                                string = i;
                        }
                        else if (c == '\"' && !isEscaped(chars, i, string))
                            string = -1;
                    }
                }
                if (end == -1)
                    throw KofiLog.exception(src, new ParseException(line, start + 1, "object is not enclosed"));

                // TODO type specifier

                // list of parsable name/value pairs in the object
                final ArrayList<Parsable<?>> entries = new ArrayList<>();
                boolean parse = true;
                for (int i = start + 1; i < end - 1; ) {
                    if (parse) {
                        // get name - stops at index of separator or consumes all remaining chars
                        final ParsableEntryName name = parseEntryName(chars, i, end - 1);

                        // name spans all remaining chars
                        if (name.length == end - 1) {
                            // name is not empty
                            if (name.start != name.end) {
                                throw KofiLog.exception(src, new ParseException(line, name.end,
                                        "object name-value separator ':' expected"));
                            }
                            // name is empty but object is already populated
                            else if (!entries.isEmpty()) {
                                throw KofiLog.exception(src, new ParseException(line, i + 1, "object entry expected"));
                            }
                            // object is empty
                            else
                                break;
                        }

                        // get value
                        final Parsable<?> value = parseValue(chars, name.length + 1, end - 1, line);
                        if (value == null)
                            throw KofiLog.exception(src,
                                    new ParseException(line, name.length + 2, "object entry value expected"));

                        entries.add(name);
                        entries.add(value);
                        i = value.length;
                        parse = false;
                    }
                    else if (chars[i] == ',') {
                        parse = true;
                        i++;
                    }
                    else
                        throw KofiLog.exception(src, new ParseException(line, i + 1,
                                "object entry separator ',' expected"));
                }

                return new ParsableKofiObject(chars, start, end, length, entries);
            }

            // TODO removing this exception would allow implementers to add their own value types
            //  call parseOther(...) default implementation throws ParseException.
            //  Seems like a bad design; it would only allow one subclass to override parseOther.
            //  Maybe add method isParsable(char) or getParser(char)
            //    isParsable: simple but checks the same characters twice
            //    getParser: complex but only iterates characters once (possible ParserParsable?)
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
                        throw new IOException("an error occurred while reading", e);
                    }
            }
            return document;
        }
        finally {
            // TODO use shutdownNow() instead
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
        // TODO It's possible to convert elements to strings in parallel, and
        //  then aggregate the result and write on single thread.
        //  This could also reduce the amount of time the file is locked (for very large files),
        //  because element conversion is done outside of the try-with block
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
     * Specialized supplier that can throw {@link IOException}. Used to create
     * instances of readers and writers as lambda expressions which can be
     * passed to other methods as parameters.
     */
    @FunctionalInterface
    protected interface Supplier<T extends Closeable> {

        /**
         * Returns a new value, potentially throwing an {@code IOException}.
         */
        @Contract(value = "-> new", pure = true)
        @NotNull
        T get() throws IOException;
    }

    /**
     * A specific region in a {@code char} array representing a value of type
     * {@code T}, which can be parsed to get the represented value with
     * {@link #getValue()}.
     *
     * @param <T> the runtime type of the value
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
         * last index in the array which is part of this parsable (including
         * whitespace), exclusive. This is <b>not</b> the length of this
         * parsable itself. Any character at or after this index in the array
         * is not part of this parsable.
         */
        public final int length;

        /**
         * Constructs a new parsable with the specified source, start, end and
         * length. {@code len} is not the length of this parsable, but the
         * highest possible length into the array source from index {@code 0}
         * that can be a part of this parsable, exclusive. Any character at or
         * after {@code len} is guaranteed to not be a part of this parsable.
         * <p>
         * Remaining characters between {@code end} and {@code len} are
         * iterated, and the final length will be the highest index before any
         * non-whitespace character, or {@code len} if all remaining characters
         * are whitespace.
         *
         * @param chars the array source
         * @param start the start of this parsable, inclusive
         * @param end   the end of this parsable, exclusive
         * @param len   the highest possible index of this parsable in the
         *              array source, exclusive
         */
        @Contract(pure = true)
        public Parsable(final char[] chars, final int start, final int end, final int len) {
            this.chars = chars;
            this.start = start;
            this.end = end;

            // most parsing loops break their cycle when a value can be
            // determined, scan for remaining whitespace here
            int n = end;
            while (n < len && isWhitespace(chars[n]))
                n++;
            length = n;
        }

        /**
         * Returns the value represented by this parsable.
         */
        public abstract T getValue();

        /**
         * Returns the type of value returned by {@link #getValue()} of
         * this parsable.
         */
        @NotNull
        public abstract Type getValueType();

        /**
         * An enumeration of the different value types that can be parsed.
         *
         * @see #getValueType()
         */
        public enum Type {
            NULL, STRING, INT, LONG, FLOAT, DOUBLE, CHAR, BOOLEAN, ARRAY, OBJECT
        }

    }

    /**
     * A parsable that represents a {@code Boolean} value.
     * <p>
     * Unlike other parsables, parsable booleans are created using static
     * factory methods because all possible values ({@code true} or
     * {@code false}) can be determined before instantiation.
     *
     * @see #getFalse(char[], int, int)
     * @see #getTrue(char[], int, int)
     */
    protected static class ParsableBoolean extends Parsable<Boolean> {

        /**
         * Returns a parsable that represents {@code false}.
         */
        @Contract(value = "!null, _, _ -> new", pure = true)
        @NotNull
        public static ParsableBoolean getFalse(final char[] chars, final int start, final int len) {
            return new ParsableBoolean(chars, start, start + FALSE.length, len, Boolean.FALSE);
        }

        /**
         * Returns a parsable that represents {@code true}.
         */
        @Contract(value = "!null, _, _ -> new", pure = true)
        @NotNull
        public static ParsableBoolean getTrue(final char[] chars, final int start, final int len) {
            return new ParsableBoolean(chars, start, start + TRUE.length, len, Boolean.TRUE);
        }

        /**
         * The value of this parsable.
         */
        @NotNull
        protected final Boolean value;

        /**
         * Private constructor, use one of the static factory methods to get an
         * instance of this parsable type.
         */
        @Contract(pure = true)
        private ParsableBoolean(final char[] chars, final int start, final int end, final int len,
                @NotNull final Boolean value) {
            super(chars, start, end, len);
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Boolean getValue() {
            return value;
        }

        /**
         * Returns {@code Type.BOOLEAN}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.BOOLEAN;
        }
    }

    /**
     * A parsable that represents a {@code Character} value.
     */
    protected static class ParsableChar extends Parsable<Character> {

        /**
         * Constructs a new {@code ParsableChar}.
         */
        @Contract(pure = true)
        public ParsableChar(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Character getValue() {
            final int len = end - start;
            return switch (len) {
                // single character
                case 3 -> chars[start + 1];
                // two-character escape sequence
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
                // four-character unicode escape sequence
                case 8 -> (char) Integer.parseInt(new String(chars, start + 3, 4), 16);
                default -> throw new RuntimeException("unexpected length {" + len + "}");
            };
        }

        /**
         * Returns {@code Type.CHAR}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.CHAR;
        }
    }

    /**
     * A parsable that represents a {@code Double} value.
     */
    protected static class ParsableDouble extends Parsable<Double> {

        /**
         * Constructs a new {@code ParsableDouble}.
         */
        @Contract(pure = true)
        public ParsableDouble(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Double getValue() {
            // do not include specifier if present
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Double.parseDouble(new String(chars, start, count));
        }

        /**
         * Returns {@code Type.DOUBLE}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.DOUBLE;
        }
    }

    /**
     * A parsable that represents an entry name. The name is trimmed for
     * whitespace and unescaped when parsed.
     */
    protected static class ParsableEntryName extends Parsable<String> {

        /**
         * Constructs a new {@code ParsableEntryName}.
         */
        @Contract(pure = true)
        public ParsableEntryName(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public String getValue() {
            return new String(unescape(chars, start, end));
        }

        /**
         * Returns {@code Type.STRING}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.STRING;
        }
    }

    /**
     * A parsable that represents a {@code Float} value. Parsables for constant
     * values, such as {@code Float.POSITIVE_INFINITY}, are created using
     * static factory methods.
     */
    protected static class ParsableFloat extends Parsable<Float> {

        /**
         * Returns a parsable that represents {@code Float.POSITIVE_INFINITY}.
         */
        @Contract(value = "!null, _, _ -> new", pure = true)
        @NotNull
        public static ParsableFloat getInfinity(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + INFINITY.length, len);
            p.value = Float.POSITIVE_INFINITY;
            return p;
        }

        /**
         * Returns a parsable that represents {@code Float.NaN}.
         */
        @Contract(value = "!null, _, _ -> new", pure = true)
        @NotNull
        public static ParsableFloat getNan(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + NAN.length, len);
            p.value = Float.NaN;
            return p;
        }

        /**
         * Returns a parsable that represents {@code Float.NEGATIVE_INFINITY}.
         */
        @Contract(value = "!null, _, _ -> new", pure = true)
        @NotNull
        public static ParsableFloat getNegativeInfinity(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + INFINITY.length + 1, len);
            p.value = Float.NEGATIVE_INFINITY;
            return p;
        }

        /**
         * Returns a parsable that represents {@code Float.POSITIVE_INFINITY}.
         */
        @Contract(value = "!null, _, _ -> new", pure = true)
        @NotNull
        public static ParsableFloat getPositiveInfinity(final char[] chars, final int start, final int len) {
            final ParsableFloat p = new ParsableFloat(chars, start, start + INFINITY.length + 1, len);
            p.value = Float.POSITIVE_INFINITY;
            return p;
        }

        /**
         * Cached value of this parsable.
         */
        @Nullable
        protected Float value = null;

        /**
         * Constructs a new {@code ParsableFloat}.
         */
        @Contract(pure = true)
        public ParsableFloat(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
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

        /**
         * Returns {@code Type.FLOAT}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.FLOAT;
        }
    }

    /**
     * A parsable that represents an {@code Integer} value.
     */
    protected static class ParsableInt extends Parsable<Integer> {

        /**
         * Constructs a new {@code ParsableInt}.
         */
        @Contract(pure = true)
        public ParsableInt(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Integer getValue() {
            return Integer.parseInt(new String(chars, start, end - start));
        }

        /**
         * Returns {@code Type.INT}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.INT;
        }
    }

    /**
     * A parsable that represents a {@link KofiArray} value.
     */
    protected static class ParsableKofiArray extends Parsable<KofiArray> {

        /**
         * List of parsable values in this array.
         */
        @NotNull
        private final List<Parsable<?>> values;

        /**
         * Constructs a new {@code ParsableKofiArray}.
         */
        @Contract(pure = true)
        public ParsableKofiArray(final char[] chars, final int start, final int end, final int len,
                @NotNull final ArrayList<Parsable<?>> values) {
            super(chars, start, end, len);
            this.values = values;
        }

        /**
         * {@inheritDoc}
         */
        @Contract(value = "-> new", pure = true)
        @NotNull
        @Override
        public KofiArray getValue() {
            final Object[] array = new Object[values.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = values.get(i).getValue();
            return new KofiArray(array);
        }

        /**
         * Returns {@code Type.ARRAY}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.ARRAY;
        }
    }

    /**
     * A parsable that represents a {@link KofiObject} value.
     */
    protected static class ParsableKofiObject extends Parsable<KofiObject> {

        /**
         * List of name-value pairs (entries) in this object.
         */
        @NotNull
        private final ArrayList<Parsable<?>> entries;

        /**
         * Constructs a new {@code ParsableKofiObject}.
         */
        @Contract(pure = true)
        public ParsableKofiObject(final char[] chars, final int start, final int end, final int len,
                @NotNull final ArrayList<Parsable<?>> entries) {
            super(chars, start, end, len);
            this.entries = entries;
        }

        /**
         * {@inheritDoc}
         */
        @Contract(value = "-> new", pure = true)
        @NotNull
        @Override
        public KofiObject getValue() {
            final KofiObject.Builder builder = new KofiObject.Builder();
            for (int i = 0; i < entries.size(); i += 2)
                builder.put((String) entries.get(i).getValue(),
                        entries.get(i + 1).getValue());
            return builder.build();
        }

        /**
         * Returns {@code Type.OBJECT}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.OBJECT;
        }
    }

    /**
     * A parsable that represents a {@code Long} value.
     */
    protected static class ParsableLong extends Parsable<Long> {

        /**
         * Constructs a new {@code ParsableLong}.
         */
        @Contract(pure = true)
        public ParsableLong(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Long getValue() {
            // do not include specifier if present, e.g. 22L -> 22
            final int count = chars[end - 1] > '9' ? end - start - 1 : end - start;
            return Long.parseLong(new String(chars, start, count));
        }

        /**
         * Returns {@code Type.LONG}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.LONG;
        }
    }

    /**
     * A parsable that represents a {@code null} value.
     */
    protected static class ParsableNull extends Parsable<Object> {

        /**
         * Constructs a new {@code ParsableNull}.
         */
        @Contract(pure = true)
        public ParsableNull(final char[] chars, final int start, final int len) {
            super(chars, start, start + NULL.length, len);
        }

        /**
         * Returns {@code null}.
         */
        @Contract(value = "-> null", pure = true)
        @Override
        @Nullable
        public Object getValue() {
            return null;
        }

        /**
         * Returns {@code Type.NULL}.
         */
        @Contract(pure = true)
        @Override
        @NotNull
        public Type getValueType() {
            return Type.NULL;
        }
    }

    /**
     * A parsable that represents a {@code String} value. The string is
     * unescaped when parsed.
     */
    protected static class ParsableString extends Parsable<String> {

        /**
         * Constructs a new {@code ParsableString}.
         */
        @Contract(pure = true)
        public ParsableString(final char[] chars, final int start, final int end, final int len) {
            super(chars, start, end, len);
        }

        /**
         * {@inheritDoc}
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public String getValue() {
            // do NOT include quotation marks here
            // that is done by StringProperty.getValueString() and
            // KofiObject.Entry(String, Object) constructor
            return new String(unescape(chars, start + 1, end - 1));
        }

        /**
         * Returns {@code Type.STRING}.
         */
        @Contract(pure = true)
        @NotNull
        @Override
        public Type getValueType() {
            return Type.STRING;
        }
    }

    /**
     * A task that parses a line of characters to an {@link Element}.
     *
     * @see #parseLine(char[], int)
     */
    protected class ParseTask implements Callable<Element> {

        /**
         * The characters to parse.
         */
        public final char[] chars;
        /**
         * The line number of the characters.
         */
        public final int line;

        /**
         * Constructs a new {@code ParseTask} with the specified characters and
         * line numer.
         *
         * @param chars the characters to parse
         * @param line  the line number of the characters
         */
        @Contract(value = "null, _ -> fail", pure = true)
        public ParseTask(final char[] chars, final int line) {
            assert chars != null : "chars is null";
            this.chars = chars;
            this.line = line;
        }

        /**
         * Calls {@link #parseLine(char[], int)} and returns the parsed
         * element.
         *
         * @return a new element
         * @throws ParseException if an error occurs while parsing
         */
        @Contract(value = "-> new", pure = true)
        @NotNull
        @Override
        public Element call() throws ParseException {
            return parseLine(chars, line);
        }
    }
}
