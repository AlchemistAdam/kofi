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

package dk.martinu.kofi;

import org.jetbrains.annotations.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import dk.martinu.kofi.spi.DocumentFileReader;
import dk.martinu.kofi.spi.DocumentFileWriter;

// TODO javadoc
public class DocumentIO {

    // TODO javadoc
    private static final ReentrantLock readLock = new ReentrantLock();
    // TODO javadoc
    private static final ReentrantLock writeLock = new ReentrantLock();
    // TODO javadoc
    private static ServiceLoader<DocumentFileReader> readers;
    // TODO javadoc
    private static ServiceLoader<DocumentFileWriter> writers;

    // TODO javadoc
    @Nullable
    public static DocumentFileReader getReader(@NotNull final Path filePath) throws NullPointerException {
        Objects.requireNonNull(filePath, "filePath is null");
        // init service providers
        if (readers == null) {
            readLock.lock();
            try {
                if (readers == null)
                    readers = ServiceLoader.load(DocumentFileReader.class);
            }
            finally {
                readLock.unlock();
            }
        }
        // get reader for file extension
        for (DocumentFileReader reader : readers)
            if (reader.canRead(filePath))
                return reader;
        // no reader was found
        return null;
    }

    // TODO javadoc
    @Nullable
    public static DocumentFileWriter getWriter(@NotNull final Path filePath, @NotNull final Document document) throws
            NullPointerException {
        Objects.requireNonNull(document, "document is null");
        // init service providers
        if (writers == null) {
            writeLock.lock();
            try {
                if (writers == null)
                    writers = ServiceLoader.load(DocumentFileWriter.class);
            }
            finally {
                writeLock.unlock();
            }
        }
        // get writer for document
        for (DocumentFileWriter writer : writers)
            if (writer.canWrite(filePath, document))
                return writer;
        // no writer was found
        return null;
    }

    // TODO javadoc
    @Contract("_ -> new")
    @NotNull
    public static Document readFile(@NotNull final Path filePath) throws NullPointerException,
            ServiceConfigurationError,
            IOException, ServiceUnavailableException {
        final DocumentFileReader reader = getReader(filePath);
        if (reader != null)
            return reader.readFile(filePath);
        else
            throw new ServiceUnavailableException("no available readers for " + filePath + " (file might not exist)");
    }

    // TODO javadoc
    public static void writeFile(@NotNull final Path filePath, @NotNull final Document document) throws
            NullPointerException, ServiceConfigurationError, IOException, ServiceUnavailableException {
        Objects.requireNonNull(filePath, "filePath is null");
        final DocumentFileWriter writer = getWriter(filePath, document);
        if (writer != null)
            writer.writeFile(filePath, document);
        else
            throw new ServiceUnavailableException("no available writers for " + filePath);
    }
}
