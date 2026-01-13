/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
class ToastManager.Entry<T extends Toast> {
    private static final long DISAPPEAR_TIME = 600L;
    private final T instance;
    final int topIndex;
    final int requiredSpaceCount;
    private long startTime;
    private long fullyVisibleTime;
    Toast.Visibility visibility;
    private long showTime;
    private float visibleWidthPortion;
    protected boolean finishedRendering;
    final /* synthetic */ ToastManager field_2245;

    /*
     * WARNING - Possible parameter corruption
     */
    ToastManager.Entry(T instance, int topIndex, int requiredSpaceCount) {
        this.field_2245 = (ToastManager)toastManager;
        this.instance = instance;
        this.topIndex = topIndex;
        this.requiredSpaceCount = requiredSpaceCount;
        this.init();
    }

    public T getInstance() {
        return this.instance;
    }

    public void init() {
        this.startTime = -1L;
        this.fullyVisibleTime = -1L;
        this.visibility = Toast.Visibility.HIDE;
        this.showTime = 0L;
        this.visibleWidthPortion = 0.0f;
        this.finishedRendering = false;
    }

    public boolean isFinishedRendering() {
        return this.finishedRendering;
    }

    private void updateVisibleWidthPortion(long time) {
        float f = MathHelper.clamp((float)(time - this.startTime) / 600.0f, 0.0f, 1.0f);
        f *= f;
        this.visibleWidthPortion = this.visibility == Toast.Visibility.HIDE ? 1.0f - f : f;
    }

    public void update() {
        long l = Util.getMeasuringTimeMs();
        if (this.startTime == -1L) {
            this.startTime = l;
            this.visibility = Toast.Visibility.SHOW;
        }
        if (this.visibility == Toast.Visibility.SHOW && l - this.startTime <= 600L) {
            this.fullyVisibleTime = l;
        }
        this.showTime = l - this.fullyVisibleTime;
        this.updateVisibleWidthPortion(l);
        this.instance.update(this.field_2245, this.showTime);
        Toast.Visibility visibility = this.instance.getVisibility();
        if (visibility != this.visibility) {
            this.startTime = l - (long)((int)((1.0f - this.visibleWidthPortion) * 600.0f));
            this.visibility = visibility;
        }
        boolean bl = this.finishedRendering;
        boolean bl2 = this.finishedRendering = this.visibility == Toast.Visibility.HIDE && l - this.startTime > 600L;
        if (this.finishedRendering && !bl) {
            this.instance.onFinishedRendering();
        }
    }

    public void draw(DrawContext context, int scaledWindowWidth) {
        if (this.finishedRendering) {
            return;
        }
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(this.instance.getXPos(scaledWindowWidth, this.visibleWidthPortion), this.instance.getYPos(this.topIndex));
        this.instance.draw(context, this.field_2245.client.textRenderer, this.showTime);
        context.getMatrices().popMatrix();
    }
}
