package net.minecraft.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.event.EntityPositionSource;
import net.minecraft.world.event.PositionSource;

public class VibrationParticleEffect implements ParticleEffect {
   private static final Codec POSITION_SOURCE_CODEC;
   public static final MapCodec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private final PositionSource destination;
   private final int arrivalInTicks;

   public VibrationParticleEffect(PositionSource destination, int arrivalInTicks) {
      this.destination = destination;
      this.arrivalInTicks = arrivalInTicks;
   }

   public ParticleType getType() {
      return ParticleTypes.VIBRATION;
   }

   public PositionSource getVibration() {
      return this.destination;
   }

   public int getArrivalInTicks() {
      return this.arrivalInTicks;
   }

   static {
      POSITION_SOURCE_CODEC = PositionSource.CODEC.validate((positionSource) -> {
         return positionSource instanceof EntityPositionSource ? DataResult.error(() -> {
            return "Entity position sources are not allowed";
         }) : DataResult.success(positionSource);
      });
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(POSITION_SOURCE_CODEC.fieldOf("destination").forGetter(VibrationParticleEffect::getVibration), Codec.INT.fieldOf("arrival_in_ticks").forGetter(VibrationParticleEffect::getArrivalInTicks)).apply(instance, VibrationParticleEffect::new);
      });
      PACKET_CODEC = PacketCodec.tuple(PositionSource.PACKET_CODEC, VibrationParticleEffect::getVibration, PacketCodecs.VAR_INT, VibrationParticleEffect::getArrivalInTicks, VibrationParticleEffect::new);
   }
}
