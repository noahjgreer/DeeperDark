/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class GlobalSettings
implements AutoCloseable {
    public static final int SIZE = new Std140SizeCalculator().putIVec3().putVec3().putVec2().putFloat().putFloat().putInt().putInt().get();
    private final GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Global Settings UBO", 136, SIZE);

    public void set(int width, int height, double glintStrength, long time, RenderTickCounter tickCounter, int menuBackgroundBlurriness, Camera camera, boolean bl) {
        Vec3d vec3d = camera.getCameraPos();
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            int i = MathHelper.floor(vec3d.x);
            int j = MathHelper.floor(vec3d.y);
            int k = MathHelper.floor(vec3d.z);
            ByteBuffer byteBuffer = Std140Builder.onStack(memoryStack, SIZE).putIVec3(i, j, k).putVec3((float)((double)i - vec3d.x), (float)((double)j - vec3d.y), (float)((double)k - vec3d.z)).putVec2(width, height).putFloat((float)glintStrength).putFloat(((float)(time % 24000L) + tickCounter.getTickProgress(false)) / 24000.0f).putInt(menuBackgroundBlurriness).putInt(bl ? 1 : 0).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice(), byteBuffer);
        }
        RenderSystem.setGlobalSettingsUniform(this.buffer);
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}
