package net.minecraft.dialog;

import com.mojang.serialization.MapCodec;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.action.DynamicRunCommandDialogAction;
import net.minecraft.dialog.action.SimpleDialogAction;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DialogActionTypes {
   public static MapCodec registerAndGetDefault(Registry registry) {
      SimpleDialogAction.CODECS.forEach((clickEventAction, codec) -> {
         Registry.register(registry, (Identifier)Identifier.ofVanilla(clickEventAction.asString()), codec);
      });
      Registry.register(registry, (Identifier)Identifier.ofVanilla("dynamic/run_command"), DynamicRunCommandDialogAction.CODEC);
      return (MapCodec)Registry.register(registry, (Identifier)Identifier.ofVanilla("dynamic/custom"), DynamicCustomDialogAction.CODEC);
   }
}
