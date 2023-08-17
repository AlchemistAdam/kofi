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

import dk.martinu.kofi.KofiObject;
import dk.martinu.kofi.KofiSerializer;

/**
 * Marks the annotated type for custom serializing. When
 * {@link KofiObject#reflect(Object)} reflecting} instances of the annotated
 * type, a custom implementation will be used to handle the serialization
 * process.
 *
 * @author Adam Martinu
 * @see dk.martinu.kofi.KofiSerializer
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
public @interface KofiSerialize {
    /**
     * The {@code Class} object of the serializer class.
     */
    Class<? extends KofiSerializer> with();
}
