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

import dk.martinu.kofi.codecs.IniCodec;
import dk.martinu.kofi.codecs.SerFileCodec;
import dk.martinu.kofi.spi.*;

module kofi {

    requires org.jetbrains.annotations;

    uses DocumentFileReader;
    uses DocumentFileWriter;
    uses DocumentStringReader;
    uses DocumentStringWriter;

    exports dk.martinu.kofi;
    exports dk.martinu.kofi.spi;
    exports dk.martinu.kofi.codecs;
    exports dk.martinu.kofi.properties;

    provides DocumentFileReader with IniCodec, SerFileCodec;
    provides DocumentFileWriter with IniCodec, SerFileCodec;
    provides DocumentStringReader with IniCodec;
    provides DocumentStringWriter with IniCodec;
}