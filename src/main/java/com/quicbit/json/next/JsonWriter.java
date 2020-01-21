package com.quicbit.json.next;

import java.util.ArrayList;
import java.util.List;

public class JsonWriter {
    private List<Character> state = new ArrayList<>();
    private StringBuilder sb = new StringBuilder();

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
        sb.append("{");
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
        state.add('[');
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
        sb.append("\"").append(k).append("\"");
        sb.append(":");
        return this;
    }

    public JsonWriter val (Object v) {
        sb.append(v);
        return this;
    }

    public String toString() {
        return sb.toString();
    }
}
