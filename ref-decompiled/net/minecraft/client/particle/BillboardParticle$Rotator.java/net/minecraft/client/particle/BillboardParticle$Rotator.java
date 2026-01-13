/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public static interface BillboardParticle.Rotator {
    public static final BillboardParticle.Rotator ALL_AXIS = (quaternion, camera, tickProgress) -> quaternion.set((Quaternionfc)camera.getRotation());
    public static final BillboardParticle.Rotator Y_AND_W_ONLY = (quaternion, camera, tickProgress) -> quaternion.set(0.0f, camera.getRotation().y, 0.0f, camera.getRotation().w);

    public void setRotation(Quaternionf var1, Camera var2, float var3);
}
