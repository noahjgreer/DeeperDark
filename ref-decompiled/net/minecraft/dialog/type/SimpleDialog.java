package net.minecraft.dialog.type;

import com.mojang.serialization.MapCodec;
import java.util.List;

public interface SimpleDialog extends Dialog {
   MapCodec getCodec();

   List getButtons();
}
