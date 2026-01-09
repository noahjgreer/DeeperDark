package net.minecraft.util.math;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Util;

public record EulerAngle(float pitch, float yaw, float roll) {
   final float pitch;
   final float yaw;
   final float roll;
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;

   public EulerAngle(float f, float g, float h) {
      f = !Float.isInfinite(f) && !Float.isNaN(f) ? f % 360.0F : 0.0F;
      g = !Float.isInfinite(g) && !Float.isNaN(g) ? g % 360.0F : 0.0F;
      h = !Float.isInfinite(h) && !Float.isNaN(h) ? h % 360.0F : 0.0F;
      this.pitch = f;
      this.yaw = g;
      this.roll = h;
   }

   public float pitch() {
      return this.pitch;
   }

   public float yaw() {
      return this.yaw;
   }

   public float roll() {
      return this.roll;
   }

   static {
      CODEC = Codec.FLOAT.listOf().comapFlatMap((list) -> {
         return Util.decodeFixedLengthList(list, 3).map((angles) -> {
            return new EulerAngle((Float)angles.get(0), (Float)angles.get(1), (Float)angles.get(2));
         });
      }, (angle) -> {
         return List.of(angle.pitch(), angle.yaw(), angle.roll());
      });
      PACKET_CODEC = new PacketCodec() {
         public EulerAngle decode(ByteBuf byteBuf) {
            return new EulerAngle(byteBuf.readFloat(), byteBuf.readFloat(), byteBuf.readFloat());
         }

         public void encode(ByteBuf byteBuf, EulerAngle eulerAngle) {
            byteBuf.writeFloat(eulerAngle.pitch);
            byteBuf.writeFloat(eulerAngle.yaw);
            byteBuf.writeFloat(eulerAngle.roll);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (EulerAngle)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }
}
