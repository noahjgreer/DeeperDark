package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public record ReferenceFont(Identifier id) implements FontLoader {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("id").forGetter(ReferenceFont::id)).apply(instance, ReferenceFont::new);
   });

   public ReferenceFont(Identifier id) {
      this.id = id;
   }

   public FontType getType() {
      return FontType.REFERENCE;
   }

   public Either build() {
      return Either.right(new FontLoader.Reference(this.id));
   }

   public Identifier id() {
      return this.id;
   }
}
