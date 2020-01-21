package com.quicbit.json.next;

import java.util.ArrayList;
import java.util.List;

public class JsonWriter {
    private List<Character> state = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();
    private boolean first = true;      // track if we are at the first value of an array or object (append comma if not)
    private boolean key = false;      // track if prior value was a key

    public JsonWriter () {
    }

    private void assertState (char c) {
        if (state.get(state.size() - 1) != c) {
            String type = c == '{' ? "object" : "array";
            throw new IllegalStateException("not in " + type);
        }
    }

    public JsonWriter obj() {
        state.add('{');
        if (!first) { sb.append(","); }
        sb.append("{");
        first = true;
        key = false;
        return this;
    }

    public JsonWriter objend () {
        assertState('{');
        state.remove(state.size() - 1);
        sb.append("}");
        return this;
    }

    public JsonWriter arr () {
        sb.append("[");
        if (!first) { sb.append(","); }
        state.add('[');
        first = true;
        key = false;
        return this;
    }

    public JsonWriter arrend () {
        assertState('[');
        state.remove(state.size() - 1);
        sb.append("]");
        return this;
    }

    public JsonWriter key (String k) {
        assertState('{');
        if (!first) { sb.append(","); }
        sb.append("\"").append(k).append("\"");
        sb.append(":");
        first = false;
        key = true;
        return this;
    }

    public JsonWriter val (Object v) {
        if (!first && !key) { sb.append(","); }
        sb.append(v);
        first = false;
        key = false;
        return this;
    }

    public String toString() {
        return sb.toString();
    }
}
