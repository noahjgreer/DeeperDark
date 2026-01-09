package net.minecraft.text;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public final class TextColor {
   private static final String RGB_PREFIX = "#";
   public static final Codec CODEC;
   private static final Map FORMATTING_TO_COLOR;
   private static final Map BY_NAME;
   private final int rgb;
   @Nullable
   private final String name;

   private TextColor(int rgb, String name) {
      this.rgb = rgb & 16777215;
      this.name = name;
   }

   private TextColor(int rgb) {
      this.rgb = rgb & 16777215;
      this.name = null;
   }

   public int getRgb() {
      return this.rgb;
   }

   public String getName() {
      return this.name != null ? this.name : this.getHexCode();
   }

   public final String getHexCode() {
      return String.format(Locale.ROOT, "#%06X", this.rgb);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TextColor textColor = (TextColor)o;
         return this.rgb == textColor.rgb;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.rgb, this.name});
   }

   public String toString() {
      return this.getName();
   }

   @Nullable
   public static TextColor fromFormatting(Formatting formatting) {
      return (TextColor)FORMATTING_TO_COLOR.get(formatting);
   }

   public static TextColor fromRgb(int rgb) {
      return new TextColor(rgb);
   }

   public static DataResult parse(String name) {
      if (name.startsWith("#")) {
         try {
            int i = Integer.parseInt(name.substring(1), 16);
            return i >= 0 && i <= 16777215 ? DataResult.success(fromRgb(i), Lifecycle.stable()) : DataResult.error(() -> {
               return "Color value out of range: " + name;
            });
         } catch (NumberFormatException var2) {
            return DataResult.error(() -> {
               return "Invalid color value: " + name;
            });
         }
      } else {
         TextColor textColor = (TextColor)BY_NAME.get(name);
         return textColor == null ? DataResult.error(() -> {
            return "Invalid color name: " + name;
         }) : DataResult.success(textColor, Lifecycle.stable());
      }
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(TextColor::parse, TextColor::getName);
      FORMATTING_TO_COLOR = (Map)Stream.of(Formatting.values()).filter(Formatting::isColor).collect(ImmutableMap.toImmutableMap(Function.identity(), (formatting) -> {
         return new TextColor(formatting.getColorValue(), formatting.getName());
      }));
      BY_NAME = (Map)FORMATTING_TO_COLOR.values().stream().collect(ImmutableMap.toImmutableMap((textColor) -> {
         return textColor.name;
      }, Function.identity()));
   }
}
