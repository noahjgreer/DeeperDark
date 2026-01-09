package net.minecraft.network.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Function11;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import com.mojang.datafixers.util.Function9;
import io.netty.buffer.ByteBuf;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface PacketCodec extends PacketDecoder, PacketEncoder {
   static PacketCodec ofStatic(final PacketEncoder encoder, final PacketDecoder decoder) {
      return new PacketCodec() {
         public Object decode(Object object) {
            return decoder.decode(object);
         }

         public void encode(Object object, Object object2) {
            encoder.encode(object, object2);
         }
      };
   }

   static PacketCodec of(final ValueFirstEncoder encoder, final PacketDecoder decoder) {
      return new PacketCodec() {
         public Object decode(Object object) {
            return decoder.decode(object);
         }

         public void encode(Object object, Object object2) {
            encoder.encode(object2, object);
         }
      };
   }

   static PacketCodec unit(final Object value) {
      return new PacketCodec() {
         public Object decode(Object object) {
            return value;
         }

         public void encode(Object object, Object object2) {
            if (!object2.equals(value)) {
               String var10002 = String.valueOf(object2);
               throw new IllegalStateException("Can't encode '" + var10002 + "', expected '" + String.valueOf(value) + "'");
            }
         }
      };
   }

   default PacketCodec collect(ResultFunction function) {
      return function.apply(this);
   }

   default PacketCodec xmap(final Function to, final Function from) {
      return new PacketCodec() {
         public Object decode(Object object) {
            return to.apply(PacketCodec.this.decode(object));
         }

         public void encode(Object object, Object object2) {
            PacketCodec.this.encode(object, from.apply(object2));
         }
      };
   }

   default PacketCodec mapBuf(final Function function) {
      return new PacketCodec() {
         public Object decode(ByteBuf byteBuf) {
            Object object = function.apply(byteBuf);
            return PacketCodec.this.decode(object);
         }

         public void encode(ByteBuf byteBuf, Object object) {
            Object object2 = function.apply(byteBuf);
            PacketCodec.this.encode(object2, object);
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   default PacketCodec dispatch(final Function type, final Function codec) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = PacketCodec.this.decode(object);
            PacketCodec packetCodec = (PacketCodec)codec.apply(object2);
            return packetCodec.decode(object);
         }

         public void encode(Object object, Object object2) {
            Object object3 = type.apply(object2);
            PacketCodec packetCodec = (PacketCodec)codec.apply(object3);
            PacketCodec.this.encode(object, object3);
            packetCodec.encode(object, object2);
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec, final Function from, final Function to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec.decode(object);
            return to.apply(object2);
         }

         public void encode(Object object, Object object2) {
            codec.encode(object, from.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final BiFunction to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            return to.apply(object2, object3);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final Function3 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            return to.apply(object2, object3, object4);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final Function4 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            return to.apply(object2, object3, object4, object5);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final PacketCodec codec5, final Function from5, final Function5 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            Object object6 = codec5.decode(object);
            return to.apply(object2, object3, object4, object5, object6);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
            codec5.encode(object, from5.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final PacketCodec codec5, final Function from5, final PacketCodec codec6, final Function from6, final Function6 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            Object object6 = codec5.decode(object);
            Object object7 = codec6.decode(object);
            return to.apply(object2, object3, object4, object5, object6, object7);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
            codec5.encode(object, from5.apply(object2));
            codec6.encode(object, from6.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final PacketCodec codec5, final Function from5, final PacketCodec codec6, final Function from6, final PacketCodec codec7, final Function from7, final Function7 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            Object object6 = codec5.decode(object);
            Object object7 = codec6.decode(object);
            Object object8 = codec7.decode(object);
            return to.apply(object2, object3, object4, object5, object6, object7, object8);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
            codec5.encode(object, from5.apply(object2));
            codec6.encode(object, from6.apply(object2));
            codec7.encode(object, from7.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final PacketCodec codec5, final Function from5, final PacketCodec codec6, final Function from6, final PacketCodec codec7, final Function from7, final PacketCodec codec8, final Function from8, final Function8 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            Object object6 = codec5.decode(object);
            Object object7 = codec6.decode(object);
            Object object8 = codec7.decode(object);
            Object object9 = codec8.decode(object);
            return to.apply(object2, object3, object4, object5, object6, object7, object8, object9);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
            codec5.encode(object, from5.apply(object2));
            codec6.encode(object, from6.apply(object2));
            codec7.encode(object, from7.apply(object2));
            codec8.encode(object, from8.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final PacketCodec codec5, final Function from5, final PacketCodec codec6, final Function from6, final PacketCodec codec7, final Function from7, final PacketCodec codec8, final Function from8, final PacketCodec codec9, final Function from9, final Function9 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            Object object6 = codec5.decode(object);
            Object object7 = codec6.decode(object);
            Object object8 = codec7.decode(object);
            Object object9 = codec8.decode(object);
            Object object10 = codec9.decode(object);
            return to.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
            codec5.encode(object, from5.apply(object2));
            codec6.encode(object, from6.apply(object2));
            codec7.encode(object, from7.apply(object2));
            codec8.encode(object, from8.apply(object2));
            codec9.encode(object, from9.apply(object2));
         }
      };
   }

   static PacketCodec tuple(final PacketCodec codec1, final Function from1, final PacketCodec codec2, final Function from2, final PacketCodec codec3, final Function from3, final PacketCodec codec4, final Function from4, final PacketCodec codec5, final Function from5, final PacketCodec codec6, final Function from6, final PacketCodec codec7, final Function from7, final PacketCodec codec8, final Function from8, final PacketCodec codec9, final Function from9, final PacketCodec codec10, final Function from10, final PacketCodec codec11, final Function from11, final Function11 to) {
      return new PacketCodec() {
         public Object decode(Object object) {
            Object object2 = codec1.decode(object);
            Object object3 = codec2.decode(object);
            Object object4 = codec3.decode(object);
            Object object5 = codec4.decode(object);
            Object object6 = codec5.decode(object);
            Object object7 = codec6.decode(object);
            Object object8 = codec7.decode(object);
            Object object9 = codec8.decode(object);
            Object object10 = codec9.decode(object);
            Object object11 = codec10.decode(object);
            Object object12 = codec11.decode(object);
            return to.apply(object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12);
         }

         public void encode(Object object, Object object2) {
            codec1.encode(object, from1.apply(object2));
            codec2.encode(object, from2.apply(object2));
            codec3.encode(object, from3.apply(object2));
            codec4.encode(object, from4.apply(object2));
            codec5.encode(object, from5.apply(object2));
            codec6.encode(object, from6.apply(object2));
            codec7.encode(object, from7.apply(object2));
            codec8.encode(object, from8.apply(object2));
            codec9.encode(object, from9.apply(object2));
            codec10.encode(object, from10.apply(object2));
            codec11.encode(object, from11.apply(object2));
         }
      };
   }

   static PacketCodec recursive(final UnaryOperator codecGetter) {
      return new PacketCodec() {
         private final Supplier codecSupplier = Suppliers.memoize(() -> {
            return (PacketCodec)codecGetter.apply(this);
         });

         public Object decode(Object object) {
            return ((PacketCodec)this.codecSupplier.get()).decode(object);
         }

         public void encode(Object object, Object object2) {
            ((PacketCodec)this.codecSupplier.get()).encode(object, object2);
         }
      };
   }

   default PacketCodec cast() {
      return this;
   }

   @FunctionalInterface
   public interface ResultFunction {
      PacketCodec apply(PacketCodec codec);
   }
}
