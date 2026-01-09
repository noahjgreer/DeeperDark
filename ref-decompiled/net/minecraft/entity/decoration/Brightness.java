package net.minecraft.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record Brightness(int block, int sky) {
   public static final Codec LIGHT_LEVEL_CODEC = Codecs.rangedInt(0, 15);
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(LIGHT_LEVEL_CODEC.fieldOf("block").forGetter(Brightness::block), LIGHT_LEVEL_CODEC.fieldOf("sky").forGetter(Brightness::sky)).apply(instance, Brightness::new);
   });
   public static final Brightness FULL = new Brightness(15, 15);

   public Brightness(int i, int j) {
      this.block = i;
      this.sky = j;
   }

   public static int pack(int block, int sky) {
      return block << 4 | sky << 20;
   }

   public int pack() {
      return pack(this.block, this.sky);
   }

   public static int unpackBlock(int packed) {
      return packed >> 4 & '\uffff';
   }

   public static int unpackSky(int packed) {
      return packed >> 20 & '\uffff';
   }

   public static Brightness unpack(int packed) {
      return new Brightness(unpackBlock(packed), unpackSky(packed));
   }

   public int block() {
      return this.block;
   }

   public int sky() {
      return this.sky;
   }
}
