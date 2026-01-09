package net.minecraft.client.gui.screen.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DialogListDialogScreen extends ColumnsDialogScreen {
   public DialogListDialogScreen(@Nullable Screen parent, DialogListDialog dialog, DialogNetworkAccess networkAccess) {
      super(parent, dialog, networkAccess);
   }

   protected Stream streamActionButtonData(DialogListDialog dialogListDialog, DialogNetworkAccess dialogNetworkAccess) {
      return dialogListDialog.dialogs().stream().map((entry) -> {
         return createButton(dialogListDialog, entry);
      });
   }

   private static DialogActionButtonData createButton(DialogListDialog dialog, RegistryEntry entry) {
      return new DialogActionButtonData(new DialogButtonData(((Dialog)entry.value()).common().getExternalTitle(), dialog.buttonWidth()), Optional.of(new SimpleDialogAction(new ClickEvent.ShowDialog(entry))));
   }
}
