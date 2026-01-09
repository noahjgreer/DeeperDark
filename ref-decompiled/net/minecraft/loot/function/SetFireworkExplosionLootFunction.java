package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public class SetFireworkExplosionLootFunction extends ConditionalLootFunction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return addConditionsField(instance).and(instance.group(FireworkExplosionComponent.Type.CODEC.optionalFieldOf("shape").forGetter((function) -> {
         return function.shape;
      }), FireworkExplosionComponent.COLORS_CODEC.optionalFieldOf("colors").forGetter((function) -> {
         return function.colors;
      }), FireworkExplosionComponent.COLORS_CODEC.optionalFieldOf("fade_colors").forGetter((function) -> {
         return function.fadeColors;
      }), Codec.BOOL.optionalFieldOf("trail").forGetter((function) -> {
         return function.trail;
      }), Codec.BOOL.optionalFieldOf("twinkle").forGetter((function) -> {
         return function.twinkle;
      }))).apply(instance, SetFireworkExplosionLootFunction::new);
   });
   public static final FireworkExplosionComponent DEFAULT_EXPLOSION;
   final Optional shape;
   final Optional colors;
   final Optional fadeColors;
   final Optional trail;
   final Optional twinkle;

   public SetFireworkExplosionLootFunction(List conditions, Optional shape, Optional colors, Optional fadeColors, Optional trail, Optional twinkle) {
      super(conditions);
      this.shape = shape;
      this.colors = colors;
      this.fadeColors = fadeColors;
      this.trail = trail;
      this.twinkle = twinkle;
   }

   protected ItemStack process(ItemStack stack, LootContext context) {
      stack.apply(DataComponentTypes.FIREWORK_EXPLOSION, DEFAULT_EXPLOSION, this::apply);
      return stack;
   }

   private FireworkExplosionComponent apply(FireworkExplosionComponent current) {
      Optional var10002 = this.shape;
      Objects.requireNonNull(current);
      FireworkExplosionComponent.Type var3 = (FireworkExplosionComponent.Type)var10002.orElseGet(current::shape);
      Optional var10003 = this.colors;
      Objects.requireNonNull(current);
      IntList var4 = (IntList)var10003.orElseGet(current::colors);
      Optional var10004 = this.fadeColors;
      Objects.requireNonNull(current);
      IntList var5 = (IntList)var10004.orElseGet(current::fadeColors);
      Optional var10005 = this.trail;
      Objects.requireNonNull(current);
      boolean var2 = (Boolean)var10005.orElseGet(current::hasTrail);
      Optional var10006 = this.twinkle;
      Objects.requireNonNull(current);
      return new FireworkExplosionComponent(var3, var4, var5, var2, (Boolean)var10006.orElseGet(current::hasTwinkle));
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.SET_FIREWORK_EXPLOSION;
   }

   static {
      DEFAULT_EXPLOSION = new FireworkExplosionComponent(FireworkExplosionComponent.Type.SMALL_BALL, IntList.of(), IntList.of(), false, false);
   }
}
