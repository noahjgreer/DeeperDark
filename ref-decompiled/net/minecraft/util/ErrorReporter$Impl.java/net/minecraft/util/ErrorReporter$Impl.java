/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.util.ErrorReporter;
import org.jspecify.annotations.Nullable;

public static class ErrorReporter.Impl
implements ErrorReporter {
    public static final ErrorReporter.Context CONTEXT = () -> "";
    private final @Nullable ErrorReporter.Impl parent;
    private final ErrorReporter.Context context;
    private final Set<ErrorEntry> errors;

    public ErrorReporter.Impl() {
        this(CONTEXT);
    }

    public ErrorReporter.Impl(ErrorReporter.Context context) {
        this.parent = null;
        this.errors = new LinkedHashSet<ErrorEntry>();
        this.context = context;
    }

    private ErrorReporter.Impl(ErrorReporter.Impl parent, ErrorReporter.Context context) {
        this.errors = parent.errors;
        this.parent = parent;
        this.context = context;
    }

    @Override
    public ErrorReporter makeChild(ErrorReporter.Context context) {
        return new ErrorReporter.Impl(this, context);
    }

    @Override
    public void report(ErrorReporter.Error error) {
        this.errors.add(new ErrorEntry(this, error));
    }

    public boolean isEmpty() {
        return this.errors.isEmpty();
    }

    public void apply(BiConsumer<String, ErrorReporter.Error> consumer) {
        ArrayList<ErrorReporter.Context> list = new ArrayList<ErrorReporter.Context>();
        StringBuilder stringBuilder = new StringBuilder();
        for (ErrorEntry errorEntry : this.errors) {
            ErrorReporter.Impl impl = errorEntry.source;
            while (impl != null) {
                list.add(impl.context);
                impl = impl.parent;
            }
            for (int i = list.size() - 1; i >= 0; --i) {
                stringBuilder.append(((ErrorReporter.Context)list.get(i)).getName());
            }
            consumer.accept(stringBuilder.toString(), errorEntry.error());
            stringBuilder.setLength(0);
            list.clear();
        }
    }

    public String getErrorsAsString() {
        HashMultimap multimap = HashMultimap.create();
        this.apply((arg_0, arg_1) -> ((Multimap)multimap).put(arg_0, arg_1));
        return multimap.asMap().entrySet().stream().map(entry -> " at " + (String)entry.getKey() + ": " + ((Collection)entry.getValue()).stream().map(ErrorReporter.Error::getMessage).collect(Collectors.joining("; "))).collect(Collectors.joining("\n"));
    }

    public String getErrorsAsLongString() {
        ArrayList<ErrorReporter.Context> list = new ArrayList<ErrorReporter.Context>();
        ErrorList errorList = new ErrorList(this.context);
        for (ErrorEntry errorEntry : this.errors) {
            ErrorReporter.Impl impl = errorEntry.source;
            while (impl != this) {
                list.add(impl.context);
                impl = impl.parent;
            }
            ErrorList errorList2 = errorList;
            for (int i = list.size() - 1; i >= 0; --i) {
                errorList2 = errorList2.get((ErrorReporter.Context)list.get(i));
            }
            list.clear();
            errorList2.errors.add(errorEntry.error);
        }
        return String.join((CharSequence)"\n", errorList.getMessages());
    }

    static final class ErrorEntry
    extends Record {
        final ErrorReporter.Impl source;
        final ErrorReporter.Error error;

        ErrorEntry(ErrorReporter.Impl source, ErrorReporter.Error error) {
            this.source = source;
            this.error = error;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorEntry.class, "source;problem", "source", "error"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorEntry.class, "source;problem", "source", "error"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorEntry.class, "source;problem", "source", "error"}, this, object);
        }

        public ErrorReporter.Impl source() {
            return this.source;
        }

        public ErrorReporter.Error error() {
            return this.error;
        }
    }

    static final class ErrorList
    extends Record {
        private final ErrorReporter.Context element;
        final List<ErrorReporter.Error> errors;
        private final Map<ErrorReporter.Context, ErrorList> children;

        public ErrorList(ErrorReporter.Context context) {
            this(context, new ArrayList<ErrorReporter.Error>(), new LinkedHashMap<ErrorReporter.Context, ErrorList>());
        }

        private ErrorList(ErrorReporter.Context element, List<ErrorReporter.Error> errors, Map<ErrorReporter.Context, ErrorList> children) {
            this.element = element;
            this.errors = errors;
            this.children = children;
        }

        public ErrorList get(ErrorReporter.Context context) {
            return this.children.computeIfAbsent(context, ErrorList::new);
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
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{ErrorList.class, "element;problems;children", "element", "errors", "children"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ErrorList.class, "element;problems;children", "element", "errors", "children"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ErrorList.class, "element;problems;children", "element", "errors", "children"}, this, object);
        }

        public ErrorReporter.Context element() {
            return this.element;
        }

        public List<ErrorReporter.Error> errors() {
            return this.errors;
        }

        public Map<ErrorReporter.Context, ErrorList> children() {
            return this.children;
        }
    }
}
