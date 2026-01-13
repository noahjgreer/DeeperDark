/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class BossBarHud.1
implements BossBarS2CPacket.Consumer {
    BossBarHud.1() {
    }

    @Override
    public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        BossBarHud.this.bossBars.put(uuid, new ClientBossBar(uuid, name, percent, color, style, darkenSky, dragonMusic, thickenFog));
    }

    @Override
    public void remove(UUID uuid) {
        BossBarHud.this.bossBars.remove(uuid);
    }

    @Override
    public void updateProgress(UUID uuid, float percent) {
        BossBarHud.this.bossBars.get(uuid).setPercent(percent);
    }

    @Override
    public void updateName(UUID uuid, Text name) {
        BossBarHud.this.bossBars.get(uuid).setName(name);
    }

    @Override
    public void updateStyle(UUID id, BossBar.Color color, BossBar.Style style) {
        ClientBossBar clientBossBar = BossBarHud.this.bossBars.get(id);
        clientBossBar.setColor(color);
        clientBossBar.setStyle(style);
    }

    @Override
    public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        ClientBossBar clientBossBar = BossBarHud.this.bossBars.get(uuid);
        clientBossBar.setDarkenSky(darkenSky);
        clientBossBar.setDragonMusic(dragonMusic);
        clientBossBar.setThickenFog(thickenFog);
    }
}
