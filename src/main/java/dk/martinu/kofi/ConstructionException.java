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

package dk.martinu.kofi;

import org.jetbrains.annotations.Contract;

import java.io.Serial;
import java.io.Serializable;

/**
 * Thrown when an exception is caught while constructing an array or object.
 *
 * @author Adam Martinu
 * @see KofiArray#construct(Class)
 * @see KofiObject#construct(Class)
 * @since 1.0
 */
public class ConstructionException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * Creates a new exception with the specified message and cause.
     */
    @Contract(pure = true)
    public ConstructionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
