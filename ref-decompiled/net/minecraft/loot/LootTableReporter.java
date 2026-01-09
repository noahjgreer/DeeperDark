package net.minecraft.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.context.ContextType;

public class LootTableReporter {
   private final ErrorReporter errorReporter;
   private final ContextType contextType;
   private final Optional dataLookup;
   private final Set referenceStack;

   public LootTableReporter(ErrorReporter errorReporter, ContextType contextType, RegistryEntryLookup.RegistryLookup dataLookup) {
      this(errorReporter, contextType, Optional.of(dataLookup), Set.of());
   }

   public LootTableReporter(ErrorReporter errorReporter, ContextType contextType) {
      this(errorReporter, contextType, Optional.empty(), Set.of());
   }

   private LootTableReporter(ErrorReporter errorReporter, ContextType contextType, Optional dataLookup, Set referenceStack) {
      this.errorReporter = errorReporter;
      this.contextType = contextType;
      this.dataLookup = dataLookup;
      this.referenceStack = referenceStack;
   }

   public LootTableReporter makeChild(ErrorReporter.Context context) {
      return new LootTableReporter(this.errorReporter.makeChild(context), this.contextType, this.dataLookup, this.referenceStack);
   }

   public LootTableReporter makeChild(ErrorReporter.Context context, RegistryKey key) {
      Set set = ImmutableSet.builder().addAll(this.referenceStack).add(key).build();
      return new LootTableReporter(this.errorReporter.makeChild(context), this.contextType, this.dataLookup, set);
   }

   public boolean isInStack(RegistryKey key) {
      return this.referenceStack.contains(key);
   }

   public void report(ErrorReporter.Error error) {
      this.errorReporter.report(error);
   }

   public void validateContext(LootContextAware contextAware) {
      Set set = contextAware.getAllowedParameters();
      Set set2 = Sets.difference(set, this.contextType.getAllowed());
      if (!set2.isEmpty()) {
         this.errorReporter.report(new ParametersNotProvidedError(set2));
      }

   }

   public RegistryEntryLookup.RegistryLookup getDataLookup() {
      return (RegistryEntryLookup.RegistryLookup)this.dataLookup.orElseThrow(() -> {
         return new UnsupportedOperationException("References not allowed");
      });
   }

   public boolean canUseReferences() {
      return this.dataLookup.isPresent();
   }

   public LootTableReporter withContextType(ContextType contextType) {
      return new LootTableReporter(this.errorReporter, contextType, this.dataLookup, this.referenceStack);
   }

   public ErrorReporter getErrorReporter() {
      return this.errorReporter;
   }

   public static record ParametersNotProvidedError(Set notProvided) implements ErrorReporter.Error {
      public ParametersNotProvidedError(Set set) {
         this.notProvided = set;
      }

      public String getMessage() {
         return "Parameters " + String.valueOf(this.notProvided) + " are not provided in this context";
      }

      public Set notProvided() {
         return this.notProvided;
      }
   }

   public static record MissingElementError(RegistryKey referenced) implements ErrorReporter.Error {
      public MissingElementError(RegistryKey registryKey) {
         this.referenced = registryKey;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.referenced.getValue());
         return "Missing element " + var10000 + " of type " + String.valueOf(this.referenced.getRegistry());
      }

      public RegistryKey referenced() {
         return this.referenced;
      }
   }

   public static record RecursionError(RegistryKey referenced) implements ErrorReporter.Error {
      public RecursionError(RegistryKey registryKey) {
         this.referenced = registryKey;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.referenced.getValue());
         return var10000 + " of type " + String.valueOf(this.referenced.getRegistry()) + " is recursively called";
      }

      public RegistryKey referenced() {
         return this.referenced;
      }
   }

   public static record ReferenceNotAllowedError(RegistryKey referenced) implements ErrorReporter.Error {
      public ReferenceNotAllowedError(RegistryKey registryKey) {
         this.referenced = registryKey;
      }

      public String getMessage() {
         String var10000 = String.valueOf(this.referenced.getValue());
         return "Reference to " + var10000 + " of type " + String.valueOf(this.referenced.getRegistry()) + " was used, but references are not allowed";
      }

      public RegistryKey referenced() {
         return this.referenced;
      }
   }
}
