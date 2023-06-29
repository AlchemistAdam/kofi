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
import java.nio.file.*;
import java.util.List;
import java.util.Objects;

import dk.martinu.kofi.Document;
import dk.martinu.kofi.spi.DocumentFileReader;
import dk.martinu.kofi.spi.DocumentFileWriter;

/**
 * Codec for reading and writing serial files using the Java Serialization API.
 *
 * @author Adam Martinu
 * @see Serializable
 * @since 1.0
 */
public class SerFileCodec implements DocumentFileReader, DocumentFileWriter {

    /**
     * List returned by {@link #getExtensions()}.
     */
    private static final List<String> EXTENSIONS = List.of("ser");

    /**
     * Returns a new instance of this service provider.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public static SerFileCodec provider() {
        return new SerFileCodec();
    }

    /**
     * See {@link DocumentFileWriter#canWrite(Path, Document)} for details. This
     * method is overridden to further require that all elements in the
     * specified document implement {@code Serializable}.
     *
     * @param filePath the path to write to
     * @param document the document to write
     * @return {@code true} if this writer can write {@code document} to
     * {@code filePath}, otherwise {@code false}
     * @throws NullPointerException if {@code filePath} or {@code document} is
     *                              {@code null}
     */
    @Contract(pure = true)
    @Override
    public boolean canWrite(@NotNull final Path filePath, @NotNull final Document document) {
        if (DocumentFileWriter.super.canWrite(filePath, document))
            return document.parallelStream().allMatch(element -> element instanceof Serializable);
        else
            return false;
    }

    /**
     * Returns a list of file extensions supported by this codec. The returned
     * list is immutable and only contains the {@code "ser"} file extension.
     */
    @Contract(pure = true)
    @NotNull
    @Override
    public List<String> getExtensions() {
        return EXTENSIONS;
    }

    /**
     * Reads a new {@link Document} from the specified path and returns it. The
     * {@code cs} parameter is unused for object deserialization. If
     * {@code filePath} is not a regular file, then an empty document is
     * returned.
     *
     * @param filePath the path to read from
     * @param cs       unused
     * @return a new document read from {@code filePath}
     * @throws NullPointerException if {@code filePath} is {@code null}
     * @throws IOException          if an error occurs is while reading from
     *                              the file
     * @see Files#isRegularFile(Path, LinkOption...)
     */
    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    @Override
    public Document readFile(@NotNull final Path filePath, @Nullable final Charset cs) throws IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        if (Files.isRegularFile(filePath))
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
                return (Document) in.readObject();
            }
            catch (ClassNotFoundException e) {
                throw new IOException("could not find class of a serialized object", e);
            }
            catch (ObjectStreamException e) {
                throw new IOException("an exception was thrown while deserializing an object", e);
            }
        else
            return new Document();
    }

    /**
     * Writes {@code document} to the specified path. The {@code cs} parameter
     * is unused for object serialization.
     *
     * @param filePath the path to write to
     * @param document the document to write
     * @param cs       unused
     * @throws NullPointerException if {@code filePath} or {@code document} is
     *                              {@code null}
     * @throws IOException          if an error occurs while writing to the
     *                              file
     */
    @Contract(pure = true)
    @Override
    public void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs)
            throws IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
        try (ObjectOutputStream in = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            in.writeObject(document);
        }
        catch (ObjectStreamException e) {
            throw new IOException("something is wrong with a serialized object", e);
        }
    }
}
