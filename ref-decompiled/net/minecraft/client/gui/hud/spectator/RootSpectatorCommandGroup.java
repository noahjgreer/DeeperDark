package net.minecraft.client.gui.hud.spectator;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RootSpectatorCommandGroup implements SpectatorMenuCommandGroup {
   private static final Text PROMPT_TEXT = Text.translatable("spectatorMenu.root.prompt");
   private final List elements = Lists.newArrayList();

   public RootSpectatorCommandGroup() {
      this.elements.add(new TeleportSpectatorMenu());
      this.elements.add(new TeamTeleportSpectatorMenu());
   }

   public List getCommands() {
      return this.elements;
   }

   public Text getPrompt() {
      return PROMPT_TEXT;
   }
}
