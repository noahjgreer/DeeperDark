package net.minecraft.client.texture;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.BufferManager;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GlTexture extends GpuTexture {
   protected final int glId;
   private final Int2IntMap depthTexToFramebufferIdCache = new Int2IntOpenHashMap();
   protected boolean closed;
   protected boolean needsReinit = true;
   private int refCount;

   protected GlTexture(int usage, String label, TextureFormat format, int width, int height, int depthOrLayers, int mipLevels, int glId) {
      super(usage, label, format, width, height, depthOrLayers, mipLevels);
      this.glId = glId;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.refCount == 0) {
            this.free();
         }

      }
   }

   private void free() {
      GlStateManager._deleteTexture(this.glId);
      IntIterator var1 = this.depthTexToFramebufferIdCache.values().iterator();

      while(var1.hasNext()) {
         int i = (Integer)var1.next();
         GlStateManager._glDeleteFramebuffers(i);
      }

   }

   public boolean isClosed() {
      return this.closed;
   }

   public int getOrCreateFramebuffer(BufferManager manager, @Nullable GpuTexture depthTexture) {
      int i = depthTexture == null ? 0 : ((GlTexture)depthTexture).glId;
      return this.depthTexToFramebufferIdCache.computeIfAbsent(i, (unused) -> {
         int j = manager.createFramebuffer();
         manager.setupFramebuffer(j, this.glId, i, 0, 0);
         return j;
      });
   }

   public void checkDirty(int target) {
      if (this.needsReinit) {
         GlStateManager._texParameter(target, 10242, GlConst.toGl(this.addressModeU));
         GlStateManager._texParameter(target, 10243, GlConst.toGl(this.addressModeV));
         switch (this.minFilter) {
            case NEAREST:
               GlStateManager._texParameter(target, 10241, this.useMipmaps ? 9986 : 9728);
               break;
            case LINEAR:
               GlStateManager._texParameter(target, 10241, this.useMipmaps ? 9987 : 9729);
         }

         switch (this.magFilter) {
            case NEAREST:
               GlStateManager._texParameter(target, 10240, 9728);
               break;
            case LINEAR:
               GlStateManager._texParameter(target, 10240, 9729);
         }

         this.needsReinit = false;
      }

   }

   public int getGlId() {
      return this.glId;
   }

   public void setAddressMode(AddressMode addressMode, AddressMode addressMode2) {
      super.setAddressMode(addressMode, addressMode2);
      this.needsReinit = true;
   }

   public void setTextureFilter(FilterMode filterMode, FilterMode filterMode2, boolean bl) {
      super.setTextureFilter(filterMode, filterMode2, bl);
      this.needsReinit = true;
   }

   public void setUseMipmaps(boolean bl) {
      super.setUseMipmaps(bl);
      this.needsReinit = true;
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
