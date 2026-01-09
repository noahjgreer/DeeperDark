package net.minecraft.loot.function;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.collection.ListOperation;

public class SetWrittenBookPagesLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(WrittenBookContentComponent.PAGES_CODEC.fieldOf("pages").forGetter((function) -> {
         return function.pages;
      }), ListOperation.UNLIMITED_SIZE_CODEC.forGetter((function) -> {
         return function.operation;
      }))).apply(instance, SetWrittenBookPagesLootFunction::new);
   });
   private final List pages;
   private final ListOperation operation;

   protected SetWrittenBookPagesLootFunction(List conditions, List pages, ListOperation operation) {
      super(conditions);
      this.pages = pages;
      this.operation = operation;
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.WRITTEN_BOOK_CONTENT, WrittenBookContentComponent.DEFAULT, this::apply);
      return stack;
   }

   @VisibleForTesting
   public WrittenBookContentComponent apply(WrittenBookContentComponent current) {
      List list = this.operation.apply(current.pages(), this.pages);
      return current.withPages(list);
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_WRITTEN_BOOK_PAGES;
   }
}
