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

import dk.martinu.kofi.spi.DocumentStringReader;
import dk.martinu.kofi.spi.DocumentStringWriter;
import org.jetbrains.annotations.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import dk.martinu.kofi.spi.DocumentFileReader;
import dk.martinu.kofi.spi.DocumentFileWriter;

// TODO format
/**
 * Contains static utility methods for retrieving service providers that can
 * read and write {@link Document}s. All service provider interfaces loaded
 * by this class are located in the {@link dk.martinu.kofi.spi} package.
 * <p>
 * <b>NOTE:</b> this class acts as a bridge between applications and service
 * providers. In scenarios where only one specific, known service provider is
 * used, do not use this class. Instead, retrieve an instance of the service
 * provider from its own class directly, for example with its
 * {@code provider()} method.
 */
public class DocumentIO {

    /**
     * Lock used when loading {@link DocumentFileReader} providers.
     */
    private static final ReentrantLock fileReadLock = new ReentrantLock();
    /**
     * Lock used when loading {@link DocumentFileWriter} providers.
     */
    private static final ReentrantLock fileWriteLock = new ReentrantLock();
    /**
     * Lock used when loading {@link DocumentStringReader} providers.
     */
    private static final ReentrantLock stringReadLock = new ReentrantLock();
    /**
     * Lock used when loading {@link DocumentStringWriter} providers.
     */
    private static final ReentrantLock stringWriteLock = new ReentrantLock();
    /**
     * {@link ServiceLoader} for {@link DocumentFileReader} providers.
     */
    private static ServiceLoader<DocumentFileReader> fileReaders;
    /**
     * {@link ServiceLoader} for {@link DocumentFileWriter} providers.
     */
    private static ServiceLoader<DocumentFileWriter> fileWriters;
    /**
     * {@link ServiceLoader} for {@link DocumentStringReader} providers.
     */
    private static ServiceLoader<DocumentStringReader> stringReaders;
    /**
     * {@link ServiceLoader} for {@link DocumentStringWriter} providers.
     */
    private static ServiceLoader<DocumentStringWriter> stringWriters;

    /**
     * Returns the first {@link DocumentFileReader} for which
     * {@link DocumentFileReader#canRead(Path) canRead(filePath)} returns
     * {@code true}. If no such reader was found, then {@code null} is
     * returned. The order in which the readers are tested is unspecified.
     *
     * @param filePath the path of the document to read
     * @return a reader which can read from {@code filePath}, otherwise {@code null}
     * @throws NullPointerException      if {@code filePath} is {@code null}
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     */
    @Contract(pure = true)
    @Nullable
    public static DocumentFileReader getFileReader(@NotNull final Path filePath) throws ServiceConfigurationError {
        Objects.requireNonNull(filePath, "filePath is null");
        fileReadLock.lock();
        try {
            if (fileReaders == null)
                initFileReaders();
            // get reader for file extension
            for (DocumentFileReader reader : fileReaders)
                if (reader.canRead(filePath))
                    return reader;
        }
        finally {
            fileReadLock.unlock();
        }
        // no reader was found
        return null;
    }

    /**
     * Loads all available service providers for {@link DocumentFileReader}
     * with {@link ServiceLoader#load(Class)} if it has not already been done,
     * otherwise calls {@link ServiceLoader#reload()}.
     */
    private static void initFileReaders() throws ServiceConfigurationError {
        fileReadLock.lock();
        try {
            if (fileReaders == null)
                fileReaders = ServiceLoader.load(DocumentFileReader.class);
            else
                fileReaders.reload();
        }
        finally {
            fileReadLock.unlock();
        }
    }

    /**
     * Loads all available service providers for {@link DocumentStringReader}
     * with {@link ServiceLoader#load(Class)} if it has not already been done,
     * otherwise calls {@link ServiceLoader#reload()}.
     */
    private static void initStringReaders() throws ServiceConfigurationError {
        stringReadLock.lock();
        try {
            if (stringReaders == null)
                stringReaders = ServiceLoader.load(DocumentStringReader.class);
            else
                stringReaders.reload();
        }
        finally {
            stringReadLock.unlock();
        }
    }

