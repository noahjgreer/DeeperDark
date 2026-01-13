/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTexture$Usage
 *  com.mojang.blaze3d.textures.TextureFormat
 *  it.unimi.dsi.fastutil.ints.Int2IntArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.BufferManager
 *  net.minecraft.client.texture.GlTexture
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlTexture
extends GpuTexture {
    private static final int UNINITIALIZED = -1;
    protected final int glId;
    private int framebufferId = -1;
    private int depthGlId = -1;
    private @Nullable Int2IntMap depthTexToFramebufferIdCache;
    protected boolean closed;
    private int refCount;

    protected GlTexture(@GpuTexture.Usage int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int glId) {
        super(usage, label, format, width, height, depthOrLayers, mipLevels);
        this.glId = glId;
    }

    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.refCount == 0) {
            this.free();
        }
    }

    private void free() {
        GlStateManager._deleteTexture((int)this.glId);
        if (this.framebufferId != -1) {
            GlStateManager._glDeleteFramebuffers((int)this.framebufferId);
        }
        if (this.depthTexToFramebufferIdCache != null) {
            IntIterator intIterator = this.depthTexToFramebufferIdCache.values().iterator();
            while (intIterator.hasNext()) {
                int i = (Integer)intIterator.next();
                GlStateManager._glDeleteFramebuffers((int)i);
            }
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    public int getOrCreateFramebuffer(BufferManager bufferManager, @Nullable GpuTexture depthTexture) {
        int i;
        int n = i = depthTexture == null ? 0 : ((GlTexture)depthTexture).glId;
        if (this.depthGlId == i) {
            return this.framebufferId;
        }
        if (this.framebufferId == -1) {
            this.framebufferId = this.createFramebuffer(bufferManager, i);
            this.depthGlId = i;
            return this.framebufferId;
        }
        if (this.depthTexToFramebufferIdCache == null) {
            this.depthTexToFramebufferIdCache = new Int2IntArrayMap();
        }
        return this.depthTexToFramebufferIdCache.computeIfAbsent(i, depthGlId -> this.createFramebuffer(bufferManager, depthGlId));
    }

    private int createFramebuffer(BufferManager bufferManager, int depthGlId) {
        int i = bufferManager.createFramebuffer();
        bufferManager.setupFramebuffer(i, this.glId, depthGlId, 0, 0);
        return i;
    }

    public int getGlId() {
        return this.glId;
    }

    public void incrementRefCount() {
        ++this.refCount;
    }

    public void decrementRefCount() {
        --this.refCount;
        if (this.closed && this.refCount == 0) {
            this.free();
        }
    }
}

