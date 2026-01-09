package net.minecraft.client.resource.language;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.dynamic.Codecs;

@Environment(EnvType.CLIENT)
public record LanguageDefinition(String region, String name, boolean rightToLeft) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_EMPTY_STRING.fieldOf("region").forGetter(LanguageDefinition::region), Codecs.NON_EMPTY_STRING.fieldOf("name").forGetter(LanguageDefinition::name), Codec.BOOL.optionalFieldOf("bidirectional", false).forGetter(LanguageDefinition::rightToLeft)).apply(instance, LanguageDefinition::new);
   });

   public LanguageDefinition(String code, String region, boolean bl) {
      this.region = code;
      this.name = region;
      this.rightToLeft = bl;
   }

   public Text getDisplayText() {
      return Text.literal(this.name + " (" + this.region + ")");
   }

   public String region() {
      return this.region;
   }

   public String name() {
      return this.name;
   }

   public boolean rightToLeft() {
      return this.rightToLeft;
   }
}
