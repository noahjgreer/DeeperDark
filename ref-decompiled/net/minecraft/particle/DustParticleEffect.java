package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3f;

public class DustParticleEffect extends AbstractDustParticleEffect {
   public static final int RED = 16711680;
   public static final DustParticleEffect DEFAULT = new DustParticleEffect(16711680, 1.0F);
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codecs.RGB.fieldOf("color").forGetter((particle) -> {
         return particle.color;
      }), SCALE_CODEC.fieldOf("scale").forGetter(AbstractDustParticleEffect::getScale)).apply(instance, DustParticleEffect::new);
   });
   public static final PacketCodec PACKET_CODEC;
   private final int color;

   public DustParticleEffect(int color, float scale) {
      super(scale);
      this.color = color;
   }

   public ParticleType getType() {
      return ParticleTypes.DUST;
   }

   public Vector3f getColor() {
      return ColorHelper.toVector(this.color);
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, (particle) -> {
         return particle.color;
      }, PacketCodecs.FLOAT, AbstractDustParticleEffect::getScale, DustParticleEffect::new);
   }
}
