/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.render.ProfilerChartGuiElementRenderer
 *  net.minecraft.client.gui.render.SpecialGuiElementRenderer
 *  net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.profiler.ProfilerTiming
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.gui.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.ProfilerTiming;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public class ProfilerChartGuiElementRenderer
extends SpecialGuiElementRenderer<ProfilerChartGuiElementRenderState> {
    public ProfilerChartGuiElementRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    public Class<ProfilerChartGuiElementRenderState> getElementClass() {
        return ProfilerChartGuiElementRenderState.class;
    }

    protected void render(ProfilerChartGuiElementRenderState profilerChartGuiElementRenderState, MatrixStack matrixStack) {
        double d = 0.0;
        matrixStack.translate(0.0f, -5.0f, 0.0f);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        for (ProfilerTiming profilerTiming : profilerChartGuiElementRenderState.chartData()) {
            float h;
            float g;
            float f;
            int l;
            int i = MathHelper.floor((double)(profilerTiming.parentSectionUsagePercentage / 4.0)) + 1;
            VertexConsumer vertexConsumer = this.vertexConsumers.getBuffer(RenderLayers.debugTriangleFan());
            int j = ColorHelper.fullAlpha((int)profilerTiming.getColor());
            int k = ColorHelper.mix((int)j, (int)-8355712);
            vertexConsumer.vertex((Matrix4fc)matrix4f, 0.0f, 0.0f, 0.0f).color(j);
            for (l = i; l >= 0; --l) {
                f = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)l / (double)i) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin((double)f) * 105.0f;
                h = MathHelper.cos((double)f) * 105.0f * 0.5f;
                vertexConsumer.vertex((Matrix4fc)matrix4f, g, h, 0.0f).color(j);
            }
            vertexConsumer = this.vertexConsumers.getBuffer(RenderLayers.debugQuads());
            for (l = i; l > 0; --l) {
                f = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)l / (double)i) * 6.2831854820251465 / 100.0);
                g = MathHelper.sin((double)f) * 105.0f;
                h = MathHelper.cos((double)f) * 105.0f * 0.5f;
                float m = (float)((d + profilerTiming.parentSectionUsagePercentage * (double)(l - 1) / (double)i) * 6.2831854820251465 / 100.0);
                float n = MathHelper.sin((double)m) * 105.0f;
                float o = MathHelper.cos((double)m) * 105.0f * 0.5f;
                if ((h + o) / 2.0f < 0.0f) continue;
                vertexConsumer.vertex((Matrix4fc)matrix4f, g, h, 0.0f).color(k);
                vertexConsumer.vertex((Matrix4fc)matrix4f, g, h + 10.0f, 0.0f).color(k);
                vertexConsumer.vertex((Matrix4fc)matrix4f, n, o + 10.0f, 0.0f).color(k);
                vertexConsumer.vertex((Matrix4fc)matrix4f, n, o, 0.0f).color(k);
            }
            d += profilerTiming.parentSectionUsagePercentage;
        }
    }

    protected float getYOffset(int height, int windowScaleFactor) {
        return (float)height / 2.0f;
    }

    protected String getName() {
        return "profiler chart";
    }
}

