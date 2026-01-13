/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector4fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector4fc;

@Environment(value=EnvType.CLIENT)
public record UniformValue.Vec4fValue(Vector4fc value) implements UniformValue
{
    public static final Codec<UniformValue.Vec4fValue> CODEC = Codecs.VECTOR_4F.xmap(UniformValue.Vec4fValue::new, UniformValue.Vec4fValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putVec4(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putVec4();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.VEC4;
    }
}
