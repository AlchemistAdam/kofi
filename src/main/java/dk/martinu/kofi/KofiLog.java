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

import java.util.logging.*;

/**
 * Singleton logger used by the KOFI API, based on the Java logging API. Contains static
 * convenience methods for logging messages to this logger.
 *
 * @author Adam Martinu
 * @see #getLogger()
 * @see Logger
 * @since 1.0
 */
public class KofiLog {

    /**
     * Name of the logger returned by {@link #getLogger()}. This is the package
     * name of this class.
     *
     * @see Class#getPackageName()
     */
    public static final String KOFI_LOGGER_NAME = KofiLog.class.getPackageName();
    /**
     * Private instance of the logger used by the Kofi API. This field should
     * never be accessed, use {@link #getLogger()} instead.
     */
    private static volatile Logger logger = null;

    /**
     * See {@link Logger#finest(String)}.
     */
    @Contract(pure = true)
    public static void finest(@Nullable final String msg) {
        getLogger().finest(msg);
    }

    /**
     * Returns the singleton logger instance held by this class, or creates a
     * new instance if it does not already exist.
     * <p>
     * When creating a new logger, it is created for a named subsystem
     * specified by {@link #KOFI_LOGGER_NAME}. Using parent handlers is
     * disabled, its level is set to {@code ALL} and adds a new
     * {@link ConsoleHandler} with a level set to {@code CONFIG}.
     *
     * @return the logger
     * @see Logger#getLogger(String)
     * @see Level
     */
    @Contract(pure = true)
    @NotNull
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

    /**
     * See {@link Logger#severe(String)}.
     */
    @Contract(pure = true)
    public static void severe(@Nullable final String msg) {
        getLogger().severe(msg);
    }

    /**
     * See {@link Logger#throwing(String, String, Throwable)}.
     */
    @Contract(pure = true)
    public static void throwing(@Nullable final String sourceClass, @Nullable final String sourceMethod,
            @Nullable final Throwable thrown) {
        getLogger().throwing(sourceClass, sourceMethod, thrown);
    }

    /**
     * See {@link Logger#warning(String)}.
     */
    @Contract(pure = true)
    public static void warning(@Nullable final String msg) {
        getLogger().warning(msg);
    }

    /**
     * Private constructor. Use {@link #getLogger()} to get a logger instance.
     */
    @Contract(pure = true)
    private KofiLog() { }

}
