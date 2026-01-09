package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Style {
   public static final Style EMPTY = new Style((TextColor)null, (Integer)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (ClickEvent)null, (HoverEvent)null, (String)null, (Identifier)null);
   public static final Identifier DEFAULT_FONT_ID = Identifier.ofVanilla("default");
   @Nullable
   final TextColor color;
   @Nullable
   final Integer shadowColor;
   @Nullable
   final Boolean bold;
   @Nullable
   final Boolean italic;
   @Nullable
   final Boolean underlined;
   @Nullable
   final Boolean strikethrough;
   @Nullable
   final Boolean obfuscated;
   @Nullable
   final ClickEvent clickEvent;
   @Nullable
   final HoverEvent hoverEvent;
   @Nullable
   final String insertion;
   @Nullable
   final Identifier font;

   private static Style of(Optional color, Optional shadowColor, Optional bold, Optional italic, Optional underlined, Optional strikethrough, Optional obfuscated, Optional clickEvent, Optional hoverEvent, Optional insertion, Optional font) {
      Style style = new Style((TextColor)color.orElse((Object)null), (Integer)shadowColor.orElse((Object)null), (Boolean)bold.orElse((Object)null), (Boolean)italic.orElse((Object)null), (Boolean)underlined.orElse((Object)null), (Boolean)strikethrough.orElse((Object)null), (Boolean)obfuscated.orElse((Object)null), (ClickEvent)clickEvent.orElse((Object)null), (HoverEvent)hoverEvent.orElse((Object)null), (String)insertion.orElse((Object)null), (Identifier)font.orElse((Object)null));
      return style.equals(EMPTY) ? EMPTY : style;
   }

   private Style(@Nullable TextColor color, @Nullable Integer shadowColor, @Nullable Boolean bold, @Nullable Boolean italic, @Nullable Boolean underlined, @Nullable Boolean strikethrough, @Nullable Boolean obfuscated, @Nullable ClickEvent clickEvent, @Nullable HoverEvent hoverEvent, @Nullable String insertion, @Nullable Identifier font) {
      this.color = color;
      this.shadowColor = shadowColor;
      this.bold = bold;
      this.italic = italic;
      this.underlined = underlined;
      this.strikethrough = strikethrough;
      this.obfuscated = obfuscated;
      this.clickEvent = clickEvent;
      this.hoverEvent = hoverEvent;
      this.insertion = insertion;
      this.font = font;
   }

   @Nullable
   public TextColor getColor() {
      return this.color;
   }

   @Nullable
   public Integer getShadowColor() {
      return this.shadowColor;
   }

   public boolean isBold() {
      return this.bold == Boolean.TRUE;
   }

   public boolean isItalic() {
      return this.italic == Boolean.TRUE;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == Boolean.TRUE;
   }

   public boolean isUnderlined() {
      return this.underlined == Boolean.TRUE;
   }

   public boolean isObfuscated() {
      return this.obfuscated == Boolean.TRUE;
   }

   public boolean isEmpty() {
      return this == EMPTY;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion;
   }

   public Identifier getFont() {
      return this.font != null ? this.font : DEFAULT_FONT_ID;
   }

   private static Style with(Style newStyle, @Nullable Object oldAttribute, @Nullable Object newAttribute) {
      return oldAttribute != null && newAttribute == null && newStyle.equals(EMPTY) ? EMPTY : newStyle;
   }

   public Style withColor(@Nullable TextColor color) {
      return Objects.equals(this.color, color) ? this : with(new Style(color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.color, color);
   }

   public Style withColor(@Nullable Formatting color) {
      return this.withColor(color != null ? TextColor.fromFormatting(color) : null);
   }

   public Style withColor(int rgbColor) {
      return this.withColor(TextColor.fromRgb(rgbColor));
   }

   public Style withShadowColor(int shadowColor) {
      return with(new Style(this.color, shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.shadowColor, shadowColor);
   }

   public Style withBold(@Nullable Boolean bold) {
      return Objects.equals(this.bold, bold) ? this : with(new Style(this.color, this.shadowColor, bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.bold, bold);
   }

   public Style withItalic(@Nullable Boolean italic) {
      return Objects.equals(this.italic, italic) ? this : with(new Style(this.color, this.shadowColor, this.bold, italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.italic, italic);
   }

   public Style withUnderline(@Nullable Boolean underline) {
      return Objects.equals(this.underlined, underline) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, underline, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.underlined, underline);
   }

   public Style withStrikethrough(@Nullable Boolean strikethrough) {
      return Objects.equals(this.strikethrough, strikethrough) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, this.underlined, strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.strikethrough, strikethrough);
   }

   public Style withObfuscated(@Nullable Boolean obfuscated) {
      return Objects.equals(this.obfuscated, obfuscated) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font), this.obfuscated, obfuscated);
   }

   public Style withClickEvent(@Nullable ClickEvent clickEvent) {
      return Objects.equals(this.clickEvent, clickEvent) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, clickEvent, this.hoverEvent, this.insertion, this.font), this.clickEvent, clickEvent);
   }

   public Style withHoverEvent(@Nullable HoverEvent hoverEvent) {
      return Objects.equals(this.hoverEvent, hoverEvent) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, hoverEvent, this.insertion, this.font), this.hoverEvent, hoverEvent);
   }

   public Style withInsertion(@Nullable String insertion) {
      return Objects.equals(this.insertion, insertion) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, insertion, this.font), this.insertion, insertion);
   }

   public Style withFont(@Nullable Identifier font) {
      return Objects.equals(this.font, font) ? this : with(new Style(this.color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, font), this.font, font);
   }

   public Style withFormatting(Formatting formatting) {
      TextColor textColor = this.color;
      Boolean boolean_ = this.bold;
      Boolean boolean2 = this.italic;
      Boolean boolean3 = this.strikethrough;
      Boolean boolean4 = this.underlined;
      Boolean boolean5 = this.obfuscated;
      switch (formatting) {
         case OBFUSCATED:
            boolean5 = true;
            break;
         case BOLD:
            boolean_ = true;
            break;
         case STRIKETHROUGH:
            boolean3 = true;
            break;
         case UNDERLINE:
            boolean4 = true;
            break;
         case ITALIC:
            boolean2 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            textColor = TextColor.fromFormatting(formatting);
      }

      return new Style(textColor, this.shadowColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withExclusiveFormatting(Formatting formatting) {
      TextColor textColor = this.color;
      Boolean boolean_ = this.bold;
      Boolean boolean2 = this.italic;
      Boolean boolean3 = this.strikethrough;
      Boolean boolean4 = this.underlined;
      Boolean boolean5 = this.obfuscated;
      switch (formatting) {
         case OBFUSCATED:
            boolean5 = true;
            break;
         case BOLD:
            boolean_ = true;
            break;
         case STRIKETHROUGH:
            boolean3 = true;
            break;
         case UNDERLINE:
            boolean4 = true;
            break;
         case ITALIC:
            boolean2 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            boolean5 = false;
            boolean_ = false;
            boolean3 = false;
            boolean4 = false;
            boolean2 = false;
            textColor = TextColor.fromFormatting(formatting);
      }

      return new Style(textColor, this.shadowColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withFormatting(Formatting... formattings) {
      TextColor textColor = this.color;
      Boolean boolean_ = this.bold;
      Boolean boolean2 = this.italic;
      Boolean boolean3 = this.strikethrough;
      Boolean boolean4 = this.underlined;
      Boolean boolean5 = this.obfuscated;
      Formatting[] var8 = formattings;
      int var9 = formattings.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         Formatting formatting = var8[var10];
         switch (formatting) {
            case OBFUSCATED:
               boolean5 = true;
               break;
            case BOLD:
               boolean_ = true;
               break;
            case STRIKETHROUGH:
               boolean3 = true;
               break;
            case UNDERLINE:
               boolean4 = true;
               break;
            case ITALIC:
               boolean2 = true;
               break;
            case RESET:
               return EMPTY;
            default:
               textColor = TextColor.fromFormatting(formatting);
         }
      }

      return new Style(textColor, this.shadowColor, boolean_, boolean2, boolean4, boolean3, boolean5, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withParent(Style parent) {
      if (this == EMPTY) {
         return parent;
      } else {
         return parent == EMPTY ? this : new Style(this.color != null ? this.color : parent.color, this.shadowColor != null ? this.shadowColor : parent.shadowColor, this.bold != null ? this.bold : parent.bold, this.italic != null ? this.italic : parent.italic, this.underlined != null ? this.underlined : parent.underlined, this.strikethrough != null ? this.strikethrough : parent.strikethrough, this.obfuscated != null ? this.obfuscated : parent.obfuscated, this.clickEvent != null ? this.clickEvent : parent.clickEvent, this.hoverEvent != null ? this.hoverEvent : parent.hoverEvent, this.insertion != null ? this.insertion : parent.insertion, this.font != null ? this.font : parent.font);
      }
   }

   public String toString() {
      final StringBuilder stringBuilder = new StringBuilder("{");

      class Writer {
         private boolean shouldAppendComma;

         Writer(final Style style) {
         }

         private void appendComma() {
            if (this.shouldAppendComma) {
               stringBuilder.append(',');
            }

            this.shouldAppendComma = true;
         }

         void append(String key, @Nullable Boolean value) {
            if (value != null) {
               this.appendComma();
               if (!value) {
                  stringBuilder.append('!');
               }

               stringBuilder.append(key);
            }

         }

         void append(String key, @Nullable Object value) {
            if (value != null) {
               this.appendComma();
               stringBuilder.append(key);
               stringBuilder.append('=');
               stringBuilder.append(value);
            }

         }
      }

      Writer writer = new Writer(this);
      writer.append("color", (Object)this.color);
      writer.append("shadowColor", (Object)this.shadowColor);
      writer.append("bold", this.bold);
      writer.append("italic", this.italic);
      writer.append("underlined", this.underlined);
      writer.append("strikethrough", this.strikethrough);
      writer.append("obfuscated", this.obfuscated);
      writer.append("clickEvent", (Object)this.clickEvent);
      writer.append("hoverEvent", (Object)this.hoverEvent);
      writer.append("insertion", (Object)this.insertion);
      writer.append("font", (Object)this.font);
      stringBuilder.append("}");
      return stringBuilder.toString();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Style)) {
         return false;
      } else {
         Style style = (Style)o;
         return this.bold == style.bold && Objects.equals(this.getColor(), style.getColor()) && Objects.equals(this.getShadowColor(), style.getShadowColor()) && this.italic == style.italic && this.obfuscated == style.obfuscated && this.strikethrough == style.strikethrough && this.underlined == style.underlined && Objects.equals(this.clickEvent, style.clickEvent) && Objects.equals(this.hoverEvent, style.hoverEvent) && Objects.equals(this.insertion, style.insertion) && Objects.equals(this.font, style.font);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.color, this.shadowColor, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion});
   }

   public static class Codecs {
      public static final MapCodec MAP_CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(TextColor.CODEC.optionalFieldOf("color").forGetter((style) -> {
            return Optional.ofNullable(style.color);
         }), net.minecraft.util.dynamic.Codecs.ARGB.optionalFieldOf("shadow_color").forGetter((style) -> {
            return Optional.ofNullable(style.shadowColor);
         }), Codec.BOOL.optionalFieldOf("bold").forGetter((style) -> {
            return Optional.ofNullable(style.bold);
         }), Codec.BOOL.optionalFieldOf("italic").forGetter((style) -> {
            return Optional.ofNullable(style.italic);
         }), Codec.BOOL.optionalFieldOf("underlined").forGetter((style) -> {
            return Optional.ofNullable(style.underlined);
         }), Codec.BOOL.optionalFieldOf("strikethrough").forGetter((style) -> {
            return Optional.ofNullable(style.strikethrough);
         }), Codec.BOOL.optionalFieldOf("obfuscated").forGetter((style) -> {
            return Optional.ofNullable(style.obfuscated);
         }), ClickEvent.CODEC.optionalFieldOf("click_event").forGetter((style) -> {
            return Optional.ofNullable(style.clickEvent);
         }), HoverEvent.CODEC.optionalFieldOf("hover_event").forGetter((style) -> {
            return Optional.ofNullable(style.hoverEvent);
         }), Codec.STRING.optionalFieldOf("insertion").forGetter((style) -> {
            return Optional.ofNullable(style.insertion);
         }), Identifier.CODEC.optionalFieldOf("font").forGetter((style) -> {
            return Optional.ofNullable(style.font);
         })).apply(instance, Style::of);
      });
      public static final Codec CODEC;
      public static final PacketCodec PACKET_CODEC;

      static {
         CODEC = MAP_CODEC.codec();
         PACKET_CODEC = PacketCodecs.unlimitedRegistryCodec(CODEC);
      }
   }
}
