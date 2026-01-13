/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.PlayerSkinDrawer
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenu
 *  net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand
 *  net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.gui.hud.spectator;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.spectator.SpectatorMenu;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class TeleportToSpecificPlayerSpectatorCommand
implements SpectatorMenuCommand {
    private final PlayerListEntry player;
    private final Text name;

    public TeleportToSpecificPlayerSpectatorCommand(PlayerListEntry player) {
        this.player = player;
        this.name = Text.literal((String)player.getProfile().name());
    }

    public void use(SpectatorMenu menu) {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket((Packet)new SpectatorTeleportC2SPacket(this.player.getProfile().id()));
    }

    public Text getName() {
        return this.name;
    }

    public void renderIcon(DrawContext context, float brightness, float alpha) {
        PlayerSkinDrawer.draw((DrawContext)context, (SkinTextures)this.player.getSkinTextures(), (int)2, (int)2, (int)12, (int)ColorHelper.getWhite((float)alpha));
    }

    public boolean isEnabled() {
        return true;
    }
}

