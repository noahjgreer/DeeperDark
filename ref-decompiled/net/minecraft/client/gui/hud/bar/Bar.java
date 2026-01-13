/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.bar.Bar
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.util.Window
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.hud.bar;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface Bar {
    public static final int WIDTH = 182;
    public static final int HEIGHT = 5;
    public static final int VERTICAL_OFFSET = 24;
    public static final Bar EMPTY = new /* Unavailable Anonymous Inner Class!! */;

    default public int getCenterX(Window window) {
        return (window.getScaledWidth() - 182) / 2;
    }

    default public int getCenterY(Window window) {
        return window.getScaledHeight() - 24 - 5;
    }

    public void renderBar(DrawContext var1, RenderTickCounter var2);

    public void renderAddons(DrawContext var1, RenderTickCounter var2);

    public static void drawExperienceLevel(DrawContext context, TextRenderer textRenderer, int level) {
        MutableText text = Text.translatable((String)"gui.experience.level", (Object[])new Object[]{level});
        int i = (context.getScaledWindowWidth() - textRenderer.getWidth((StringVisitable)text)) / 2;
        int n = context.getScaledWindowHeight() - 24;
        Objects.requireNonNull(textRenderer);
        int j = n - 9 - 2;
        context.drawText(textRenderer, (Text)text, i + 1, j, -16777216, false);
        context.drawText(textRenderer, (Text)text, i - 1, j, -16777216, false);
        context.drawText(textRenderer, (Text)text, i, j + 1, -16777216, false);
        context.drawText(textRenderer, (Text)text, i, j - 1, -16777216, false);
        context.drawText(textRenderer, (Text)text, i, j, -8323296, false);
    }
}

