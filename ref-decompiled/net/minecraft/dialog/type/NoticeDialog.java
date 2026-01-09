package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.screen.ScreenTexts;

public record NoticeDialog(DialogCommonData common, DialogActionButtonData action) implements SimpleDialog {
   public static final DialogActionButtonData OK_BUTTON;
   public static final MapCodec CODEC;

   public NoticeDialog(DialogCommonData dialogCommonData, DialogActionButtonData dialogActionButtonData) {
      this.common = dialogCommonData;
      this.action = dialogActionButtonData;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Optional getCancelAction() {
      return this.action.action();
   }

   public List getButtons() {
      return List.of(this.action);
   }

   public DialogCommonData common() {
      return this.common;
   }

   public DialogActionButtonData action() {
      return this.action;
   }

   static {
      OK_BUTTON = new DialogActionButtonData(new DialogButtonData(ScreenTexts.OK, 150), Optional.empty());
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(DialogCommonData.CODEC.forGetter(NoticeDialog::common), DialogActionButtonData.CODEC.optionalFieldOf("action", OK_BUTTON).forGetter(NoticeDialog::action)).apply(instance, NoticeDialog::new);
      });
   }
}
