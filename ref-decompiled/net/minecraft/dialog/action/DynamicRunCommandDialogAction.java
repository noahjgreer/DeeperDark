package net.minecraft.dialog.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.text.ClickEvent;

public record DynamicRunCommandDialogAction(ParsedTemplate template) implements DialogAction {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ParsedTemplate.CODEC.fieldOf("template").forGetter(DynamicRunCommandDialogAction::template)).apply(instance, DynamicRunCommandDialogAction::new);
   });

   public DynamicRunCommandDialogAction(ParsedTemplate parsedTemplate) {
      this.template = parsedTemplate;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Optional createClickEvent(Map valueGetters) {
      String string = this.template.apply(DialogAction.ValueGetter.resolveAll(valueGetters));
      return Optional.of(new ClickEvent.RunCommand(string));
   }

   public ParsedTemplate template() {
      return this.template;
   }
}
