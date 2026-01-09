package net.minecraft.client.gui.screen.dialog;

import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.dialog.type.ColumnsDialog;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class ColumnsDialogScreen extends DialogScreen {
   public static final int field_61004 = 5;

   public ColumnsDialogScreen(@Nullable Screen parent, ColumnsDialog dialog, DialogNetworkAccess networkAccess) {
      super(parent, dialog, networkAccess);
   }

   protected void initBody(DirectionalLayoutWidget directionalLayoutWidget, DialogControls dialogControls, ColumnsDialog columnsDialog, DialogNetworkAccess dialogNetworkAccess) {
      super.initBody(directionalLayoutWidget, dialogControls, columnsDialog, dialogNetworkAccess);
      List list = this.streamActionButtonData(columnsDialog, dialogNetworkAccess).map((actionButtonData) -> {
         return dialogControls.createButton(actionButtonData).build();
      }).toList();
      directionalLayoutWidget.add(createGridWidget(list, columnsDialog.columns()));
   }

   protected abstract Stream streamActionButtonData(ColumnsDialog dialog, DialogNetworkAccess networkAccess);

   protected void initHeaderAndFooter(ThreePartsLayoutWidget threePartsLayoutWidget, DialogControls dialogControls, ColumnsDialog columnsDialog, DialogNetworkAccess dialogNetworkAccess) {
      super.initHeaderAndFooter(threePartsLayoutWidget, dialogControls, columnsDialog, dialogNetworkAccess);
      columnsDialog.exitAction().ifPresentOrElse((actionButtonData) -> {
         threePartsLayoutWidget.addFooter(dialogControls.createButton(actionButtonData).build());
      }, () -> {
         threePartsLayoutWidget.setFooterHeight(5);
      });
   }
}
