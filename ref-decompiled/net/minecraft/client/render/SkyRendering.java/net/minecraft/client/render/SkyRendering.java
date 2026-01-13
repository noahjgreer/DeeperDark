/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.EndLightFlashManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MoonPhase;
import net.minecraft.world.attribute.EnvironmentAttributeInterpolator;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class SkyRendering
implements AutoCloseable {
    private static final Identifier SUN_TEXTURE = Identifier.ofVanilla("sun");
    private static final Identifier END_FLASH_TEXTURE = Identifier.ofVanilla("end_flash");
    private static final Identifier END_SKY_TEXTURE = Identifier.ofVanilla("textures/environment/end_sky.png");
    private static final float field_53144 = 512.0f;
    private static final int field_57932 = 10;
    private static final int field_57933 = 1500;
    private static final float field_62950 = 30.0f;
    private static final float field_62951 = 100.0f;
    private static final float field_62952 = 20.0f;
    private static final float field_62953 = 100.0f;
    private static final int field_62954 = 16;
    private static final int field_57934 = 6;
    private static final float field_62955 = 100.0f;
    private static final float field_62956 = 60.0f;
    private final SpriteAtlasTexture celestialAtlasTexture;
    private final GpuBuffer starVertexBuffer;
    private final GpuBuffer topSkyVertexBuffer;
    private final GpuBuffer bottomSkyVertexBuffer;
    private final GpuBuffer endSkyVertexBuffer;
    private final GpuBuffer sunVertexBuffer;
    private final GpuBuffer moonPhaseVertexBuffer;
    private final GpuBuffer sunRiseVertexBuffer;
    private final GpuBuffer endFlashVertexBuffer;
    private final RenderSystem.ShapeIndexBuffer indexBuffer2 = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
    private final AbstractTexture endSkyTexture;
    private int starIndexCount;

    public SkyRendering(TextureManager textureManager, AtlasManager atlasManager) {
        this.celestialAtlasTexture = atlasManager.getAtlasTexture(Atlases.CELESTIALS);
        this.starVertexBuffer = this.createStars();
        this.endSkyVertexBuffer = SkyRendering.createEndSky();
        this.endSkyTexture = this.bindTexture(textureManager, END_SKY_TEXTURE);
        this.endFlashVertexBuffer = SkyRendering.createEndFlash(this.celestialAtlasTexture);
        this.sunVertexBuffer = SkyRendering.createSun(this.celestialAtlasTexture);
        this.moonPhaseVertexBuffer = SkyRendering.createMoonPhases(this.celestialAtlasTexture);
        this.sunRiseVertexBuffer = this.createSunRise();
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(10 * VertexFormats.POSITION.getVertexSize());){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
            this.createSky(bufferBuilder, 16.0f);
            try (BuiltBuffer builtBuffer = bufferBuilder.end();){
                this.topSkyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Top sky vertex buffer", 32, builtBuffer.getBuffer());
            }
            bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
            this.createSky(bufferBuilder, -16.0f);
            builtBuffer = bufferBuilder.end();
            try {
                this.bottomSkyVertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Bottom sky vertex buffer", 32, builtBuffer.getBuffer());
            }
            finally {
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
            }
        }
    }

    private AbstractTexture bindTexture(TextureManager textureManager, Identifier texture) {
        return textureManager.getTexture(texture);
    }

    private GpuBuffer createSunRise() {
        int i = 18;
        int j = VertexFormats.POSITION_COLOR.getVertexSize();
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(18 * j);){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
            int k = ColorHelper.getWhite(1.0f);
            int l = ColorHelper.getWhite(0.0f);
            bufferBuilder.vertex(0.0f, 100.0f, 0.0f).color(k);
            for (int m = 0; m <= 16; ++m) {
                float f = (float)m * ((float)Math.PI * 2) / 16.0f;
                float g = MathHelper.sin(f);
                float h = MathHelper.cos(f);
                bufferBuilder.vertex(g * 120.0f, h * 120.0f, -h * 40.0f).color(l);
            }
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Sunrise/Sunset fan", 32, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
                return gpuBuffer;
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private static GpuBuffer createSun(SpriteAtlasTexture atlas) {
        return SkyRendering.createQuadVertexBuffer("Sun quad", atlas.getSprite(SUN_TEXTURE));
    }

    private static GpuBuffer createEndFlash(SpriteAtlasTexture atlas) {
        return SkyRendering.createQuadVertexBuffer("End flash quad", atlas.getSprite(END_FLASH_TEXTURE));
    }

    private static GpuBuffer createQuadVertexBuffer(String description, Sprite sprite) {
        VertexFormat vertexFormat = VertexFormats.POSITION_TEXTURE;
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(4 * vertexFormat.getVertexSize());){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, vertexFormat);
            bufferBuilder.vertex(-1.0f, 0.0f, -1.0f).texture(sprite.getMinU(), sprite.getMinV());
            bufferBuilder.vertex(1.0f, 0.0f, -1.0f).texture(sprite.getMaxU(), sprite.getMinV());
            bufferBuilder.vertex(1.0f, 0.0f, 1.0f).texture(sprite.getMaxU(), sprite.getMaxV());
            bufferBuilder.vertex(-1.0f, 0.0f, 1.0f).texture(sprite.getMinU(), sprite.getMaxV());
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> description, 32, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
                return gpuBuffer;
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private static GpuBuffer createMoonPhases(SpriteAtlasTexture atlas) {
        MoonPhase[] moonPhases = MoonPhase.values();
        VertexFormat vertexFormat = VertexFormats.POSITION_TEXTURE;
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(moonPhases.length * 4 * vertexFormat.getVertexSize());){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, vertexFormat);
            for (MoonPhase moonPhase : moonPhases) {
                Sprite sprite = atlas.getSprite(Identifier.ofVanilla("moon/" + moonPhase.asString()));
                bufferBuilder.vertex(-1.0f, 0.0f, -1.0f).texture(sprite.getMaxU(), sprite.getMaxV());
                bufferBuilder.vertex(1.0f, 0.0f, -1.0f).texture(sprite.getMinU(), sprite.getMaxV());
                bufferBuilder.vertex(1.0f, 0.0f, 1.0f).texture(sprite.getMinU(), sprite.getMinV());
                bufferBuilder.vertex(-1.0f, 0.0f, 1.0f).texture(sprite.getMaxU(), sprite.getMinV());
            }
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Moon phases", 32, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
                return gpuBuffer;
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private GpuBuffer createStars() {
        Random random = Random.create(10842L);
        float f = 100.0f;
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(VertexFormats.POSITION.getVertexSize() * 1500 * 4);){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
            for (int i = 0; i < 1500; ++i) {
                float g = random.nextFloat() * 2.0f - 1.0f;
                float h = random.nextFloat() * 2.0f - 1.0f;
                float j = random.nextFloat() * 2.0f - 1.0f;
                float k = 0.15f + random.nextFloat() * 0.1f;
                float l = MathHelper.magnitude(g, h, j);
                if (l <= 0.010000001f || l >= 1.0f) continue;
                Vector3f vector3f = new Vector3f(g, h, j).normalize(100.0f);
                float m = (float)(random.nextDouble() * 3.1415927410125732 * 2.0);
                Matrix3f matrix3f = new Matrix3f().rotateTowards((Vector3fc)new Vector3f((Vector3fc)vector3f).negate(), (Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f)).rotateZ(-m);
                bufferBuilder.vertex((Vector3fc)new Vector3f(k, -k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
                bufferBuilder.vertex((Vector3fc)new Vector3f(k, k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
                bufferBuilder.vertex((Vector3fc)new Vector3f(-k, k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
                bufferBuilder.vertex((Vector3fc)new Vector3f(-k, -k, 0.0f).mul((Matrix3fc)matrix3f).add((Vector3fc)vector3f));
            }
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                this.starIndexCount = builtBuffer.getDrawParameters().indexCount();
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Stars vertex buffer", 40, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
                return gpuBuffer;
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private void createSky(VertexConsumer vertexConsumer, float height) {
        float f = Math.signum(height) * 512.0f;
        vertexConsumer.vertex(0.0f, height, 0.0f);
        for (int i = -180; i <= 180; i += 45) {
            vertexConsumer.vertex(f * MathHelper.cos((float)i * ((float)Math.PI / 180)), height, 512.0f * MathHelper.sin((float)i * ((float)Math.PI / 180)));
        }
    }

    private static GpuBuffer createEndSky() {
        try (BufferAllocator bufferAllocator = BufferAllocator.fixedSized(24 * VertexFormats.POSITION_TEXTURE_COLOR.getVertexSize());){
            BufferBuilder bufferBuilder = new BufferBuilder(bufferAllocator, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            for (int i = 0; i < 6; ++i) {
                Matrix4f matrix4f = new Matrix4f();
                switch (i) {
                    case 1: {
                        matrix4f.rotationX(1.5707964f);
                        break;
                    }
                    case 2: {
                        matrix4f.rotationX(-1.5707964f);
                        break;
                    }
                    case 3: {
                        matrix4f.rotationX((float)Math.PI);
                        break;
                    }
                    case 4: {
                        matrix4f.rotationZ(1.5707964f);
                        break;
                    }
                    case 5: {
                        matrix4f.rotationZ(-1.5707964f);
                    }
                }
                bufferBuilder.vertex((Matrix4fc)matrix4f, -100.0f, -100.0f, -100.0f).texture(0.0f, 0.0f).color(-14145496);
                bufferBuilder.vertex((Matrix4fc)matrix4f, -100.0f, -100.0f, 100.0f).texture(0.0f, 16.0f).color(-14145496);
                bufferBuilder.vertex((Matrix4fc)matrix4f, 100.0f, -100.0f, 100.0f).texture(16.0f, 16.0f).color(-14145496);
                bufferBuilder.vertex((Matrix4fc)matrix4f, 100.0f, -100.0f, -100.0f).texture(16.0f, 0.0f).color(-14145496);
            }
            BuiltBuffer builtBuffer = bufferBuilder.end();
            try {
                GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "End sky vertex buffer", 40, builtBuffer.getBuffer());
                if (builtBuffer != null) {
                    builtBuffer.close();
                }
                return gpuBuffer;
            }
            catch (Throwable throwable) {
                if (builtBuffer != null) {
                    try {
                        builtBuffer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    public void renderTopSky(int i) {
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)ColorHelper.toRgbaVector(i), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky disc", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_SKY);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.topSkyVertexBuffer);
            renderPass.draw(0, 10);
        }
    }

    public void updateRenderState(ClientWorld world, float tickProgress, Camera camera, SkyRenderState state) {
        state.skybox = world.getDimension().skybox();
        if (state.skybox == DimensionType.Skybox.NONE) {
            return;
        }
        if (state.skybox == DimensionType.Skybox.END) {
            EndLightFlashManager endLightFlashManager = world.getEndLightFlashManager();
            if (endLightFlashManager == null) {
                return;
            }
            state.endFlashIntensity = endLightFlashManager.getSkyFactor(tickProgress);
            state.endFlashPitch = endLightFlashManager.getPitch();
            state.endFlashYaw = endLightFlashManager.getYaw();
            return;
        }
        EnvironmentAttributeInterpolator environmentAttributeInterpolator = camera.getEnvironmentAttributeInterpolator();
        state.sunAngle = environmentAttributeInterpolator.get(EnvironmentAttributes.SUN_ANGLE_VISUAL, tickProgress).floatValue() * ((float)Math.PI / 180);
        state.moonAngle = environmentAttributeInterpolator.get(EnvironmentAttributes.MOON_ANGLE_VISUAL, tickProgress).floatValue() * ((float)Math.PI / 180);
        state.starAngle = environmentAttributeInterpolator.get(EnvironmentAttributes.STAR_ANGLE_VISUAL, tickProgress).floatValue() * ((float)Math.PI / 180);
        state.rainGradient = 1.0f - world.getRainGradient(tickProgress);
        state.starBrightness = environmentAttributeInterpolator.get(EnvironmentAttributes.STAR_BRIGHTNESS_VISUAL, tickProgress).floatValue();
        state.sunriseAndSunsetColor = camera.getEnvironmentAttributeInterpolator().get(EnvironmentAttributes.SUNRISE_SUNSET_COLOR_VISUAL, tickProgress);
        state.moonPhase = environmentAttributeInterpolator.get(EnvironmentAttributes.MOON_PHASE_VISUAL, tickProgress);
        state.skyColor = environmentAttributeInterpolator.get(EnvironmentAttributes.SKY_COLOR_VISUAL, tickProgress);
        state.shouldRenderSkyDark = this.isSkyDark(tickProgress, world);
    }

    private boolean isSkyDark(float tickProgress, ClientWorld world) {
        return MinecraftClient.getInstance().player.getCameraPosVec((float)tickProgress).y - world.getLevelProperties().getSkyDarknessHeight(world) < 0.0;
    }

    public void renderSkyDark() {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.translate(0.0f, 12.0f, 0.0f);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(0.0f, 0.0f, 0.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky dark", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_SKY);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.bottomSkyVertexBuffer);
            renderPass.draw(0, 10);
        }
        matrix4fStack.popMatrix();
    }

    public void renderCelestialBodies(MatrixStack matrices, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float alpha, float starBrightness) {
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation(sunAngle));
        this.renderSun(alpha, matrices);
        matrices.pop();
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation(moonAngle));
        this.renderMoon(moonPhase, alpha, matrices);
        matrices.pop();
        if (starBrightness > 0.0f) {
            matrices.push();
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation(starAngle));
            this.renderStars(starBrightness, matrices);
            matrices.pop();
        }
        matrices.pop();
    }

    private void renderSun(float alpha, MatrixStack matrices) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)matrices.peek().getPositionMatrix());
        matrix4fStack.translate(0.0f, 100.0f, 0.0f);
        matrix4fStack.scale(30.0f, 1.0f, 30.0f);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, alpha), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBuffer gpuBuffer = this.indexBuffer2.getIndexBuffer(6);
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky sun", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_CELESTIAL);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindTexture("Sampler0", this.celestialAtlasTexture.getGlTextureView(), this.celestialAtlasTexture.getSampler());
            renderPass.setVertexBuffer(0, this.sunVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer2.getIndexType());
            renderPass.drawIndexed(0, 0, 6, 1);
        }
        matrix4fStack.popMatrix();
    }

    private void renderMoon(MoonPhase moonPhase, float alpha, MatrixStack matrices) {
        int i = moonPhase.getIndex() * 4;
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)matrices.peek().getPositionMatrix());
        matrix4fStack.translate(0.0f, 100.0f, 0.0f);
        matrix4fStack.scale(20.0f, 1.0f, 20.0f);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, alpha), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBuffer gpuBuffer = this.indexBuffer2.getIndexBuffer(6);
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sky moon", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_CELESTIAL);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindTexture("Sampler0", this.celestialAtlasTexture.getGlTextureView(), this.celestialAtlasTexture.getSampler());
            renderPass.setVertexBuffer(0, this.moonPhaseVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer2.getIndexType());
            renderPass.drawIndexed(i, 0, 6, 1);
        }
        matrix4fStack.popMatrix();
    }

    private void renderStars(float brightness, MatrixStack matrices) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)matrices.peek().getPositionMatrix());
        RenderPipeline renderPipeline = RenderPipelines.POSITION_STARS;
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBuffer gpuBuffer = this.indexBuffer2.getIndexBuffer(this.starIndexCount);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(brightness, brightness, brightness, brightness), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Stars", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.starVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer2.getIndexType());
            renderPass.drawIndexed(0, 0, this.starIndexCount, 1);
        }
        matrix4fStack.popMatrix();
    }

    public void renderGlowingSky(MatrixStack matrices, float solarAngle, int color) {
        float f = ColorHelper.getAlphaFloat(color);
        if (f <= 0.001f) {
            return;
        }
        matrices.push();
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        float g = MathHelper.sin(solarAngle) < 0.0f ? 180.0f : 0.0f;
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(g + 90.0f));
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)matrices.peek().getPositionMatrix());
        matrix4fStack.scale(1.0f, 1.0f, f);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)ColorHelper.toRgbaVector(color), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Sunrise sunset", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_COLOR_SUNRISE_SUNSET);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.setVertexBuffer(0, this.sunRiseVertexBuffer);
            renderPass.draw(0, 18);
        }
        matrix4fStack.popMatrix();
        matrices.pop();
    }

    public void renderEndSky() {
        RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
        GpuBuffer gpuBuffer = shapeIndexBuffer.getIndexBuffer(36);
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)RenderSystem.getModelViewMatrix(), (Vector4fc)new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "End sky", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_END_SKY);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindTexture("Sampler0", this.endSkyTexture.getGlTextureView(), this.endSkyTexture.getSampler());
            renderPass.setVertexBuffer(0, this.endSkyVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, shapeIndexBuffer.getIndexType());
            renderPass.drawIndexed(0, 0, 36, 1);
        }
    }

    public void drawEndLightFlash(MatrixStack matrices, float intensity, float pitch, float yaw) {
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - yaw));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - pitch));
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        matrix4fStack.mul((Matrix4fc)matrices.peek().getPositionMatrix());
        matrix4fStack.translate(0.0f, 100.0f, 0.0f);
        matrix4fStack.scale(60.0f, 1.0f, 60.0f);
        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write((Matrix4fc)matrix4fStack, (Vector4fc)new Vector4f(intensity, intensity, intensity, intensity), (Vector3fc)new Vector3f(), (Matrix4fc)new Matrix4f());
        GpuTextureView gpuTextureView = MinecraftClient.getInstance().getFramebuffer().getColorAttachmentView();
        GpuTextureView gpuTextureView2 = MinecraftClient.getInstance().getFramebuffer().getDepthAttachmentView();
        GpuBuffer gpuBuffer = this.indexBuffer2.getIndexBuffer(6);
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "End flash", gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());){
            renderPass.setPipeline(RenderPipelines.POSITION_TEX_COLOR_CELESTIAL);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
            renderPass.bindTexture("Sampler0", this.celestialAtlasTexture.getGlTextureView(), this.celestialAtlasTexture.getSampler());
            renderPass.setVertexBuffer(0, this.endFlashVertexBuffer);
            renderPass.setIndexBuffer(gpuBuffer, this.indexBuffer2.getIndexType());
            renderPass.drawIndexed(0, 0, 6, 1);
        }
        matrix4fStack.popMatrix();
    }

    @Override
    public void close() {
        this.sunVertexBuffer.close();
        this.moonPhaseVertexBuffer.close();
        this.starVertexBuffer.close();
        this.topSkyVertexBuffer.close();
        this.bottomSkyVertexBuffer.close();
        this.endSkyVertexBuffer.close();
        this.sunRiseVertexBuffer.close();
        this.endFlashVertexBuffer.close();
    }
}
