/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.DynamicUniformStorage;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public record DynamicUniforms.ChunkSectionsValue(Matrix4fc modelView, int x, int y, int z, float visibility, int textureAtlasWidth, int textureAtlasHeight) implements DynamicUniformStorage.Uploadable
{
    @Override
    public void write(ByteBuffer buffer) {
        Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putFloat(this.visibility).putIVec2(this.textureAtlasWidth, this.textureAtlasHeight).putIVec3(this.x, this.y, this.z);
    }
}
