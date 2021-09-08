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

public class Document implements Iterable<Element<?>>, Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    /**
     * A list of all elements in this document. The order of the elements
     * defines how they are logically related to each other. For example, a
     * property belongs to the section at the highest index which is located
     * prior to the property in the list.
     */
    @NotNull
    protected final ArrayList<Element<?>> elementList;

    /**
     * Creates a new empty document with a default capacity of <code>32</code>.
     */
    @Contract(pure = true)
    public Document() {
        this(32);
    }

    @Contract(pure = true)
    public Document(@NotNull final Collection<Element<?>> elements) throws NullPointerException {
        this(Objects.requireNonNull(elements, "elements is null").size());
        elementList.addAll(elements);
    }

    /**
     * Creates a new empty document with the specified <code>capacity</code>.
     *
     * @throws IllegalArgumentException if <code>capacity</code> is negative.
     */
    @Contract(pure = true)
    public Document(final int capacity) throws IllegalArgumentException {
        elementList = new ArrayList<>(capacity);
    }

    public void addArray(@NotNull final String key, @NotNull final JsonArray array) throws NullPointerException {
        addArray(null, key, array);
    }

    public void addArray(@Nullable final String section, @NotNull final String key, @NotNull final JsonArray array)
            throws NullPointerException {
        Objects.requireNonNull(array, "array is null");
        addProperty(section, new ArrayProperty(key, array));
    }

    public void addBoolean(@NotNull final String key, final boolean value) throws NullPointerException {
        addBoolean(null, key, value);
    }

    public void addBoolean(@Nullable final String section, @NotNull final String key, final boolean value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new BooleanProperty(key, value));
    }

    public void addChar(@NotNull final String key, final char value) throws NullPointerException {
        addChar(null, key, value);
    }

    public void addChar(@Nullable final String section, @NotNull final String key, final char value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new CharProperty(key, value));
    }

    public void addDouble(@NotNull final String key, final double value) throws NullPointerException {
        addDouble(null, key, value);
    }

    public void addDouble(@Nullable final String section, @NotNull final String key, final double value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new DoubleProperty(key, value));
    }

    public void addElement(@Nullable final Element<?> element) {
        addElement(elementList.size(), element);
    }

    public void addElement(@Range(from = 0, to = Integer.MAX_VALUE) final int index, @Nullable final Element<?> element)
            throws IndexOutOfBoundsException {
        if (element == null)
            return;
        elementList.add(index, element);
    }

    public void addFloat(@NotNull final String key, final float value) throws NullPointerException {
        addFloat(null, key, value);
    }

    public void addFloat(@Nullable final String section, @NotNull final String key, final float value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new FloatProperty(key, value));
    }

    public void addInt(@NotNull final String key, final int value) throws NullPointerException {
        addInt(null, key, value);
    }

    public void addInt(@Nullable final String section, @NotNull final String key, final int value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new IntProperty(key, value));
    }

    public void addLong(@NotNull final String key, final long value) throws NullPointerException {
        addLong(null, key, value);
    }

    public void addLong(@Nullable final String section, @NotNull final String key, final long value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new LongProperty(key, value));
    }

    public void addProperties(@NotNull final Property<?>... properties) throws NullPointerException {
        addProperties(null, properties);
    }

    public void addProperties(@Nullable final String section, @NotNull final Property<?>... properties) throws
            NullPointerException {
        Objects.requireNonNull(properties, "properties array is null");
        for (Property<?> property : properties)
            addProperty(section, property);
    }

    public void addProperty(@NotNull final Property<?> property) throws NullPointerException {
        addProperty(null, property);
    }

    public void addProperty(@Nullable final String section, @NotNull final Property<?> property) throws
            NullPointerException {
        Objects.requireNonNull(property, "property is null");
        Element<?> element;
        for (int i = section != null ? addSectionImpl(section) + 1 : 0; i < elementList.size(); i++) {
            element = elementList.get(i);
            if (element instanceof Section) {
                elementList.add(i, property);
                return;
            }
            else if (element instanceof Property<?> p && p.matches(property.getKey())) {
                elementList.remove(i);
                elementList.add(i, property);
                return;
            }
        }
        // property was not added to list if this statement is reached
        elementList.add(property);
    }

    public void addString(@NotNull final String key, @Nullable final String value) throws NullPointerException {
        addString(null, key, value);
    }

    public void addString(@Nullable final String section, @NotNull final String key, final String value) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        addProperty(section, new StringProperty(key, value));
    }

    /**
     * Creates and returns a deep copy of this document. Whether the copied
     * elements themselves are deep or shallow copies is not specified.
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

    public boolean contains(@NotNull final String key) throws NullPointerException {
        return contains(null, key);
    }

    public boolean contains(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return contains(section, key, null);
    }

    public boolean contains(@NotNull final String key, @Nullable final Class<?> valueType) throws NullPointerException {
        return contains(null, key, valueType);
    }

    public boolean contains(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) throws NullPointerException {
        return getProperty(section, key, valueType) != null;
    }

    /**
     * Returns and unmodifiable list of all the elements in this document.
     */
    @Contract(value = "-> new", pure = true)
    @NotNull
    public List<Element<?>> elements() {
        return Collections.unmodifiableList(elementList);
    }

    /**
     * Compares the specified object with this document for equality. Returns
     * <code>true</code> if <code>obj</code> is also a document and all
     * elements in both documents are equal, otherwise
     * <code>false</code>.
     *
     * @param obj the object to be compared for equality with this document.
     * @return true if the specified object is equal to this document.
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

    @Override
    public void forEach(@NotNull final Consumer<? super Element<?>> action) throws NullPointerException {
        elementList.forEach(action);
    }

    @Contract(pure = true)
    @Nullable
    public JsonArray getArray(@NotNull final String key) throws NullPointerException {
        return getArray(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public JsonArray getArray(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        return getArray(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public JsonArray getArray(@NotNull final String key, @Nullable final JsonArray def) throws NullPointerException {
        return getArray(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public JsonArray getArray(@Nullable final String section, @NotNull final String key, @Nullable final JsonArray def)
            throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<JsonArray> p = getProperty(section, key, JsonArray.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public Boolean getBoolean(@NotNull final String key) throws NullPointerException {
        return getBoolean(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public Boolean getBoolean(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getBoolean(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Boolean getBoolean(@NotNull final String key, @Nullable final Boolean def) throws NullPointerException {
        return getBoolean(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Boolean getBoolean(@Nullable final String section, @NotNull final String key, @Nullable final Boolean def)
            throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<Boolean> p = getProperty(section, key, Boolean.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public Character getChar(@NotNull final String key) throws NullPointerException {
        return getChar(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public Character getChar(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getChar(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Character getChar(@NotNull final String key, @Nullable final Character def) throws NullPointerException {
        return getChar(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Character getChar(@Nullable final String section, @NotNull final String key, final Character def) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<Character> p = getProperty(section, key, Character.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public Double getDouble(@NotNull final String key) throws NullPointerException {
        return getDouble(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public Double getDouble(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getDouble(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Double getDouble(@NotNull final String key, @Nullable final Double def) throws NullPointerException {
        return getDouble(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Double getDouble(@Nullable final String section, @NotNull final String key, final Double def) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<Double> p = getProperty(section, key, Double.class);
        return p != null ? p.getValue() : def;
    }

    /**
     * Returns the element at the specified index in this document.
     *
     * @throws IndexOutOfBoundsException if <code>index</code> is out of range
     *                                   (<code>index < 0 || index >= size()</code>)
     */
    @Contract(pure = true)
    @NotNull
    public Element<?> getElement(@Range(from = 0, to = Integer.MAX_VALUE) int index) throws IndexOutOfBoundsException {
        return elementList.get(index);
    }

    @Contract(pure = true)
    @Nullable
    public Float getFloat(@NotNull final String key) throws NullPointerException {
        return getFloat(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public Float getFloat(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getFloat(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Float getFloat(@NotNull final String key, @Nullable final Float def) throws NullPointerException {
        return getFloat(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Float getFloat(@Nullable final String section, @NotNull final String key, final Float def) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<Float> p = getProperty(section, key, Float.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public Integer getInt(@NotNull final String key) throws NullPointerException {
        return getInt(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public Integer getInt(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getInt(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Integer getInt(@NotNull final String key, @Nullable final Integer def) throws NullPointerException {
        return getInt(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Integer getInt(@Nullable final String section, @NotNull final String key, final Integer def) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<Integer> p = getProperty(section, key, Integer.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public Long getLong(@NotNull final String key) throws NullPointerException {
        return getLong(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public Long getLong(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getLong(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Long getLong(@NotNull final String key, @Nullable final Long def) throws NullPointerException {
        return getLong(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Long getLong(@Nullable final String section, @NotNull final String key, final Long def) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<Long> p = getProperty(section, key, Long.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public JsonObject getObject(@NotNull final String key) throws NullPointerException {
        return getObject(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public JsonObject getObject(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        return getObject(section, key, null);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public JsonObject getObject(@NotNull final String key, @Nullable final JsonObject def) throws NullPointerException {
        return getObject(null, key, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public JsonObject getObject(@Nullable final String section, @NotNull final String key, @Nullable final JsonObject def)
            throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<JsonObject> p = getProperty(section, key, JsonObject.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(pure = true)
    @Nullable
    public Property<?>[] getProperties(@Nullable final String section) {
        return getProperties(section, null);
    }

    @Contract(pure = true)
    @Nullable
    public <V> Property<V>[] getProperties(@Nullable final String section, @Nullable Class<V> valueType) {
        return getPropertiesImpl(section, valueType);
    }

    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@NotNull final String key) throws NullPointerException {
        return getProperty(null, key);
    }

    @Contract(pure = true)
    @Nullable
    public Property<?> getProperty(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        return getProperty(section, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public <V> Property<V> getProperty(@NotNull final String key, @Nullable final Class<V> valueType) throws
            NullPointerException {
        return getProperty(null, key, valueType);
    }

    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    @Nullable
    public <V> Property<V> getProperty(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<V> valueType) throws NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final int index = getPropertyIndexImpl(section, key, valueType);
        if (index != -1)
            return (Property<V>) elementList.get(index);
        else
            return null;
    }

    @Contract(pure = true)
    @Nullable
    public Section getSection(@NotNull final String section) throws NullPointerException {
        Objects.requireNonNull(section, "section is null");
        final int index = getSectionIndexImpl(section);
        if (index != -1)
            return (Section) elementList.get(index);
        else
            return null;
    }

    @Contract(pure = true)
    @Nullable
    public String getString(@NotNull final String key) throws NullPointerException {
        return getString(null, key, null);
    }

    @Contract(pure = true)
    @Nullable
    public String getString(@Nullable final String section, @NotNull final String key) throws NullPointerException {
        return getString(section, key, null);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public String getString(@Nullable final String section, @NotNull final String key, final String def) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final Property<String> p = getProperty(section, key, String.class);
        return p != null ? p.getValue() : def;
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @Nullable
    public Object getValue(@NotNull final String key, @Nullable Object def) throws NullPointerException {
        return getValue(null, key, null, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public Object getValue(@Nullable final String section, @NotNull final String key, @Nullable Object def) throws
            NullPointerException {
        return getValue(section, key, null, def);
    }

    @Contract(value = "_, _, !null -> !null", pure = true)
    @Nullable
    public <V> V getValue(@NotNull final String key, @Nullable final Class<V> valueType, @Nullable V def) throws
            NullPointerException {
        return getValue(null, key, valueType, def);
    }

    @Contract(value = "_, _, _, !null -> !null", pure = true)
    @Nullable
    public <V> V getValue(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<V> valueType, @Nullable V def) throws NullPointerException {
        Property<V> property = getProperty(section, key, valueType);
        return property != null ? property.getValue() : def;
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
     * Returns an iterator over the elements in this document in proper
     * sequence. The returned iterator is fail-fast.
     */
    @Contract(value = "-> new")
    @NotNull
    @Override
    public Iterator<Element<?>> iterator() {
        return elementList.iterator();
    }

    @Contract(pure = true)
    @NotNull
    public Stream<Element<?>> parallelStream() {
        return elementList.parallelStream();
    }

    // TODO move code into protected impl method
    public int removeProperties(@Nullable final String section) {
        final int index;
        if (section != null) {
            index = getSectionIndexImpl(section) + 1;
            // section was not found
            if (index == 0)
                return -1;
        }
        else
            index = 0;
        Element<?> element;
        int count = 0;
        for (int i = index; i < elementList.size(); ) {
            element = elementList.get(i);
            // break loop if a section is reached
            if (element instanceof Section)
                break;
            // remove element if it is a property - i is unchanged
            if (element instanceof Property<?>) {
                elementList.remove(i);
                count++;
                // remove comments preceding the property
                for (int k = i - 1; k >= 0; k--) {
                    element = elementList.get(k);
                    if (element instanceof Comment) {
                        elementList.remove(k);
                        i = k;
                    }
                    else
                        break;
                }
            }
            // advance to next element
            else
                i++;
        }
        return count;
    }

    // TODO move code into protected impl method
    public boolean removeProperty(@Nullable final String section, @NotNull final String key) throws
            NullPointerException {
        Objects.requireNonNull(key, "key is null");
        final int index;
        if (section != null) {
            index = getSectionIndexImpl(section) + 1;
            // section was not found
            if (index == 0)
                return false;
        }
        else
            index = 0;
        Element<?> element;
        for (int i = index; i < elementList.size(); i++) {
            element = elementList.get(i);
            // break loop if a section is reached
            if (element instanceof Section)
                return false;
            // remove element if it is a property and matches key
            if (element instanceof Property<?> p && p.matches(key)) {
                elementList.remove(i);
                // remove comments preceding the property
                for (int k = i - 1; k >= 0; k--) {
                    element = elementList.get(k);
                    if (element instanceof Comment)
                        elementList.remove(k);
                    else
                        break;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of elements in this document.
     */
    @Contract(pure = true)
    public int size() {
        return elementList.size();
    }

    /**
     * Returns a <em>late-binding</em> and <em>fail-fast</em> Spliterator over
     * the elements in this document. The Spliterator reports
     * {@link Spliterator#SIZED}, {@link Spliterator#SUBSIZED} and
     * {@link Spliterator#ORDERED}.
     */
    @Contract(value = "-> new")
    @NotNull
    @Override
    public Spliterator<Element<?>> spliterator() {
        return elementList.spliterator();
    }

    @Contract(pure = true)
    @NotNull
    public Stream<Element<?>> stream() {
        return elementList.stream();
    }

    protected int addSectionImpl(@NotNull final String section) {
        int index = getSectionIndexImpl(section);
        if (index == -1)
            elementList.add(index = elementList.size(), new Section(section));
        return index;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Contract(pure = true)
    protected <V> Property<V>[] getPropertiesImpl(@Nullable final String section, @Nullable final Class<V> valueType) {
        final int index;
        if (section != null) {
            index = getSectionIndexImpl(section) + 1;
            if (index == 0)
                return null;
        }
        else
            index = 0;
        final ArrayList<Property<V>> subList = new ArrayList<>(elements().size() - index);
        Element<?> e;
        for (int i = index; i < elements().size(); i++) {
            e = elementList.get(i);
            if (e instanceof Section)
                break;
            else if (e instanceof Property p && p.matches(valueType))
                subList.add((Property<V>) p);
        }
        return (Property<V>[]) subList.toArray();
    }

    @Contract(pure = true)
    protected int getPropertyIndexImpl(@Nullable final String section, @NotNull final String key,
            @Nullable final Class<?> valueType) {
        // get starting index
        final int index;
        if (section == null)
            index = 0;
        else if ((index = getSectionIndexImpl(section) + 1) == 0)
            return -1;

        // iterate elements
        Element<?> e;
        for (int i = index; i < elementList.size(); i++) {
            e = elementList.get(i);
            // start of next section - property not found
            if (e instanceof Section)
                return -1;
                // property found
            else if (e instanceof Property<?> && ((Property<?>) e).matches(key, valueType))
                return i;
        }

        // property not found
        return -1;
    }

    @Contract(pure = true)
    protected int getSectionIndexImpl(@NotNull final String section) {
        // iterate elements
        Element<?> e;
        for (int i = 0; i < elementList.size(); i++) {
            e = elementList.get(i);
            // section found
            if (e instanceof Section && ((Section) e).matches(section))
                return i;
        }

        // section not found
        return -1;
    }
}
