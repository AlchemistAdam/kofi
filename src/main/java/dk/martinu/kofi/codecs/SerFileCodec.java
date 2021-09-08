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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import dk.martinu.kofi.Document;
import dk.martinu.kofi.spi.DocumentFileReader;
import dk.martinu.kofi.spi.DocumentFileWriter;

public class SerFileCodec implements DocumentFileReader, DocumentFileWriter {

    @Contract(value = "-> new", pure = true)
    @NotNull
    public static SerFileCodec provider() {
        return new SerFileCodec();
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
                        return document.parallelStream().allMatch(element -> element instanceof Serializable);
            }
        }
        return false;
    }

    @Contract(value = "_ -> new", pure = true)
    @NotNull
    @Override
    public Document readFile(@NotNull final Path filePath) throws NullPointerException, IOException {
        return readFile(filePath, null);
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    @Override
    public Document readFile(@NotNull final Path filePath, @Nullable final Charset cs) throws NullPointerException,
            IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Document) in.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new IOException("could not find class of a serialized object", e);
        }
        catch (ObjectStreamException e) {
            throw new IOException("an exception was thrown while deserializing an object", e);
        }
    }

    @Contract(pure = true)
    @Override
    public void writeFile(@NotNull final Path filePath, @NotNull final Document document) throws NullPointerException,
            IOException {
        writeFile(filePath, document, null);
    }

    @Contract(pure = true)
    @Override
    public void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs)
            throws NullPointerException, IOException {
        Objects.requireNonNull(filePath, "filePath is null");
        try (ObjectOutputStream in = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            in.writeObject(document);
        }
        catch (ObjectStreamException e) {
            throw new IOException("something is wrong with a serialized object", e);
        }
    }

    public enum Extension {

        SER(".ser");

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
}
