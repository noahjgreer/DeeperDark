/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer$MappedView
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.GpuDevice
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.textures.TextureFormat
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.MappableRingBuffer
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.EndLightFlashManager
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.LightmapTextureManager
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.util.profiler.Profilers
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.dimension.DimensionType
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.EndLightFlashManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class LightmapTextureManager
implements AutoCloseable {
    public static final int MAX_LIGHT_COORDINATE = 0xF000F0;
    public static final int MAX_SKY_LIGHT_COORDINATE = 0xF00000;
    public static final int MAX_BLOCK_LIGHT_COORDINATE = 240;
    private static final int field_53098 = 16;
    private static final int UBO_SIZE = new Std140SizeCalculator().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putFloat().putVec3().putVec3().get();
    private final GpuTexture glTexture;
    private final GpuTextureView glTextureView;
    private boolean dirty;
    private float flickerIntensity;
    private final GameRenderer renderer;
    private final MinecraftClient client;
    private final MappableRingBuffer buffer;
    private final Random field_64675 = Random.create();

    public LightmapTextureManager(GameRenderer gameRenderer, MinecraftClient client) {
        this.renderer = gameRenderer;
        this.client = client;
        GpuDevice gpuDevice = RenderSystem.getDevice();
        this.glTexture = gpuDevice.createTexture("Light Texture", 12, TextureFormat.RGBA8, 16, 16, 1, 1);
        this.glTextureView = gpuDevice.createTextureView(this.glTexture);
        gpuDevice.createCommandEncoder().clearColorTexture(this.glTexture, -1);
        this.buffer = new MappableRingBuffer(() -> "Lightmap UBO", 130, UBO_SIZE);
    }

    public GpuTextureView getGlTextureView() {
        return this.glTextureView;
    }

    @Override
    public void close() {
        this.glTexture.close();
        this.glTextureView.close();
        this.buffer.close();
    }

    public void tick() {
        this.flickerIntensity += (this.field_64675.nextFloat() - this.field_64675.nextFloat()) * this.field_64675.nextFloat() * this.field_64675.nextFloat() * 0.1f;
        this.flickerIntensity *= 0.9f;
        this.dirty = true;
    }

    private float getDarkness(LivingEntity entity, float factor, float tickProgress) {
        float f = 0.45f * factor;
        return Math.max(0.0f, MathHelper.cos((double)(((float)entity.age - tickProgress) * (float)Math.PI * 0.025f)) * f);
    }

    public void update(float tickProgress) {
        float h;
        Vector3f vector3f;
        if (!this.dirty) {
            return;
        }
        this.dirty = false;
        Profiler profiler = Profilers.get();
        profiler.push("lightTex");
        ClientWorld clientWorld = this.client.world;
        if (clientWorld == null) {
            return;
        }
        Camera camera = this.client.gameRenderer.getCamera();
        int i = (Integer)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SKY_LIGHT_COLOR_VISUAL, tickProgress);
        float f = clientWorld.getDimension().ambientLight();
        float g = ((Float)camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SKY_LIGHT_FACTOR_VISUAL, tickProgress)).floatValue();
        EndLightFlashManager endLightFlashManager = clientWorld.getEndLightFlashManager();
        if (endLightFlashManager != null) {
            vector3f = new Vector3f(0.99f, 1.12f, 1.0f);
            if (!((Boolean)this.client.options.getHideLightningFlashes().getValue()).booleanValue()) {
                h = endLightFlashManager.getSkyFactor(tickProgress);
                g = this.client.inGameHud.getBossBarHud().shouldThickenFog() ? (g += h / 3.0f) : (g += h);
            }
        } else {
            vector3f = new Vector3f(1.0f, 1.0f, 1.0f);
        }
        h = ((Double)this.client.options.getDarknessEffectScale().getValue()).floatValue();
        float j = this.client.player.getEffectFadeFactor(StatusEffects.DARKNESS, tickProgress) * h;
        float k = this.getDarkness((LivingEntity)this.client.player, j, tickProgress) * h;
        float l = this.client.player.getUnderwaterVisibility();
        float m = this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION) ? GameRenderer.getNightVisionStrength((LivingEntity)this.client.player, (float)tickProgress) : (l > 0.0f && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER) ? l : 0.0f);
        float n = this.flickerIntensity + 1.5f;
        float o = ((Double)this.client.options.getGamma().getValue()).floatValue();
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(this.buffer.getBlocking(), false, true);){
            Std140Builder.intoBuffer((ByteBuffer)mappedView.data()).putFloat(f).putFloat(g).putFloat(n).putFloat(m).putFloat(k).putFloat(this.renderer.getSkyDarkness(tickProgress)).putFloat(Math.max(0.0f, o - j)).putVec3((Vector3fc)ColorHelper.toRgbVector((int)i)).putVec3((Vector3fc)vector3f);
        }
        try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "Update light", this.glTextureView, OptionalInt.empty());){
            renderPass.setPipeline(RenderPipelines.BILT_SCREEN_LIGHTMAP);
            RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
            renderPass.setUniform("LightmapInfo", this.buffer.getBlocking());
            renderPass.draw(0, 3);
        }
        this.buffer.rotate();
        profiler.pop();
    }

    public static float getBrightness(DimensionType type, int lightLevel) {
        return LightmapTextureManager.getBrightness((float)type.ambientLight(), (int)lightLevel);
    }

    public static float getBrightness(float ambientLight, int lightLevel) {
        float f = (float)lightLevel / 15.0f;
        float g = f / (4.0f - 3.0f * f);
        return MathHelper.lerp((float)ambientLight, (float)g, (float)1.0f);
    }

    public static int pack(int block, int sky) {
        return block << 4 | sky << 20;
    }

    public static int getBlockLightCoordinates(int light) {
        return light >>> 4 & 0xF;
    }

    public static int getSkyLightCoordinates(int light) {
        return light >>> 20 & 0xF;
    }

    public static int applyEmission(int light, int lightEmission) {
        if (lightEmission == 0) {
            return light;
        }
        int i = Math.max(LightmapTextureManager.getSkyLightCoordinates((int)light), lightEmission);
        int j = Math.max(LightmapTextureManager.getBlockLightCoordinates((int)light), lightEmission);
        return LightmapTextureManager.pack((int)j, (int)i);
    }
}

