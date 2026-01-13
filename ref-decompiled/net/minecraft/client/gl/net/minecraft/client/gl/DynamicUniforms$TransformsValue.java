/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fc
 *  org.joml.Vector3fc
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.DynamicUniformStorage;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public record DynamicUniforms.TransformsValue(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix) implements DynamicUniformStorage.Uploadable
{
    @Override
    public void write(ByteBuffer buffer) {
        Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix);
    }
}
