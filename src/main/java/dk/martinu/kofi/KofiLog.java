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

import org.jetbrains.annotations.NotNull;

import java.util.logging.*;

public class KofiLog {

    /**
     * Name of the logger returned by {@link #getLogger()}.
     */
    public static final String KOFI_LOGGER_NAME = KofiLog.class.getPackageName();
    /**
     * Private instance of the logger used by the Kofi API. This field should
     * never be accessed, use {@link #getLogger()} instead.
     */
    private static volatile Logger logger = null;

    public static Logger getLogger() {
        if (logger == null)
            synchronized (KofiLog.class) {
                if (logger == null) {
                    logger = Logger.getLogger(KOFI_LOGGER_NAME);
                    logger.setUseParentHandlers(false);
                    logger.setLevel(Level.ALL);

                    final ConsoleHandler handler = new ConsoleHandler();
                    handler.setLevel(Level.CONFIG);
                    logger.addHandler(handler);
                }
            }
        return logger;
    }

    public static void severe(@NotNull final String msg) {
        getLogger().severe(msg);
    }

    public static void warning(@NotNull final String msg) {
        getLogger().warning(msg);
    }

    private KofiLog() { }

}
