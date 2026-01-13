/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInterpolateSizeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderSizeChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningBlocksChangedS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderWarningTimeChangedS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;

class PlayerManager.1
implements WorldBorderListener {
    final /* synthetic */ ServerWorld field_62506;

    PlayerManager.1() {
        this.field_62506 = serverWorld;
    }

    @Override
    public void onSizeChange(WorldBorder border, double size) {
        PlayerManager.this.sendToDimension(new WorldBorderSizeChangedS2CPacket(border), this.field_62506.getRegistryKey());
    }

    @Override
    public void onInterpolateSize(WorldBorder border, double fromSize, double toSize, long time, long l) {
        PlayerManager.this.sendToDimension(new WorldBorderInterpolateSizeS2CPacket(border), this.field_62506.getRegistryKey());
    }

    @Override
    public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
        PlayerManager.this.sendToDimension(new WorldBorderCenterChangedS2CPacket(border), this.field_62506.getRegistryKey());
    }

    @Override
    public void onWarningTimeChanged(WorldBorder border, int warningTime) {
        PlayerManager.this.sendToDimension(new WorldBorderWarningTimeChangedS2CPacket(border), this.field_62506.getRegistryKey());
    }

    @Override
    public void onWarningBlocksChanged(WorldBorder border, int warningBlockDistance) {
        PlayerManager.this.sendToDimension(new WorldBorderWarningBlocksChangedS2CPacket(border), this.field_62506.getRegistryKey());
    }

    @Override
    public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
    }

    @Override
    public void onSafeZoneChanged(WorldBorder border, double safeZoneRadius) {
    }
}
