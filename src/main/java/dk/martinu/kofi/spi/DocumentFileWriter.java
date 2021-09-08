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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import dk.martinu.kofi.Document;

public interface DocumentFileWriter {

    boolean canWrite(@NotNull final Path filePath, @Nullable final Document document) throws NullPointerException;

    void writeFile(@NotNull final Path filePath, @NotNull final Document document) throws NullPointerException,
            IOException;

    void writeFile(@NotNull final Path filePath, @NotNull final Document document, @Nullable final Charset cs) throws
            NullPointerException, IOException;
}
