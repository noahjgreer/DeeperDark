/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenu
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup
 *  net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu
 *  net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.world.GameMode
 */
package net.minecraft.client.gui.hud.spectator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class TeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    private static final Identifier TELEPORT_TO_PLAYER_TEXTURE = Identifier.ofVanilla((String)"spectator/teleport_to_player");
    private static final Comparator<PlayerListEntry> ORDERING = Comparator.comparing(a -> a.getProfile().id());
    private static final Text TELEPORT_TEXT = Text.translatable((String)"spectatorMenu.teleport");
    private static final Text PROMPT_TEXT = Text.translatable((String)"spectatorMenu.teleport.prompt");
    private final List<SpectatorMenuCommand> elements;

    public TeleportSpectatorMenu() {
        this(MinecraftClient.getInstance().getNetworkHandler().getListedPlayerListEntries());
    }

    public TeleportSpectatorMenu(Collection<PlayerListEntry> entries) {
        this.elements = entries.stream().filter(entry -> entry.getGameMode() != GameMode.SPECTATOR).sorted(ORDERING).map(TeleportToSpecificPlayerSpectatorCommand::new).collect(Collectors.toUnmodifiableList());
    }

    public List<SpectatorMenuCommand> getCommands() {
        return this.elements;
    }

    public Text getPrompt() {
        return PROMPT_TEXT;
    }

    public void use(SpectatorMenu menu) {
        menu.selectElement((SpectatorMenuCommandGroup)this);
    }

    public Text getName() {
        return TELEPORT_TEXT;
    }

    public void renderIcon(DrawContext context, float brightness, float alpha) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TELEPORT_TO_PLAYER_TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats((float)alpha, (float)brightness, (float)brightness, (float)brightness));
    }

    public boolean isEnabled() {
        return !this.elements.isEmpty();
    }
}

