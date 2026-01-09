package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record ReplaceBlockEnchantmentEffect(Vec3i offset, Optional predicate, BlockStateProvider blockState, Optional triggerGameEvent) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(ReplaceBlockEnchantmentEffect::offset), BlockPredicate.BASE_CODEC.optionalFieldOf("predicate").forGetter(ReplaceBlockEnchantmentEffect::predicate), BlockStateProvider.TYPE_CODEC.fieldOf("block_state").forGetter(ReplaceBlockEnchantmentEffect::blockState), GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(ReplaceBlockEnchantmentEffect::triggerGameEvent)).apply(instance, ReplaceBlockEnchantmentEffect::new);
   });

   public ReplaceBlockEnchantmentEffect(Vec3i vec3i, Optional optional, BlockStateProvider blockStateProvider, Optional optional2) {
      this.offset = vec3i;
      this.predicate = optional;
      this.blockState = blockStateProvider;
      this.triggerGameEvent = optional2;
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      BlockPos blockPos = BlockPos.ofFloored(pos).add(this.offset);
      if ((Boolean)this.predicate.map((predicate) -> {
         return predicate.test(world, blockPos);
      }).orElse(true) && world.setBlockState(blockPos, this.blockState.get(user.getRandom(), blockPos))) {
         this.triggerGameEvent.ifPresent((gameEvent) -> {
            world.emitGameEvent(user, gameEvent, blockPos);
         });
      }

   }

   public MapCodec getCodec() {
      return CODEC;
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
