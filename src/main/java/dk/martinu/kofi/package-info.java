/*
 * Copyright (c) 2022, Adam Martinu. All rights reserved. Altering or
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

/**
 * Provides the fundamental classes of the KoFi API.
 * <p>
 * The {@link dk.martinu.kofi.Document}, {@link dk.martinu.kofi.Element},
 * {@link dk.martinu.kofi.Whitespace}, {@link dk.martinu.kofi.Comment},
 * {@link dk.martinu.kofi.Section} and {@link dk.martinu.kofi.Property} classes
 * are primary classes for storing documents and their contents.
 * <p>
 * {@link dk.martinu.kofi.KofiArray} and {@link dk.martinu.kofi.KofiObject}
 * store and implement the behaviour for the complex array and object values.
 * Both of these classes extend {@link dk.martinu.kofi.KofiValue}, which can be
 * extended to provide custom complex or non-complex value implementations.
 * <p>
 * The {@link dk.martinu.kofi.DocumentIO} class gives access to installed
 * service providers for reading and writing documents.
 * <p>
 * {@link dk.martinu.kofi.KofiLog} is a lightweight logging utility, based on
 * the Java Logging API, which is used internally.
 *
 * @author Adam Martinu
 * @since 1.0
 */
package dk.martinu.kofi;