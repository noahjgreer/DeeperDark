package net.minecraft.loot.entry;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.ErrorReporter;

public abstract class CombinedEntry extends LootPoolEntry {
   public static final ErrorReporter.Error EMPTY_CHILDREN_LIST_ERROR = new ErrorReporter.Error() {
      public String getMessage() {
         return "Empty children list";
      }
   };
   protected final List children;
   private final EntryCombiner predicate;

   protected CombinedEntry(List terms, List conditions) {
      super(conditions);
      this.children = terms;
      this.predicate = this.combine(terms);
   }

   public void validate(LootTableReporter reporter) {
      super.validate(reporter);
      if (this.children.isEmpty()) {
         reporter.report(EMPTY_CHILDREN_LIST_ERROR);
      }

      for(int i = 0; i < this.children.size(); ++i) {
         ((LootPoolEntry)this.children.get(i)).validate(reporter.makeChild(new ErrorReporter.NamedListElementContext("children", i)));
      }

   }

   protected abstract EntryCombiner combine(List terms);

   public final boolean expand(LootContext lootContext, Consumer consumer) {
      return !this.test(lootContext) ? false : this.predicate.expand(lootContext, consumer);
   }

   public static MapCodec createCodec(Factory factory) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         Products.P2 var10000 = instance.group(LootPoolEntryTypes.CODEC.listOf().optionalFieldOf("children", List.of()).forGetter((entry) -> {
            return entry.children;
         })).and(addConditionsField(instance).t1());
         Objects.requireNonNull(factory);
         return var10000.apply(instance, factory::create);
      });
   }

   @FunctionalInterface
   public interface Factory {
      CombinedEntry create(List terms, List conditions);
   }
}
