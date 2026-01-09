package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.PacketCodec;

public abstract class ParticleType {
   private final boolean alwaysShow;

   protected ParticleType(boolean alwaysShow) {
      this.alwaysShow = alwaysShow;
   }

   public boolean shouldAlwaysSpawn() {
      return this.alwaysShow;
   }

   public abstract MapCodec getCodec();

   public abstract PacketCodec getPacketCodec();
}
