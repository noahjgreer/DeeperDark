package net.minecraft.client.gui.hud.spectator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class TeleportSpectatorMenu implements SpectatorMenuCommandGroup, SpectatorMenuCommand {
   private static final Identifier TELEPORT_TO_PLAYER_TEXTURE = Identifier.ofVanilla("spectator/teleport_to_player");
   private static final Comparator ORDERING = Comparator.comparing((a) -> {
      return a.getProfile().getId();
   });
   private static final Text TELEPORT_TEXT = Text.translatable("spectatorMenu.teleport");
   private static final Text PROMPT_TEXT = Text.translatable("spectatorMenu.teleport.prompt");
   private final List elements;

   public TeleportSpectatorMenu() {
      this(MinecraftClient.getInstance().getNetworkHandler().getListedPlayerListEntries());
   }

   public TeleportSpectatorMenu(Collection entries) {
      this.elements = entries.stream().filter((entry) -> {
         return entry.getGameMode() != GameMode.SPECTATOR;
      }).sorted(ORDERING).map((entry) -> {
         return new TeleportToSpecificPlayerSpectatorCommand(entry.getProfile());
      }).toList();
   }

   public List getCommands() {
      return this.elements;
   }

   public Text getPrompt() {
      return PROMPT_TEXT;
   }

   public void use(SpectatorMenu menu) {
      menu.selectElement(this);
   }

   public Text getName() {
      return TELEPORT_TEXT;
   }

   public void renderIcon(DrawContext context, float brightness, float alpha) {
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TELEPORT_TO_PLAYER_TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
   }

   public boolean isEnabled() {
      return !this.elements.isEmpty();
   }
}
