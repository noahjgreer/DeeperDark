package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public interface ErrorReporter {
   ErrorReporter EMPTY = new ErrorReporter() {
      public ErrorReporter makeChild(Context context) {
         return this;
      }

      public void report(Error error) {
      }
   };

   ErrorReporter makeChild(Context context);

   void report(Error error);

   public static class Logging extends Impl implements AutoCloseable {
      private final Logger logger;

      public Logging(Logger logger) {
         this.logger = logger;
      }

      public Logging(Context context, Logger logger) {
         super(context);
         this.logger = logger;
      }

      public void close() {
         if (!this.isEmpty()) {
            this.logger.warn("[{}] Serialization errors:\n{}", this.logger.getName(), this.getErrorsAsLongString());
         }

      }
   }

   public static class Impl implements ErrorReporter {
      public static final Context CONTEXT = () -> {
         return "";
      };
      @Nullable
      private final Impl parent;
      private final Context context;
      private final Set errors;

      public Impl() {
         this(CONTEXT);
      }

      public Impl(Context context) {
         this.parent = null;
         this.errors = new LinkedHashSet();
         this.context = context;
      }

      private Impl(Impl parent, Context context) {
         this.errors = parent.errors;
         this.parent = parent;
         this.context = context;
      }

      public ErrorReporter makeChild(Context context) {
         return new Impl(this, context);
      }

      public void report(Error error) {
         this.errors.add(new ErrorEntry(this, error));
      }

      public boolean isEmpty() {
         return this.errors.isEmpty();
      }

      public void apply(BiConsumer consumer) {
         List list = new ArrayList();
         StringBuilder stringBuilder = new StringBuilder();
         Iterator var4 = this.errors.iterator();

         while(var4.hasNext()) {
            ErrorEntry errorEntry = (ErrorEntry)var4.next();

            for(Impl impl = errorEntry.source; impl != null; impl = impl.parent) {
               list.add(impl.context);
            }

            for(int i = list.size() - 1; i >= 0; --i) {
               stringBuilder.append(((Context)list.get(i)).getName());
            }

            consumer.accept(stringBuilder.toString(), errorEntry.error());
            stringBuilder.setLength(0);
            list.clear();
         }

      }

      public String getErrorsAsString() {
         Multimap multimap = HashMultimap.create();
         Objects.requireNonNull(multimap);
         this.apply(multimap::put);
         return (String)multimap.asMap().entrySet().stream().map((entry) -> {
            String var10000 = (String)entry.getKey();
            return " at " + var10000 + ": " + (String)((Collection)entry.getValue()).stream().map(Error::getMessage).collect(Collectors.joining("; "));
         }).collect(Collectors.joining("\n"));
      }

      public String getErrorsAsLongString() {
         List list = new ArrayList();
         ErrorList errorList = new ErrorList(this.context);
         Iterator var3 = this.errors.iterator();

         while(var3.hasNext()) {
            ErrorEntry errorEntry = (ErrorEntry)var3.next();

            for(Impl impl = errorEntry.source; impl != this; impl = impl.parent) {
               list.add(impl.context);
            }

            ErrorList errorList2 = errorList;

            for(int i = list.size() - 1; i >= 0; --i) {
               errorList2 = errorList2.get((Context)list.get(i));
            }

            list.clear();
            errorList2.errors.add(errorEntry.error);
         }

         return String.join("\n", errorList.getMessages());
      }

      static record ErrorEntry(Impl source, Error error) {
         final Impl source;
         final Error error;

         ErrorEntry(Impl impl, Error error) {
            this.source = impl;
            this.error = error;
         }

         public Impl source() {
            return this.source;
         }

         public Error error() {
            return this.error;
         }
      }

      static record ErrorList(Context element, List errors, Map children) {
         final List errors;

         public ErrorList(Context context) {
            this(context, new ArrayList(), new LinkedHashMap());
         }

         private ErrorList(Context context, List list, Map map) {
            this.element = context;
            this.errors = list;
            this.children = map;
         }

         public ErrorList get(Context context) {
            return (ErrorList)this.children.computeIfAbsent(context, ErrorList::new);
         }

         public List getMessages() {
            int i = this.errors.size();
            int j = this.children.size();
            if (i == 0 && j == 0) {
               return List.of();
            } else {
               ArrayList list;
               if (i == 0 && j == 1) {
                  list = new ArrayList();
                  this.children.forEach((context, errors) -> {
                     list.addAll(errors.getMessages());
                  });
                  String var10002 = this.element.getName();
                  list.set(0, var10002 + (String)list.get(0));
                  return list;
               } else if (i == 1 && j == 0) {
                  String var10000 = this.element.getName();
                  return List.of(var10000 + ": " + ((Error)this.errors.getFirst()).getMessage());
               } else {
                  list = new ArrayList();
                  this.children.forEach((context, errors) -> {
                     list.addAll(errors.getMessages());
                  });
                  list.replaceAll((message) -> {
                     return "  " + message;
                  });
                  Iterator var4 = this.errors.iterator();

                  while(var4.hasNext()) {
                     Error error = (Error)var4.next();
                     list.add("  " + error.getMessage());
                  }

                  list.addFirst(this.element.getName() + ":");
                  return list;
               }
            }
         }

         public Context element() {
            return this.element;
         }

         public List errors() {
            return this.errors;
         }

         public Map children() {
            return this.children;
         }
      }
   }

   public static record ReferenceLootTableContext(RegistryKey id) implements Context {
      public ReferenceLootTableContext(RegistryKey registryKey) {
         this.id = registryKey;
      }

      public String getName() {
         String var10000 = String.valueOf(this.id.getValue());
         return "->{" + var10000 + "@" + String.valueOf(this.id.getRegistry()) + "}";
      }

      public RegistryKey id() {
         return this.id;
      }
   }

   public static record ListElementContext(int index) implements Context {
      public ListElementContext(int i) {
         this.index = i;
      }

      public String getName() {
         return "[" + this.index + "]";
      }

      public int index() {
         return this.index;
      }
   }

   public static record NamedListElementContext(String key, int index) implements Context {
      public NamedListElementContext(String string, int i) {
         this.key = string;
         this.index = i;
      }

      public String getName() {
         return "." + this.key + "[" + this.index + "]";
      }

      public String key() {
         return this.key;
      }

      public int index() {
         return this.index;
      }
   }

   public static record MapElementContext(String key) implements Context {
      public MapElementContext(String string) {
         this.key = string;
      }

      public String getName() {
         return "." + this.key;
      }

      public String key() {
         return this.key;
      }
   }

   public static record LootTableContext(RegistryKey id) implements Context {
      public LootTableContext(RegistryKey registryKey) {
         this.id = registryKey;
      }

      public String getName() {
         String var10000 = String.valueOf(this.id.getValue());
         return "{" + var10000 + "@" + String.valueOf(this.id.getRegistry()) + "}";
      }

      public RegistryKey id() {
         return this.id;
      }
   }

   public static record CriterionContext(String name) implements Context {
      public CriterionContext(String string) {
         this.name = string;
      }

      public String getName() {
         return this.name;
      }

      public String name() {
         return this.name;
      }
   }

   @FunctionalInterface
   public interface Context {
      String getName();
   }

   public interface Error {
      String getMessage();
   }
}
