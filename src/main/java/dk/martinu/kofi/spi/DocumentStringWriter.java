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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import dk.martinu.kofi.Document;
import dk.martinu.kofi.DocumentIO;

/**
 * Service provider interface for reading a {@link Document} from a string.
 * Implementations of this interface (service providers) can be retrieved with
 * the {@link DocumentIO} class.
 *
 * @author Adam Martinu
 * @see DocumentIO#getStringReaders()
 * @since 1.0
 */
@FunctionalInterface
public interface DocumentStringWriter {

    /**
     * Writes {@code document} to a string and returns it.
     *
     * @param document the document to write
     * @return a string representation of {@code document}
     * @throws NullPointerException if {@code document} is {@code null}
     * @throws IOException          if an error occurs is while writing the
     *                              document
     */
    @Contract(pure = true)
    @NotNull
    String writeString(@NotNull final Document document) throws IOException;
}
