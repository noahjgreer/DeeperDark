/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.TeleportSpectatorMenu;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
static class TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand
implements SpectatorMenuCommand {
    private final Team team;
    private final Supplier<SkinTextures> skinTexturesSupplier;
    private final List<PlayerListEntry> scoreboardEntries;

    private TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand(Team team, List<PlayerListEntry> scoreboardEntries, Supplier<SkinTextures> skinTexturesSupplier) {
        this.team = team;
        this.scoreboardEntries = scoreboardEntries;
        this.skinTexturesSupplier = skinTexturesSupplier;
    }

    public static Optional<SpectatorMenuCommand> create(MinecraftClient client, Team team) {
        ArrayList<PlayerListEntry> list = new ArrayList<PlayerListEntry>();
        for (String string : team.getPlayerList()) {
            PlayerListEntry playerListEntry = client.getNetworkHandler().getPlayerListEntry(string);
            if (playerListEntry == null || playerListEntry.getGameMode() == GameMode.SPECTATOR) continue;
            list.add(playerListEntry);
        }
        if (list.isEmpty()) {
            return Optional.empty();
        }
        PlayerListEntry playerListEntry2 = (PlayerListEntry)list.get(Random.create().nextInt(list.size()));
        return Optional.of(new TeamTeleportSpectatorMenu.TeleportToSpecificTeamCommand(team, list, playerListEntry2::getSkinTextures));
    }

    @Override
    public void use(SpectatorMenu menu) {
        menu.selectElement(new TeleportSpectatorMenu(this.scoreboardEntries));
    }

    @Override
    public Text getName() {
        return this.team.getDisplayName();
    }

    @Override
    public void renderIcon(DrawContext context, float brightness, float alpha) {
        Integer integer = this.team.getColor().getColorValue();
        if (integer != null) {
            float f = (float)(integer >> 16 & 0xFF) / 255.0f;
            float g = (float)(integer >> 8 & 0xFF) / 255.0f;
            float h = (float)(integer & 0xFF) / 255.0f;
            context.fill(1, 1, 15, 15, ColorHelper.fromFloats(alpha, f * brightness, g * brightness, h * brightness));
        }
        PlayerSkinDrawer.draw(context, this.skinTexturesSupplier.get(), 2, 2, 12, ColorHelper.fromFloats(alpha, brightness, brightness, brightness));
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
