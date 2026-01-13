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
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.OrderedText;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

@Environment(value=EnvType.CLIENT)
static class ChatHud.Forwarder
implements ChatHud.Backend {
    private final DrawnTextConsumer drawer;

    public ChatHud.Forwarder(DrawnTextConsumer drawer) {
        this.drawer = drawer;
    }

    @Override
    public void updatePose(Consumer<Matrix3x2f> transformer) {
        DrawnTextConsumer.Transformation transformation = this.drawer.getTransformation();
        Matrix3x2f matrix3x2f = new Matrix3x2f(transformation.pose());
        transformer.accept(matrix3x2f);
        this.drawer.setTransformation(transformation.withPose((Matrix3x2fc)matrix3x2f));
    }

    @Override
    public void fill(int x1, int y1, int x2, int y2, int color) {
    }

    @Override
    public boolean text(int y, float opacity, OrderedText text) {
        this.drawer.text(Alignment.LEFT, 0, y, text);
        return false;
    }

    @Override
    public void indicator(int x1, int y1, int x2, int y2, float opacity, MessageIndicator indicator) {
    }

    @Override
    public void indicatorIcon(int left, int bottom, boolean forceDraw, MessageIndicator indicator, MessageIndicator.Icon icon) {
    }
}
