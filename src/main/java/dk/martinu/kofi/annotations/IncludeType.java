/*
 * Copyright (c) 2023, Adam Martinu. All rights reserved. Altering or
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

package dk.martinu.kofi.annotations;

/**
 * Enum flags for specifying when to include class types when serializing
 * objects.
 * TODO give examples
 *
 * @author Adam Martinu
 * @see KofiOptions
 * @since 1.0
 */
public enum IncludeType {
    /**
     * Indicates the type of an object should be included when the type extends
     * or implements the destination type.
     */
    SUBTYPE,
    /**
     * Indicates the type of an object should always be included regardless of
     * the destination type.
     */
    ALWAYS,
    /**
     * Indicates the type of an object should never be included regardless of
     * the destination type.
     */
    NEVER
}
