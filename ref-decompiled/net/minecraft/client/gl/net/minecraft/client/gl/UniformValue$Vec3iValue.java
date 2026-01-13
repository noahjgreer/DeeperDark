/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3ic
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3ic;

@Environment(value=EnvType.CLIENT)
public record UniformValue.Vec3iValue(Vector3ic value) implements UniformValue
{
    public static final Codec<UniformValue.Vec3iValue> CODEC = Codecs.VECTOR_3I.xmap(UniformValue.Vec3iValue::new, UniformValue.Vec3iValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putIVec3(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putIVec3();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.IVEC3;
    }
}
