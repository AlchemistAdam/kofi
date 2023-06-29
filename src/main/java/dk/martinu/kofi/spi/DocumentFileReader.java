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

package dk.martinu.kofi.spi;

import dk.martinu.kofi.Document;
import dk.martinu.kofi.DocumentIO;

import org.jetbrains.annotations.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

/**
 * Service provider interface for reading a {@link Document} from file.
 * Implementations of this interface (service providers) can be retrieved with
 * the {@link DocumentIO} class.
 * <p>
 * For implementations of this interface provided by the KoFi API, see
 * {@link dk.martinu.kofi.codecs}.
 *
 * @author Adam Martinu
 * @see DocumentIO#getFileReaders()
 * @since 1.0
 */
public interface DocumentFileReader {

    /**
     * Returns {@code true} if this reader can read a {@link Document} from the
     * specified path, otherwise {@code false}.
     * <p>
     * The default implementation returns {@code true} if {@code filePath} is a
     * regular file and its file extenstion is equal to one of the extentions
     * returned by {@link #getExtensions()}, ignoring case.
     *
     * @param filePath the path to read from
     * @return {@code true} if this reader can read from {@code filePath},
     * otherwise {@code false}
     * @throws NullPointerException if {@code filePath} is {@code null}
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    @Contract(pure = true)
    default boolean canRead(@NotNull final Path filePath) {
        Objects.requireNonNull(filePath, "filePath is null");
        if (Files.isRegularFile(filePath)) {
            final String fileName = filePath.getFileName().toString();
            final int index = fileName.lastIndexOf('.');
            final String extension = index != -1 ? fileName.substring(index + 1) : fileName;
            for (String ext : getExtensions())
                if (extension.equalsIgnoreCase(ext))
                    return true;
        }
        return false;
    }

    /**
     * Returns a list of file extensions supported by this reader.
     */
    @NotNull
    List<String> getExtensions();

    /**
     * Reads a new {@link Document} from the specified path and returns it. The
     * default implementation delegates to {@link #readFile(Path, Charset)}
     * with a {@code null} {@link Charset}.
     *
     * @param filePath the path to read from
     * @return a new document read from {@code filePath}
     * @throws NullPointerException if {@code filePath} is {@code null}
     * @throws IOException          if an error occurs is while reading from
     *                              the file
     */
    @Contract(value = "_ -> new")
    @NotNull
    default Document readFile(@NotNull final Path filePath) throws IOException {
        return readFile(filePath, null);
    }

    /**
     * Reads a new {@link Document} from the specified path using the specified
     * {@link Charset}, and returns it. If {@code cs} is {@code null} then a
     * default {@code Charset} is used. If {@code filePath} is not a regular
     * file, then an empty document is returned.
     *
     * @param filePath the path to read from
     * @param cs       the {@code Charset} to use, or {@code null}
     * @return a new document read from {@code filePath}
     * @throws NullPointerException if {@code filePath} is {@code null}
     * @throws IOException          if an error occurs is while reading from
     *                              the file
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    @Contract(value = "_, _ -> new")
    @NotNull
    Document readFile(@NotNull final Path filePath, @Nullable final Charset cs) throws IOException;
}
