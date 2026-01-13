/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.DrawnTextConsumer$Transformation
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.SplashTextRenderer
 *  net.minecraft.client.resource.SplashTextResourceSupplier
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Matrix3x2f
 *  org.joml.Matrix3x2fc
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

@Environment(value=EnvType.CLIENT)
public class SplashTextRenderer {
    public static final SplashTextRenderer MERRY_X_MAS = new SplashTextRenderer(SplashTextResourceSupplier.MERRY_X_MAS_);
    public static final SplashTextRenderer HAPPY_NEW_YEAR = new SplashTextRenderer(SplashTextResourceSupplier.HAPPY_NEW_YEAR_);
    public static final SplashTextRenderer OOOOO_O_O_OOOOO__SPOOKY = new SplashTextRenderer(SplashTextResourceSupplier.OOOOO_O_O_OOOOO__SPOOKY_);
    private static final int TEXT_X = 123;
    private static final int TEXT_Y = 69;
    private static final float TEXT_ROTATION = -0.34906584f;
    private final Text text;

    public SplashTextRenderer(Text text) {
        this.text = text;
    }

    public void render(DrawContext context, int screenWidth, TextRenderer textRenderer, float alpha) {
        int i = textRenderer.getWidth((StringVisitable)this.text);
        DrawnTextConsumer drawnTextConsumer = context.getTextConsumer();
        float f = 1.8f - MathHelper.abs((float)(MathHelper.sin((double)((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0f * ((float)Math.PI * 2))) * 0.1f));
        float g = f * 100.0f / (float)(i + 32);
        Matrix3x2f matrix3x2f = new Matrix3x2f(drawnTextConsumer.getTransformation().pose()).translate((float)screenWidth / 2.0f + 123.0f, 69.0f).rotate(-0.34906584f).scale(g);
        DrawnTextConsumer.Transformation transformation = drawnTextConsumer.getTransformation().withOpacity(alpha).withPose((Matrix3x2fc)matrix3x2f);
        drawnTextConsumer.text(Alignment.LEFT, -i / 2, -8, transformation, this.text);
    }
}

