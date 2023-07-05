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

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import dk.martinu.kofi.properties.*;

/**
 * A collection of {@link Element elements} with {@code add}, {@code get},
 * {@code contains} and {@code remove} methods provided for {@link Section} and
 * {@link Property} elements.
 * <p>
 * Documents inherently contain the <i>global section</i>; a pseudo-section
 * that does not exist within the document, but can be thought of as a global
 * scope to access elements at the very beginning of a document (which are not
 * enclosed in a section). In contexts where a section name is used, using
 * {@code null} refers to the global section.
 * <p>
 * <b>This implementation is not synchronized.</b> Access to a document should
 * be synchronized externally if multiple threads use it concurrently and at
 * least one of them modifies it.
 *
 * @author Adam Martinu
 * @since 1.0
 */
@SuppressWarnings("unused")
public class Document implements Iterable<Element>, Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 202307052158L;

    /**
     * A list of all elements in this document. The order of the elements in
     * this list defines how they are logically related to each other. For
     * example, a property at index {@code i} belongs to the section at the
     * highest index {@code k} for which <code>k &lt; i</code> is {@code true}.
     */
    @NotNull
    protected final ArrayList<Element> list;

    /**
     * Creates a document with an initial capacity of {@code 32}.
     */
    @Contract(pure = true)
    public Document() {
        this(32);
    }

    /**
     * Creates a document with the specified initial {@code capacity}.
     *
     * @throws IllegalArgumentException if {@code capacity} is negative
     */
    @Contract(pure = true)
    public Document(@Range(from = 0, to = Integer.MAX_VALUE) final int capacity) {
        list = new ArrayList<>(capacity);
    }

    /**
     * Creates a new document with the specified elements.
     *
     * @param elements A collection of elements to add to this document
     * @throws NullPointerException if {@code elements} is {@code null} or
     *                              contains {@code null} elements
     */
    @Contract(pure = true)
    public Document(@NotNull final Collection<Element> elements) {
        Objects.requireNonNull(elements, "elements is null");
        final Element[] a = elements.toArray(new Element[elements.size()]);
        list = new ArrayList<>(a.length);
        for (Element element : a)
            list.add(Objects.requireNonNull(element, "element is null"));
    }

    /**
     * Searches for an {@link ArrayProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if an array value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptArray(@Nullable final String section, @NotNull final String key,
            final Consumer<KofiArray> consumer) {
        return acceptValue(section, key, KofiArray.class, consumer);
    }

    /**
     * Searches for a {@link BooleanProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a {@code boolean} value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptBoolean(@Nullable final String section, @NotNull final String key,
            final Consumer<Boolean> consumer) {
        return acceptValue(section, key, Boolean.class, consumer);
    }

    /**
     * Searches for a {@link CharProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a {@code char} value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptChar(@Nullable final String section, @NotNull final String key,
            final Consumer<Character> consumer) {
        return acceptValue(section, key, Character.class, consumer);
    }

    /**
     * Searches for a {@link DoubleProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a {@code double} value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptDouble(@Nullable final String section, @NotNull final String key,
            final Consumer<Double> consumer) {
        return acceptValue(section, key, Double.class, consumer);
    }

    /**
     * Searches for a {@link FloatProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a {@code float} value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptFloat(@Nullable final String section, @NotNull final String key,
            final Consumer<Float> consumer) {
        return acceptValue(section, key, Float.class, consumer);
    }

    /**
     * Searches for an {@link IntProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if an {@code int} value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptInt(@Nullable final String section, @NotNull final String key,
            final Consumer<Integer> consumer) {
        return acceptValue(section, key, Integer.class, consumer);
    }

    /**
     * Searches for a {@link LongProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a {@code long} value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptLong(@Nullable final String section, @NotNull final String key, final Consumer<Long> consumer) {
        return acceptValue(section, key, Long.class, consumer);
    }

    /**
     * Searches for a {@link ObjectProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if an object value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptObject(@Nullable final String section, @NotNull final String key,
            final Consumer<KofiObject> consumer) {
        return acceptValue(section, key, KofiObject.class, consumer);
    }

    /**
     * Searches for a {@link StringProperty} in the specified section that
     * matches {@code key}. If a property is found, then its value is accepted
     * by the specified consumer and {@code true} is returned. Otherwise
     * {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a string value was accepted, otherwise
     * {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #acceptValue(String, String, Class, Consumer)
     * @see Property#matches(String)
     */
    public boolean acceptString(@Nullable final String section, @NotNull final String key,
            final Consumer<String> consumer) {
        return acceptValue(section, key, String.class, consumer);
    }

    /**
     * Searches for a {@link Property} in the specified section that
     * matches {@code key} and {@code valueType}. If a property is found, then
     * its value is accepted by the specified consumer and {@code true} is
     * returned. Otherwise {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a value was accepted, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    public <V> boolean acceptValue(@Nullable final String section, @NotNull final String key, Class<V> valueType,
            final Consumer<V> consumer) {
        final V value = getValue(section, key, valueType, null);
        if (value != null) {
            consumer.accept(value);
            return true;
        }
        else
            return false;
    }

    /**
     * Adds an {@link ArrayProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addArray(@NotNull final String key, @Nullable final KofiArray value) {
        addArray(null, key, value);
    }

    /**
     * Adds an {@link ArrayProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addArray(@Nullable final String section, @NotNull final String key, @Nullable final KofiArray value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new ArrayProperty(key, value));
    }

    /**
     * Adds a {@link BooleanProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addBoolean(@NotNull final String key, final boolean value) {
        addBoolean(null, key, value);
    }

    /**
     * Adds a {@link BooleanProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addBoolean(@Nullable final String section, @NotNull final String key, final boolean value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new BooleanProperty(key, value));
    }

    /**
     * Adds a {@link CharProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addChar(@NotNull final String key, final char value) {
        addChar(null, key, value);
    }

    /**
     * Adds a {@link CharProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addChar(@Nullable final String section, @NotNull final String key, final char value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new CharProperty(key, value));
    }

    /**
     * Adds a {@link DoubleProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addDouble(@NotNull final String key, final double value) {
        addDouble(null, key, value);
    }

    /**
     * Adds a {@link DoubleProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addDouble(@Nullable final String section, @NotNull final String key, final double value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new DoubleProperty(key, value));
    }

    /**
     * Appends {@code element} to the end of this document. This method does
     * nothing if {@code element} is {@code null}.
     *
     * @param element the element to add.
     */
    public void addElement(@Nullable final Element element) {
        addElement(list.size(), element);
    }

    /**
     * Inserts {@code element} at the specified {@code index} in this document.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices). This
     * method does nothing if {@code element} is {@code null}.
     *
     * @param index   index at which {@code element} is to be inserted
     * @param element the element to insert
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds
     *                                   {@code (index < 0 || index > size())}
     */
    public void addElement(@Range(from = 0, to = Integer.MAX_VALUE) final int index, @Nullable final Element element) {
        if (element == null)
            return;
        list.add(index, element);
    }

    /**
     * Adds a {@link FloatProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addFloat(@NotNull final String key, final float value) {
        addFloat(null, key, value);
    }

    /**
     * Adds a {@link FloatProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addFloat(@Nullable final String section, @NotNull final String key, final float value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new FloatProperty(key, value));
    }

    /**
     * Adds an {@link IntProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addInt(@NotNull final String key, final int value) {
        addInt(null, key, value);
    }

    /**
     * Adds an {@link IntProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addInt(@Nullable final String section, @NotNull final String key, final int value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new IntProperty(key, value));
    }

    /**
     * Adds a {@link LongProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addLong(@NotNull final String key, final long value) {
        addLong(null, key, value);
    }

    /**
     * Adds a {@link LongProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addLong(@Nullable final String section, @NotNull final String key, final long value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new LongProperty(key, value));
    }

    /**
     * Adds a {@link NullProperty} to the global section.
     *
     * @param key the property key
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addNull(@NotNull final String key) {
        addNull(null, key);
    }

    /**
     * Adds an {@link NullProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addNull(@Nullable final String section, @NotNull final String key) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new NullProperty(key));
    }

    /**
     * Adds an {@link ObjectProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addObject(@NotNull final String key, @Nullable final KofiObject value) {
        addObject(null, key, value);
    }

    /**
     * Adds an {@link ObjectProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addObject(@Nullable final String section, @NotNull final String key, @Nullable final KofiObject value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new ObjectProperty(key, value));
    }

    /**
     * Adds each {@link Property} in {@code properties} to the global section.
     *
     * @param properties the properties to add
     * @throws NullPointerException if {@code properties} is {@code null} or
     *                              contains {@code null} properties
     */
    public void addProperties(@NotNull final Property<?>... properties) {
        addProperties(null, properties);
    }

    /**
     * Adds each {@link Property} in {@code properties} to the specified
     * section.
     *
     * @param section    name of the section to add {@code property} to, can be
     *                   {@code null}
     * @param properties the properties to add
     * @throws NullPointerException if {@code properties} is {@code null} or
     *                              contains {@code null} properties
     */
    public void addProperties(@Nullable final String section, @NotNull final Property<?>... properties) {
        Objects.requireNonNull(properties, "properties array is null");
        final Property<?>[] a = Arrays.copyOf(properties, properties.length);
        // TODO add properties in parallel for better performance if/when possible
        //  this would require the collection of properties to use gap content
        for (Property<?> property : a)
            addProperty(section, property);
    }

    /**
     * Adds {@code property} to the global section. If the global section
     * already contains a {@link Property} that matches {@code property.key},
     * then it is replaced and the original property is returned. Otherwise
     * {@code null} is returned.
     *
     * @param property the property to add.
     * @return the property that was replaced, or {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see Property#matches(String)
     */
    @Nullable
    public Property<?> addProperty(@NotNull final Property<?> property) {
        return addProperty(null, property);
    }

    /**
     * Adds {@code property} to the specified section. If the section already
     * contains a {@link Property} that matches {@code property.key}, then it
     * is replaced and the original property is returned. Otherwise
     * {@code null} is returned.
     *
     * @param section  name of the section to add {@code property} to, can be
     *                 {@code null}
     * @param property the property to add.
     * @return the property that was replaced, or {@code null}
     * @throws NullPointerException if {@code property} is {@code null}
     * @see Property#matches(String)
     */
    @Nullable
    public Property<?> addProperty(@Nullable final String section, @NotNull final Property<?> property) {
        Objects.requireNonNull(property, "property is null");
        Element element;
        for (int i = section != null ? addSection(section) + 1 : 0; i < list.size(); i++) {
            element = list.get(i);
            // end of section, insert property
            if (element instanceof Section) {
                list.add(i, property);
                return null;
            }
            // section already contains property, replace it
            else if (element instanceof Property<?> p && p.matches(property.key)) {
                list.remove(i);
                list.add(i, property);
                return p;
            }
        }
        // end of document, append property
        list.add(property);
        return null;
    }

    /**
     * Attaches the specified comments to the property in the global section
     * that matches {@code key}. Any previously attached comments are removed.
     *
     * @param key      the property key
     * @param comments the comments to attach
     * @throws NullPointerException if {@code key} or {@code comments} is
     *                              {@code null}
     * @see Property#matches(String)
     */
    public void addPropertyComments(@NotNull final String key, @NotNull final Collection<String> comments) {
        addPropertyComments(null, key, comments);
    }

    /**
     * Attaches the specified comments to the property in the specified section
     * that matches {@code key}. Any previously attached comments are removed.
     *
     * @param section  the section to search in, can be {@code null}
     * @param key      the property key
     * @param comments the comments to attach
     * @throws NullPointerException if {@code key} or {@code comments} is
     *                              {@code null}
     * @see Section#matches(String)
     * @see Property#matches(String)
     */
    public void addPropertyComments(@Nullable final String section, @NotNull final String key,
            @NotNull final Collection<String> comments) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(comments, "comments is null");
        final int index = getPropertyIndex(section, key, null);
        if (index == -1)
            return;
        // current comment count
        int count = 0;
        Element element;
        for (int i = index - 1; i >= 0; i--) {
            element = list.get(i);
            if (element instanceof Comment)
                count++;
            else
                break;
        }
        if (count != 0) {
            for (int i = index - 1, k = 0; k < count; i--, k++)
                list.remove(i);
        }
        // incrementable comment index
        final IntValue cIndex = new IntValue(index - count);
        comments.stream().forEachOrdered(comment -> list.add(cIndex.getAndIncrement(), new Comment(comment)));
    }

    /**
     * Appends a new {@link Section} with the specified name to this document
     * if it is not already present and returns its index (position).
     *
     * @param section the name of the section to add
     * @return the index of the section in this document
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int addSection(@NotNull final String section) {
        int index = getSectionIndex(Objects.requireNonNull(section, "section is null"));
        if (index == -1)
            list.add(index = list.size(), new Section(section));
        return index;
    }

    /**
     * Attaches the specified comments to the specified section. Any previously
     * attached comments are removed.
     *
     * @param section  the section to match against
     * @param comments the comments to attach
     * @throws NullPointerException if {@code section} or {@code comments} is
     *                              {@code null}
     * @see Section#matches(String)
     */
    public void addSectionComments(@NotNull final String section, @NotNull final Collection<String> comments) {
        Objects.requireNonNull(section, "section is null");
        Objects.requireNonNull(comments, "comments is null");
        final int index = getSectionIndex(section);
        if (index == -1)
            return;
        // current comment count
        int count = 0;
        Element element;
        for (int i = index - 1; i >= 0; i--) {
            element = list.get(i);
            if (element instanceof Comment)
                count++;
            else
                break;
        }
        if (count != 0) {
            for (int i = index - 1, k = 0; k < count; i--, k++)
                list.remove(i);
        }
        // incrementable comment index
        final IntValue cIndex = new IntValue(index - count);
        comments.stream().forEachOrdered(comment -> list.add(cIndex.getAndIncrement(), new Comment(comment)));
    }

    /**
     * Adds a {@link StringProperty} to the global section.
     *
     * @param key   the property key
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(Property)
     */
    public void addString(@NotNull final String key, @Nullable final String value) {
        addString(null, key, value);
    }

    /**
     * Adds a {@link StringProperty} to the specified section.
     *
     * @param section name of the section to add the property to, can be
     *                {@code null}
     * @param key     the property key
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #addProperty(String, Property)
     */
    public void addString(@Nullable final String section, @NotNull final String key, final String value) {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new StringProperty(key, value));
    }

    /**
     * Removes all elements from this document
     */
    public void clear() {
        list.clear();
    }

    /**
     * Creates and returns a deep copy of this document. Whether the copied
     * elements themselves are deep or shallow copies is unspecified.
     *
     * @return a copy of this document
     * @throws CloneNotSupportedException if one of the elements in this
     *                                    document could not be cloned
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Document clone() throws CloneNotSupportedException {
        final Document document = new Document(list.size());
        for (int i = 0; i < list.size(); i++)
            document.list.add(i, list.get(i).clone());
        return document;

    }

    /**
     * Returns {@code true} if this document contains any property in the
     * global section that matches {@code key}.
     *
     * @param key the property key to match against
     * @return {@code true} if a property was found, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    public boolean contains(@NotNull final String key) {
        return contains(null, key);
    }

    /**
     * Returns {@code true} if this document contains any property in the
     * specified section that matches {@code key}.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a property was found, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    public boolean contains(@Nullable final String section, @NotNull final String key) {
        return contains(section, key, null);
    }

    /**
     * Returns {@code true} if this document contains any property in the
     * global section, that matches {@code key} and {@code valueType}.
     *
     * @param key       the property key to match against
     * @param valueType the property value type, can be {@code null}
     * @return {@code true} if a property was found, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    public boolean contains(@NotNull final String key, @Nullable final Class<?> valueType) {
        return contains(null, key, valueType);
    }

    /**
     * Returns {@code true} if this document contains any property in the
     * specified section that matches {@code key} and {@code valueType}.
     *
     * @param section   the name of the section to search in, can be
     *                  {@code null}
     * @param key       the property key to match against
     * @param valueType the property value type, can be {@code null}
     * @return {@code true} if a property was found, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    public boolean contains(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) {
        return getProperty(section, key, valueType) != null;
    }

    /**
     * Returns an unmodifiable view of all elements in this document.
     *
     * @see Collections#unmodifiableList(List)
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public List<Element> elements() {
        return Collections.unmodifiableList(list);
    }

    /**
     * Compares the specified object with this document for equality. Returns
     * {@code true} if {@code obj} is also a document and all elements in both
     * documents are equal, otherwise {@code false}.
     *
     * @param obj the object to be compared for equality with this document
     * @return {@code true} if the specified object is equal to this document,
     * otherwise {@code false}
     * @see ArrayList#equals(Object)
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Document document)
            return document.list.equals(list);
        else
            return false;
    }

    /**
     * Performs the specified action for each element in this document until
     * all elements have been processed or the action throws an exception.
     * Actions are performed in the order of iteration, from index {@code 0} to
     * {@code size() - 1}. Exceptions thrown by the action are relayed to the
     * caller. The behavior of this method is unspecified if the action
     * modifies this document.
     *
     * @param action the action to perform on each element
     * @throws NullPointerException if {@code action} is {@code null}
     */
    @Override
    public void forEach(@NotNull final Consumer<? super Element> action) {
        Objects.requireNonNull(action, "action is null");
        list.forEach(action);
    }

    /**
     * Returns the value of an {@link ArrayProperty} in the global section that
     * matches {@code key} and {@code KofiArray}, or {@code null} if no
     * property was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public KofiArray getArray(@NotNull final String key) {
        return getArray(null, key, null);
    }

    /**
     * Returns the value of an {@link ArrayProperty} in the specified section
     * that matches {@code key} and {@code KofiArray}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public KofiArray getArray(@Nullable final String section, @NotNull final String key) {
        return getArray(section, key, null);
    }

    /**
     * Returns the value of an {@link ArrayProperty} in the global section that
     * matches {@code key} and {@code KofiArray}, or {@code def} if no
     * property was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public KofiArray getArray(@NotNull final String key, @Nullable final KofiArray def) {
        return getArray(null, key, def);
    }

    /**
     * Returns the value of an {@link ArrayProperty} in the specified section
     * that matches {@code key} and {@code KofiArray}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public KofiArray getArray(@Nullable final String section, @NotNull final String key,
            @Nullable final KofiArray def) {
        return getValue(section, key, KofiArray.class, def);
    }

    /**
     * Returns the value of a {@link BooleanProperty} in the global section
     * that matches {@code key} and {@code Boolean}, or {@code null} if no
     * property was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Boolean getBoolean(@NotNull final String key) {
        return getBoolean(null, key, null);
    }

    /**
     * Returns the value of a {@link BooleanProperty} in the specified section
     * that matches {@code key} and {@code Boolean}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Boolean getBoolean(@Nullable final String section, @NotNull final String key) {
        return getBoolean(section, key, null);
    }

    /**
     * Returns the value of a {@link BooleanProperty} in the global section
     * that matches {@code key} and {@code Boolean}, or {@code def} if no
     * property was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Boolean getBoolean(@NotNull final String key, @Nullable final Boolean def) {
        return getBoolean(null, key, def);
    }

    /**
     * Returns the value of a {@link BooleanProperty} in the specified section
     * that matches {@code key} and {@code Boolean}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Boolean getBoolean(@Nullable final String section, @NotNull final String key, @Nullable final Boolean def) {
        return getValue(section, key, Boolean.class, def);
    }

    /**
     * Returns the value of a {@link CharProperty} in the global section that
     * matches {@code key} and {@code Character}, or {@code null} if no
     * property was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see #getValue(String, Class, Object)
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Character getChar(@NotNull final String key) {
        return getChar(null, key, null);
    }

    /**
     * Returns the value of a {@link CharProperty} in the specified section
     * that matches {@code key} and {@code Character}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Character getChar(@Nullable final String section, @NotNull final String key) {
        return getChar(section, key, null);
    }

    /**
     * Returns the value of a {@link CharProperty} in the global section that
     * matches {@code key} and {@code Character}, or {@code def} if no
     * property was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Character getChar(@NotNull final String key, @Nullable final Character def) {
        return getChar(null, key, def);
    }

    /**
     * Returns the value of a {@link CharProperty} in the specified section
     * that matches {@code key} and {@code Character}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Character getChar(@Nullable final String section, @NotNull final String key, @Nullable final Character def) {
        return getValue(section, key, Character.class, def);
    }

    /**
     * Returns the value of a {@link DoubleProperty} in the global section that
     * matches {@code key} and {@code Double}, or {@code null} if no property
     * was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Double getDouble(@NotNull final String key) {
        return getDouble(null, key, null);
    }

    /**
     * Returns the value of a {@link DoubleProperty} in the specified section
     * that matches {@code key} and {@code Double}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Double getDouble(@Nullable final String section, @NotNull final String key) {
        return getDouble(section, key, null);
    }

    /**
     * Returns the value of a {@link DoubleProperty} in the global section that
     * matches {@code key} and {@code Double}, or {@code def} if no property
     * was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Double getDouble(@NotNull final String key, @Nullable final Double def) {
        return getDouble(null, key, def);
    }

    /**
     * Returns the value of a {@link DoubleProperty} in the specified section
     * that matches {@code key} and {@code Double}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Double getDouble(@Nullable final String section, @NotNull final String key, @Nullable final Double def) {
        return getValue(section, key, Double.class, def);
    }

    /**
     * Returns the element at the specified index in this document.
     *
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds
     *                                   {@code (index < 0 || index >= size())}
     */
    @Contract(pure = true)
    @NotNull
    public Element getElement(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return list.get(index);
    }

    /**
     * Returns the value of a {@link FloatProperty} in the global section that
     * matches {@code key} and {@code Float}, or {@code null} if no property
     * was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Float getFloat(@NotNull final String key) {
        return getFloat(null, key, null);
    }

    /**
     * Returns the value of a {@link FloatProperty} in the specified section
     * that matches {@code key} and {@code Float}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Float getFloat(@Nullable final String section, @NotNull final String key) {
        return getFloat(section, key, null);
    }

    /**
     * Returns the value of a {@link FloatProperty} in the global section that
     * matches {@code key} and {@code Float}, or {@code def} if no property
     * was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Float getFloat(@NotNull final String key, @Nullable final Float def) {
        return getFloat(null, key, def);
    }

    /**
     * Returns the value of a {@link FloatProperty} in the specified section
     * that matches {@code key} and {@code Float}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Float getFloat(@Nullable final String section, @NotNull final String key, @Nullable final Float def) {
        return getValue(section, key, Float.class, def);
    }

    /**
     * Returns the value of an {@link IntProperty} in the global section that
     * matches {@code key} and {@code Integer}, or {@code null} if no property
     * was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Integer getInt(@NotNull final String key) {
        return getInt(null, key, null);
    }

    /**
     * Returns the value of an {@link IntProperty} in the specified section
     * that matches {@code key} and {@code Integer}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Integer getInt(@Nullable final String section, @NotNull final String key) {
        return getInt(section, key, null);
    }

    /**
     * Returns the value of an {@link IntProperty} in the global section that
     * matches {@code key} and {@code Integer}, or {@code def} if no property
     * was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Integer getInt(@NotNull final String key, @Nullable final Integer def) {
        return getInt(null, key, def);
    }

    /**
     * Returns the value of an {@link IntProperty} in the specified section
     * that matches {@code key} and {@code Integer}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Integer getInt(@Nullable final String section, @NotNull final String key, @Nullable final Integer def) {
        return getValue(section, key, Integer.class, def);
    }

    /**
     * Returns the value of a {@link LongProperty} in the global section that
     * matches {@code key} and {@code Long}, or {@code null} if no property was
     * found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Long getLong(@NotNull final String key) {
        return getLong(null, key, null);
    }

    /**
     * Returns the value of a {@link LongProperty} in the specified section
     * that matches {@code key} and {@code Long}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public Long getLong(@Nullable final String section, @NotNull final String key) {
        return getLong(section, key, null);
    }

    /**
     * Returns the value of a {@link LongProperty} in the global section that
     * matches {@code key} and {@code Long}, or {@code def} if no property was
     * found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Long getLong(@NotNull final String key, @Nullable final Long def) {
        return getLong(null, key, def);
    }

    /**
     * Returns the value of a {@link LongProperty} in the specified section
     * that matches {@code key} and {@code Long}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Long getLong(@Nullable final String section, @NotNull final String key, @Nullable final Long def) {
        return getValue(section, key, Long.class, def);
    }

    /**
     * Returns the value of an {@link ObjectProperty} in the global section
     * that matches {@code key} and {@code Object}, or {@code null} if no
     * property was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public KofiObject getObject(@NotNull final String key) {
        return getObject(null, key, null);
    }

    /**
     * Returns the value of an {@link ObjectProperty} in the specified section
     * that matches {@code key} and {@code Object}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public KofiObject getObject(@Nullable final String section, @NotNull final String key) {
        return getObject(section, key, null);
    }

    /**
     * Returns the value of an {@link ObjectProperty} in the global section
     * that matches {@code key} and {@code Object}, or {@code def} if no
     * property was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public KofiObject getObject(@NotNull final String key, @Nullable final KofiObject def) {
        return getObject(null, key, def);
    }

    /**
     * Returns the value of an {@link ObjectProperty} in the specified section
     * that matches {@code key} and {@code Object}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public KofiObject getObject(@Nullable final String section, @NotNull final String key,
            @Nullable final KofiObject def) {
        return getValue(section, key, KofiObject.class, def);
    }

    /**
     * Returns a list of all properties in the specified section, or
     * {@code null} if no section was found.
     *
     * @param section the name of the section to match against, can be
     *                {@code null}
     * @return An array of all properties in the section, or {@code null}
     * @see Section#matches(String)
     */
    @Contract(value = "_ -> new", pure = true)
    @Nullable
    public List<Property<?>> getProperties(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return null;
        final ArrayList<Property<?>> subList = new ArrayList<>(elements().size() - index);
        Element e;
        for (int i = index; i < elements().size(); i++) {
            e = list.get(i);
            if (e instanceof Section)
                break;
            else if (e instanceof Property<?> p)
                subList.add(p);
        }
        return subList;
    }

    /**
     * Returns a list of all properties in the specified section that matches
     * {@code valueType}, or {@code null} if no section was found.
     *
     * @param section   the name of the section to match against, can be
     *                  {@code null}
     * @param valueType the property values type, or {@code null}
     * @param <V>       runtime type of the property values
     * @return An array of all matching properties in the section, or
     * {@code null}
     * @see Property#matches(Class)
     * @see Section#matches(String)
     */
    @SuppressWarnings("unchecked")
    @Contract(value = "_, _ -> new", pure = true)
    @Nullable
    public <V> List<Property<V>> getProperties(@Nullable final String section, @Nullable Class<V> valueType) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return null;
        final ArrayList<Property<V>> subList = new ArrayList<>(elements().size() - index);
        Element e;
        for (int i = index; i < elements().size(); i++) {
            e = list.get(i);
            if (e instanceof Section)
                break;
            else if (e instanceof Property<?> p && p.matches(valueType))
                subList.add((Property<V>) p);
        }
        return subList;
    }

    /**
     * Returns the property in the global section that matches the {@code key},
     * or {@code null} if no property was found.
     *
     * @param key the property key to match against
     * @return the property that was found, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@NotNull final String key) {
        return getProperty(null, key);
    }

    /**
     * Returns the property in the specified section that matches the specified
     * {@code key}, or {@code null} if no property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the property that was found, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@Nullable final String section, @NotNull final String key) {
        return getProperty(section, key, null);
    }

    /**
     * Returns the property in the global section that matches {@code key} and
     * {@code valueType}, or {@code null} if no property was found.
     *
     * @param key       the property key to match against
     * @param valueType the property value type to match against, can be
     *                  {@code null}
     * @param <V>       runtime type of the property value
     * @return the property that was found, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public <V> Property<V> getProperty(@NotNull final String key, @Nullable final Class<V> valueType) {
        return getProperty(null, key, valueType);
    }

    /**
     * Returns the property in the specified section that matches {@code key}
     * and {@code valueType}, or {@code null} if no property was found.
     *
     * @param section   the name of the section to search in, can be
     *                  {@code null}
     * @param key       the property key to match against
     * @param valueType the property value type to match against, can be
     *                  {@code null}
     * @param <V>       runtime type of the property value
     * @return the property that was found, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    @Nullable
    public <V> Property<V> getProperty(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<V> valueType) {
        Objects.requireNonNull(key, "key is null");
        final int index = getPropertyIndex(section, key, valueType);
        if (index != -1)
            return (Property<V>) list.get(index);
        else
            return null;
    }

    /**
     * Returns a list of comments attached to the property in the global
     * section that matches {@code key}, or {@code null} if no section or
     * property was found.
     *
     * @param key the property key to match against
     * @return a list of comments, or {@code null}
     * @see Property#matches(String)
     */
    @Contract(value = "_ -> new", pure = true)
    @Nullable
    public List<Comment> getPropertyComments(@NotNull final String key) {
        return getPropertyComments(null, key);
    }

    /**
     * Returns a list of comments attached to the property in the specified
     * section that matches {@code key}, or {@code null} if no section or
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return a list of comments, or {@code null}
     * @see Section#matches(String)
     * @see Property#matches(String)
     */
    @Contract(value = "_, _ -> new", pure = true)
    @Nullable
    public List<Comment> getPropertyComments(@Nullable final String section, @NotNull final String key) {
        final int index = getPropertyIndex(section, key, null);
        if (index == -1)
            return null;
        final ArrayList<Comment> comments = new ArrayList<>();
        for (int i = index - 1; i >= 0; i--) {
            if (list.get(i) instanceof Comment comment)
                comments.add(comment);
            else
                break;
        }
        Collections.reverse(comments);
        return comments;
    }

    /**
     * Returns the number of properties in the section in this document that
     * matches the specified section name. If no section was found then
     * {@code -1} is returned.
     *
     * @param section the name of the section to match against, can be
     *                {@code null}
     * @return the number of properties, or {@code -1}
     */
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    public int getPropertyCount(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return -1;
        int count = 0;
        Element e;
        for (int i = index; i < list.size(); i++) {
            e = list.get(i);
            if (e instanceof Property)
                count++;
            else if (e instanceof Section)
                break;
        }
        return count;
    }

    /**
     * Returns the section in this document that matches {@code section}, or
     * {@code null} if no section was found.
     *
     * @param section the name of the section to match against, can be
     *                {@code null}
     * @return the section that was found, or {@code null}
     * @throws NullPointerException if {@code section} is {@code null}
     * @see Section#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public Section getSection(@NotNull final String section) {
        final int index = getSectionIndex(Objects.requireNonNull(section, "section is null"));
        if (index != -1)
            return (Section) list.get(index);
        else
            return null;
    }

    /**
     * Returns a list of comments attached to the specified section, or
     * {@code null} if no section was found.
     *
     * @param section the name of the section to match against, can be
     *                {@code null}
     * @return a list of comments, or {@code null}
     * @see Section#matches(String)
     */
    @Contract(value = "_-> new", pure = true)
    @Nullable
    public List<Comment> getSectionComments(@NotNull final String section) {
        final int index = getSectionIndex(Objects.requireNonNull(section, "section is null"));
        if (index == -1)
            return null;
        final LinkedList<Comment> comments = new LinkedList<>();
        for (int i = index - 1; i >= 0; i--) {
            if (list.get(i) instanceof Comment comment)
                comments.addFirst(comment);
            else
                break;
        }
        return comments;
    }

    /**
     * Returns the number of sections in this document.
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getSectionCount() {
        int count = 0;
        Element e;
        for (int i = 0; i < list.size(); i++) {
            e = list.get(i);
            if (e instanceof Section)
                count++;
        }
        return count;
    }

    /**
     * Returns a list of sections in this document.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public List<Section> getSections() {
        final ArrayList<Section> sections = new ArrayList<>();
        Element e;
        for (int i = 0; i < list.size(); i++) {
            e = list.get(i);
            if (e instanceof Section s)
                sections.add(s);
        }
        return sections;
    }

    /**
     * Returns the value of a {@link StringProperty} in the global section that
     * matches {@code key} and {@code String}, or {@code null} if no property
     * was found.
     *
     * @param key the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public String getString(@NotNull final String key) {
        return getString(null, key, null);
    }

    /**
     * Returns the value of a {@link StringProperty} in the specified section
     * that matches {@code key} and {@code String}, or {@code null} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return the value of the found property, or {@code null}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public String getString(@Nullable final String section, @NotNull final String key) {
        return getString(section, key, null);
    }

    /**
     * Returns the value of a {@link StringProperty} in the specified section
     * that matches {@code key} and {@code String}, or {@code def} if no
     * property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public String getString(@Nullable final String section, @NotNull final String key, final String def) {
        return getValue(section, key, String.class, def);
    }

    /**
     * Returns the value of the property in the global section that matches
     * {@code key}, or {@code def} if no property was found.
     *
     * @param key the property key to match against
     * @param def the default value to return if no property was found
     * @param <V> runtime type of the property value
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public <V> V getValue(@NotNull final String key, @Nullable V def) {
        return getValue(null, key, null, def);
    }

    /**
     * Returns the value of the property in the specified section that matches
     * {@code key}, or {@code def} if no property was found.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @param def     the default value to return if no property was found
     * @param <V>     runtime type of the property value
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    @Nullable
    public <V> V getValue(@Nullable final String section, @NotNull final String key, @Nullable V def) {
        return getValue(section, key, null, def);
    }

    /**
     * Returns the value of the property in the global section that matches
     * {@code key} and {@code valueType}, or {@code def} if no property was
     * found.
     *
     * @param key       the property key to match against
     * @param valueType the property value type to match against, or
     *                  {@code null}
     * @param def       the default value to return if no property was found
     * @param <V>       runtime type of the property value
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Nullable
    public <V> V getValue(@NotNull final String key, @Nullable final Class<V> valueType, @Nullable V def) {
        return getValue(null, key, valueType, def);
    }

    /**
     * Returns the value of the property in the specified section that matches
     * {@code key} and {@code valueType}, or {@code def} if no property was
     * found.
     *
     * @param section   the name of the section to search in, can be
     *                  {@code null}
     * @param key       the property key to match against
     * @param valueType the property value type to match against, or
     *                  {@code null}
     * @param def       the default value to return if no property was found
     * @param <V>       runtime type of the property value
     * @return the value of the found property, or {@code def}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String, Class)
     */
    @Nullable
    public <V> V getValue(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<V> valueType, @Nullable V def) {
        final Property<V> property = getProperty(section, key, valueType);
        return property != null ? property.value : def;
    }

    /**
     * Returns a hash code value for this document. This method delegates to
     * {@link ArrayList#hashCode()} and is considered to be consistent with
     * equals.
     *
     * @return a hash code value for this document
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        return list.hashCode();
    }

    /**
     * Returns {@code true} if this document does not contain a property in the
     * global section that matches {@code key} or its value is {@code null}.
     * Otherwise {@code false} is returned.
     *
     * @param key the property key to match against
     * @return {@code true} if no property with was found or its value is
     * {@code null}, otherwise false
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    public boolean isNull(@NotNull final String key) {
        return isNull(null, key, null);
    }

    /**
     * Returns {@code true} if this document does not contain a property in the
     * global section that matches {@code key} and {@code valueType}, or its
     * value is {@code null}. Otherwise {@code false} is returned.
     *
     * @param key       the property key to match against
     * @param valueType the property value type to match against, or
     *                  {@code null}
     * @return {@code true} if no property with was found or its value is
     * {@code null}, otherwise false
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    public boolean isNull(@NotNull final String key, @Nullable final Class<?> valueType) {
        return isNull(null, key, valueType);
    }

    /**
     * Returns {@code true} if this document does not contain a property in the
     * specified section that matches {@code key} or its value is {@code null}.
     * Otherwise {@code false} is returned.
     *
     * @param section the name of the section to search in, can be
     *                {@code null}
     * @param key     the property key to match against
     * @return {@code true} if no property with was found or its value is
     * {@code null}, otherwise false
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    public boolean isNull(@Nullable final String section, @NotNull final String key) {
        return isNull(section, key, null);
    }

    /**
     * Returns {@code true} if this document does not contain a property in the
     * specified section that matches {@code key} and {@code valueType}, or
     * its value is {@code null}. Otherwise {@code false} is returned.
     *
     * @param section   the name of the section to search in, can be
     *                  {@code null}
     * @param key       the property key to match against
     * @param valueType the property value type to match against, or
     *                  {@code null}
     * @return {@code true} if no property with was found or its value is
     * {@code null}, otherwise false
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    public boolean isNull(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) {
        final Property<?> property = getProperty(section, key, valueType);
        return property == null || property.value == null;
    }

    /**
     * Returns an iterator over the elements in this document in proper
     * sequence. The returned iterator is fail-fast.
     *
     * @return an iterator over the elements in this document
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return list.iterator();
    }

    /**
     * Returns a possibly parallel stream with this document as its source.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public Stream<Element> parallelStream() {
        return list.parallelStream();
    }

    /**
     * Removes all properties from the specified section.
     *
     * @param section the name of the section, can be {@code null}
     */
    public void removeProperties(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index != -1)
            removeProperties(index);
    }

    /**
     * Removes a property and its comments, if any, from the global section
     * that matches {@code key}.
     *
     * @param key the property key to match against
     * @return {@code true} if a property was removed, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    public boolean removeProperty(@NotNull final String key) {
        return removeProperty(null, key);
    }

    /**
     * Removes a property and its comments, if any, from the specified section
     * that matches {@code key}.
     *
     * @param section the name of the section, can be {@code null}
     * @param key     the property key to match against
     * @return {@code true} if a property was removed, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @see Property#matches(String)
     */
    public boolean removeProperty(@Nullable final String section, @NotNull final String key) {
        Objects.requireNonNull(key, "key is null");
        final int index = getElementsIndex(section);
        if (index == -1)
            return false;
        Element element;
        for (int i = index; i < list.size(); i++) {
            element = list.get(i);
            // break loop if a section is reached
            if (element instanceof Section)
                return false;
            // remove element if it is a property and matches key
            if (element instanceof Property<?> p && p.matches(key)) {
                list.remove(i);
                // remove comments preceding the property
                removeComments(i - 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the specified section, its comments (if any) and its properties.
     *
     * @param section the name of the section, can be {@code null}
     * @see Section#matches(String)
     */
    public void removeSection(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return;
        removeProperties(index);
        if (section != null) {
            list.remove(index - 1);
            removeComments(index - 2);
        }
    }

    /**
     * Returns the number of elements in this document.
     */
    @Contract(pure = true)
    public int size() {
        return list.size();
    }

    /**
     * Returns a <i>late-binding</i> and <i>fail-fast</i> spliterator over the
     * elements in this document. The spliterator reports
     * {@link Spliterator#SIZED}, {@link Spliterator#SUBSIZED} and
     * {@link Spliterator#ORDERED}.
     *
     * @return a spliterator over the elements in this document
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<Element> spliterator() {
        return list.spliterator();
    }

    /**
     * Returns a sequential stream with this document as its source.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public Stream<Element> stream() {
        return list.stream();
    }

    /**
     * Returns the starting index of the specified section's elements, or
     * {@code -1} if no section was found.
     *
     * @param section the name of the section, can be {@code null}
     * @return the starting index, or {@code -1}
     * @see Section#matches(String)
     */
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    protected int getElementsIndex(@Nullable final String section) {
        if (section != null) {
            final int index = getSectionIndex(section);
            return index != -1 ? index + 1 : -1;
        }
        else
            return 0;
    }

    /**
     * Returns the index of the property that matches {@code key} and
     * {@code valueType} in the specified section, or {@code -1} if no property
     * was found.
     *
     * @param section   the name of the section, can be {@code null}
     * @param key       the property key to match against
     * @param valueType the property value type to match against, can be
     *                  {@code null}
     * @return the index of the property, or {@code -1}.
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    protected int getPropertyIndex(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) {
        // get starting index
        final int index = getElementsIndex(section);
        if (index == -1)
            return -1;
        // iterate elements
        Element e;
        for (int i = index; i < list.size(); i++) {
            e = list.get(i);
            // start of next section, property not found
            if (e instanceof Section) {
                return -1;
            }
            // property found
            boolean b = e instanceof Property<?> p && p.matches(key, valueType);
            if (b)
                return i;
        }
        // property not found
        return -1;
    }

    /**
     * Returns the index of the specified section, or {@code -1} if no section
     * was found.
     *
     * @param section the name of the section
     * @return the index of the section, or {@code -1}
     * @see Section#matches(String)
     */
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    protected int getSectionIndex(@NotNull final String section) {
        // iterate elements
        Element e;
        for (int i = 0; i < list.size(); i++) {
            e = list.get(i);
            // section found
            if (e instanceof Section s && s.matches(section))
                return i;
        }
        // section not found
        return -1;
    }

    /**
     * Iterates the elements of this document, starting at the specified index,
     * and removes all comments in decreasing order until an element is reached
     * that is not a comment or this document has been exhausted.
     *
     * @param index the starting index, inclusive
     * @throws IndexOutOfBoundsException if {@code index >= size()} is
     *                                   {@code true}
     */
    protected void removeComments(final int index) {
        for (int i = index; i >= 0; i--)
            if (list.get(i) instanceof Comment)
                list.remove(i);
            else
                break;
    }

    /**
     * Removes all properties (and their comments, if any) from this document,
     * starting at the specified index, until a section is reached or this
     * document is exhausted.
     *
     * @param index the starting index, inclusive
     * @throws IndexOutOfBoundsException if {@code index < 0} is {@code true}
     */
    protected void removeProperties(@Range(from = 0, to = Integer.MAX_VALUE) final int index) {
        Element element;
        for (int i = index; i < list.size(); ) {
            element = list.get(i);
            // break loop if a section is reached
            if (element instanceof Section)
                break;
            // remove element if it is a property
            if (element instanceof Property<?>) {
                list.remove(i);
                // remove comments preceding the property
                removeComments(i - 1);
            }
            // advance to next element
            else
                i++;
        }
    }

    /**
     * An {@code int} value that can be incremented.
     */
    protected static class IntValue {

        /**
         * Current value.
         */
        public int value;

        /**
         * Constructs a new adder with the specified initial value.
         */
        public IntValue(final int value) {
            this.value = value;
        }

        /**
         * Returns the current value and then increments it.
         */
        public int getAndIncrement() {
            return value++;
        }
    }
}
