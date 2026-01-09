package net.minecraft.dialog;

import com.mojang.serialization.MapCodec;
import net.minecraft.dialog.type.ConfirmationDialog;
import net.minecraft.dialog.type.DialogListDialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.dialog.type.NoticeDialog;
import net.minecraft.dialog.type.ServerLinksDialog;
import net.minecraft.registry.Registry;

public class DialogTypes {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (String)"notice", NoticeDialog.CODEC);
      Registry.register(registry, (String)"server_links", ServerLinksDialog.CODEC);
      Registry.register(registry, (String)"dialog_list", DialogListDialog.CODEC);
      Registry.register(registry, (String)"multi_action", MultiActionDialog.CODEC);
      return (MapCodec)Registry.register(registry, (String)"confirmation", ConfirmationDialog.CODEC);
   }
}
