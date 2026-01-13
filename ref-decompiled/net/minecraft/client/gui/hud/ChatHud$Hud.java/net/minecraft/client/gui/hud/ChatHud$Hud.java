/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 */
package net.minecraft.client.gui.hud;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

@Environment(value=EnvType.CLIENT)
static class ChatHud.Hud
implements ChatHud.Backend {
    private final DrawContext context;
    private final DrawnTextConsumer textConsumer;
    private DrawnTextConsumer.Transformation transformation;

    public ChatHud.Hud(DrawContext context) {
        this.context = context;
        this.textConsumer = context.getTextConsumer(DrawContext.HoverType.NONE, null);
        this.transformation = this.textConsumer.getTransformation();
    }

    @Override
    public void updatePose(Consumer<Matrix3x2f> transformer) {
        transformer.accept((Matrix3x2f)this.context.getMatrices());
        this.transformation = this.transformation.withPose((Matrix3x2fc)new Matrix3x2f((Matrix3x2fc)this.context.getMatrices()));
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
        this.context.fill(x1, y1, x2, y2, color);
    }

    @Override
    public boolean text(int y, float opacity, OrderedText text) {
        this.textConsumer.text(Alignment.LEFT, 0, y, this.transformation.withOpacity(opacity), text);
        return false;
    }

    @Override
    public void indicator(int x1, int y1, int x2, int y2, float opacity, MessageIndicator indicator) {
        int i = ColorHelper.withAlpha(opacity, indicator.indicatorColor());
        this.context.fill(x1, y1, x2, y2, i);
    }

    @Override
    public void indicatorIcon(int left, int bottom, boolean forceDraw, MessageIndicator indicator, MessageIndicator.Icon icon) {
    }
}
