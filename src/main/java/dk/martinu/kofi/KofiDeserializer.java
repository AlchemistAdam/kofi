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

package dk.martinu.kofi;

import org.jetbrains.annotations.NotNull;

import dk.martinu.kofi.annotations.KofiDeserialize;

/**
 * Interface that allows for custom deserializing when
 * {@link KofiObject#construct(Class) constructing} objects. Use in conjunction
 * with the {@link KofiDeserialize} annotation.
 * <p>
 * A class that implements this interface must provide a public, static method
 * called {@code instance}, that takes exactly 0 parameters and returns an
 * instance of the class:
 * <pre>
 *     public static <i>ClassType</i> instance()
 * </pre>
 *
 * @author Adam Martinu
 * @since 1.0
 */
public interface KofiDeserializer {

    /**
     * Deserializes an instance from the specified {@code KofiObject} and
     * returns it.
     */
    @NotNull
    Object deserialize(@NotNull KofiObject obj);
}
