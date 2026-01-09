package net.minecraft.entity.attribute;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Formatting;

public class EntityAttribute {
   public static final Codec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private final double fallback;
   private boolean tracked;
   private final String translationKey;
   private Category category;

   protected EntityAttribute(String translationKey, double fallback) {
      this.category = EntityAttribute.Category.POSITIVE;
      this.fallback = fallback;
      this.translationKey = translationKey;
   }

   public double getDefaultValue() {
      return this.fallback;
   }

   public boolean isTracked() {
      return this.tracked;
   }

   public EntityAttribute setTracked(boolean tracked) {
      this.tracked = tracked;
      return this;
   }

   public EntityAttribute setCategory(Category category) {
      this.category = category;
      return this;
   }

   public double clamp(double value) {
      return value;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }

   public Formatting getFormatting(boolean addition) {
      return this.category.getFormatting(addition);
   }

   static {
      CODEC = Registries.ATTRIBUTE.getEntryCodec();
      PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ATTRIBUTE);
   }

   public static enum Category {
      POSITIVE,
      NEUTRAL,
      NEGATIVE;

      public Formatting getFormatting(boolean addition) {
         Formatting var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = addition ? Formatting.BLUE : Formatting.RED;
               break;
            case 1:
               var10000 = Formatting.GRAY;
               break;
            case 2:
               var10000 = addition ? Formatting.RED : Formatting.BLUE;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Category[] method_60495() {
         return new Category[]{POSITIVE, NEUTRAL, NEGATIVE};
      }
   }
}
