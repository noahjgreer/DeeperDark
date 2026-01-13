/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.Submittable;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ItemPickupParticleRenderer
extends ParticleRenderer<ItemPickupParticle> {
    public ItemPickupParticleRenderer(ParticleManager particleManager) {
        super(particleManager);
    }

    @Override
    public Submittable render(Frustum frustum, Camera camera, float tickProgress) {
        return new Result(this.particles.stream().map(particle -> Instance.create(particle, camera, tickProgress)).toList());
    }

    @Environment(value=EnvType.CLIENT)
    record Result(List<Instance> instances) implements Submittable
    {
        @Override
        public void submit(OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
            MatrixStack matrixStack = new MatrixStack();
            EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
            for (Instance instance : this.instances) {
                entityRenderManager.render(instance.itemRenderState, cameraRenderState, instance.xOffset, instance.yOffset, instance.zOffset, matrixStack, orderedRenderCommandQueue);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Instance
    extends Record {
        final EntityRenderState itemRenderState;
        final double xOffset;
        final double yOffset;
        final double zOffset;

        private Instance(EntityRenderState itemRenderState, double xOffset, double yOffset, double zOffset) {
            this.itemRenderState = itemRenderState;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
        }

        public static Instance create(ItemPickupParticle particle, Camera camera, float tickProgress) {
            float f = ((float)particle.ticksExisted + tickProgress) / 3.0f;
            f *= f;
            double d = MathHelper.lerp((double)tickProgress, particle.lastTargetX, particle.targetX);
            double e = MathHelper.lerp((double)tickProgress, particle.lastTargetY, particle.targetY);
            double g = MathHelper.lerp((double)tickProgress, particle.lastTargetZ, particle.targetZ);
            double h = MathHelper.lerp((double)f, particle.renderState.x, d);
            double i = MathHelper.lerp((double)f, particle.renderState.y, e);
            double j = MathHelper.lerp((double)f, particle.renderState.z, g);
            Vec3d vec3d = camera.getCameraPos();
            return new Instance(particle.renderState, h - vec3d.getX(), i - vec3d.getY(), j - vec3d.getZ());
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Instance.class, "itemRenderState;xOffset;yOffset;zOffset", "itemRenderState", "xOffset", "yOffset", "zOffset"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Instance.class, "itemRenderState;xOffset;yOffset;zOffset", "itemRenderState", "xOffset", "yOffset", "zOffset"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Instance.class, "itemRenderState;xOffset;yOffset;zOffset", "itemRenderState", "xOffset", "yOffset", "zOffset"}, this, object);
        }

        public EntityRenderState itemRenderState() {
            return this.itemRenderState;
        }

        public double xOffset() {
            return this.xOffset;
        }

        public double yOffset() {
            return this.yOffset;
        }

        public double zOffset() {
            return this.zOffset;
        }
    }
}
