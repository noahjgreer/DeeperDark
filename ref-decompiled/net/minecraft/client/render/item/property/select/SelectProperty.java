package net.minecraft.client.render.item.property.select;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface SelectProperty {
   @Nullable
   Object getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext displayContext);

   Codec valueCodec();

   Type getType();

   @Environment(EnvType.CLIENT)
   public static record Type(MapCodec switchCodec) {
      public Type(MapCodec mapCodec) {
         this.switchCodec = mapCodec;
      }

      public static Type create(MapCodec propertyCodec, Codec valueCodec) {
         MapCodec mapCodec = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(propertyCodec.forGetter(SelectItemModel.UnbakedSwitch::property), createCaseListCodec(valueCodec).forGetter(SelectItemModel.UnbakedSwitch::cases)).apply(instance, SelectItemModel.UnbakedSwitch::new);
         });
         return new Type(mapCodec);
      }

      public static MapCodec createCaseListCodec(Codec conditionCodec) {
         return SelectItemModel.SwitchCase.createCodec(conditionCodec).listOf().validate(Type::validateCases).fieldOf("cases");
      }

      private static DataResult validateCases(List cases) {
         if (cases.isEmpty()) {
            return DataResult.error(() -> {
               return "Empty case list";
            });
         } else {
            Multiset multiset = HashMultiset.create();
            Iterator var2 = cases.iterator();

            while(var2.hasNext()) {
               SelectItemModel.SwitchCase switchCase = (SelectItemModel.SwitchCase)var2.next();
               multiset.addAll(switchCase.values());
            }

            return multiset.size() != multiset.entrySet().size() ? DataResult.error(() -> {
               Stream var10000 = multiset.entrySet().stream().filter((entry) -> {
                  return entry.getCount() > 1;
               }).map((entry) -> {
                  return entry.getElement().toString();
               });
               return "Duplicate case conditions: " + (String)var10000.collect(Collectors.joining(", "));
            }) : DataResult.success(cases);
         }
      }

      public MapCodec switchCodec() {
         return this.switchCodec;
      }
   }
}
