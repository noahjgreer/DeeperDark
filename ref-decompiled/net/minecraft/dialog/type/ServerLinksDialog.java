package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.util.dynamic.Codecs;

public record ServerLinksDialog(DialogCommonData common, Optional exitAction, int columns, int buttonWidth) implements ColumnsDialog {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DialogCommonData.CODEC.forGetter(ServerLinksDialog::common), DialogActionButtonData.CODEC.optionalFieldOf("exit_action").forGetter(ServerLinksDialog::exitAction), Codecs.POSITIVE_INT.optionalFieldOf("columns", 2).forGetter(ServerLinksDialog::columns), WIDTH_CODEC.optionalFieldOf("button_width", 150).forGetter(ServerLinksDialog::buttonWidth)).apply(instance, ServerLinksDialog::new);
   });

   public ServerLinksDialog(DialogCommonData dialogCommonData, Optional optional, int i, int j) {
      this.common = dialogCommonData;
      this.exitAction = optional;
      this.columns = i;
      this.buttonWidth = j;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public DialogCommonData common() {
      return this.common;
   }

   public Optional exitAction() {
      return this.exitAction;
   }

   public int columns() {
      return this.columns;
   }

   public int buttonWidth() {
      return this.buttonWidth;
   }
}
