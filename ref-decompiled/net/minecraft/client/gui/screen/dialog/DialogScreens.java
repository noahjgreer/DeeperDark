package net.minecraft.client.gui.screen.dialog;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.dialog.type.ServerLinksDialog;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DialogScreens {
   private static final Map DIALOG_SCREEN_FACTORIES = new HashMap();

   private static void register(MapCodec dialogCodec, Factory factory) {
      DIALOG_SCREEN_FACTORIES.put(dialogCodec, factory);
   }

   @Nullable
   public static DialogScreen create(Dialog dialog, @Nullable Screen parent, DialogNetworkAccess networkAccess) {
      Factory factory = (Factory)DIALOG_SCREEN_FACTORIES.get(dialog.getCodec());
      return factory != null ? factory.create(parent, dialog, networkAccess) : null;
   }

   public static void bootstrap() {
      register(ConfirmationDialog.CODEC, SimpleDialogScreen::new);
      register(NoticeDialog.CODEC, SimpleDialogScreen::new);
      register(DialogListDialog.CODEC, DialogListDialogScreen::new);
      register(MultiActionDialog.CODEC, MultiActionDialogScreen::new);
      register(ServerLinksDialog.CODEC, ServerLinksDialogScreen::new);
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface Factory {
      DialogScreen create(@Nullable Screen parent, Dialog dialog, DialogNetworkAccess networkAccess);
   }
}
