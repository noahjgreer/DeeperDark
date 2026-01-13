/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.particle;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.particle.ElderGuardianParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
static final class ElderGuardianParticleRenderer.State
extends Record {
    final Model<Unit> model;
    final MatrixStack matrices;
    final RenderLayer renderLayer;
    final int color;

    private ElderGuardianParticleRenderer.State(Model<Unit> model, MatrixStack matrices, RenderLayer renderLayer, int color) {
        this.model = model;
        this.matrices = matrices;
        this.renderLayer = renderLayer;
        this.color = color;
    }

    public static ElderGuardianParticleRenderer.State create(ElderGuardianParticle particle, Camera camera, float tickProgress) {
        float f = ((float)particle.age + tickProgress) / (float)particle.maxAge;
        float g = 0.05f + 0.5f * MathHelper.sin(f * (float)Math.PI);
        int i = ColorHelper.fromFloats(g, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        matrixStack.multiply((Quaternionfc)camera.getRotation());
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(60.0f - 150.0f * f));
        float h = 0.42553192f;
        matrixStack.scale(0.42553192f, -0.42553192f, -0.42553192f);
        matrixStack.translate(0.0f, -0.56f, 3.5f);
        return new ElderGuardianParticleRenderer.State(particle.model, matrixStack, particle.renderLayer, i);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ElderGuardianParticleRenderer.State.class, "model;poseStack;renderType;color", "model", "matrices", "renderLayer", "color"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ElderGuardianParticleRenderer.State.class, "model;poseStack;renderType;color", "model", "matrices", "renderLayer", "color"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ElderGuardianParticleRenderer.State.class, "model;poseStack;renderType;color", "model", "matrices", "renderLayer", "color"}, this, object);
    }

    public Model<Unit> model() {
        return this.model;
    }

    public MatrixStack matrices() {
        return this.matrices;
    }

    public RenderLayer renderLayer() {
        return this.renderLayer;
    }

    public int color() {
        return this.color;
    }
}
