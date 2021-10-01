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

package dk.martinu.kofi;

import org.jetbrains.annotations.*;

public class Json {

    @Contract(value = "null -> null", pure = true)
    @Nullable
    protected static Object getKnownType(@Nullable final Object object) {
        if (object == null || isKnownType(object))
            return object;
        else if (object instanceof Object[])
            return new JsonArray((Object[]) object);
        else if (object.getClass().isArray())
            return JsonArray.reflect(object);
        else
            return JsonObject.reflect(object);
    }

    @Contract(pure = true)
    protected static boolean isKnownType(@NotNull final Object obj) {
        return obj instanceof String
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof Character
                || obj instanceof Boolean
                || obj instanceof JsonArray
                || obj instanceof JsonObject;
    }
}
