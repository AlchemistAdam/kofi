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

import java.lang.annotation.*;

import dk.martinu.kofi.KofiDeserializer;
import dk.martinu.kofi.KofiObject;

/**
 * Marks the annotated type for custom deserializing. When
 * {@link KofiObject#construct(Class) constructing} instances of the annotated
 * type, a custom implementation will be used to handle the deserialization
 * process.
 *
 * @author Adam Martinu
 * @see dk.martinu.kofi.KofiDeserializer
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface KofiDeserialize {
    /**
     * The {@code Class} object of the deserializer class.
     */
    Class<? extends KofiDeserializer> with();
}
