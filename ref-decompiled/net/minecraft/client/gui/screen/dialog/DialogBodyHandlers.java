package net.minecraft.client.gui.screen.dialog;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ItemStackWidget;
import net.minecraft.client.gui.widget.NarratedMultilineTextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class DialogBodyHandlers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map DIALOG_BODY_HANDLERS = new HashMap();

   private static void register(MapCodec dialogBodyCodec, DialogBodyHandler dialogBodyHandler) {
      DIALOG_BODY_HANDLERS.put(dialogBodyCodec, dialogBodyHandler);
   }

   @Nullable
   private static DialogBodyHandler getHandler(DialogBody dialogBody) {
      return (DialogBodyHandler)DIALOG_BODY_HANDLERS.get(dialogBody.getTypeCodec());
   }

   @Nullable
   public static Widget createWidget(DialogScreen dialogScreen, DialogBody dialogBody) {
      DialogBodyHandler dialogBodyHandler = getHandler(dialogBody);
      if (dialogBodyHandler == null) {
         LOGGER.warn("Unrecognized dialog body {}", dialogBody);
         return null;
      } else {
         return dialogBodyHandler.createWidget(dialogScreen, dialogBody);
      }
   }

   public static void bootstrap() {
      register(PlainMessageDialogBody.CODEC, new PlainMessageDialogBodyHandler());
      register(ItemDialogBody.CODEC, new ItemDialogBodyHandler());
   }

   static void runActionFromStyle(DialogScreen dialogScreen, @Nullable Style style) {
      if (style != null) {
         ClickEvent clickEvent = style.getClickEvent();
         if (clickEvent != null) {
            dialogScreen.runAction(Optional.of(clickEvent));
         }
      }

   }

   @Environment(EnvType.CLIENT)
   private static class PlainMessageDialogBodyHandler implements DialogBodyHandler {
      PlainMessageDialogBodyHandler() {
      }

      public Widget createWidget(DialogScreen dialogScreen, PlainMessageDialogBody plainMessageDialogBody) {
         return (new NarratedMultilineTextWidget(plainMessageDialogBody.width(), plainMessageDialogBody.contents(), dialogScreen.getTextRenderer(), false, false, 4)).setStyleConfig(true, (style) -> {
            DialogBodyHandlers.runActionFromStyle(dialogScreen, style);
         }).setCentered(true);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class ItemDialogBodyHandler implements DialogBodyHandler {
      ItemDialogBodyHandler() {
      }

      public Widget createWidget(DialogScreen dialogScreen, ItemDialogBody itemDialogBody) {
         if (itemDialogBody.description().isPresent()) {
            PlainMessageDialogBody plainMessageDialogBody = (PlainMessageDialogBody)itemDialogBody.description().get();
            DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(2);
            directionalLayoutWidget.getMainPositioner().alignVerticalCenter();
            ItemStackWidget itemStackWidget = new ItemStackWidget(MinecraftClient.getInstance(), 0, 0, itemDialogBody.width(), itemDialogBody.height(), ScreenTexts.EMPTY, itemDialogBody.item(), itemDialogBody.showDecorations(), itemDialogBody.showTooltip());
            directionalLayoutWidget.add(itemStackWidget);
            directionalLayoutWidget.add((new NarratedMultilineTextWidget(plainMessageDialogBody.width(), plainMessageDialogBody.contents(), dialogScreen.getTextRenderer(), false, false, 4)).setStyleConfig(true, (style) -> {
               DialogBodyHandlers.runActionFromStyle(dialogScreen, style);
            }));
            return directionalLayoutWidget;
         } else {
            return new ItemStackWidget(MinecraftClient.getInstance(), 0, 0, itemDialogBody.width(), itemDialogBody.height(), itemDialogBody.item().getName(), itemDialogBody.item(), itemDialogBody.showDecorations(), itemDialogBody.showTooltip());
         }
      }
   }
}
