/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record UniformValue.Vec3fValue(Vector3fc value) implements UniformValue
{
    public static final Codec<UniformValue.Vec3fValue> CODEC = Codecs.VECTOR_3F.xmap(UniformValue.Vec3fValue::new, UniformValue.Vec3fValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putVec3(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putVec3();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.VEC3;
    }
}