    /**
     * Creates and returns a new iterator containing all available
     * {@link DocumentStringReader}s. The returned iterator is mutable and safe
     * for modification.
     *
     * @return an iterator of available readers
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     */
    @SuppressWarnings("unused")
    @Contract(value = "-> new", pure = true)
    @NotNull
    public static Iterator<DocumentStringReader> getStringReaders() throws ServiceConfigurationError {
        stringReadLock.lock();
        try {
            if (stringReaders == null)
                initStringReaders();
            final List<ServiceLoader.Provider<DocumentStringReader>> providers = stringReaders.stream().toList();
            final ArrayList<DocumentStringReader> readers = new ArrayList<>(providers.size());
            providers.forEach(provider -> readers.add(provider.get()));
            return readers.iterator();
        }
        finally {
            stringReadLock.unlock();
        }
    }

    /**
     * Creates and returns a new iterator containing all available
     * {@link DocumentStringWriter}s. The returned iterator is mutable and safe
     * for modification.
     *
     * @return an iterator of available writers
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     */
    @SuppressWarnings("unused")
    @Contract(value = "-> new", pure = true)
    @NotNull
    public static Iterator<DocumentStringWriter> getStringWriters() throws ServiceConfigurationError {
        stringWriteLock.lock();
        try {
            if (stringWriters == null)
                initStringWriters();
            final List<ServiceLoader.Provider<DocumentStringWriter>> providers = stringWriters.stream().toList();
            final ArrayList<DocumentStringWriter> writers = new ArrayList<>(providers.size());
            providers.forEach(provider -> writers.add(provider.get()));
            return writers.iterator();
        }
        finally {
            stringWriteLock.unlock();
        }
    }

    /**
     * Loads all available service providers for {@link DocumentStringWriter}
     * with {@link ServiceLoader#load(Class)} if it has not already been done,
     * otherwise calls {@link ServiceLoader#reload()}.
     */
    private static void initStringWriters() throws ServiceConfigurationError {
        stringWriteLock.lock();
        try {
            if (stringWriters == null)
                stringWriters = ServiceLoader.load(DocumentStringWriter.class);
            else
                stringWriters.reload();
        }
        finally {
            stringWriteLock.unlock();
        }
    }

    /**
     * Creates and returns a new iterator containing all available
     * {@link DocumentFileReader}s. The returned iterator is mutable and safe
     * for modification.
     *
     * @return an iterator of available readers
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     */
    @SuppressWarnings("unused")
    @Contract(value = "-> new", pure = true)
    @NotNull
    public static Iterator<DocumentFileReader> getFileReaders() throws ServiceConfigurationError {
        fileReadLock.lock();
        try {
            if (fileReaders == null)
                initFileReaders();
            final List<ServiceLoader.Provider<DocumentFileReader>> providers = fileReaders.stream().toList();
            final ArrayList<DocumentFileReader> readers = new ArrayList<>(providers.size());
            providers.forEach(provider -> readers.add(provider.get()));
            return readers.iterator();
        }
        finally {
            fileReadLock.unlock();
        }
    }

    /**
     * Returns the first {@link DocumentFileWriter} for which
     * {@link DocumentFileWriter#canWrite(Path, Document) canWrite(filePath, document)}
     * returns {@code true}. If no such writer was found, then {@code null} is
     * returned. The order in which the writers are tested is unspecified.
     *
     * @param filePath the path to write the document to
     * @param document the {@link Document} to write
     * @return a writer which can write the specified document to
     * {@code filePath}, otherwise {@code null}
     * @throws NullPointerException      if {@code filePath} or {@code document} is
     *                                   {@code null}
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     */
    @Contract(pure = true)
    @Nullable
    public static DocumentFileWriter getFileWriter(@NotNull final Path filePath, @NotNull final Document document)
            throws ServiceConfigurationError {
        Objects.requireNonNull(filePath, "filePath is null");
        Objects.requireNonNull(document, "document is null");
            fileWriteLock.lock();
            try {
                if (fileWriters == null)
                    initFileWriters();
                // get writer for document
                for (DocumentFileWriter writer : fileWriters)
                    if (writer.canWrite(filePath, document))
                        return writer;
            }
            finally {
                fileWriteLock.unlock();
            }
        // no writer was found
        return null;
    }

    /**
     * Loads all available service providers for {@link DocumentFileWriter}
     * with {@link ServiceLoader#load(Class)} if it has not already been done,
     * otherwise calls {@link ServiceLoader#reload()}.
     */
    private static void initFileWriters() {
        fileWriteLock.lock();
        try {
            if (fileWriters == null)
                fileWriters = ServiceLoader.load(DocumentFileWriter.class);
            else
                fileWriters.reload();
        } finally {
            fileWriteLock.unlock();
        }
    }

