package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record ReplaceDiskEnchantmentEffect(EnchantmentLevelBasedValue radius, EnchantmentLevelBasedValue height, Vec3i offset, Optional predicate, BlockStateProvider blockState, Optional triggerGameEvent) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(EnchantmentLevelBasedValue.CODEC.fieldOf("radius").forGetter(ReplaceDiskEnchantmentEffect::radius), EnchantmentLevelBasedValue.CODEC.fieldOf("height").forGetter(ReplaceDiskEnchantmentEffect::height), Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceDiskEnchantmentEffect::offset), BlockPredicate.BASE_CODEC.optionalFieldOf("predicate").forGetter(ReplaceDiskEnchantmentEffect::predicate), BlockStateProvider.TYPE_CODEC.fieldOf("block_state").forGetter(ReplaceDiskEnchantmentEffect::blockState), GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(ReplaceDiskEnchantmentEffect::triggerGameEvent)).apply(instance, ReplaceDiskEnchantmentEffect::new);
   });

   public ReplaceDiskEnchantmentEffect(EnchantmentLevelBasedValue enchantmentLevelBasedValue, EnchantmentLevelBasedValue enchantmentLevelBasedValue2, Vec3i vec3i, Optional optional, BlockStateProvider blockStateProvider, Optional optional2) {
      this.radius = enchantmentLevelBasedValue;
      this.height = enchantmentLevelBasedValue2;
      this.offset = vec3i;
      this.predicate = optional;
      this.blockState = blockStateProvider;
      this.triggerGameEvent = optional2;
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      BlockPos blockPos = BlockPos.ofFloored(pos).add(this.offset);
      Random random = user.getRandom();
      int i = (int)this.radius.getValue(level);
      int j = (int)this.height.getValue(level);
      Iterator var10 = BlockPos.iterate(blockPos.add(-i, 0, -i), blockPos.add(i, Math.min(j - 1, 0), i)).iterator();

      while(var10.hasNext()) {
         BlockPos blockPos2 = (BlockPos)var10.next();
         if (blockPos2.getSquaredDistanceFromCenter(pos.getX(), (double)blockPos2.getY() + 0.5, pos.getZ()) < (double)MathHelper.square(i) && (Boolean)this.predicate.map((predicate) -> {
            return predicate.test(world, blockPos2);
         }).orElse(true) && world.setBlockState(blockPos2, this.blockState.get(random, blockPos2))) {
            this.triggerGameEvent.ifPresent((gameEvent) -> {
               world.emitGameEvent(user, gameEvent, blockPos2);
            });
         }
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public EnchantmentLevelBasedValue radius() {
      return this.radius;
   }

   public EnchantmentLevelBasedValue height() {
      return this.height;
   }

   public Vec3i offset() {
      return this.offset;
   }

   public Optional predicate() {
      return this.predicate;
   }

   public BlockStateProvider blockState() {
      return this.blockState;
   }

   public Optional triggerGameEvent() {
      return this.triggerGameEvent;
   }
}
