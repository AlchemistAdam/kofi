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
 * <p>The {@code Document} class is the core of the KOFI API. Documents are
 * containers for {@link Element elements}. Documents contain mainly two types
 * of elements; {@link Section sections} and {@link Property properties}, and
 * provides methods to add, get and remove them. Elements are stored in an
 * array, and the index of each element corresponds to its position in an
 * INI-file document; line 1 would be the element at index {@code 0}, line 2 at
 * index {@code 1}, and so on.</p>
 *
 * <p><b>Note that this implementation is not synchronized.</b> If multiple
 * threads access a {@code Document} instance concurrently, and at least one of
 * the threads modifies the document structurally, it <i>must</i> be
 * synchronized externally. (A structural modification is any operation that
 * adds or deletes one or more elements)</p>
 *
 * <p id="global">All documents inherently contain the <i>global section</i>,
 * which is a pseudo-section that does not exist within the document itself,
 * but can be thought of as a global scope to access elements at the very
 * beginning of a document (which are not enclosed in a section). The name of
 * the <i>global section</i> is equal to {@code null}.</p>
 *
 * @author Adam Martinu
 * @since 1.0
 */
@SuppressWarnings("unused")
public class Document implements Iterable<Element>, Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * A list of all {@link Element elements} in this document. The order of
     * the elements defines how they are logically related to each other. For
     * example, a {@link Property property}  at index {@code i} belongs to the
     * {@link Section section}  at the highest index {@code k} for which
     * {@code k &lt i} is {@code true}.
     */
    @NotNull
    protected final ArrayList<Element> elementList;

    /**
     * Creates a new empty document with an initial capacity of
     * {@code 32}.
     */
    @Contract(pure = true)
    public Document() {
        this(32);
    }

    /**
     * Creates a new empty document with the specified initial
     * {@code capacity}.
     *
     * @throws IllegalArgumentException if {@code capacity} is negative.
     */
    @Contract(pure = true)
    public Document(final int capacity) throws IllegalArgumentException {
        elementList = new ArrayList<>(capacity);
    }

    /**
     * Creates a new document with the specified {@link Element elements}.
     *
     * @param elements A collection of elements to add to this document.
     * @throws NullPointerException if {@code elements} is {@code null} or
     *                              contains {@code null} elements.
     */
    @Contract(pure = true)
    public Document(@NotNull final Collection<Element> elements) throws NullPointerException {
        this(Objects.requireNonNull(elements, "elements is null").size());
        if (elements.contains(null))
            throw new NullPointerException("collection contains null elements");
        elementList.addAll(elements);
    }

    /**
     * Delegate method to add an {@link ArrayProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addArray(@NotNull final String key, @Nullable final JsonArray value) throws NullPointerException {
        addArray(null, key, value);
    }

    /**
     * Delegate method to add an {@link ArrayProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addArray(@Nullable final String section, @NotNull final String key, @Nullable final JsonArray value)
            throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new ArrayProperty(key, value));
    }

    /**
     * Delegate method to add a {@link BooleanProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addBoolean(@NotNull final String key, final boolean value) throws NullPointerException {
        addBoolean(null, key, value);
    }

    /**
     * Delegate method to add a {@link BooleanProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addBoolean(@Nullable final String section, @NotNull final String key, final boolean value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new BooleanProperty(key, value));
    }

    /**
     * Delegate method to add a {@link CharProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addChar(@NotNull final String key, final char value) throws NullPointerException {
        addChar(null, key, value);
    }

    /**
     * Delegate method to add a {@link CharProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addChar(@Nullable final String section, @NotNull final String key, final char value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new CharProperty(key, value));
    }

    /**
     * Delegate method to add a {@link DoubleProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addDouble(@NotNull final String key, final double value) throws NullPointerException {
        addDouble(null, key, value);
    }

    /**
     * Delegate method to add a {@link DoubleProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addDouble(@Nullable final String section, @NotNull final String key, final double value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new DoubleProperty(key, value));
    }

    /**
     * Adds the specified {@link Element element} to the end of this document.
     * if {@code element} is {@code null} this method does nothing.
     *
     * @param element the element to add.
     */
    public void addElement(@Nullable final Element element) {
        addElement(elementList.size(), element);
    }

    /**
     * Inserts the {@code element} at the specified {@code index} in this
     * document. Shifts any {@link Element element} currently at that position
     * and any subsequent elements to the right (adds one to their indices). if
     * {@code element} is {@code null} this method does nothing.
     *
     * @param index   index at which the {@code element} is to be inserted.
     * @param element the element to insert.
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds
     *                                   {@code (index &lt 0 || index &gt size())}.
     */
    public void addElement(@Range(from = 0, to = Integer.MAX_VALUE) final int index, @Nullable final Element element)
            throws IndexOutOfBoundsException {
        if (element == null)
            return;
        elementList.add(index, element);
    }

    /**
     * Delegate method to add a {@link FloatProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addFloat(@NotNull final String key, final float value) throws NullPointerException {
        addFloat(null, key, value);
    }

    /**
     * Delegate method to add a {@link FloatProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addFloat(@Nullable final String section, @NotNull final String key, final float value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new FloatProperty(key, value));
    }

    /**
     * Delegate method to add an {@link IntProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addInt(@NotNull final String key, final int value) throws NullPointerException {
        addInt(null, key, value);
    }

    /**
     * Delegate method to add an {@link IntProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addInt(@Nullable final String section, @NotNull final String key, final int value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new IntProperty(key, value));
    }

    /**
     * Delegate method to add a {@link LongProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addLong(@NotNull final String key, final long value) throws NullPointerException {
        addLong(null, key, value);
    }

    /**
     * Delegate method to add a {@link LongProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addLong(@Nullable final String section, @NotNull final String key, final long value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new LongProperty(key, value));
    }

    /**
     * Delegate method to add a {@link NullProperty}.
     *
     * @param key the property key.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addNull(@NotNull final String key) throws NullPointerException {
        addNull(null, key);
    }

    /**
     * Delegate method to add an {@link NullProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addNull(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new NullProperty(key));
    }

    /**
     * Delegate method to add an {@link ObjectProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addObject(@NotNull final String key, @Nullable final JsonObject value) throws NullPointerException {
        addObject(null, key, value);
    }

    /**
     * Delegate method to add an {@link ObjectProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addObject(@Nullable final String section, @NotNull final String key, @Nullable final JsonObject value)
            throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new ObjectProperty(key, value));
    }

    /**
     * Adds all {@code properties} to the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param properties the properties to add.
     * @throws NullPointerException if {@code properties} is {@code null} or
     *                              contains {@code null} properties.
     */
    public void addProperties(@NotNull final Property<?>... properties) throws NullPointerException {
        addProperties(null, properties);
    }

    /**
     * Adds all {@code properties} to the specified {@code section}, or
     * {@code null} to add them to the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param section    name of the section to add {@code property} to,
     *                   or {@code null}.
     * @param properties the properties to add.
     * @throws NullPointerException if {@code properties} is {@code null} or
     *                              contains {@code null} properties.
     */
    public void addProperties(@Nullable final String section, @NotNull final Property<?>... properties) throws
            NullPointerException {
        Objects.requireNonNull(properties, "properties array is null");
        for (Property<?> property : properties)
            addProperty(section, property);
    }

    /**
     * Adds {@code property} to the
     * <a href="#global"><i>global section</i></a>. If the global section
     * already contains a {@link Property property} with a matching property
     * key, then it is replaced and the original property is returned.
     * Otherwise {@code null} is returned.
     *
     * @param property the property to add.
     * @return the property that was replaced, or {@code null}.
     * @throws NullPointerException if {@code property} is {@code null}.
     * @see Property#matches(String)
     */
    public Property<?> addProperty(@NotNull final Property<?> property) throws NullPointerException {
        return addProperty(null, property);
    }

    /**
     * Adds {@code property} to the specified {@code section}, or {@code null}
     * to add it to the <a href="#global"><i>global section</i></a>. If the
     * section already contains a {@link Property property} with a matching
     * property key, then it is replaced and the original property is returned.
     * Otherwise {@code null} is returned.
     *
     * @param section  name of the section to add {@code property} to, or
     *                 {@code null}.
     * @param property the property to add.
     * @return the property that was replaced, or {@code null}.
     * @throws NullPointerException if {@code property} is {@code null}.
     * @see Property#matches(String)
     */
    @Nullable
    public Property<?> addProperty(@Nullable final String section, @NotNull final Property<?> property) throws
            NullPointerException {
        Objects.requireNonNull(property, "property is null");
        Element element;
        for (int i = section != null ? addSection(section) + 1 : 0; i < elementList.size(); i++) {
            element = elementList.get(i);
            // end of section, insert property
            if (element instanceof Section) {
                elementList.add(i, property);
                return null;
            }
            // section already contains property, replace it
            else if (element instanceof Property<?> p && p.matches(property.key)) {
                elementList.remove(i);
                elementList.add(i, property);
                return p;
            }
        }
        // end of document, append property
        elementList.add(property);
        return null;
    }

    /**
     * Adds a new {@link Section section} with the specified name to this
     * document if it is not already present and returns its index.
     *
     * @param section the name of the section to add.
     * @return the index of the section in this document.
     */
    public int addSection(@NotNull final String section) {
        int index = getSectionIndex(section);
        if (index == -1)
            elementList.add(index = elementList.size(), new Section(section));
        return index;
    }

    /**
     * Delegate method to add a {@link StringProperty}.
     *
     * @param key   the property key.
     * @param value the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(Property)
     */
    public void addString(@NotNull final String key, @Nullable final String value) throws NullPointerException {
        addString(null, key, value);
    }

    /**
     * Delegate method to add a {@link StringProperty} to the specified
     * {@code section}.
     *
     * @param section name of the section to add the property to, or
     *                {@code null}.
     * @param key     the property key.
     * @param value   the property value
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #addProperty(String, Property)
     */
    public void addString(@Nullable final String section, @NotNull final String key, final String value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new StringProperty(key, value));
    }

    /**
     * Removes all {@link Element elements} from this document
     */
    public void clear() {
        elementList.clear();
    }

    /**
     * Creates and returns a deep copy of this document. Whether the copied
     * {@link Element elements} themselves are deep or shallow copies is not
     * specified.
     *
     * @return a copy of this document.
     * @throws CloneNotSupportedException if one of the elements in this
     *                                    document could not be cloned.
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Document clone() throws CloneNotSupportedException {
        final Document document = new Document(elementList.size());
        for (int i = 0; i < elementList.size(); i++)
            document.elementList.add(i, elementList.get(i).clone());
        return document;

    }

    /**
     * Returns {@code true} if this document contains any
     * {@link Property property} in the
     * <a href="#global"><i>global section</i></a> that matches the specified
     * {@code key}.
     *
     * @param key the property key to match against.
     * @return {@code true} if a property was found, otherwise {@code false}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    public boolean contains(@NotNull final String key) throws NullPointerException {
        return contains(null, key);
    }

    /**
     * Returns {@code true} if this document contains any
     * {@link Property property} in the specified {@code section}, or
     * {@code null} for the <a href="#global"><i>global section</i></a>, that
     * matches the specified {@code key}.
     *
     * @param section name of the section to search in, or {@code null}.
     * @param key     the property key to match against.
     * @return {@code true} if a property was found, otherwise {@code false}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    public boolean contains(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return contains(section, key, null);
    }

    /**
     * Returns {@code true} if this document contains any
     * {@link Property property} in the
     * <a href="#global"><i>global section</i></a>, that matches the specified
     * {@code key} and {@code valueType}.
     *
     * @param key       the property key to match against.
     * @param valueType the property value type, or {@code null}.
     * @return {@code true} if a property was found, otherwise {@code false}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    public boolean contains(@NotNull final String key, @Nullable final Class<?> valueType) throws NullPointerException {
        return contains(null, key, valueType);
    }

    /**
     * Returns {@code true} if this document contains any
     * {@link Property property} in the specified {@code section}, or
     * {@code null} for the <a href="#global"><i>global section</i></a>, that
     * matches the specified {@code key} and {@code valueType}.
     *
     * @param section   name of the section to search in, or {@code null}.
     * @param key       the property key to match against.
     * @param valueType the property value type, or {@code null}.
     * @return {@code true} if a property was found, otherwise {@code false}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    public boolean contains(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) throws NullPointerException {
        return getProperty(section, key, valueType) != null;
    }

    /**
     * Returns and unmodifiable list of all the {@link Element elements} in
     * this document.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public List<Element> elements() {
        return Collections.unmodifiableList(elementList);
    }

    /**
     * Compares the specified object with this document for equality. Returns
     * {@code true} if {@code obj} is also a document and all
     * {@link Element elements} in both documents are equal, otherwise
     * {@code false}.
     *
     * @param obj the object to be compared for equality with this document.
     * @return true if the specified object is equal to this document,
     * otherwise {@code false}.
     */
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Document)
            return ((Document) obj).elementList.equals(elementList);
        else
            return false;
    }

    /**
     * Performs the given {@code action} for each {@link Element element} in
     * this document until all elements have been processed or the action
     * throws an exception. Actions are performed in the order of iteration,
     * from index {@code 0} to {@code size() - 1}. Exceptions thrown by the
     * action are relayed to the caller. The behavior of this method is
     * unspecified if the action performs structural changes to this document.
     *
     * @param action the action to perform on each element.
     * @throws NullPointerException if {@code action} is {@code null}.
     */
    @Override
    public void forEach(@NotNull final Consumer<? super Element> action) throws NullPointerException {
        Objects.requireNonNull(action, "action is null");
        elementList.forEach(action);
    }

    /**
     * Delegate method to get the value of an {@link ArrayProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public JsonArray getArray(@NotNull final String key) throws NullPointerException {
        return getArray(null, key, null);
    }

    /**
     * Delegate method to get the value of an {@link ArrayProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public JsonArray getArray(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        return getArray(section, key, null);
    }

    /**
     * Delegate method to get the value of an {@link ArrayProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public JsonArray getArray(@NotNull final String key, @Nullable final JsonArray def) throws NullPointerException {
        return getArray(null, key, def);
    }

    /**
     * Delegate method to get the value of an {@link ArrayProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public JsonArray getArray(@Nullable final String section, @NotNull final String key, @Nullable final JsonArray def)
            throws NullPointerException {
        return getValue(section, key, JsonArray.class, def);
    }

    /**
     * Delegate method to get the value of a {@link BooleanProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Boolean getBoolean(@NotNull final String key) throws NullPointerException {
        return getBoolean(null, key, null);
    }

    /**
     * Delegate method to get the value of a {@link BooleanProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Boolean getBoolean(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getBoolean(section, key, null);
    }

    /**
     * Delegate method to get the value of a {@link BooleanProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Boolean getBoolean(@NotNull final String key, @Nullable final Boolean def) throws NullPointerException {
        return getBoolean(null, key, def);
    }

    /**
     * Delegate method to get the value of a {@link BooleanProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Boolean getBoolean(@Nullable final String section, @NotNull final String key, @Nullable final Boolean def)
            throws NullPointerException {
        return getValue(section, key, Boolean.class, def);
    }

    /**
     * Delegate method to get the value of a {@link CharProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Character getChar(@NotNull final String key) throws NullPointerException {
        return getChar(null, key, null);
    }

    /**
     * Delegate method to get the value of a {@link CharProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Character getChar(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getChar(section, key, null);
    }

    /**
     * Delegate method to get the value of a {@link CharProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Character getChar(@NotNull final String key, @Nullable final Character def) throws NullPointerException {
        return getChar(null, key, def);
    }

    /**
     * Delegate method to get the value of a {@link CharProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Character getChar(@Nullable final String section, @NotNull final String key, @Nullable final Character def)
            throws NullPointerException {
        return getValue(section, key, Character.class, def);
    }

    /**
     * Delegate method to get the value of a {@link DoubleProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Double getDouble(@NotNull final String key) throws NullPointerException {
        return getDouble(null, key, null);
    }

    /**
     * Delegate method to get the value of a {@link DoubleProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Double getDouble(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getDouble(section, key, null);
    }

    /**
     * Delegate method to get the value of a {@link DoubleProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Double getDouble(@NotNull final String key, @Nullable final Double def) throws NullPointerException {
        return getDouble(null, key, def);
    }

    /**
     * Delegate method to get the value of a {@link DoubleProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Double getDouble(@Nullable final String section, @NotNull final String key, @Nullable final Double def)
            throws NullPointerException {
        return getValue(section, key, Double.class, def);
    }

    /**
     * Returns the {@link Element element}  at the specified index in this
     * document.
     *
     * @throws IndexOutOfBoundsException if {@code index} is out of range
     *                                   ({@code index < 0 || index >= size()})
     */
    @Contract(pure = true)
    @NotNull
    public Element getElement(@Range(from = 0, to = Integer.MAX_VALUE) int index) throws IndexOutOfBoundsException {
        return elementList.get(index);
    }

    /**
     * Delegate method to get the value of a {@link FloatProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Float getFloat(@NotNull final String key) throws NullPointerException {
        return getFloat(null, key, null);
    }

    /**
     * Delegate method to get the value of a {@link FloatProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Float getFloat(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getFloat(section, key, null);
    }

    /**
     * Delegate method to get the value of a {@link FloatProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Float getFloat(@NotNull final String key, @Nullable final Float def) throws NullPointerException {
        return getFloat(null, key, def);
    }

    /**
     * Delegate method to get the value of a {@link FloatProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Float getFloat(@Nullable final String section, @NotNull final String key, @Nullable final Float def) throws
            NullPointerException {
        return getValue(section, key, Float.class, def);
    }

    /**
     * Delegate method to get the value of an {@link IntProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Integer getInt(@NotNull final String key) throws NullPointerException {
        return getInt(null, key, null);
    }

    /**
     * Delegate method to get the value of an {@link IntProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Integer getInt(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getInt(section, key, null);
    }

    /**
     * Delegate method to get the value of an {@link IntProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Integer getInt(@NotNull final String key, @Nullable final Integer def) throws NullPointerException {
        return getInt(null, key, def);
    }

    /**
     * Delegate method to get the value of an {@link IntProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Integer getInt(@Nullable final String section, @NotNull final String key, @Nullable final Integer def) throws
            NullPointerException {
        return getValue(section, key, Integer.class, def);
    }

    /**
     * Delegate method to get the value of a {@link LongProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Long getLong(@NotNull final String key) throws NullPointerException {
        return getLong(null, key, null);
    }

    /**
     * Delegate method to get the value of a {@link LongProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public Long getLong(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getLong(section, key, null);
    }

    /**
     * Delegate method to get the value of a {@link LongProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Long getLong(@NotNull final String key, @Nullable final Long def) throws NullPointerException {
        return getLong(null, key, def);
    }

    /**
     * Delegate method to get the value of a {@link LongProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Long getLong(@Nullable final String section, @NotNull final String key, @Nullable final Long def) throws
            NullPointerException {
        return getValue(section, key, Long.class, def);
    }

    /**
     * Delegate method to get the value of an {@link ObjectProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public JsonObject getObject(@NotNull final String key) throws NullPointerException {
        return getObject(null, key, null);
    }

    /**
     * Delegate method to get the value of an {@link ObjectProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public JsonObject getObject(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        return getObject(section, key, null);
    }

    /**
     * Delegate method to get the value of an {@link ObjectProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public JsonObject getObject(@NotNull final String key, @Nullable final JsonObject def) throws NullPointerException {
        return getObject(null, key, def);
    }

    /**
     * Delegate method to get the value of an {@link ObjectProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public JsonObject getObject(@Nullable final String section, @NotNull final String key,
            @Nullable final JsonObject def) throws NullPointerException {
        return getValue(section, key, JsonObject.class, def);
    }

    /**
     * Returns an array of all {@link Property properties} in the specified
     * {@code section}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>, or {@code null} if no
     * ection was found.
     *
     * @param section the name of the section to match against.
     * @return An array of all properties in the section, or {@code null}.
     * @see Section#matches(String)
     */
    @Contract(value = "_ -> new", pure = true)
    @Nullable
    public Property<?>[] getProperties(@Nullable final String section) {
        return getProperties(section, null);
    }

    /**
     * Returns an array of all {@link Property properties} in the specified
     * {@code section}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>, that matches the specified
     * {@code valueType}, or {@code null} if no section was found.
     *
     * @param section   the name of the section to match against.
     * @param valueType the property values type, or {@code null}.
     * @param <V>       runtime type of the property values.
     * @return An array of all matching properties in the section, or {@code null}.
     * @see Section#matches(String)
     */
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Contract(value = "_, _ -> new", pure = true)
    @Nullable
    public <V> Property<V>[] getProperties(@Nullable final String section, @Nullable Class<V> valueType) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return null;
        final ArrayList<Property<V>> subList = new ArrayList<>(elements().size() - index);
        Element e;
        for (int i = index; i < elements().size(); i++) {
            e = elementList.get(i);
            if (e instanceof Section)
                break;
            else if (e instanceof Property p && p.matches(valueType))
                subList.add((Property<V>) p);
        }
        return (Property<V>[]) subList.toArray();
    }

    /**
     * Returns the {@link Property property} in the
     * <a href="#global"><i>global section</i></a> that matches the specified
     * {@code key}, or {@code null} if no such property was found.
     *
     * @param key the property key to match against.
     * @return the property that was found, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@NotNull final String key) throws NullPointerException {
        return getProperty(null, key);
    }

    /**
     * Returns the {@link Property property} in the specified {@code section},
     * or {@code null} for the <a href="#global"><i>global section</i></a>,
     * that matches the specified {@code key}, or {@code null} if no such
     * property was found.
     *
     * @param section name of the section to search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the property that was found, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        return getProperty(section, key, null);
    }

    /**
     * Returns the {@link Property property} in the
     * <a href="#global"><i>global section</i></a> that matches the specified
     * {@code key} and {@code valueType}, or {@code null} if no such property
     * was found.
     *
     * @param key       the property key to match against.
     * @param valueType the property value type, or {@code null}.
     * @param <V>       runtime type of the property value.
     * @return the property that was found, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    @Nullable
    public <V> Property<V> getProperty(@NotNull final String key, @Nullable final Class<V> valueType) throws
            NullPointerException {
        return getProperty(null, key, valueType);
    }

    /**
     * Returns the {@link Property property} in the specified {@code section},
     * or {@code null} for the <a href="#global"><i>global section</i></a>,
     * that matches the specified {@code key} and {@code valueType}, or
     * {@code null} if no such property was found.
     *
     * @param section   name of the section to search in, or {@code null}.
     * @param key       the property key to match against.
     * @param valueType the property value type, or {@code null}.
     * @param <V>       runtime type of the property value.
     * @return the property that was found, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String, Class)
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    @Nullable
    public <V> Property<V> getProperty(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<V> valueType) throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final int index = getPropertyIndex(section, key, valueType);
        if (index != -1)
            return (Property<V>) elementList.get(index);
        else
            return null;
    }

    /**
     * Returns the number of {@link Property properties} in the
     * {@link Section section} in this document that matches the specified
     * section name, or {@code null} for  the
     * <a href="#global"><i>global section</i></a>. If no section was found
     * then {@code -1} is returned.
     *
     * @param section the name of the section to match against.
     * @return the number of properties, or {@code -1}.
     */
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    public int getPropertyCount(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return -1;
        int count = 0;
        Element e;
        for (int i = index; i < elementList.size(); i++) {
            e = elementList.get(i);
            if (e instanceof Property)
                count++;
            else if (e instanceof Section)
                break;
        }
        return count;
    }

    /**
     * Returns the {@link Section section} in this document that matches the
     * specified section name, or {@code null} if no such section was found.
     *
     * @param section the name of the section to match against.
     * @return the section that was found, or {@code null}.
     * @throws NullPointerException if {@code section} is {@code null}.
     */
    @Contract(pure = true)
    @Nullable
    public Section getSection(@NotNull final String section) throws NullPointerException {
        Objects.requireNonNull(section, "section is null");
        final int index = getSectionIndex(section);
        if (index != -1)
            return (Section) elementList.get(index);
        else
            return null;
    }

    /**
     * Returns the number of {@link Section sections} in this document. This
     * count will only include the <a href="#global"><i>global section</i></a>
     * if this document contains global {@link Property properties}.
     */
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    public int getSectionCount() {
        int count = 0;
        Element e;
        for (int i = 0; i < elementList.size(); i++) {
            e = elementList.get(i);
            if (e instanceof Section)
                count++;
        }
        return count;
    }

    /**
     * Returns a list of {@link Section sections} in this document. This
     * list will only include the <a href="#global"><i>global section</i></a>
     * if this document contains global {@link Property properties}.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public List<Section> getSections() {
        final ArrayList<Section> list = new ArrayList<>();
        Element e;
        for (int i = 0; i < elementList.size(); i++) {
            e = elementList.get(i);
            if (e instanceof Section s)
                list.add(s);
        }
        return list;
    }

    /**
     * Delegate method to get the value of a {@link StringProperty} in the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param key the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public String getString(@NotNull final String key) throws NullPointerException {
        return getString(null, key, null);
    }

    /**
     * Delegate method to get the value of a {@link StringProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @return the value of the found property, or {@code null}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(pure = true)
    @Nullable
    public String getString(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getString(section, key, null);
    }

    /**
     * Delegate method to get the value of a {@link StringProperty} in the
     * specified {@code section}.
     *
     * @param section the name of the section search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #getValue(String, String, Class, Object)
     */
    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public String getString(@Nullable final String section, @NotNull final String key, final String def) throws
            NullPointerException {
        return getValue(section, key, String.class, def);
    }

    /**
     * Returns the value of the {@link Property property} in the
     * <a href="#global"><i>global section</i></a> that matches the specified
     * {@code key}, or {@code def} if no such property was found.
     *
     * @param key the property key to match against.
     * @param def the default value to return if no property was found.
     * @param <V> runtime type of the property value.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    @Contract(pure = true)
    @Nullable
    public <V> V getValue(@NotNull final String key, @Nullable V def) throws NullPointerException {
        return getValue(null, key, null, def);
    }

    /**
     * Returns the value of the {@link Property property} in the specified
     * {@code section}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>, that matches the specified
     * {@code key}, or {@code def} if no such property was found.
     *
     * @param section name of the section to search in, or {@code null}.
     * @param key     the property key to match against.
     * @param def     the default value to return if no property was found.
     * @param <V>     runtime type of the property value.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    @Nullable
    public <V> V getValue(@Nullable final String section, @NotNull final String key, @Nullable V def) throws
            NullPointerException {
        return getValue(section, key, null, def);
    }

    /**
     * Returns the value of the {@link Property property} in the
     * <a href="#global"><i>global section</i></a> that matches the specified
     * {@code key} and {@code valueType}, or {@code def} if no such property
     * was found.
     *
     * @param key       the property key to match against.
     * @param valueType the property value type, or {@code null}.
     * @param def       the default value to return if no property was found.
     * @param <V>       runtime type of the property value.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String, Class)
     */
    @Nullable
    public <V> V getValue(@NotNull final String key, @Nullable final Class<V> valueType, @Nullable V def) throws
            NullPointerException {
        return getValue(null, key, valueType, def);
    }

    /**
     * Returns the value of the {@link Property property} in the specified
     * {@code section}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>, that matches the specified
     * {@code key} and {@code valueType}, or {@code def} if no such property
     * was found.
     *
     * @param section   name of the section to search in, or {@code null}.
     * @param key       the property key to match against.
     * @param valueType the property value type, or {@code null}.
     * @param def       the default value to return if no property was found.
     * @param <V>       runtime type of the property value.
     * @return the value of the found property, or {@code def}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String, Class)
     */
    @Nullable
    public <V> V getValue(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<V> valueType, @Nullable V def) throws NullPointerException {
        Property<V> property = getProperty(section, key, valueType);
        return property != null ? property.value : def;
    }

    /**
     * Returns a hash code value for this document. This method delegates to
     * {@link ArrayList#hashCode()} and is considered to be consistent with
     * equals.
     */
    @Contract(pure = true)
    @Override
    public int hashCode() {
        return elementList.hashCode();
    }

    /**
     * Returns an iterator over the {@link Element elements} in this document
     * in proper sequence. The returned iterator is fail-fast.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Iterator<Element> iterator() {
        return elementList.iterator();
    }

    /**
     * Returns a possibly parallel stream with this document as its source.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public Stream<Element> parallelStream() {
        return elementList.parallelStream();
    }

    /**
     * Removes all {@link Property properties} from the specified
     * {@code section}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param section the name of the section, or {@code null}.
     */
    public void removeProperties(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index != -1)
            removeProperties(index);
    }

    /**
     * Removes any {@link Property property} from the specified
     * {@code section}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>, that matches the given
     * {@code key}.
     *
     * @param section the name of the section, or {@code null}.
     * @param key     the key of the property to remove.
     * @return {@code true} if a property was removed, otherwise {@code false}.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see Property#matches(String)
     */
    public boolean removeProperty(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final int index = getElementsIndex(section);
        if (index == -1)
            return false;
        Element element;
        for (int i = index; i < elementList.size(); i++) {
            element = elementList.get(i);
            // break loop if a section is reached
            if (element instanceof Section)
                return false;
            // remove element if it is a property and matches key
            if (element instanceof Property<?> p && p.matches(key)) {
                elementList.remove(i);
                // remove comments preceding the property
                removeComments(i - 1);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the specified {@code section} and its
     * {@link Property properties}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param section the name of the section, or {@code null}
     * @see Section#matches
     */
    public void removeSection(@Nullable final String section) {
        final int index = getElementsIndex(section);
        if (index == -1)
            return;
        removeProperties(index);
        if (section != null) {
            elementList.remove(index - 1);
            removeComments(index - 2);
        }
    }

    /**
     * Returns the number of {@link Element elements} in this document.
     */
    @Contract(pure = true)
    public int size() {
        return elementList.size();
    }

    /**
     * Returns a <em>late-binding</em> and <em>fail-fast</em> Spliterator over
     * the {@link Element elements} in this document. The Spliterator reports
     * {@link Spliterator#SIZED}, {@link Spliterator#SUBSIZED} and
     * {@link Spliterator#ORDERED}.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    @Override
    public Spliterator<Element> spliterator() {
        return elementList.spliterator();
    }

    /**
     * Returns a sequential stream with this document as its source.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public Stream<Element> stream() {
        return elementList.stream();
    }

    /**
     * Helper method to return the index of the specified {@code section}'s
     * {@link Element elements}, or {@code null} for the
     * <a href="#global"><i>global section</i></a>.
     *
     * @param section the name of the section, or {@code null}.
     * @return the index.
     */
    @Contract(pure = true)
    protected int getElementsIndex(@Nullable final String section) {
        if (section != null) {
            final int index = getSectionIndex(section);
            return index != -1 ? index + 1 : -1;
        }
        else
            return 0;
    }

    /**
     * Helper method to return the index of any {@link Property property}
     * matching the specified {@code key} and {@code valueType} in the
     * specified {@code section}, or null for the
     * <a href="#global"><i>global section</i></a>. If no matching property was
     * found, then {@code -1} is returned.
     *
     * @param section   the name of the section.
     * @param key       the property key.
     * @param valueType the property value type
     * @return the index of the property, or {@code -1}.
     * @see Property#matches(String, Class)
     */
    @Contract(pure = true)
    protected int getPropertyIndex(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) {
        // get starting index
        final int index = getElementsIndex(section);
        if (index == -1)
            return -1;
        // iterate elements
        Element e;
        for (int i = index; i < elementList.size(); i++) {
            e = elementList.get(i);
            // start of next section, property not found
            if (e instanceof Section) {
                return -1;
            }
            // property found
            else if (e instanceof Property<?> p && p.matches(key, valueType))
                return i;
        }
        // property not found
        return -1;
    }

    /**
     * Helper method to return the index of the specified {@code section}, or
     * {@code -1} if not found.
     *
     * @param section the name of the section.
     * @return the index of the section, or {@code -1}.
     * @see Section#matches(String)
     */
    @Contract(pure = true)
    protected int getSectionIndex(@NotNull final String section) {
        // iterate elements
        Element e;
        for (int i = 0; i < elementList.size(); i++) {
            e = elementList.get(i);
            // section found
            if (e instanceof Section s && s.matches(section))
                return i;
        }
        // section not found
        return -1;
    }

    /**
     * Helper method to remove {@link Comment comments} beginning at the
     * specified {@code index}. The method will iterate the document in
     * decreasing order until the document is exhausted or an
     * {@link Element element} is reached that is not a comment.
     *
     * @param index the index of the comment.
     * @throws IndexOutOfBoundsException if {@code index &gt= size()} is
     *                                   {@code true}.
     */
    protected void removeComments(@Range(from = 0, to = Integer.MAX_VALUE) final int index) throws
            IndexOutOfBoundsException {
        for (int i = index; i >= 0; i--)
            if (elementList.get(i) instanceof Comment)
                elementList.remove(i);
            else
                break;
    }

    /**
     * Helper method to remove all {@link Property properties} in the document,
     * beginning at the specified {@code index}, until the document is
     * exhausted or a {@link Section section} is reached.
     *
     * @param index the index to start from, inclusive.
     * @throws IndexOutOfBoundsException if {@code index &lt 0} is
     *                                   {@code true}.
     */
    protected void removeProperties(@Range(from = 0, to = Integer.MAX_VALUE) final int index) throws
            IndexOutOfBoundsException {
        Element element;
        for (int i = index; i < elementList.size(); ) {
            element = elementList.get(i);
            // break loop if a section is reached
            if (element instanceof Section)
                break;
            // remove element if it is a property
            if (element instanceof Property<?>) {
                elementList.remove(i);
                // remove comments preceding the property
                removeComments(i - 1);
            }
            // advance to next element
            else
                i++;
        }
    }
}
