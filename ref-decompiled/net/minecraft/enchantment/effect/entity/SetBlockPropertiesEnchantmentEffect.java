package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.event.GameEvent;

public record SetBlockPropertiesEnchantmentEffect(BlockStateComponent properties, Vec3i offset, Optional triggerGameEvent) implements EnchantmentEntityEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockStateComponent.CODEC.fieldOf("properties").forGetter(SetBlockPropertiesEnchantmentEffect::properties), Vec3i.CODEC.optionalFieldOf("offset", Vec3i.ZERO).forGetter(SetBlockPropertiesEnchantmentEffect::offset), GameEvent.CODEC.optionalFieldOf("trigger_game_event").forGetter(SetBlockPropertiesEnchantmentEffect::triggerGameEvent)).apply(instance, SetBlockPropertiesEnchantmentEffect::new);
   });

   public SetBlockPropertiesEnchantmentEffect(BlockStateComponent properties) {
      this(properties, Vec3i.ZERO, Optional.of(GameEvent.BLOCK_CHANGE));
   }

   public SetBlockPropertiesEnchantmentEffect(BlockStateComponent blockStateComponent, Vec3i vec3i, Optional optional) {
      this.properties = blockStateComponent;
      this.offset = vec3i;
      this.triggerGameEvent = optional;
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
      BlockPos blockPos = BlockPos.ofFloored(pos).add(this.offset);
      BlockState blockState = user.getWorld().getBlockState(blockPos);
      BlockState blockState2 = this.properties.applyToState(blockState);
      if (blockState != blockState2 && user.getWorld().setBlockState(blockPos, blockState2, 3)) {
         this.triggerGameEvent.ifPresent((gameEvent) -> {
            world.emitGameEvent(user, gameEvent, blockPos);
         });
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public BlockStateComponent properties() {
      return this.properties;
   }

   public Vec3i offset() {
      return this.offset;
   }

   public Optional triggerGameEvent() {
      return this.triggerGameEvent;
   }
}
