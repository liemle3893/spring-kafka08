package com.sk.zk_kafka.util;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MutableConfiguration implements Map<Object, Object> {

    private final Properties delegate;

    public MutableConfiguration() {
        this.delegate = new Properties();
    }

    public MutableConfiguration(Properties props) {
        this.delegate = props;
    }

    public Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        return this.delegate.computeIfPresent(key, remappingFunction);
    }

    public void storeToXML(OutputStream os, String comment) throws java.io.IOException {
        this.delegate.storeToXML(os, comment);
    }

    public Object compute(Object key, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        return this.delegate.compute(key, remappingFunction);
    }

    public boolean contains(Object value) {
        return this.delegate.contains(value);
    }

    public Object put(Object key, Object value) {
        return this.delegate.put(key, value);
    }

    public Object setProperty(String key, String value) {
        return this.delegate.setProperty(key, value);
    }

    public String getProperty(String key) {
        return this.delegate.getProperty(key);
    }

    public Set<String> stringPropertyNames() {
        return this.delegate.stringPropertyNames();
    }

    public void list(PrintStream out) {
        this.delegate.list(out);
    }

    public void store(Writer writer, String comments) throws java.io.IOException {
        this.delegate.store(writer, comments);
    }

    public Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        return this.delegate.merge(key, value, remappingFunction);
    }

    public Object remove(Object key) {
        return this.delegate.remove(key);
    }

    public Object getOrDefault(Object key, Object defaultValue) {
        return this.delegate.getOrDefault(key, defaultValue);
    }

    public Enumeration<Object> elements() {
        return this.delegate.elements();
    }

    public Object computeIfAbsent(Object key, Function<? super Object, ? extends Object> mappingFunction) {
        return this.delegate.computeIfAbsent(key, mappingFunction);
    }

    public Object get(Object key) {
        return this.delegate.get(key);
    }

    public void storeToXML(OutputStream os, String comment, String encoding) throws java.io.IOException {
        this.delegate.storeToXML(os, comment, encoding);
    }

    public Object putIfAbsent(Object key, Object value) {
        return this.delegate.putIfAbsent(key, value);
    }

    public boolean replace(Object key, Object oldValue, Object newValue) {
        return this.delegate.replace(key, oldValue, newValue);
    }

    public Set<Object> keySet() {
        return this.delegate.keySet();
    }

    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        this.delegate.forEach(action);
    }

    public boolean remove(Object key, Object value) {
        return this.delegate.remove(key, value);
    }

    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    public Object replace(Object key, Object value) {
        return this.delegate.replace(key, value);
    }

    public void clear() {
        this.delegate.clear();
    }

    public Collection<Object> values() {
        return this.delegate.values();
    }

    public String getProperty(String key, String defaultValue) {
        return this.delegate.getProperty(key, defaultValue);
    }

    public Object clone() {
        return this.delegate.clone();
    }

    public void replaceAll(BiFunction<? super Object, ? super Object, ? extends Object> function) {
        this.delegate.replaceAll(function);
    }

    public Enumeration<Object> keys() {
        return this.delegate.keys();
    }

    public void load(InputStream inStream) throws java.io.IOException {
        this.delegate.load(inStream);
    }

    public int size() {
        return this.delegate.size();
    }

    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    public void putAll(Map<? extends Object, ? extends Object> t) {
        this.delegate.putAll(t);
    }

    public void list(PrintWriter out) {
        this.delegate.list(out);
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public void store(OutputStream out, String comments) throws IOException {
        this.delegate.store(out, comments);
    }

    public void load(Reader reader) throws IOException {
        this.delegate.load(reader);
    }

    public void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
        this.delegate.loadFromXML(in);
    }

    public Enumeration<?> propertyNames() {
        return this.delegate.propertyNames();
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.delegate.entrySet();
    }
}
