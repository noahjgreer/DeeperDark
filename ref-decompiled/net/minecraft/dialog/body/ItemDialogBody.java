package net.minecraft.dialog.body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.item.ItemStack;
import net.minecraft.util.dynamic.Codecs;

public record ItemDialogBody(ItemStack item, Optional description, boolean showDecorations, boolean showTooltip, int width, int height) implements DialogBody {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ItemStack.VALIDATED_CODEC.fieldOf("item").forGetter(ItemDialogBody::item), PlainMessageDialogBody.ALTERNATIVE_CODEC.optionalFieldOf("description").forGetter(ItemDialogBody::description), Codec.BOOL.optionalFieldOf("show_decorations", true).forGetter(ItemDialogBody::showDecorations), Codec.BOOL.optionalFieldOf("show_tooltip", true).forGetter(ItemDialogBody::showTooltip), Codecs.rangedInt(1, 256).optionalFieldOf("width", 16).forGetter(ItemDialogBody::width), Codecs.rangedInt(1, 256).optionalFieldOf("height", 16).forGetter(ItemDialogBody::height)).apply(instance, ItemDialogBody::new);
   });

   public ItemDialogBody(ItemStack itemStack, Optional optional, boolean bl, boolean bl2, int i, int j) {
      this.item = itemStack;
      this.description = optional;
      this.showDecorations = bl;
      this.showTooltip = bl2;
      this.width = i;
      this.height = j;
   }

   public MapCodec getTypeCodec() {
      return CODEC;
   }

   public ItemStack item() {
      return this.item;
   }

   public Optional description() {
      return this.description;
   }

   public boolean showDecorations() {
      return this.showDecorations;
   }

   public boolean showTooltip() {
      return this.showTooltip;
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }
}
