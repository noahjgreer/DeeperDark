package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.util.dynamic.Codecs;

public class SetBookCoverLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(RawFilteredPair.createCodec(Codec.string(0, 32)).optionalFieldOf("title").forGetter((function) -> {
         return function.title;
      }), Codec.STRING.optionalFieldOf("author").forGetter((function) -> {
         return function.author;
      }), Codecs.rangedInt(0, 3).optionalFieldOf("generation").forGetter((function) -> {
         return function.generation;
      }))).apply(instance, SetBookCoverLootFunction::new);
   });
   private final Optional author;
   private final Optional title;
   private final Optional generation;

   public SetBookCoverLootFunction(List conditions, Optional title, Optional author, Optional generation) {
      super(conditions);
      this.author = author;
      this.title = title;
      this.generation = generation;
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.WRITTEN_BOOK_CONTENT, WrittenBookContentComponent.DEFAULT, this::apply);
      return stack;
   }

   private WrittenBookContentComponent apply(WrittenBookContentComponent current) {
      Optional var10002 = this.title;
      Objects.requireNonNull(current);
      RawFilteredPair var2 = (RawFilteredPair)var10002.orElseGet(current::title);
      Optional var10003 = this.author;
      Objects.requireNonNull(current);
      String var3 = (String)var10003.orElseGet(current::author);
      Optional var10004 = this.generation;
      Objects.requireNonNull(current);
      return new WrittenBookContentComponent(var2, var3, (Integer)var10004.orElseGet(current::generation), current.pages(), current.resolved());
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_BOOK_COVER;
   }
}
