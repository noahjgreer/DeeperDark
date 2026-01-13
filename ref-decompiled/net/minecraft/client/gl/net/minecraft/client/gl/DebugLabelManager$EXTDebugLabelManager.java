/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.EXTDebugLabel
 */
package net.minecraft.client.gl;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.DebugLabelManager;
import net.minecraft.client.gl.GlGpuBuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBufferManager;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.util.StringHelper;
import org.lwjgl.opengl.EXTDebugLabel;

@Environment(value=EnvType.CLIENT)
static class DebugLabelManager.EXTDebugLabelManager
extends DebugLabelManager {
    DebugLabelManager.EXTDebugLabelManager() {
    }

    @Override
    public void labelGlGpuBuffer(GlGpuBuffer buffer) {
        Supplier<String> supplier = buffer.debugLabelSupplier;
        if (supplier != null) {
            EXTDebugLabel.glLabelObjectEXT((int)37201, (int)buffer.id, (CharSequence)StringHelper.truncate(supplier.get(), 256, true));
        }
    }

    @Override
    public void labelGlTexture(GlTexture texture) {
        EXTDebugLabel.glLabelObjectEXT((int)5890, (int)texture.glId, (CharSequence)StringHelper.truncate(texture.getLabel(), 256, true));
    }

    @Override
    public void labelCompiledShader(CompiledShader shader) {
        EXTDebugLabel.glLabelObjectEXT((int)35656, (int)shader.getHandle(), (CharSequence)StringHelper.truncate(shader.getDebugLabel(), 256, true));
    }

    @Override
    public void labelShaderProgram(ShaderProgram program) {
        EXTDebugLabel.glLabelObjectEXT((int)35648, (int)program.getGlRef(), (CharSequence)StringHelper.truncate(program.getDebugLabel(), 256, true));
    }

    @Override
    public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
        EXTDebugLabel.glLabelObjectEXT((int)32884, (int)buffer.glId, (CharSequence)StringHelper.truncate(buffer.vertexFormat.toString(), 256, true));
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
