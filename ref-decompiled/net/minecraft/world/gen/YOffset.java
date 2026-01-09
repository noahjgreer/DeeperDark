package net.minecraft.world.gen;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.world.dimension.DimensionType;

public interface YOffset {
   Codec OFFSET_CODEC = Codec.xor(YOffset.Fixed.CODEC, Codec.xor(YOffset.AboveBottom.CODEC, YOffset.BelowTop.CODEC)).xmap(YOffset::fromEither, YOffset::map);
   YOffset BOTTOM = aboveBottom(0);
   YOffset TOP = belowTop(0);

   static YOffset fixed(int offset) {
      return new Fixed(offset);
   }

   static YOffset aboveBottom(int offset) {
      return new AboveBottom(offset);
   }

   static YOffset belowTop(int offset) {
      return new BelowTop(offset);
   }

   static YOffset getBottom() {
      return BOTTOM;
   }

   static YOffset getTop() {
      return TOP;
   }

   private static YOffset fromEither(Either either) {
      return (YOffset)either.map(Function.identity(), Either::unwrap);
   }

   private static Either map(YOffset yOffset) {
      return yOffset instanceof Fixed ? Either.left((Fixed)yOffset) : Either.right(yOffset instanceof AboveBottom ? Either.left((AboveBottom)yOffset) : Either.right((BelowTop)yOffset));
   }

   int getY(HeightContext context);

   public static record Fixed(int y) implements YOffset {
      public static final Codec CODEC;

      public Fixed(int i) {
         this.y = i;
      }

      public int getY(HeightContext context) {
         return this.y;
      }

      public String toString() {
         return this.y + " absolute";
      }

      public int y() {
         return this.y;
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("absolute").xmap(Fixed::new, Fixed::y).codec();
      }
   }

   public static record AboveBottom(int offset) implements YOffset {
      public static final Codec CODEC;

      public AboveBottom(int i) {
         this.offset = i;
      }

      public int getY(HeightContext context) {
         return context.getMinY() + this.offset;
      }

      public String toString() {
         return this.offset + " above bottom";
      }

      public int offset() {
         return this.offset;
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("above_bottom").xmap(AboveBottom::new, AboveBottom::offset).codec();
      }
   }

   public static record BelowTop(int offset) implements YOffset {
      public static final Codec CODEC;

      public BelowTop(int i) {
         this.offset = i;
      }

      public int getY(HeightContext context) {
         return context.getHeight() - 1 + context.getMinY() - this.offset;
      }

      public String toString() {
         return this.offset + " below top";
      }

      public int offset() {
         return this.offset;
      }

      static {
         CODEC = Codec.intRange(DimensionType.MIN_HEIGHT, DimensionType.MAX_COLUMN_HEIGHT).fieldOf("below_top").xmap(BelowTop::new, BelowTop::offset).codec();
      }
   }
}
