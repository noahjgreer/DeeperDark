package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public interface PlainTextContent extends TextContent {
   MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContent::string)).apply(instance, PlainTextContent::of);
   });
   TextContent.Type TYPE = new TextContent.Type(CODEC, "text");
   PlainTextContent EMPTY = new PlainTextContent() {
      public String toString() {
         return "empty";
      }

      public String string() {
         return "";
      }
   };

   static PlainTextContent of(String string) {
      return (PlainTextContent)(string.isEmpty() ? EMPTY : new Literal(string));
   }

   String string();

   default TextContent.Type getType() {
      return TYPE;
   }

   public static record Literal(String string) implements PlainTextContent {
      public Literal(String string) {
         this.string = string;
      }

      public Optional visit(StringVisitable.Visitor visitor) {
         return visitor.accept(this.string);
      }

      public Optional visit(StringVisitable.StyledVisitor visitor, Style style) {
         return visitor.accept(style, this.string);
      }

      public String toString() {
         return "literal{" + this.string + "}";
      }

      public String string() {
         return this.string;
      }
   }
}
