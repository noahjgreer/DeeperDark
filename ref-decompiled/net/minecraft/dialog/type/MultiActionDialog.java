package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.util.dynamic.Codecs;

public record MultiActionDialog(DialogCommonData common, List actions, Optional exitAction, int columns) implements ColumnsDialog {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DialogCommonData.CODEC.forGetter(MultiActionDialog::common), Codecs.nonEmptyList(DialogActionButtonData.CODEC.listOf()).fieldOf("actions").forGetter(MultiActionDialog::actions), DialogActionButtonData.CODEC.optionalFieldOf("exit_action").forGetter(MultiActionDialog::exitAction), Codecs.POSITIVE_INT.optionalFieldOf("columns", 2).forGetter(MultiActionDialog::columns)).apply(instance, MultiActionDialog::new);
   });

   public MultiActionDialog(DialogCommonData dialogCommonData, List list, Optional optional, int i) {
      this.common = dialogCommonData;
      this.actions = list;
      this.exitAction = optional;
      this.columns = i;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public DialogCommonData common() {
      return this.common;
   }

   public List actions() {
      return this.actions;
   }

   public Optional exitAction() {
      return this.exitAction;
   }

   public int columns() {
      return this.columns;
   }
}
