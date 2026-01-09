package net.minecraft.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class KeybindTextContent implements TextContent {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.STRING.fieldOf("keybind").forGetter((content) -> {
         return content.key;
      })).apply(instance, KeybindTextContent::new);
   });
   public static final TextContent.Type TYPE;
   private final String key;
   @Nullable
   private Supplier translated;

   public KeybindTextContent(String key) {
      this.key = key;
   }

   private Text getTranslated() {
      if (this.translated == null) {
         this.translated = (Supplier)KeybindTranslations.factory.apply(this.key);
      }

      return (Text)this.translated.get();
   }

   public Optional visit(StringVisitable.Visitor visitor) {
      return this.getTranslated().visit(visitor);
   }

   public Optional visit(StringVisitable.StyledVisitor visitor, Style style) {
      return this.getTranslated().visit(visitor, style);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof KeybindTextContent) {
            KeybindTextContent keybindTextContent = (KeybindTextContent)o;
            if (this.key.equals(keybindTextContent.key)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.key.hashCode();
   }

   public String toString() {
      return "keybind{" + this.key + "}";
   }

   public String getKey() {
      return this.key;
   }

   public TextContent.Type getType() {
      return TYPE;
   }

   static {
      TYPE = new TextContent.Type(CODEC, "keybind");
   }
}
