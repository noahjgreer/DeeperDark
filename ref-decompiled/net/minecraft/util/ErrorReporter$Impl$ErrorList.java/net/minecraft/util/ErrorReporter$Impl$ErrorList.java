/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.ErrorReporter;

static final class ErrorReporter.Impl.ErrorList
extends Record {
    private final ErrorReporter.Context element;
    final List<ErrorReporter.Error> errors;
    private final Map<ErrorReporter.Context, ErrorReporter.Impl.ErrorList> children;

    public ErrorReporter.Impl.ErrorList(ErrorReporter.Context context) {
        this(context, new ArrayList<ErrorReporter.Error>(), new LinkedHashMap<ErrorReporter.Context, ErrorReporter.Impl.ErrorList>());
    }

    private ErrorReporter.Impl.ErrorList(ErrorReporter.Context element, List<ErrorReporter.Error> errors, Map<ErrorReporter.Context, ErrorReporter.Impl.ErrorList> children) {
        this.element = element;
        this.errors = errors;
        this.children = children;
    }

    public ErrorReporter.Impl.ErrorList get(ErrorReporter.Context context) {
        return this.children.computeIfAbsent(context, ErrorReporter.Impl.ErrorList::new);
    }

    public List<String> getMessages() {
        int i = this.errors.size();
        int j = this.children.size();
        if (i == 0 && j == 0) {
            return List.of();
        }
        if (i == 0 && j == 1) {
            ArrayList<String> list = new ArrayList<String>();
            this.children.forEach((context, errors) -> list.addAll(errors.getMessages()));
            list.set(0, this.element.getName() + (String)list.get(0));
            return list;
        }
        if (i == 1 && j == 0) {
            return List.of(this.element.getName() + ": " + this.errors.getFirst().getMessage());
        }
        ArrayList<String> list = new ArrayList<String>();
        this.children.forEach((context, errors) -> list.addAll(errors.getMessages()));
        list.replaceAll(message -> "  " + message);
        for (ErrorReporter.Error error : this.errors) {
            list.add("  " + error.getMessage());
        }
        list.addFirst(this.element.getName() + ":");
        return list;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorReporter.Impl.ErrorList.class, "element;problems;children", "element", "errors", "children"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorReporter.Impl.ErrorList.class, "element;problems;children", "element", "errors", "children"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorReporter.Impl.ErrorList.class, "element;problems;children", "element", "errors", "children"}, this, object);
    }

    public ErrorReporter.Context element() {
        return this.element;
    }

    public List<ErrorReporter.Error> errors() {
        return this.errors;
    }

    public Map<ErrorReporter.Context, ErrorReporter.Impl.ErrorList> children() {
        return this.children;
    }
}
