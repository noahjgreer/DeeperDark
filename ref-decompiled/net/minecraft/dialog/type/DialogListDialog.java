package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogCommonData;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;

public record DialogListDialog(DialogCommonData common, RegistryEntryList dialogs, Optional exitAction, int columns, int buttonWidth) implements ColumnsDialog {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(DialogCommonData.CODEC.forGetter(DialogListDialog::common), Dialog.ENTRY_LIST_CODEC.fieldOf("dialogs").forGetter(DialogListDialog::dialogs), DialogActionButtonData.CODEC.optionalFieldOf("exit_action").forGetter(DialogListDialog::exitAction), Codecs.POSITIVE_INT.optionalFieldOf("columns", 2).forGetter(DialogListDialog::columns), WIDTH_CODEC.optionalFieldOf("button_width", 150).forGetter(DialogListDialog::buttonWidth)).apply(instance, DialogListDialog::new);
   });

   public DialogListDialog(DialogCommonData dialogCommonData, RegistryEntryList registryEntryList, Optional optional, int i, int j) {
      this.common = dialogCommonData;
      this.dialogs = registryEntryList;
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

   public RegistryEntryList dialogs() {
      return this.dialogs;
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
