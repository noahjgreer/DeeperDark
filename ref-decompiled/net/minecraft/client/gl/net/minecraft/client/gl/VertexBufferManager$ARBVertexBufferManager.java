/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.opengl.ARBVertexAttribBinding
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
import org.lwjgl.opengl.ARBVertexAttribBinding;

@Environment(value=EnvType.CLIENT)
static class VertexBufferManager.ARBVertexBufferManager
extends VertexBufferManager {
    private final Map<VertexFormat, VertexBufferManager.AllocatedBuffer> cache = new HashMap<VertexFormat, VertexBufferManager.AllocatedBuffer>();
    private final DebugLabelManager labeler;
    private final boolean applyMesaWorkaround;

    public VertexBufferManager.ARBVertexBufferManager(DebugLabelManager labeler) {
        String string;
        this.labeler = labeler;
        this.applyMesaWorkaround = "Mesa".equals(GlStateManager._getString(7936)) ? (string = GlStateManager._getString(7938)).contains("25.0.0") || string.contains("25.0.1") || string.contains("25.0.2") : false;
    }

    @Override
    public void setupBuffer(VertexFormat format, @Nullable GlGpuBuffer into) {
        VertexBufferManager.AllocatedBuffer allocatedBuffer = this.cache.get(format);
        if (allocatedBuffer == null) {
            int i = GlStateManager._glGenVertexArrays();
            GlStateManager._glBindVertexArray(i);
            if (into != null) {
                List<VertexFormatElement> list = format.getElements();
                for (int j = 0; j < list.size(); ++j) {
                    VertexFormatElement vertexFormatElement = list.get(j);
                    GlStateManager._enableVertexAttribArray(j);
                    switch (vertexFormatElement.usage()) {
                        case POSITION: 
                        case GENERIC: 
                        case UV: {
                            if (vertexFormatElement.type() == VertexFormatElement.Type.FLOAT) {
                                ARBVertexAttribBinding.glVertexAttribFormat((int)j, (int)vertexFormatElement.count(), (int)GlConst.toGl(vertexFormatElement.type()), (boolean)false, (int)format.getOffset(vertexFormatElement));
                                break;
                            }
                            ARBVertexAttribBinding.glVertexAttribIFormat((int)j, (int)vertexFormatElement.count(), (int)GlConst.toGl(vertexFormatElement.type()), (int)format.getOffset(vertexFormatElement));
                            break;
                        }
                        case NORMAL: 
                        case COLOR: {
                            ARBVertexAttribBinding.glVertexAttribFormat((int)j, (int)vertexFormatElement.count(), (int)GlConst.toGl(vertexFormatElement.type()), (boolean)true, (int)format.getOffset(vertexFormatElement));
                        }
                    }
                    ARBVertexAttribBinding.glVertexAttribBinding((int)j, (int)0);
                }
            }
            if (into != null) {
                ARBVertexAttribBinding.glBindVertexBuffer((int)0, (int)into.id, (long)0L, (int)format.getVertexSize());
            }
            VertexBufferManager.AllocatedBuffer allocatedBuffer2 = new VertexBufferManager.AllocatedBuffer(i, format, into);
            this.labeler.labelAllocatedBuffer(allocatedBuffer2);
            this.cache.put(format, allocatedBuffer2);
            return;
        }
        GlStateManager._glBindVertexArray(allocatedBuffer.glId);
        if (into != null && allocatedBuffer.buffer != into) {
            if (this.applyMesaWorkaround && allocatedBuffer.buffer != null && allocatedBuffer.buffer.id == into.id) {
                ARBVertexAttribBinding.glBindVertexBuffer((int)0, (int)0, (long)0L, (int)0);
            }
            ARBVertexAttribBinding.glBindVertexBuffer((int)0, (int)into.id, (long)0L, (int)format.getVertexSize());
            allocatedBuffer.buffer = into;
        }
    }
}
