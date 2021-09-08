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

import java.io.IOException;

import dk.martinu.kofi.codecs.IniCodec;

public class MainDebug {

    public static void main(String[] args) throws IOException {
        final String input = "object = { \"name\": \"John\", \"age\": 50, \"sex\": \"male\" }";
        final IniCodec codec = new IniCodec();
        final Document document = codec.readString(input);
        System.out.println("document: " + document.getElement(0).getString());

        final JsonObject.Entry[] objectEntries = {
                new JsonObject.Entry("name", "John"),
                new JsonObject.Entry("age", 50),
                new JsonObject.Entry("sex", "male")
        };
        JsonObject jsonObject = new JsonObject(objectEntries);
        System.out.println("local: " + jsonObject);
        System.out.println(jsonObject.equals(document.getObject("object")));
    }
}
