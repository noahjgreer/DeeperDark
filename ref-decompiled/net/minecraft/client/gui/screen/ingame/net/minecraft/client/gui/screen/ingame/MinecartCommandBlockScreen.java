/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public class MinecartCommandBlockScreen
extends AbstractCommandBlockScreen {
    private final CommandBlockMinecartEntity field_63536;

    public MinecartCommandBlockScreen(CommandBlockMinecartEntity commandBlockMinecartEntity) {
        this.field_63536 = commandBlockMinecartEntity;
    }

    @Override
    public CommandBlockExecutor getCommandExecutor() {
        return this.field_63536.getCommandExecutor();
    }

    @Override
    int getTrackOutputButtonHeight() {
        return 150;
    }

    @Override
    protected void init() {
        super.init();
        this.consoleCommandTextField.setText(this.getCommandExecutor().getCommand());
    }

    @Override
    protected void syncSettingsToServer() {
        this.client.getNetworkHandler().sendPacket(new UpdateCommandBlockMinecartC2SPacket(this.field_63536.getId(), this.consoleCommandTextField.getText(), this.field_63536.getCommandExecutor().isTrackingOutput()));
    }
}
