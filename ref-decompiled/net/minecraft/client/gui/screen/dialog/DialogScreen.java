package net.minecraft.client.gui.screen.dialog;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.ScrollableLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class DialogScreen extends Screen {
   public static final Text CUSTOM_SCREEN_REJECTED_DISCONNECT_TEXT = Text.translatable("menu.custom_screen_info.disconnect");
   private static final int field_60758 = 20;
   private static final ButtonTextures WARNING_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla("dialog/warning_button"), Identifier.ofVanilla("dialog/warning_button_disabled"), Identifier.ofVanilla("dialog/warning_button_highlighted"));
   private final Dialog dialog;
   private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
   @Nullable
   private final Screen parent;
   @Nullable
   private ScrollableLayoutWidget contents;
   private ButtonWidget warningButton;
   private final DialogNetworkAccess networkAccess;
   private Supplier cancelAction;

   public DialogScreen(@Nullable Screen parent, Dialog dialog, DialogNetworkAccess networkAccess) {
      super(dialog.common().title());
      this.cancelAction = DialogControls.EMPTY_ACTION_CLICK_EVENT;
      this.dialog = dialog;
      this.parent = parent;
      this.networkAccess = networkAccess;
   }

   protected final void init() {
      super.init();
      this.warningButton = this.createWarningButton();
      this.warningButton.setNavigationOrder(-10);
      DialogControls dialogControls = new DialogControls(this);
      DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(10);
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
      this.layout.addHeader(this.createHeader());
      Iterator var3 = this.dialog.common().body().iterator();

      while(var3.hasNext()) {
         DialogBody dialogBody = (DialogBody)var3.next();
         Widget widget = DialogBodyHandlers.createWidget(this, dialogBody);
         if (widget != null) {
            directionalLayoutWidget.add(widget);
         }
      }

      var3 = this.dialog.common().inputs().iterator();

      while(var3.hasNext()) {
         DialogInput dialogInput = (DialogInput)var3.next();
         Objects.requireNonNull(directionalLayoutWidget);
         dialogControls.addInput(dialogInput, directionalLayoutWidget::add);
      }

      this.initBody(directionalLayoutWidget, dialogControls, this.dialog, this.networkAccess);
      this.contents = new ScrollableLayoutWidget(this.client, directionalLayoutWidget, this.layout.getContentHeight());
      this.layout.addBody(this.contents);
      this.initHeaderAndFooter(this.layout, dialogControls, this.dialog, this.networkAccess);
      this.cancelAction = dialogControls.createClickEvent(this.dialog.getCancelAction());
      this.layout.forEachChild((child) -> {
         if (child != this.warningButton) {
            this.addDrawableChild(child);
         }

      });
      this.addDrawableChild(this.warningButton);
      this.refreshWidgetPositions();
   }

   protected void initBody(DirectionalLayoutWidget bodyLayout, DialogControls controls, Dialog dialog, DialogNetworkAccess networkAccess) {
   }

   protected void initHeaderAndFooter(ThreePartsLayoutWidget layout, DialogControls controls, Dialog dialog, DialogNetworkAccess networkAccess) {
   }

   protected void refreshWidgetPositions() {
      this.contents.setHeight(this.layout.getContentHeight());
      this.layout.refreshPositions();
      this.resetWarningButtonPosition();
   }

   protected Widget createHeader() {
      DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(10);
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();
      directionalLayoutWidget.add(new TextWidget(this.title, this.textRenderer));
      directionalLayoutWidget.add(this.warningButton);
      return directionalLayoutWidget;
   }

   protected void resetWarningButtonPosition() {
      int i = this.warningButton.getX();
      int j = this.warningButton.getY();
      if (i < 0 || j < 0 || i > this.width - 20 || j > this.height - 20) {
         this.warningButton.setX(Math.max(0, this.width - 40));
         this.warningButton.setY(Math.min(5, this.height));
      }

   }

   private ButtonWidget createWarningButton() {
      TexturedButtonWidget texturedButtonWidget = new TexturedButtonWidget(0, 0, 20, 20, WARNING_BUTTON_TEXTURES, (button) -> {
         this.client.setScreen(DialogScreen.WarningScreen.create(this.client, this));
      }, Text.translatable("menu.custom_screen_info.button_narration"));
      texturedButtonWidget.setTooltip(Tooltip.of(Text.translatable("menu.custom_screen_info.tooltip")));
      return texturedButtonWidget;
   }

   public boolean shouldPause() {
      return this.dialog.common().pause();
   }

   public boolean shouldCloseOnEsc() {
      return this.dialog.common().canCloseWithEscape();
   }

   public void close() {
      this.runAction((Optional)this.cancelAction.get(), AfterAction.CLOSE);
   }

   public void runAction(Optional clickEvent) {
      this.runAction(clickEvent, this.dialog.common().afterAction());
   }

   public void runAction(Optional clickEvent, AfterAction afterAction) {
      Object var10000;
      switch (afterAction) {
         case NONE:
            var10000 = this;
            break;
         case CLOSE:
            var10000 = this.parent;
            break;
         case WAIT_FOR_RESPONSE:
            var10000 = new WaitingForResponseScreen(this.parent);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      Screen screen = var10000;
      if (clickEvent.isPresent()) {
         this.handleClickEvent((ClickEvent)clickEvent.get(), (Screen)screen);
      } else {
         this.client.setScreen((Screen)screen);
      }

   }

   private void handleClickEvent(ClickEvent clickEvent, @Nullable Screen afterActionScreen) {
      Objects.requireNonNull(clickEvent);
      byte var4 = 0;
      switch (clickEvent.typeSwitch<invokedynamic>(clickEvent, var4)) {
         case 0:
            ClickEvent.RunCommand var5 = (ClickEvent.RunCommand)clickEvent;
            ClickEvent.RunCommand var10000 = var5;

            String var10;
            try {
               var10 = var10000.command();
            } catch (Throwable var9) {
               throw new MatchException(var9.toString(), var9);
            }

            String var11 = var10;
            this.networkAccess.runClickEventCommand(CommandManager.stripLeadingSlash(var11), afterActionScreen);
            break;
         case 1:
            ClickEvent.ShowDialog showDialog = (ClickEvent.ShowDialog)clickEvent;
            this.networkAccess.showDialog(showDialog.dialog(), afterActionScreen);
            break;
         case 2:
            ClickEvent.Custom custom = (ClickEvent.Custom)clickEvent;
            this.networkAccess.sendCustomClickActionPacket(custom.id(), custom.payload());
            this.client.setScreen(afterActionScreen);
            break;
         default:
            handleBasicClickEvent(clickEvent, this.client, afterActionScreen);
      }

   }

   @Nullable
   public Screen getParentScreen() {
      return this.parent;
   }

   protected static Widget createGridWidget(List widgets, int columns) {
      GridWidget gridWidget = new GridWidget();
      gridWidget.getMainPositioner().alignHorizontalCenter();
      gridWidget.setColumnSpacing(2).setRowSpacing(2);
      int i = widgets.size();
      int j = i / columns;
      int k = j * columns;

      for(int l = 0; l < k; ++l) {
         gridWidget.add((Widget)widgets.get(l), l / columns, l % columns);
      }

      if (i != k) {
         DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(2);
         directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();

         for(int m = k; m < i; ++m) {
            directionalLayoutWidget.add((Widget)widgets.get(m));
         }

         gridWidget.add(directionalLayoutWidget, j, 0, 1, columns);
      }

      return gridWidget;
   }

   @Environment(EnvType.CLIENT)
   public static class WarningScreen extends ConfirmScreen {
      private final MutableObject dialogScreen;

      public static Screen create(MinecraftClient client, Screen screen) {
         return new WarningScreen(client, new MutableObject(screen));
      }

      private WarningScreen(MinecraftClient client, MutableObject mutableObject) {
         super((bl) -> {
            if (bl) {
               GameMenuScreen.disconnect(client, DialogScreen.CUSTOM_SCREEN_REJECTED_DISCONNECT_TEXT);
            } else {
               client.setScreen((Screen)mutableObject.getValue());
            }

         }, Text.translatable("menu.custom_screen_info.title"), Text.translatable("menu.custom_screen_info.contents"), ScreenTexts.returnToMenuOrDisconnect(client.isInSingleplayer()), ScreenTexts.BACK);
         this.dialogScreen = mutableObject;
      }

      @Nullable
      public Screen getDialogScreen() {
         return (Screen)this.dialogScreen.getValue();
      }

      public void setDialogScreen(@Nullable Screen screen) {
         this.dialogScreen.setValue(screen);
      }
   }
}
