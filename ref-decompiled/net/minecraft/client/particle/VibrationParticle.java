/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BillboardParticleSubmittable
 *  net.minecraft.client.particle.VibrationParticle
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.event.PositionSource
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.PositionSource;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class VibrationParticle
extends BillboardParticle {
    private final PositionSource vibration;
    private float field_28250;
    private float field_28248;
    private float field_40507;
    private float field_40508;

    VibrationParticle(ClientWorld world, double x, double y, double z, PositionSource vibration, int maxAge, Sprite sprite) {
        super(world, x, y, z, 0.0, 0.0, 0.0, sprite);
        this.scale = 0.3f;
        this.vibration = vibration;
        this.maxAge = maxAge;
        Optional optional = vibration.getPos((World)world);
        if (optional.isPresent()) {
            Vec3d vec3d = (Vec3d)optional.get();
            double d = x - vec3d.getX();
            double e = y - vec3d.getY();
            double f = z - vec3d.getZ();
            this.field_28248 = this.field_28250 = (float)MathHelper.atan2((double)d, (double)f);
            this.field_40508 = this.field_40507 = (float)MathHelper.atan2((double)e, (double)Math.sqrt(d * d + f * f));
        }
    }

    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        float f = MathHelper.sin((double)(((float)this.age + tickProgress - (float)Math.PI * 2) * 0.05f)) * 2.0f;
        float g = MathHelper.lerp((float)tickProgress, (float)this.field_28248, (float)this.field_28250);
        float h = MathHelper.lerp((float)tickProgress, (float)this.field_40508, (float)this.field_40507) + 1.5707964f;
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationY(g).rotateX(-h).rotateY(f);
        this.render(submittable, camera, quaternionf, tickProgress);
        quaternionf.rotationY((float)(-Math.PI) + g).rotateX(h).rotateY(f);
        this.render(submittable, camera, quaternionf, tickProgress);
    }

    public int getBrightness(float tint) {
        return 240;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        Optional optional = this.vibration.getPos((World)this.world);
        if (optional.isEmpty()) {
            this.markDead();
            return;
        }
        int i = this.maxAge - this.age;
        double d = 1.0 / (double)i;
        Vec3d vec3d = (Vec3d)optional.get();
        this.x = MathHelper.lerp((double)d, (double)this.x, (double)vec3d.getX());
        this.y = MathHelper.lerp((double)d, (double)this.y, (double)vec3d.getY());
        this.z = MathHelper.lerp((double)d, (double)this.z, (double)vec3d.getZ());
        double e = this.x - vec3d.getX();
        double f = this.y - vec3d.getY();
        double g = this.z - vec3d.getZ();
        this.field_28248 = this.field_28250;
        this.field_28250 = (float)MathHelper.atan2((double)e, (double)g);
        this.field_40508 = this.field_40507;
        this.field_40507 = (float)MathHelper.atan2((double)f, (double)Math.sqrt(e * e + g * g));
    }
}

