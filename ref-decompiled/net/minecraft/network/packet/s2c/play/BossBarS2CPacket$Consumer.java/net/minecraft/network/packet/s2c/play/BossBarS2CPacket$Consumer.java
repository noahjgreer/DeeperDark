/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;

public static interface BossBarS2CPacket.Consumer {
    default public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
    }

    default public void remove(UUID uuid) {
    }

    default public void updateProgress(UUID uuid, float percent) {
    }

    default public void updateName(UUID uuid, Text name) {
    }

    default public void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
    }

    default public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
    }
}
