/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.system.MemoryUtil
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.textures.GpuTexture;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.SpriteTexturedVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class Sprite
implements AutoCloseable {
    private final Identifier atlasId;
    private final SpriteContents contents;
    private final int x;
    private final int y;
    private final float minU;
    private final float maxU;
    private final float minV;
    private final float maxV;
    private final int padding;

    protected Sprite(Identifier atlasId, SpriteContents contents, int atlasWidth, int atlasHeight, int x, int y, int padding) {
        this.atlasId = atlasId;
        this.contents = contents;
        this.padding = padding;
        this.x = x;
        this.y = y;
        this.minU = (float)(x + padding) / (float)atlasWidth;
        this.maxU = (float)(x + padding + contents.getWidth()) / (float)atlasWidth;
        this.minV = (float)(y + padding) / (float)atlasHeight;
        this.maxV = (float)(y + padding + contents.getHeight()) / (float)atlasHeight;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public float getMinU() {
        return this.minU;
    }

    public float getMaxU() {
        return this.maxU;
    }

    public SpriteContents getContents() {
        return this.contents;
    }

    public @Nullable SpriteContents.Animator createAnimator(GpuBufferSlice bufferSlice, int animationInfoSize) {
        return this.contents.createAnimator(bufferSlice, animationInfoSize);
    }

    public float getFrameU(float frame) {
        float f = this.maxU - this.minU;
        return this.minU + f * frame;
    }

    public float getMinV() {
        return this.minV;
    }

    public float getMaxV() {
        return this.maxV;
    }

    public float getFrameV(float frame) {
        float f = this.maxV - this.minV;
        return this.minV + f * frame;
    }

    public Identifier getAtlasId() {
        return this.atlasId;
    }

    public String toString() {
        return "TextureAtlasSprite{contents='" + String.valueOf(this.contents) + "', u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + "}";
    }

    public void upload(GpuTexture texture, int mipmap) {
        this.contents.upload(texture, mipmap);
    }

    public VertexConsumer getTextureSpecificVertexConsumer(VertexConsumer consumer) {
        return new SpriteTexturedVertexConsumer(consumer, this);
    }

    boolean isAnimated() {
        return this.contents.isAnimated();
    }

    public void putSpriteInfo(ByteBuffer buffer, int offset, int maxLevel, int width, int height, int stride) {
        for (int i = 0; i <= maxLevel; ++i) {
            Std140Builder.intoBuffer(MemoryUtil.memSlice((ByteBuffer)buffer, (int)(offset + i * stride), (int)stride)).putMat4f((Matrix4fc)new Matrix4f().ortho2D(0.0f, (float)(width >> i), 0.0f, (float)(height >> i))).putMat4f((Matrix4fc)new Matrix4f().translate((float)(this.x >> i), (float)(this.y >> i), 0.0f).scale((float)(this.contents.getWidth() + this.padding * 2 >> i), (float)(this.contents.getHeight() + this.padding * 2 >> i), 1.0f)).putFloat((float)this.padding / (float)this.contents.getWidth()).putFloat((float)this.padding / (float)this.contents.getHeight()).putInt(i);
        }
    }

    @Override
    public void close() {
        this.contents.close();
    }
}
