package net.minecraft.client.option;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TranslatableOption;

@Environment(EnvType.CLIENT)
public enum InactivityFpsLimit implements TranslatableOption, StringIdentifiable {
   MINIMIZED(0, "minimized", "options.inactivityFpsLimit.minimized"),
   AFK(1, "afk", "options.inactivityFpsLimit.afk");

   public static final Codec Codec = StringIdentifiable.createCodec(InactivityFpsLimit::values);
   private final int ordinal;
   private final String name;
   private final String translationKey;

   private InactivityFpsLimit(final int ordinal, final String name, final String translationKey) {
      this.ordinal = ordinal;
      this.name = name;
      this.translationKey = translationKey;
   }

   public int getId() {
      return this.ordinal;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static InactivityFpsLimit[] method_61961() {
      return new InactivityFpsLimit[]{MINIMIZED, AFK};
   }
}
