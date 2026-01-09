package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;

public class SimpleParticleType extends ParticleType implements ParticleEffect {
   private final MapCodec codec = MapCodec.unit(this::getType);
   private final PacketCodec packetCodec = PacketCodec.unit(this);

   protected SimpleParticleType(boolean alwaysShow) {
      super(alwaysShow);
   }

   public SimpleParticleType getType() {
      return this;
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   public PacketCodec getPacketCodec() {
      return this.packetCodec;
   }

   // $FF: synthetic method
   public ParticleType getType() {
      return this.getType();
   }
}
