package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBVertexAttribBinding;
import org.lwjgl.opengl.GLCapabilities;

@Environment(EnvType.CLIENT)
public abstract class VertexBufferManager {
   public static VertexBufferManager create(GLCapabilities capabilities, DebugLabelManager labeler, Set usedCapabilities) {
      if (capabilities.GL_ARB_vertex_attrib_binding && GlBackend.allowGlArbVABinding) {
         usedCapabilities.add("GL_ARB_vertex_attrib_binding");
         return new ARBVertexBufferManager(labeler);
      } else {
         return new DefaultVertexBufferManager(labeler);
      }
   }

   public abstract void setupBuffer(VertexFormat format, GlGpuBuffer into);

   @Environment(EnvType.CLIENT)
   static class ARBVertexBufferManager extends VertexBufferManager {
      private final Map cache = new HashMap();
      private final DebugLabelManager labeler;
      private final boolean applyMesaWorkaround;

      public ARBVertexBufferManager(DebugLabelManager labeler) {
         this.labeler = labeler;
         if ("Mesa".equals(GlStateManager._getString(7936))) {
            String string = GlStateManager._getString(7938);
            this.applyMesaWorkaround = string.contains("25.0.0") || string.contains("25.0.1") || string.contains("25.0.2");
         } else {
            this.applyMesaWorkaround = false;
         }

      }

      public void setupBuffer(VertexFormat format, GlGpuBuffer into) {
         AllocatedBuffer allocatedBuffer = (AllocatedBuffer)this.cache.get(format);
         if (allocatedBuffer == null) {
            int i = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(i);
            List list = format.getElements();

            for(int j = 0; j < list.size(); ++j) {
               VertexFormatElement vertexFormatElement = (VertexFormatElement)list.get(j);
               GlStateManager._enableVertexAttribArray(j);
               switch (vertexFormatElement.usage()) {
                  case POSITION:
                  case GENERIC:
                  case UV:
                     if (vertexFormatElement.type() == VertexFormatElement.Type.FLOAT) {
                        ARBVertexAttribBinding.glVertexAttribFormat(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), false, format.getOffset(vertexFormatElement));
                     } else {
                        ARBVertexAttribBinding.glVertexAttribIFormat(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), format.getOffset(vertexFormatElement));
                     }
                     break;
                  case NORMAL:
                  case COLOR:
                     ARBVertexAttribBinding.glVertexAttribFormat(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), true, format.getOffset(vertexFormatElement));
               }

               ARBVertexAttribBinding.glVertexAttribBinding(j, 0);
            }

            ARBVertexAttribBinding.glBindVertexBuffer(0, into.id, 0L, format.getVertexSize());
            AllocatedBuffer allocatedBuffer2 = new AllocatedBuffer(i, format, into);
            this.labeler.labelAllocatedBuffer(allocatedBuffer2);
            this.cache.put(format, allocatedBuffer2);
         } else {
            GlStateManager._glBindVertexArray(allocatedBuffer.glId);
            if (allocatedBuffer.buffer != into) {
               if (this.applyMesaWorkaround && allocatedBuffer.buffer != null && allocatedBuffer.buffer.id == into.id) {
                  ARBVertexAttribBinding.glBindVertexBuffer(0, 0, 0L, 0);
               }

               ARBVertexAttribBinding.glBindVertexBuffer(0, into.id, 0L, format.getVertexSize());
               allocatedBuffer.buffer = into;
            }

         }
      }
   }

   @Environment(EnvType.CLIENT)
   static class DefaultVertexBufferManager extends VertexBufferManager {
      private final Map cache = new HashMap();
      private final DebugLabelManager labeler;

      public DefaultVertexBufferManager(DebugLabelManager labeler) {
         this.labeler = labeler;
      }

      public void setupBuffer(VertexFormat format, GlGpuBuffer into) {
         AllocatedBuffer allocatedBuffer = (AllocatedBuffer)this.cache.get(format);
         if (allocatedBuffer == null) {
            int i = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(i);
            GlStateManager._glBindBuffer(34962, into.id);
            setupBuffer(format, true);
            AllocatedBuffer allocatedBuffer2 = new AllocatedBuffer(i, format, into);
            this.labeler.labelAllocatedBuffer(allocatedBuffer2);
            this.cache.put(format, allocatedBuffer2);
         } else {
            GlStateManager._glBindVertexArray(allocatedBuffer.glId);
            if (allocatedBuffer.buffer != into) {
               GlStateManager._glBindBuffer(34962, into.id);
               allocatedBuffer.buffer = into;
               setupBuffer(format, false);
            }

         }
      }

      private static void setupBuffer(VertexFormat format, boolean vbaIsNew) {
         int i = format.getVertexSize();
         List list = format.getElements();

         for(int j = 0; j < list.size(); ++j) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)list.get(j);
            if (vbaIsNew) {
               GlStateManager._enableVertexAttribArray(j);
            }

            switch (vertexFormatElement.usage()) {
               case POSITION:
               case GENERIC:
               case UV:
                  if (vertexFormatElement.type() == VertexFormatElement.Type.FLOAT) {
                     GlStateManager._vertexAttribPointer(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), false, i, (long)format.getOffset(vertexFormatElement));
                  } else {
                     GlStateManager._vertexAttribIPointer(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), i, (long)format.getOffset(vertexFormatElement));
                  }
                  break;
               case NORMAL:
               case COLOR:
                  GlStateManager._vertexAttribPointer(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), true, i, (long)format.getOffset(vertexFormatElement));
            }
         }

      }
   }

   @Environment(EnvType.CLIENT)
   public static class AllocatedBuffer {
      final int glId;
      final VertexFormat vertexFormat;
      @Nullable
      GlGpuBuffer buffer;

      AllocatedBuffer(int glId, VertexFormat vertexFormat, @Nullable GlGpuBuffer buffer) {
         this.glId = glId;
         this.vertexFormat = vertexFormat;
         this.buffer = buffer;
      }
   }
}
