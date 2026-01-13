/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.KHRDebug
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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.KHRDebug;

@Environment(value=EnvType.CLIENT)
static class DebugLabelManager.KHRDebugLabelManager
extends DebugLabelManager {
    private final int maxLabelLength = GL11.glGetInteger((int)33512);

    DebugLabelManager.KHRDebugLabelManager() {
    }

    @Override
    public void labelGlGpuBuffer(GlGpuBuffer buffer) {
        Supplier<String> supplier = buffer.debugLabelSupplier;
        if (supplier != null) {
            KHRDebug.glObjectLabel((int)33504, (int)buffer.id, (CharSequence)StringHelper.truncate(supplier.get(), this.maxLabelLength, true));
        }
    }

    @Override
    public void labelGlTexture(GlTexture texture) {
        KHRDebug.glObjectLabel((int)5890, (int)texture.glId, (CharSequence)StringHelper.truncate(texture.getLabel(), this.maxLabelLength, true));
    }

    @Override
    public void labelCompiledShader(CompiledShader shader) {
        KHRDebug.glObjectLabel((int)33505, (int)shader.getHandle(), (CharSequence)StringHelper.truncate(shader.getDebugLabel(), this.maxLabelLength, true));
    }

    @Override
    public void labelShaderProgram(ShaderProgram program) {
        KHRDebug.glObjectLabel((int)33506, (int)program.getGlRef(), (CharSequence)StringHelper.truncate(program.getDebugLabel(), this.maxLabelLength, true));
    }

    @Override
    public void labelAllocatedBuffer(VertexBufferManager.AllocatedBuffer buffer) {
        KHRDebug.glObjectLabel((int)32884, (int)buffer.glId, (CharSequence)StringHelper.truncate(buffer.vertexFormat.toString(), this.maxLabelLength, true));
    }

    @Override
    public void pushDebugGroup(Supplier<String> labelGetter) {
        KHRDebug.glPushDebugGroup((int)33354, (int)0, (CharSequence)labelGetter.get());
    }

    @Override
    public void popDebugGroup() {
        KHRDebug.glPopDebugGroup();
    }

    @Override
    public boolean isUsable() {
        return true;
    }
}
