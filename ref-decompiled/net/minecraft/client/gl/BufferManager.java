package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import java.nio.ByteBuffer;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBBufferStorage;
import org.lwjgl.opengl.ARBDirectStateAccess;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GLCapabilities;

@Environment(EnvType.CLIENT)
public abstract class BufferManager {
   public static BufferManager create(GLCapabilities capabilities, Set usedCapabilities) {
      if (capabilities.GL_ARB_direct_state_access && GlBackend.allowGlArbDirectAccess) {
         usedCapabilities.add("GL_ARB_direct_state_access");
         return new ARBBufferManager();
      } else {
         return new DefaultBufferManager();
      }
   }

   abstract int createBuffer();

   abstract void setBufferData(int buffer, long size, int usage);

   abstract void setBufferData(int buffer, ByteBuffer data, int usage);

   abstract void setBufferSubData(int buffer, int offset, ByteBuffer data);

   abstract void setBufferStorage(int buffer, long size, int flags);

   abstract void setBufferStorage(int buffer, ByteBuffer data, int flags);

   @Nullable
   abstract ByteBuffer mapBufferRange(int buffer, int offset, int length, int access);

   abstract void unmapBuffer(int buffer);

   abstract int createFramebuffer();

   abstract void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget);

   abstract void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter);

   abstract void flushMappedBufferRange(int buffer, int offset, int length);

   abstract void method_72237(int i, int j, int k, int l, int m);

   @Environment(EnvType.CLIENT)
   private static class ARBBufferManager extends BufferManager {
      ARBBufferManager() {
      }

      int createBuffer() {
         return ARBDirectStateAccess.glCreateBuffers();
      }

      void setBufferData(int buffer, long size, int usage) {
         ARBDirectStateAccess.glNamedBufferData(buffer, size, usage);
      }

      void setBufferData(int buffer, ByteBuffer data, int usage) {
         ARBDirectStateAccess.glNamedBufferData(buffer, data, usage);
      }

      void setBufferSubData(int buffer, int offset, ByteBuffer data) {
         ARBDirectStateAccess.glNamedBufferSubData(buffer, (long)offset, data);
      }

      void setBufferStorage(int buffer, long size, int flags) {
         ARBDirectStateAccess.glNamedBufferStorage(buffer, size, flags);
      }

      void setBufferStorage(int buffer, ByteBuffer data, int flags) {
         ARBDirectStateAccess.glNamedBufferStorage(buffer, data, flags);
      }

      @Nullable
      ByteBuffer mapBufferRange(int buffer, int offset, int length, int access) {
         return ARBDirectStateAccess.glMapNamedBufferRange(buffer, (long)offset, (long)length, access);
      }

      void unmapBuffer(int buffer) {
         ARBDirectStateAccess.glUnmapNamedBuffer(buffer);
      }

      public int createFramebuffer() {
         return ARBDirectStateAccess.glCreateFramebuffers();
      }

      public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget) {
         ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, 36064, colorAttachment, mipLevel);
         ARBDirectStateAccess.glNamedFramebufferTexture(framebuffer, 36096, depthAttachment, mipLevel);
         if (bindTarget != 0) {
            GlStateManager._glBindFramebuffer(bindTarget, framebuffer);
         }

      }

      public void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
         ARBDirectStateAccess.glBlitNamedFramebuffer(readFramebuffer, writeFramebuffer, srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
      }

      void flushMappedBufferRange(int buffer, int offset, int length) {
         ARBDirectStateAccess.glFlushMappedNamedBufferRange(buffer, (long)offset, (long)length);
      }

      void method_72237(int i, int j, int k, int l, int m) {
         ARBDirectStateAccess.glCopyNamedBufferSubData(i, j, (long)k, (long)l, (long)m);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class DefaultBufferManager extends BufferManager {
      DefaultBufferManager() {
      }

      int createBuffer() {
         return GlStateManager._glGenBuffers();
      }

      void setBufferData(int buffer, long size, int usage) {
         GlStateManager._glBindBuffer(36663, buffer);
         GlStateManager._glBufferData(36663, size, GlConst.bufferUsageToGlEnum(usage));
         GlStateManager._glBindBuffer(36663, 0);
      }

      void setBufferData(int buffer, ByteBuffer data, int usage) {
         GlStateManager._glBindBuffer(36663, buffer);
         GlStateManager._glBufferData(36663, data, GlConst.bufferUsageToGlEnum(usage));
         GlStateManager._glBindBuffer(36663, 0);
      }

      void setBufferSubData(int buffer, int offset, ByteBuffer data) {
         GlStateManager._glBindBuffer(36663, buffer);
         GlStateManager._glBufferSubData(36663, offset, data);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void setBufferStorage(int buffer, long size, int flags) {
         GlStateManager._glBindBuffer(36663, buffer);
         ARBBufferStorage.glBufferStorage(36663, size, flags);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void setBufferStorage(int buffer, ByteBuffer data, int flags) {
         GlStateManager._glBindBuffer(36663, buffer);
         ARBBufferStorage.glBufferStorage(36663, data, flags);
         GlStateManager._glBindBuffer(36663, 0);
      }

      @Nullable
      ByteBuffer mapBufferRange(int buffer, int offset, int length, int access) {
         GlStateManager._glBindBuffer(36663, buffer);
         ByteBuffer byteBuffer = GlStateManager._glMapBufferRange(36663, offset, length, access);
         GlStateManager._glBindBuffer(36663, 0);
         return byteBuffer;
      }

      void unmapBuffer(int buffer) {
         GlStateManager._glBindBuffer(36663, buffer);
         GlStateManager._glUnmapBuffer(36663);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void flushMappedBufferRange(int buffer, int offset, int length) {
         GlStateManager._glBindBuffer(36663, buffer);
         GL30.glFlushMappedBufferRange(36663, (long)offset, (long)length);
         GlStateManager._glBindBuffer(36663, 0);
      }

      void method_72237(int i, int j, int k, int l, int m) {
         GlStateManager._glBindBuffer(36662, i);
         GlStateManager._glBindBuffer(36663, j);
         GL31.glCopyBufferSubData(36662, 36663, (long)k, (long)l, (long)m);
         GlStateManager._glBindBuffer(36662, 0);
         GlStateManager._glBindBuffer(36663, 0);
      }

      public int createFramebuffer() {
         return GlStateManager.glGenFramebuffers();
      }

      public void setupFramebuffer(int framebuffer, int colorAttachment, int depthAttachment, int mipLevel, int bindTarget) {
         int i = bindTarget == 0 ? '販' : bindTarget;
         int j = GlStateManager.getFrameBuffer(i);
         GlStateManager._glBindFramebuffer(i, framebuffer);
         GlStateManager._glFramebufferTexture2D(i, 36064, 3553, colorAttachment, mipLevel);
         GlStateManager._glFramebufferTexture2D(i, 36096, 3553, depthAttachment, mipLevel);
         if (bindTarget == 0) {
            GlStateManager._glBindFramebuffer(i, j);
         }

      }

      public void setupBlitFramebuffer(int readFramebuffer, int writeFramebuffer, int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
         int i = GlStateManager.getFrameBuffer(36008);
         int j = GlStateManager.getFrameBuffer(36009);
         GlStateManager._glBindFramebuffer(36008, readFramebuffer);
         GlStateManager._glBindFramebuffer(36009, writeFramebuffer);
         GlStateManager._glBlitFrameBuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
         GlStateManager._glBindFramebuffer(36008, i);
         GlStateManager._glBindFramebuffer(36009, j);
      }
   }
}
