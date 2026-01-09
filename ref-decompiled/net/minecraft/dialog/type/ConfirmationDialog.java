package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogCommonData;

public record ConfirmationDialog(DialogCommonData common, DialogActionButtonData yesButton, DialogActionButtonData noButton) implements SimpleDialog {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DialogCommonData.CODEC.forGetter(ConfirmationDialog::common), DialogActionButtonData.CODEC.fieldOf("yes").forGetter(ConfirmationDialog::yesButton), DialogActionButtonData.CODEC.fieldOf("no").forGetter(ConfirmationDialog::noButton)).apply(instance, ConfirmationDialog::new);
   });

   public ConfirmationDialog(DialogCommonData dialogCommonData, DialogActionButtonData dialogActionButtonData, DialogActionButtonData dialogActionButtonData2) {
      this.common = dialogCommonData;
      this.yesButton = dialogActionButtonData;
      this.noButton = dialogActionButtonData2;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Optional getCancelAction() {
      return this.noButton.action();
   }

   public List getButtons() {
      return List.of(this.yesButton, this.noButton);
   }

   public DialogCommonData common() {
      return this.common;
   }

   public DialogActionButtonData yesButton() {
      return this.yesButton;
   }

   public DialogActionButtonData noButton() {
      return this.noButton;
   }
}