    /**
     * Creates and returns a new iterator containing all available
     * {@link DocumentFileWriter}s. The returned iterator is mutable and safe
     * for modification.
     *
     * @return an iterator of available writers
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    @NotNull
    public static Iterator<DocumentFileWriter> getFileWriters() {
        fileWriteLock.lock();
        try {
            if (fileWriters == null)
                initFileWriters();
            final List<ServiceLoader.Provider<DocumentFileWriter>> providers = fileWriters.stream().toList();
            final ArrayList<DocumentFileWriter> writers = new ArrayList<>(providers.size());
            providers.forEach(provider -> writers.add(provider.get()));
            return writers.iterator();
        }
        finally {
            fileWriteLock.unlock();
        }
    }

    /**
     * Retrieves the first {@link DocumentFileReader} which can read the
     * specified path, reads a {@link Document} from the path and returns it.
     *
     * @param filePath the path of the document to read
     * @return a new document read from {@code filePath}
     * @throws NullPointerException if {@code filePath} is {@code null}
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     * @throws IOException if an error occurs while reading from the file
     * @throws ServiceUnavailableException if no available file readers can
     * read from {@code filePath}
     * @see #getFileReader(Path)
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static Document readFile(@NotNull final Path filePath) throws ServiceConfigurationError, IOException,
            ServiceUnavailableException {
        final DocumentFileReader reader = getFileReader(filePath);
        if (reader != null)
            return reader.readFile(filePath);
        else
            throw new ServiceUnavailableException("no available file readers for " + filePath);
    }

    /**
     * Retrieves the first {@link DocumentStringReader} available, reads a
     * {@link Document} from the specified string and returns it.
     *
     * @param string the string to read
     * @return a new document read from {@code string}
     * @throws NullPointerException if {@code string} is {@code null}
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     * @throws IOException if an error occurs while reading the string
     * @throws ServiceUnavailableException if there are no available string
     * readers
     */
    @SuppressWarnings("unused")
    @Contract(value = "_ -> new", pure = true)
    @NotNull
    public static Document readString(@NotNull final String string) throws IOException, ServiceConfigurationError,
            ServiceUnavailableException {
        Objects.requireNonNull(string, "string is null");
        final DocumentStringReader reader;
        stringReadLock.lock();
        try {
            if (stringReaders == null)
                initStringReaders();
            reader = stringReaders
                    .findFirst()
                    .orElseThrow(() -> new ServiceUnavailableException("no available string readers"));
        }
        finally {
            stringReadLock.unlock();
        }
        return reader.readString(string);
    }

    /**
     * Retrieves the first {@link DocumentStringWriter} available, writes
     * the specified document to a string and returns it.
     *
     * @param document the document to write
     * @return a string representation of {@code document}
     * @throws NullPointerException if {@code document} is {@code null}
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     * @throws IOException if an error occurs while writing the document
     * @throws ServiceUnavailableException if there are no available string
     * writers
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    @NotNull
    public static String writeString(@NotNull final Document document) throws ServiceConfigurationError, IOException,
            ServiceUnavailableException {
        Objects.requireNonNull(document, "document is null");
        final DocumentStringWriter writer;
        stringWriteLock.lock();
        try {
            if (stringWriters == null)
                initStringWriters();
            writer = stringWriters
                    .findFirst()
                    .orElseThrow(() -> new ServiceUnavailableException("no available string writers"));
        }
        finally {
            stringWriteLock.unlock();
        }
        return writer.writeString(document);
    }

    /**
     * Retrieves the first {@link DocumentFileWriter} which can write
     * {@code document} to the specified path and writes it to the path.
     *
     * @param filePath the path to write the document to
     * @param document the document to write
     * @throws NullPointerException if {@code filePath} or {@code document} is
     * {@code null}
     * @throws ServiceConfigurationError see {@link ServiceLoader} for details
     * @throws IOException if an error occurs while reading to the file
     * @throws ServiceUnavailableException if no available writers can write
     * {@code document} to {@code filePath}
     */
    @SuppressWarnings("unused")
    @Contract(pure = true)
    public static void writeFile(@NotNull final Path filePath, @NotNull final Document document) throws
            NullPointerException, ServiceConfigurationError, IOException, ServiceUnavailableException {
        final DocumentFileWriter writer = getFileWriter(filePath, document);
        if (writer != null)
            writer.writeFile(filePath, document);
        else
            throw new ServiceUnavailableException("no available file writers for " + filePath);
    }
}
