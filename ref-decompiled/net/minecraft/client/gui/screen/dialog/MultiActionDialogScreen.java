package net.minecraft.client.gui.screen.dialog;

import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.dialog.type.MultiActionDialog;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MultiActionDialogScreen extends ColumnsDialogScreen {
   public MultiActionDialogScreen(@Nullable Screen parent, MultiActionDialog dialog, DialogNetworkAccess networkAccess) {
      super(parent, dialog, networkAccess);
   }

   protected Stream streamActionButtonData(MultiActionDialog multiActionDialog, DialogNetworkAccess dialogNetworkAccess) {
      return multiActionDialog.actions().stream();
   }
}
