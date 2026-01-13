/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.client.render.DiffuseLighting$1
 *  net.minecraft.client.render.DiffuseLighting$Type
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.dimension.DimensionType$CardinalLightType
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DiffuseLighting
implements AutoCloseable {
    private static final Vector3f DEFAULT_DIFFUSION_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f DEFAULT_DIFFUSION_LIGHT_1 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    private static final Vector3f DARKENED_DIFFUSION_LIGHT_0 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f DARKENED_DIFFUSION_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.7f).normalize();
    private static final Vector3f INVENTORY_DIFFUSION_LIGHT_0 = new Vector3f(0.2f, -1.0f, 1.0f).normalize();
    private static final Vector3f INVENTORY_DIFFUSION_LIGHT_1 = new Vector3f(-0.2f, -1.0f, 0.0f).normalize();
    public static final int UBO_SIZE = new Std140SizeCalculator().putVec3().putVec3().get();
    private final GpuBuffer buffer;
    private final long roundedUboSize;

    public DiffuseLighting() {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.roundedUboSize = MathHelper.roundUpToMultiple((int)UBO_SIZE, (int)gpuDevice.getUniformOffsetAlignment());
        this.buffer = gpuDevice.createBuffer(() -> "Lighting UBO", 136, this.roundedUboSize * (long)Type.values().length);
        Matrix4f matrix4f = new Matrix4f().rotationY(-0.3926991f).rotateX(2.3561945f);
        this.updateBuffer(Type.ITEMS_FLAT, matrix4f.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_0, new Vector3f()), matrix4f.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_1, new Vector3f()));
        Matrix4f matrix4f2 = new Matrix4f().scaling(1.0f, -1.0f, 1.0f).rotateYXZ(1.0821041f, 3.2375858f, 0.0f).rotateYXZ(-0.3926991f, 2.3561945f, 0.0f);
        this.updateBuffer(Type.ITEMS_3D, matrix4f2.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_0, new Vector3f()), matrix4f2.transformDirection((Vector3fc)DEFAULT_DIFFUSION_LIGHT_1, new Vector3f()));
        this.updateBuffer(Type.ENTITY_IN_UI, INVENTORY_DIFFUSION_LIGHT_0, INVENTORY_DIFFUSION_LIGHT_1);
        Matrix4f matrix4f3 = new Matrix4f();
        this.updateBuffer(Type.PLAYER_SKIN, matrix4f3.transformDirection((Vector3fc)INVENTORY_DIFFUSION_LIGHT_0, new Vector3f()), matrix4f3.transformDirection((Vector3fc)INVENTORY_DIFFUSION_LIGHT_1, new Vector3f()));
    }

    public void updateLevelBuffer(DimensionType.CardinalLightType cardinalLightType) {
        switch (1.field_64417[cardinalLightType.ordinal()]) {
            case 1: {
                this.updateBuffer(Type.LEVEL, DEFAULT_DIFFUSION_LIGHT_0, DEFAULT_DIFFUSION_LIGHT_1);
                break;
            }
            case 2: {
                this.updateBuffer(Type.LEVEL, DARKENED_DIFFUSION_LIGHT_0, DARKENED_DIFFUSION_LIGHT_1);
            }
        }
    }

    private void updateBuffer(Type type, Vector3f light0Diffusion, Vector3f light1Diffusion) {
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = Std140Builder.onStack((MemoryStack)memoryStack, (int)UBO_SIZE).putVec3((Vector3fc)light0Diffusion).putVec3((Vector3fc)light1Diffusion).get();
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice((long)type.ordinal() * this.roundedUboSize, this.roundedUboSize), byteBuffer);
        }
    }

    public void setShaderLights(Type type) {
        RenderSystem.setShaderLights((GpuBufferSlice)this.buffer.slice((long)type.ordinal() * this.roundedUboSize, (long)UBO_SIZE));
    }

    @Override
    public void close() {
        this.buffer.close();
    }
}

