package net.minecraft.client.gl;

import com.mojang.logging.LogUtils;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.StringHelper;
import org.lwjgl.opengl.EXTDebugLabel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.KHRDebug;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class DebugLabelManager {
   private static final Logger LOGGER = LogUtils.getLogger();

   public void labelGlGpuBuffer(GlGpuBuffer buffer) {
   }

   public void labelGlTexture(GlTexture texture) {
   }

   public void labelCompiledShader(CompiledShader shader) {
   }

   public void labelShaderProgram(ShaderProgram program) {
   }

   public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
   }

   public void pushDebugGroup(Supplier labelGetter) {
   }

   public void popDebugGroup() {
   }

   public static DebugLabelManager create(GLCapabilities capabilities, boolean debugEnabled, Set usedCapabilities) {
      if (debugEnabled) {
         if (capabilities.GL_KHR_debug && GlBackend.allowGlKhrDebug) {
            usedCapabilities.add("GL_KHR_debug");
            return new KHRDebugLabelManager();
         }

         if (capabilities.GL_EXT_debug_label && GlBackend.allowExtDebugLabel) {
            usedCapabilities.add("GL_EXT_debug_label");
            return new EXTDebugLabelManager();
         }

         LOGGER.warn("Debug labels unavailable: neither KHR_debug nor EXT_debug_label are supported");
      }

      return new NoOpDebugLabelManager();
   }

   public boolean isUsable() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   static class KHRDebugLabelManager extends DebugLabelManager {
      private final int maxLabelLength = GL11.glGetInteger(33512);

      public void labelGlGpuBuffer(GlGpuBuffer buffer) {
         Supplier supplier = buffer.debugLabelSupplier;
         if (supplier != null) {
            KHRDebug.glObjectLabel(33504, buffer.id, StringHelper.truncate((String)supplier.get(), this.maxLabelLength, true));
         }

      }

      public void labelGlTexture(GlTexture texture) {
         KHRDebug.glObjectLabel(5890, texture.glId, StringHelper.truncate(texture.getLabel(), this.maxLabelLength, true));
      }

      public void labelCompiledShader(CompiledShader shader) {
         KHRDebug.glObjectLabel(33505, shader.getHandle(), StringHelper.truncate(shader.getDebugLabel(), this.maxLabelLength, true));
      }

      public void labelShaderProgram(ShaderProgram program) {
         KHRDebug.glObjectLabel(33506, program.getGlRef(), StringHelper.truncate(program.getDebugLabel(), this.maxLabelLength, true));
      }

      public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
         KHRDebug.glObjectLabel(32884, buffer.glId, StringHelper.truncate(buffer.vertexFormat.toString(), this.maxLabelLength, true));
      }

      public void pushDebugGroup(Supplier labelGetter) {
         KHRDebug.glPushDebugGroup(33354, 0, (CharSequence)labelGetter.get());
      }

      public void popDebugGroup() {
         KHRDebug.glPopDebugGroup();
      }

      public boolean isUsable() {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class EXTDebugLabelManager extends DebugLabelManager {
      EXTDebugLabelManager() {
      }

      public void labelGlGpuBuffer(GlGpuBuffer buffer) {
         Supplier supplier = buffer.debugLabelSupplier;
         if (supplier != null) {
            EXTDebugLabel.glLabelObjectEXT(37201, buffer.id, StringHelper.truncate((String)supplier.get(), 256, true));
         }

      }

      public void labelGlTexture(GlTexture texture) {
         EXTDebugLabel.glLabelObjectEXT(5890, texture.glId, StringHelper.truncate(texture.getLabel(), 256, true));
      }

      public void labelCompiledShader(CompiledShader shader) {
         EXTDebugLabel.glLabelObjectEXT(35656, shader.getHandle(), StringHelper.truncate(shader.getDebugLabel(), 256, true));
      }

      public void labelShaderProgram(ShaderProgram program) {
         EXTDebugLabel.glLabelObjectEXT(35648, program.getGlRef(), StringHelper.truncate(program.getDebugLabel(), 256, true));
      }

      public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
         EXTDebugLabel.glLabelObjectEXT(32884, buffer.glId, StringHelper.truncate(buffer.vertexFormat.toString(), 256, true));
      }

      public boolean isUsable() {
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   static class NoOpDebugLabelManager extends DebugLabelManager {
   }
}
