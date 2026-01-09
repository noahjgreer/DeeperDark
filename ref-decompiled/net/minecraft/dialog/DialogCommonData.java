package net.minecraft.dialog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record DialogCommonData(Text title, Optional externalTitle, boolean canCloseWithEscape, boolean pause, AfterAction afterAction, List body, List inputs) {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(TextCodecs.CODEC.fieldOf("title").forGetter(DialogCommonData::title), TextCodecs.CODEC.optionalFieldOf("external_title").forGetter(DialogCommonData::externalTitle), Codec.BOOL.optionalFieldOf("can_close_with_escape", true).forGetter(DialogCommonData::canCloseWithEscape), Codec.BOOL.optionalFieldOf("pause", true).forGetter(DialogCommonData::pause), AfterAction.CODEC.optionalFieldOf("after_action", AfterAction.CLOSE).forGetter(DialogCommonData::afterAction), DialogBody.LIST_CODEC.optionalFieldOf("body", List.of()).forGetter(DialogCommonData::body), DialogInput.CODEC.listOf().optionalFieldOf("inputs", List.of()).forGetter(DialogCommonData::inputs)).apply(instance, DialogCommonData::new);
   }).validate((data) -> {
      return data.pause && !data.afterAction.canUnpause() ? DataResult.error(() -> {
         return "Dialogs that pause the game must use after_action values that unpause it after user action!";
      }) : DataResult.success(data);
   });

   public DialogCommonData(Text text, Optional optional, boolean bl, boolean bl2, AfterAction afterAction, List list, List list2) {
      this.title = text;
      this.externalTitle = optional;
      this.canCloseWithEscape = bl;
      this.pause = bl2;
      this.afterAction = afterAction;
      this.body = list;
      this.inputs = list2;
   }

   public Text getExternalTitle() {
      return (Text)this.externalTitle.orElse(this.title);
   }

   public Text title() {
      return this.title;
   }

   public Optional externalTitle() {
      return this.externalTitle;
   }

   public boolean canCloseWithEscape() {
      return this.canCloseWithEscape;
   }

   public boolean pause() {
      return this.pause;
   }

   public AfterAction afterAction() {
      return this.afterAction;
   }

   public List body() {
      return this.body;
   }

   public List inputs() {
      return this.inputs;
   }
}
