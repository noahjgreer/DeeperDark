package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.collection.ListOperation;

public class SetWritableBookPagesLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(WritableBookContentComponent.PAGES_CODEC.fieldOf("pages").forGetter((function) -> {
         return function.pages;
      }), ListOperation.createCodec(100).forGetter((function) -> {
         return function.operation;
      }))).apply(instance, SetWritableBookPagesLootFunction::new);
   });
   private final List pages;
   private final ListOperation operation;

   protected SetWritableBookPagesLootFunction(List conditions, List pages, ListOperation operation) {
      super(conditions);
      this.pages = pages;
      this.operation = operation;
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.WRITABLE_BOOK_CONTENT, WritableBookContentComponent.DEFAULT, this::apply);
      return stack;
   }

   public WritableBookContentComponent apply(WritableBookContentComponent current) {
      List list = this.operation.apply(current.pages(), this.pages, 100);
      return current.withPages(list);
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_WRITABLE_BOOK_PAGES;
   }
}
