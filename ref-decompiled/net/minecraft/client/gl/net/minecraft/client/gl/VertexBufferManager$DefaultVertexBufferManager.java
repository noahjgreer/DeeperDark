/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.DebugLabelManager;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.VertexBufferManager;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class VertexBufferManager.DefaultVertexBufferManager
extends VertexBufferManager {
    private final Map<VertexFormat, VertexBufferManager.AllocatedBuffer> cache = new HashMap<VertexFormat, VertexBufferManager.AllocatedBuffer>();
    private final DebugLabelManager labeler;

    public VertexBufferManager.DefaultVertexBufferManager(DebugLabelManager labeler) {
        this.labeler = labeler;
    }

    @Override
    public void setupBuffer(VertexFormat format, @Nullable GlGpuBuffer into) {
        VertexBufferManager.AllocatedBuffer allocatedBuffer = this.cache.get(format);
        if (allocatedBuffer == null) {
            int i = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(i);
            if (into != null) {
                GlStateManager._glBindBuffer(34962, into.id);
                VertexBufferManager.DefaultVertexBufferManager.setupBuffer(format, true);
            }
            VertexBufferManager.AllocatedBuffer allocatedBuffer2 = new VertexBufferManager.AllocatedBuffer(i, format, into);
            this.labeler.labelAllocatedBuffer(allocatedBuffer2);
            this.cache.put(format, allocatedBuffer2);
            return;
        }
        GlStateManager._glBindVertexArray(allocatedBuffer.glId);
        if (into != null && allocatedBuffer.buffer != into) {
            GlStateManager._glBindBuffer(34962, into.id);
            allocatedBuffer.buffer = into;
            VertexBufferManager.DefaultVertexBufferManager.setupBuffer(format, false);
        }
    }

    private static void setupBuffer(VertexFormat format, boolean vbaIsNew) {
        int i = format.getVertexSize();
        List<VertexFormatElement> list = format.getElements();
        block4: for (int j = 0; j < list.size(); ++j) {
            VertexFormatElement vertexFormatElement = list.get(j);
            if (vbaIsNew) {
                GlStateManager._enableVertexAttribArray(j);
            }
            switch (vertexFormatElement.usage()) {
                case POSITION: 
                case GENERIC: 
                case UV: {
                    if (vertexFormatElement.type() == VertexFormatElement.Type.FLOAT) {
                        GlStateManager._vertexAttribPointer(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), false, i, format.getOffset(vertexFormatElement));
                        continue block4;
                    }
                    GlStateManager._vertexAttribIPointer(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), i, format.getOffset(vertexFormatElement));
                    continue block4;
                }
                case NORMAL: 
                case COLOR: {
                    GlStateManager._vertexAttribPointer(j, vertexFormatElement.count(), GlConst.toGl(vertexFormatElement.type()), true, i, format.getOffset(vertexFormatElement));
                }
            }
        }
    }
}
