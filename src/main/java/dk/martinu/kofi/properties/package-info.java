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

/**
 * Contains all {@link dk.martinu.kofi.Property Property} implementations
 * provided by the KoFi API.
 * <p>
 * The following value types are supported:
 * <ul>
 *     <li>
 *         {@code null} as
 *         {@link dk.martinu.kofi.properties.NullProperty NullProperty} (not
 *         specifically a type, but {@code null} values are supported)
 *     </li>
 *     <li>
 *         {@code String} as
 *         {@link dk.martinu.kofi.properties.StringProperty StringProperty}
 *     </li>
 *     <li>
 *         {@code Double} as
 *         {@link dk.martinu.kofi.properties.DoubleProperty DoubleProperty}
 *     </li>
 *     <li>
 *         {@code Float} as
 *         {@link dk.martinu.kofi.properties.FloatProperty FloatProperty}
 *     </li>
 *     <li>
 *         {@code Long} as
 *         {@link dk.martinu.kofi.properties.LongProperty LongProperty}
 *     </li>
 *     <li>
 *         {@code Integer} as
 *         {@link dk.martinu.kofi.properties.IntProperty IntProperty}
 *     </li>
 *     <li>
 *         {@code Boolean} as
 *         {@link dk.martinu.kofi.properties.BooleanProperty BooleanProperty}
 *     </li>
 *     <li>
 *         {@code Character} as
 *         {@link dk.martinu.kofi.properties.CharProperty CharProperty}
 *     </li>
 *     <li>
 *         {@link dk.martinu.kofi.KofiArray KofiArray} as
 *         {@link dk.martinu.kofi.properties.ArrayProperty ArrayProperty}
 *     </li>
 *     <li>
 *         {@link dk.martinu.kofi.KofiObject KofiObject} as
 *         {@link dk.martinu.kofi.properties.ObjectProperty ObjectProperty}
 *     </li>
 * </ul>
 *
 * @author Adam Martinu
 * @since 1.0
 */
package dk.martinu.kofi.properties;