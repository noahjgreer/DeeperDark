package net.minecraft.client.gui.screen.dialog;

import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.type.SimpleDialog;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SimpleDialogScreen extends DialogScreen {
   public SimpleDialogScreen(@Nullable Screen parent, SimpleDialog dialog, DialogNetworkAccess networkAccess) {
      super(parent, dialog, networkAccess);
   }

   protected void initHeaderAndFooter(ThreePartsLayoutWidget threePartsLayoutWidget, DialogControls dialogControls, SimpleDialog simpleDialog, DialogNetworkAccess dialogNetworkAccess) {
      super.initHeaderAndFooter(threePartsLayoutWidget, dialogControls, simpleDialog, dialogNetworkAccess);
      DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
      Iterator var6 = simpleDialog.getButtons().iterator();

      while(var6.hasNext()) {
         DialogActionButtonData dialogActionButtonData = (DialogActionButtonData)var6.next();
         directionalLayoutWidget.add(dialogControls.createButton(dialogActionButtonData).build());
      }

      threePartsLayoutWidget.addFooter(directionalLayoutWidget);
   }
}
