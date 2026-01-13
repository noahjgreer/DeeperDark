/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.enums.CameraSubmersionType
 *  net.minecraft.client.gl.MappableRingBuffer
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.fog.AtmosphericFogModifier
 *  net.minecraft.client.render.fog.BlindnessEffectFogModifier
 *  net.minecraft.client.render.fog.DarknessEffectFogModifier
 *  net.minecraft.client.render.fog.FogData
 *  net.minecraft.client.render.fog.FogModifier
 *  net.minecraft.client.render.fog.FogRenderer
 *  net.minecraft.client.render.fog.FogRenderer$FogType
 *  net.minecraft.client.render.fog.LavaFogModifier
 *  net.minecraft.client.render.fog.PowderSnowFogModifier
 *  net.minecraft.client.render.fog.WaterFogModifier
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 *  org.lwjgl.system.MemoryStack
 */
package net.minecraft.client.render.fog;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.AtmosphericFogModifier;
import net.minecraft.client.render.fog.BlindnessEffectFogModifier;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.render.fog.LavaFogModifier;
import net.minecraft.client.render.fog.PowderSnowFogModifier;
import net.minecraft.client.render.fog.WaterFogModifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public class FogRenderer
implements AutoCloseable {
    public static final int FOG_UBO_SIZE = new Std140SizeCalculator().putVec4().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().get();
    private static final List<FogModifier> FOG_MODIFIERS = Lists.newArrayList((Object[])new FogModifier[]{new LavaFogModifier(), new PowderSnowFogModifier(), new BlindnessEffectFogModifier(), new DarknessEffectFogModifier(), new WaterFogModifier(), new AtmosphericFogModifier()});
    private static boolean fogEnabled = true;
    private final GpuBuffer emptyBuffer;
    private final MappableRingBuffer fogBuffer;

    public FogRenderer() {
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.fogBuffer = new MappableRingBuffer(() -> "Fog UBO", 130, FOG_UBO_SIZE);
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(FOG_UBO_SIZE);
            this.applyFog(byteBuffer, 0, new Vector4f(0.0f), Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            this.emptyBuffer = gpuDevice.createBuffer(() -> "Empty fog", 128, byteBuffer.flip());
        }
        RenderSystem.setShaderFog((GpuBufferSlice)this.getFogBuffer(FogType.NONE));
    }

    @Override
    public void close() {
        this.emptyBuffer.close();
        this.fogBuffer.close();
    }

    public void rotate() {
        this.fogBuffer.rotate();
    }

    public GpuBufferSlice getFogBuffer(FogType fogType) {
        if (!fogEnabled) {
            return this.emptyBuffer.slice(0L, (long)FOG_UBO_SIZE);
        }
        return switch (fogType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> this.emptyBuffer.slice(0L, (long)FOG_UBO_SIZE);
            case 1 -> this.fogBuffer.getBlocking().slice(0L, (long)FOG_UBO_SIZE);
        };
    }

    private Vector4f getFogColor(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness) {
        LivingEntity livingEntity2;
        float l;
        CameraSubmersionType cameraSubmersionType = this.getCameraSubmersionType(camera);
        Entity entity = camera.getFocusedEntity();
        FogModifier fogModifier = null;
        FogModifier fogModifier2 = null;
        for (FogModifier fogModifier3 : FOG_MODIFIERS) {
            if (!fogModifier3.shouldApply(cameraSubmersionType, entity)) continue;
            if (fogModifier == null && fogModifier3.isColorSource()) {
                fogModifier = fogModifier3;
            }
            if (fogModifier2 != null || !fogModifier3.isDarknessModifier()) continue;
            fogModifier2 = fogModifier3;
        }
        if (fogModifier == null) {
            throw new IllegalStateException("No color source environment found");
        }
        int i = fogModifier.getFogColor(world, camera, viewDistance, tickProgress);
        float f = world.getLevelProperties().getVoidDarknessRange();
        float g = MathHelper.clamp((float)((f + (float)world.getBottomY() - (float)camera.getCameraPos().y) / f), (float)0.0f, (float)1.0f);
        if (fogModifier2 != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            g = fogModifier2.applyDarknessModifier(livingEntity, g, tickProgress);
        }
        float h = ColorHelper.getRedFloat((int)i);
        float j = ColorHelper.getGreenFloat((int)i);
        float k = ColorHelper.getBlueFloat((int)i);
        if (g > 0.0f && cameraSubmersionType != CameraSubmersionType.LAVA && cameraSubmersionType != CameraSubmersionType.POWDER_SNOW) {
            l = MathHelper.square((float)(1.0f - g));
            h *= l;
            j *= l;
            k *= l;
        }
        if (skyDarkness > 0.0f) {
            h = MathHelper.lerp((float)skyDarkness, (float)h, (float)(h * 0.7f));
            j = MathHelper.lerp((float)skyDarkness, (float)j, (float)(j * 0.6f));
            k = MathHelper.lerp((float)skyDarkness, (float)k, (float)(k * 0.6f));
        }
        l = cameraSubmersionType == CameraSubmersionType.WATER ? (entity instanceof ClientPlayerEntity ? ((ClientPlayerEntity)entity).getUnderwaterVisibility() : 1.0f) : (entity instanceof LivingEntity && (livingEntity2 = (LivingEntity)entity).hasStatusEffect(StatusEffects.NIGHT_VISION) && !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS) ? GameRenderer.getNightVisionStrength((LivingEntity)livingEntity2, (float)tickProgress) : 0.0f);
        if (h != 0.0f && j != 0.0f && k != 0.0f) {
            float m = 1.0f / Math.max(h, Math.max(j, k));
            h = MathHelper.lerp((float)l, (float)h, (float)(h * m));
            j = MathHelper.lerp((float)l, (float)j, (float)(j * m));
            k = MathHelper.lerp((float)l, (float)k, (float)(k * m));
        }
        return new Vector4f(h, j, k, 1.0f);
    }

    public static boolean toggleFog() {
        fogEnabled = !fogEnabled;
        return fogEnabled;
    }

    public Vector4f applyFog(Camera camera, int viewDistance, RenderTickCounter renderTickCounter, float f, ClientWorld clientWorld) {
        float g = renderTickCounter.getTickProgress(false);
        Vector4f vector4f = this.getFogColor(camera, g, clientWorld, viewDistance, f);
        float h = viewDistance * 16;
        CameraSubmersionType cameraSubmersionType = this.getCameraSubmersionType(camera);
        Entity entity = camera.getFocusedEntity();
        FogData fogData = new FogData();
        for (FogModifier fogModifier : FOG_MODIFIERS) {
            if (!fogModifier.shouldApply(cameraSubmersionType, entity)) continue;
            fogModifier.applyStartEndModifier(fogData, camera, clientWorld, h, renderTickCounter);
            break;
        }
        float i = MathHelper.clamp((float)(h / 10.0f), (float)4.0f, (float)64.0f);
        fogData.renderDistanceStart = h - i;
        fogData.renderDistanceEnd = h;
        try (GpuBuffer.MappedView mappedView = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.fogBuffer.getBlocking(), false, true);){
            this.applyFog(mappedView.data(), 0, vector4f, fogData.environmentalStart, fogData.environmentalEnd, fogData.renderDistanceStart, fogData.renderDistanceEnd, fogData.skyEnd, fogData.cloudEnd);
        }
        return vector4f;
    }

    private CameraSubmersionType getCameraSubmersionType(Camera camera) {
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        if (cameraSubmersionType == CameraSubmersionType.NONE) {
            return CameraSubmersionType.ATMOSPHERIC;
        }
        return cameraSubmersionType;
    }

    private void applyFog(ByteBuffer buffer, int bufPos, Vector4f fogColor, float environmentalStart, float environmentalEnd, float renderDistanceStart, float renderDistanceEnd, float skyEnd, float cloudEnd) {
        buffer.position(bufPos);
        Std140Builder.intoBuffer((ByteBuffer)buffer).putVec4((Vector4fc)fogColor).putFloat(environmentalStart).putFloat(environmentalEnd).putFloat(renderDistanceStart).putFloat(renderDistanceEnd).putFloat(skyEnd).putFloat(cloudEnd);
    }
}

