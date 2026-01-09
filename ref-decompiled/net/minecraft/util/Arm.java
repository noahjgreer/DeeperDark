package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.function.ValueLists;

public enum Arm implements TranslatableOption, StringIdentifiable {
   LEFT(0, "left", "options.mainHand.left"),
   RIGHT(1, "right", "options.mainHand.right");

   public static final Codec CODEC = StringIdentifiable.createCodec(Arm::values);
   public static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(Arm::getId, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
   private final int id;
   private final String name;
   private final String translationKey;

   private Arm(final int id, final String name, final String translationKey) {
      this.id = id;
      this.name = name;
      this.translationKey = translationKey;
   }

   public Arm getOpposite() {
      return this == LEFT ? RIGHT : LEFT;
   }

   public int getId() {
      return this.id;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static Arm[] method_36606() {
      return new Arm[]{LEFT, RIGHT};
   }
}
