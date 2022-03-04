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
 * The {@code Document}, {@code Element}, {@code Whitespace}, {@code Comment},
 * {@code Section} and {@code Property} classes are primary classes for storing
 * documents and their contents in memory.
 * <p>
 * {@code KofiArray} and {@code KofiObject} store and implement the behaviour
 * for the complex array and object values. Both of these classes extend
 * {@code KofiValue}, which can be extended to provide custom complex and
 * non-complex value implementations.
 * <p>
 * The {@code DocumentIO} class gives access to installed service providers for
 * reading and writing documents.
 * <p>
 * {@code KofiLog} is a lightweight logging utility based on the Java Logging
 * API which is used throughout the API.
 *
 *
 * @author Adam Martinu
 * @since 1.0
 */
package dk.martinu.kofi;