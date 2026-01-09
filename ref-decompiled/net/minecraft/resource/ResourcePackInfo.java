package net.minecraft.resource;

import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Optional;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public record ResourcePackInfo(String id, Text title, ResourcePackSource source, Optional knownPackInfo) {
   public ResourcePackInfo(String string, Text text, ResourcePackSource resourcePackSource, Optional optional) {
      this.id = string;
      this.title = text;
      this.source = resourcePackSource;
      this.knownPackInfo = optional;
   }

   public Text getInformationText(boolean enabled, Text description) {
      return Texts.bracketed(this.source.decorate(Text.literal(this.id))).styled((style) -> {
         return style.withColor(enabled ? Formatting.GREEN : Formatting.RED).withInsertion(StringArgumentType.escapeIfRequired(this.id)).withHoverEvent(new HoverEvent.ShowText(Text.empty().append(this.title).append("\n").append(description)));
      });
   }

   public String id() {
      return this.id;
   }

   public Text title() {
      return this.title;
   }

   public ResourcePackSource source() {
      return this.source;
   }

   public Optional knownPackInfo() {
      return this.knownPackInfo;
   }
}
