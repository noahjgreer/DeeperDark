package net.minecraft.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public interface AllOfEnchantmentEffects {
   static MapCodec buildCodec(Codec baseCodec, Function fromList, Function toList) {
      return RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(baseCodec.listOf().fieldOf("effects").forGetter(toList)).apply(instance, fromList);
      });
   }

   static EntityEffects allOf(EnchantmentEntityEffect... entityEffects) {
      return new EntityEffects(List.of(entityEffects));
   }

   static LocationBasedEffects allOf(EnchantmentLocationBasedEffect... locationBasedEffects) {
      return new LocationBasedEffects(List.of(locationBasedEffects));
   }

   static ValueEffects allOf(EnchantmentValueEffect... valueEffects) {
      return new ValueEffects(List.of(valueEffects));
   }

   public static record EntityEffects(List effects) implements EnchantmentEntityEffect {
      public static final MapCodec CODEC;

      public EntityEffects(List list) {
         this.effects = list;
      }

      public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
         Iterator var6 = this.effects.iterator();

         while(var6.hasNext()) {
            EnchantmentEntityEffect enchantmentEntityEffect = (EnchantmentEntityEffect)var6.next();
            enchantmentEntityEffect.apply(world, level, context, user, pos);
         }

      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public List effects() {
         return this.effects;
      }

      static {
         CODEC = AllOfEnchantmentEffects.buildCodec(EnchantmentEntityEffect.CODEC, EntityEffects::new, EntityEffects::effects);
      }
   }

   public static record LocationBasedEffects(List effects) implements EnchantmentLocationBasedEffect {
      public static final MapCodec CODEC;

      public LocationBasedEffects(List list) {
         this.effects = list;
      }

      public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
         Iterator var7 = this.effects.iterator();

         while(var7.hasNext()) {
            EnchantmentLocationBasedEffect enchantmentLocationBasedEffect = (EnchantmentLocationBasedEffect)var7.next();
            enchantmentLocationBasedEffect.apply(world, level, context, user, pos, newlyApplied);
         }

      }

      public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
         Iterator var5 = this.effects.iterator();

         while(var5.hasNext()) {
            EnchantmentLocationBasedEffect enchantmentLocationBasedEffect = (EnchantmentLocationBasedEffect)var5.next();
            enchantmentLocationBasedEffect.remove(context, user, pos, level);
         }

      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public List effects() {
         return this.effects;
      }

      static {
         CODEC = AllOfEnchantmentEffects.buildCodec(EnchantmentLocationBasedEffect.CODEC, LocationBasedEffects::new, LocationBasedEffects::effects);
      }
   }

   public static record ValueEffects(List effects) implements EnchantmentValueEffect {
      public static final MapCodec CODEC;

      public ValueEffects(List list) {
         this.effects = list;
      }

      public float apply(int level, Random random, float inputValue) {
         EnchantmentValueEffect enchantmentValueEffect;
         for(Iterator var4 = this.effects.iterator(); var4.hasNext(); inputValue = enchantmentValueEffect.apply(level, random, inputValue)) {
            enchantmentValueEffect = (EnchantmentValueEffect)var4.next();
         }

         return inputValue;
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public List effects() {
         return this.effects;
      }

      static {
         CODEC = AllOfEnchantmentEffects.buildCodec(EnchantmentValueEffect.CODEC, ValueEffects::new, ValueEffects::effects);
      }
   }
}
