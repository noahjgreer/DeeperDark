/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.openal.AL10
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundListenerTransform;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.openal.AL10;

@Environment(value=EnvType.CLIENT)
public class SoundListener {
    private SoundListenerTransform transform = SoundListenerTransform.DEFAULT;

    public void setTransform(SoundListenerTransform transform) {
        this.transform = transform;
        Vec3d vec3d = transform.position();
        Vec3d vec3d2 = transform.forward();
        Vec3d vec3d3 = transform.up();
        AL10.alListener3f((int)4100, (float)((float)vec3d.x), (float)((float)vec3d.y), (float)((float)vec3d.z));
        AL10.alListenerfv((int)4111, (float[])new float[]{(float)vec3d2.x, (float)vec3d2.y, (float)vec3d2.z, (float)vec3d3.getX(), (float)vec3d3.getY(), (float)vec3d3.getZ()});
    }

    public void init() {
        this.setTransform(SoundListenerTransform.DEFAULT);
    }

    public SoundListenerTransform getTransform() {
        return this.transform;
    }
}
