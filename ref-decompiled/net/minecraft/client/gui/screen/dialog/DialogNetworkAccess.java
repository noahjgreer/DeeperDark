package net.minecraft.client.gui.screen.dialog;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.ServerLinks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface DialogNetworkAccess {
   void disconnect(Text reason);

   void runClickEventCommand(String command, @Nullable Screen afterActionScreen);

   void showDialog(RegistryEntry dialog, @Nullable Screen afterActionScreen);

   void sendCustomClickActionPacket(Identifier id, Optional payload);

   ServerLinks getServerLinks();
}
