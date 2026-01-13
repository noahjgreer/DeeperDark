/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Matrix4fc;

@Environment(value=EnvType.CLIENT)
public record UniformValue.Matrix4fValue(Matrix4fc value) implements UniformValue
{
    public static final Codec<UniformValue.Matrix4fValue> CODEC = Codecs.MATRIX_4F.xmap(UniformValue.Matrix4fValue::new, UniformValue.Matrix4fValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putMat4f(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putMat4f();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.MATRIX4X4;
    }
}
