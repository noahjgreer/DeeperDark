/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.SubtitlesHud
 *  net.minecraft.client.gui.hud.SubtitlesHud$SoundEntry
 *  net.minecraft.client.gui.hud.SubtitlesHud$SubtitleEntry
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstanceListener
 *  net.minecraft.client.sound.SoundListenerTransform
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.client.sound.WeightedSoundSet
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class SubtitlesHud
implements SoundInstanceListener {
    private static final long REMOVE_DELAY = 3000L;
    private final MinecraftClient client;
    private final List<SubtitleEntry> entries = Lists.newArrayList();
    private boolean enabled;
    private final List<SubtitleEntry> audibleEntries = new ArrayList();

    public SubtitlesHud(MinecraftClient client) {
        this.client = client;
    }

    public void render(DrawContext context) {
        SoundManager soundManager = this.client.getSoundManager();
        if (!this.enabled && ((Boolean)this.client.options.getShowSubtitles().getValue()).booleanValue()) {
            soundManager.registerListener((SoundInstanceListener)this);
            this.enabled = true;
        } else if (this.enabled && !((Boolean)this.client.options.getShowSubtitles().getValue()).booleanValue()) {
            soundManager.unregisterListener((SoundInstanceListener)this);
            this.enabled = false;
        }
        if (!this.enabled) {
            return;
        }
        SoundListenerTransform soundListenerTransform = soundManager.getListenerTransform();
        Vec3d vec3d = soundListenerTransform.position();
        Vec3d vec3d2 = soundListenerTransform.forward();
        Vec3d vec3d3 = soundListenerTransform.right();
        this.audibleEntries.clear();
        for (SubtitleEntry subtitleEntry : this.entries) {
            if (!subtitleEntry.canHearFrom(vec3d)) continue;
            this.audibleEntries.add(subtitleEntry);
        }
        if (this.audibleEntries.isEmpty()) {
            return;
        }
        int i = 0;
        int j = 0;
        double d = (Double)this.client.options.getNotificationDisplayTime().getValue();
        Iterator iterator = this.audibleEntries.iterator();
        while (iterator.hasNext()) {
            SubtitleEntry subtitleEntry2 = (SubtitleEntry)iterator.next();
            subtitleEntry2.removeExpired(3000.0 * d);
            if (!subtitleEntry2.hasSounds()) {
                iterator.remove();
                continue;
            }
            j = Math.max(j, this.client.textRenderer.getWidth((StringVisitable)subtitleEntry2.getText()));
        }
        j += this.client.textRenderer.getWidth("<") + this.client.textRenderer.getWidth(" ") + this.client.textRenderer.getWidth(">") + this.client.textRenderer.getWidth(" ");
        if (!this.audibleEntries.isEmpty()) {
            context.createNewRootLayer();
        }
        for (SubtitleEntry subtitleEntry2 : this.audibleEntries) {
            int k = 255;
            Text text = subtitleEntry2.getText();
            SoundEntry soundEntry = subtitleEntry2.getNearestSound(vec3d);
            if (soundEntry == null) continue;
            Vec3d vec3d4 = soundEntry.location.subtract(vec3d).normalize();
            double e = vec3d3.dotProduct(vec3d4);
            double f = vec3d2.dotProduct(vec3d4);
            boolean bl = f > 0.5;
            int l = j / 2;
            Objects.requireNonNull(this.client.textRenderer);
            int m = 9;
            int n = m / 2;
            float g = 1.0f;
            int o = this.client.textRenderer.getWidth((StringVisitable)text);
            int p = MathHelper.floor((float)MathHelper.clampedLerp((float)((float)(Util.getMeasuringTimeMs() - soundEntry.time) / (float)(3000.0 * d)), (float)255.0f, (float)75.0f));
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)context.getScaledWindowWidth() - (float)l * 1.0f - 2.0f, (float)(context.getScaledWindowHeight() - 35) - (float)(i * (m + 1)) * 1.0f);
            context.getMatrices().scale(1.0f, 1.0f);
            context.fill(-l - 1, -n - 1, l + 1, n + 1, this.client.options.getTextBackgroundColor(0.8f));
            int q = ColorHelper.getArgb((int)255, (int)p, (int)p, (int)p);
            if (!bl) {
                if (e > 0.0) {
                    context.drawTextWithShadow(this.client.textRenderer, ">", l - this.client.textRenderer.getWidth(">"), -n, q);
                } else if (e < 0.0) {
                    context.drawTextWithShadow(this.client.textRenderer, "<", -l, -n, q);
                }
            }
            context.drawTextWithShadow(this.client.textRenderer, text, -o / 2, -n, q);
            context.getMatrices().popMatrix();
            ++i;
        }
    }

    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet, float range) {
        if (soundSet.getSubtitle() == null) {
            return;
        }
        Text text = soundSet.getSubtitle();
        if (!this.entries.isEmpty()) {
            for (SubtitleEntry subtitleEntry : this.entries) {
                if (!subtitleEntry.getText().equals((Object)text)) continue;
                subtitleEntry.reset(new Vec3d(sound.getX(), sound.getY(), sound.getZ()));
                return;
            }
        }
        this.entries.add(new SubtitleEntry(text, range, new Vec3d(sound.getX(), sound.getY(), sound.getZ())));
    }
}

