package net.minecraft.item.consume;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public record TeleportRandomlyConsumeEffect(float diameter) implements ConsumeEffect {
   private static final float DEFAULT_DIAMETER = 16.0F;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.POSITIVE_FLOAT.optionalFieldOf("diameter", 16.0F).forGetter(TeleportRandomlyConsumeEffect::diameter)).apply(instance, TeleportRandomlyConsumeEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public TeleportRandomlyConsumeEffect() {
      this(16.0F);
   }

   public TeleportRandomlyConsumeEffect(float f) {
      this.diameter = f;
   }

   public ConsumeEffect.Type getType() {
      return ConsumeEffect.Type.TELEPORT_RANDOMLY;
   }

   public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
      boolean bl = false;

      for(int i = 0; i < 16; ++i) {
         double d = user.getX() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter;
         double e = MathHelper.clamp(user.getY() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter, (double)world.getBottomY(), (double)(world.getBottomY() + ((ServerWorld)world).getLogicalHeight() - 1));
         double f = user.getZ() + (user.getRandom().nextDouble() - 0.5) * (double)this.diameter;
         if (user.hasVehicle()) {
            user.stopRiding();
         }

         Vec3d vec3d = user.getPos();
         if (user.teleport(d, e, f, true)) {
            world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of((Entity)user));
            SoundCategory soundCategory;
            SoundEvent soundEvent;
            if (user instanceof FoxEntity) {
               soundEvent = SoundEvents.ENTITY_FOX_TELEPORT;
               soundCategory = SoundCategory.NEUTRAL;
            } else {
               soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
               soundCategory = SoundCategory.PLAYERS;
            }

            world.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
            user.onLanding();
            bl = true;
            break;
         }
      }

      if (bl && user instanceof PlayerEntity playerEntity) {
         playerEntity.clearCurrentExplosion();
      }

      return bl;
   }

   public float diameter() {
      return this.diameter;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, TeleportRandomlyConsumeEffect::diameter, TeleportRandomlyConsumeEffect::new);
   }
}
