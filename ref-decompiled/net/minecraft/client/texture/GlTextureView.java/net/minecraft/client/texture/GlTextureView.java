/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2IntArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2IntMap
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.texture.GlTexture;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlTextureView
extends GpuTextureView {
    private static final int UNINITIALIZED = -1;
    private boolean closed;
    private int framebufferId = -1;
    private int depthGlId = -1;
    private @Nullable Int2IntMap depthTexToFramebufferIdCache;

    protected GlTextureView(GlTexture texture, int baseMipLevel, int mipLevels) {
        super(texture, baseMipLevel, mipLevels);
        texture.incrementRefCount();
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            this.texture().decrementRefCount();
            if (this.framebufferId != -1) {
                GlStateManager._glDeleteFramebuffers(this.framebufferId);
            }
            if (this.depthTexToFramebufferIdCache != null) {
                IntIterator intIterator = this.depthTexToFramebufferIdCache.values().iterator();
                while (intIterator.hasNext()) {
                    int i = (Integer)intIterator.next();
                    GlStateManager._glDeleteFramebuffers(i);
                }
            }
        }
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
        bufferManager.setupFramebuffer(i, this.texture().glId, depthGlId, this.baseMipLevel(), 0);
        return i;
    }

    @Override
    public GlTexture texture() {
        return (GlTexture)super.texture();
    }

    @Override
    public /* synthetic */ GpuTexture texture() {
        return this.texture();
    }
}
