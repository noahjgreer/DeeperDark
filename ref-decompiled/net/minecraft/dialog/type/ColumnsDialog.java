package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.dialog.DialogActionButtonData;

public interface ColumnsDialog extends Dialog {
   MapCodec getCodec();

   int columns();

   Optional exitAction();

   default Optional getCancelAction() {
      return this.exitAction().flatMap(DialogActionButtonData::action);
   }
}
