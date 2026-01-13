/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.ClientBossBar
 *  net.minecraft.entity.boss.BossBar
 *  net.minecraft.entity.boss.BossBar$Color
 *  net.minecraft.entity.boss.BossBar$Style
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.hud;

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ClientBossBar
extends BossBar {
    private static final long HEALTH_CHANGE_ANIMATION_MS = 100L;
    protected float healthLatest;
    protected long timeHealthSet;

    public ClientBossBar(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
        super(uuid, name, color, style);
        this.healthLatest = percent;
        this.percent = percent;
        this.timeHealthSet = Util.getMeasuringTimeMs();
        this.setDarkenSky(darkenSky);
        this.setDragonMusic(dragonMusic);
        this.setThickenFog(thickenFog);
    }

    public void setPercent(float percent) {
        this.percent = this.getPercent();
        this.healthLatest = percent;
        this.timeHealthSet = Util.getMeasuringTimeMs();
    }

    public float getPercent() {
        long l = Util.getMeasuringTimeMs() - this.timeHealthSet;
        float f = MathHelper.clamp((float)((float)l / 100.0f), (float)0.0f, (float)1.0f);
        return MathHelper.lerp((float)f, (float)this.percent, (float)this.healthLatest);
    }
}

