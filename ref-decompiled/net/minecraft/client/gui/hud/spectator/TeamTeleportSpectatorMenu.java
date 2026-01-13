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
 *  net.minecraft.client.gui.hud.spectator.TeamTeleportSpectatorMenu
 *  net.minecraft.client.gui.hud.spectator.TeamTeleportSpectatorMenu$TeleportToSpecificTeamCommand
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.gui.hud.spectator;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.TeamTeleportSpectatorMenu;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TeamTeleportSpectatorMenu
implements SpectatorMenuCommandGroup,
SpectatorMenuCommand {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"spectator/teleport_to_team");
    private static final Text TEAM_TELEPORT_TEXT = Text.translatable((String)"spectatorMenu.team_teleport");
    private static final Text PROMPT_TEXT = Text.translatable((String)"spectatorMenu.team_teleport.prompt");
    private final List<SpectatorMenuCommand> commands;

    public TeamTeleportSpectatorMenu() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        this.commands = TeamTeleportSpectatorMenu.getCommands((MinecraftClient)minecraftClient, (Scoreboard)minecraftClient.world.getScoreboard());
    }

    private static List<SpectatorMenuCommand> getCommands(MinecraftClient client, Scoreboard scoreboard) {
        return scoreboard.getTeams().stream().flatMap(team -> TeleportToSpecificTeamCommand.create((MinecraftClient)client, (Team)team).stream()).toList();
    }

    public List<SpectatorMenuCommand> getCommands() {
        return this.commands;
    }

    public Text getPrompt() {
        return PROMPT_TEXT;
    }

    public void use(SpectatorMenu menu) {
        menu.selectElement((SpectatorMenuCommandGroup)this);
    }

    public Text getName() {
        return TEAM_TELEPORT_TEXT;
    }

    public void renderIcon(DrawContext context, float brightness, float alpha) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, 16, 16, ColorHelper.fromFloats((float)alpha, (float)brightness, (float)brightness, (float)brightness));
    }

    public boolean isEnabled() {
        return !this.commands.isEmpty();
    }
}

