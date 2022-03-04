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

import org.jetbrains.annotations.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

import dk.martinu.kofi.Document;
import dk.martinu.kofi.DocumentIO;

/**
 * Service provider interface for writing a {@link Document} to a file.
 * Implementations of this interface (service providers) can be retrieved with
 * the {@link DocumentIO} class.
 * <p>
 * For implementations of this interface provided by the KoFi API, see
 * {@link dk.martinu.kofi.codecs}.
 *
 * @author Adam Martinu
 * @see DocumentIO#getFileWriters()
 * @since 1.0
 */
public interface DocumentFileWriter {

    /**
     * Returns {@code true} if this writer can write {@link Document} to the
     * specified path, otherwise {@code false}.
     * <p>
     * The default implementation returns {@code true} if {@code filePath} does
     * not exist or is a regular file, and its file extenstion is equal to one
     * of the extentions returned by {@link #getExtensions()}, ignoring case.
     * <p>
     * <b>NOTE:</b> a return value of {@code true} is not a guarantee that a
     * document can be written <i>successfully</i>; writing a document in an
     * erroneous state or failing to encode its elements can throw an
     * exception.
     *
     * @param filePath the path to write to
     * @param document the document to write
     * @return {@code true} if this writer can write {@code document} to
     * {@code filePath}, otherwise {@code false}
     * @throws NullPointerException if {@code filePath} or {@code document} is
     *                              {@code null}
     * @see Files#exists(Path, LinkOption...)
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    @Contract(pure = true)
    default boolean canWrite(@NotNull final Path filePath, @NotNull final Document document) {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
        if (!Files.exists(filePath) || Files.isRegularFile(filePath)) {
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
     * Returns a list of file extensions supported by this writer.
     */
    @Contract(pure = true)
    @NotNull
    List<String> getExtensions();

    /**
     * Writes {@code document} to the specified path. The default
     * implementation delegates to {@link #writeFile(Path, Document, Charset)}
     * with a {@code null} {@link Charset}.
     *
     * @param filePath the path to write to
     * @param document the document to write
     * @throws NullPointerException if {@code filePath} or {@code document} is
     *                              {@code null}
     * @throws IOException          if an error occurs while writing to the
     *                              file
     */
    @Contract(pure = true)
    default void writeFile(@NotNull final Path filePath, @NotNull final Document document) throws IOException {
        writeFile(filePath, document, null);
    }

    /**
     * Writes {@code document} to the specified path using the specified
     * {@link Charset}. If {@code cs} is {@code null} then a default
     * {@code Charset} is used.
     *
     * @param filePath the path to write to
     * @param document the document to write
     * @param cs       the {@code Charset} to use, or {@code null}
     * @throws NullPointerException if {@code filePath} or {@code document} is
     *                              {@code null}
     * @throws IOException          if an error occurs while writing to the
     *                              file
     */
    @Contract(pure = true)
    void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs) throws
            IOException;
}
