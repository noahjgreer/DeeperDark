package net.minecraft.dialog;

import com.mojang.serialization.MapCodec;
import net.minecraft.dialog.body.ItemDialogBody;
import net.minecraft.dialog.body.PlainMessageDialogBody;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DialogBodyTypes {
   public static MapCodec registerAndGetDefault(Registry registry) {
      Registry.register(registry, (Identifier)Identifier.ofVanilla("item"), ItemDialogBody.CODEC);
      return (MapCodec)Registry.register(registry, (Identifier)Identifier.ofVanilla("plain_message"), PlainMessageDialogBody.CODEC);
   }
}
