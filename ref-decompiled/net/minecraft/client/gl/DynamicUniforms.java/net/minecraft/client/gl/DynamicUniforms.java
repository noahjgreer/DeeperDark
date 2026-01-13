/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.DynamicUniformStorage;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public class DynamicUniforms
implements AutoCloseable {
    public static final int TRANSFORMS_SIZE = new Std140SizeCalculator().putMat4f().putVec4().putVec3().putMat4f().get();
    public static final int CHUNK_SECTIONS_SIZE = new Std140SizeCalculator().putMat4f().putFloat().putIVec2().putIVec3().get();
    private static final int DEFAULT_CAPACITY = 2;
    private final DynamicUniformStorage<TransformsValue> transformsStorage = new DynamicUniformStorage("Dynamic Transforms UBO", TRANSFORMS_SIZE, 2);
    private final DynamicUniformStorage<ChunkSectionsValue> chunkSectionsStorage = new DynamicUniformStorage("Chunk Sections UBO", CHUNK_SECTIONS_SIZE, 2);

    public void clear() {
        this.transformsStorage.clear();
        this.chunkSectionsStorage.clear();
    }

    @Override
    public void close() {
        this.transformsStorage.close();
        this.chunkSectionsStorage.close();
    }

    public GpuBufferSlice write(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix) {
        return this.transformsStorage.write(new TransformsValue((Matrix4fc)new Matrix4f(modelView), (Vector4fc)new Vector4f(colorModulator), (Vector3fc)new Vector3f(modelOffset), (Matrix4fc)new Matrix4f(textureMatrix)));
    }

    public GpuBufferSlice[] writeTransforms(TransformsValue ... values) {
        return this.transformsStorage.writeAll(values);
    }

    public GpuBufferSlice[] writeChunkSections(ChunkSectionsValue ... values) {
        return this.chunkSectionsStorage.writeAll(values);
    }

    @Environment(value=EnvType.CLIENT)
    public record TransformsValue(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix) implements DynamicUniformStorage.Uploadable
    {
        @Override
        public void write(ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record ChunkSectionsValue(Matrix4fc modelView, int x, int y, int z, float visibility, int textureAtlasWidth, int textureAtlasHeight) implements DynamicUniformStorage.Uploadable
    {
        @Override
        public void write(ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putFloat(this.visibility).putIVec2(this.textureAtlasWidth, this.textureAtlasHeight).putIVec3(this.x, this.y, this.z);
        }
    }
}
