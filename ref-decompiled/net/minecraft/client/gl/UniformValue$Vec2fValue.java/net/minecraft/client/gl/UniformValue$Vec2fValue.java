/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2fc
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;
import net.minecraft.util.dynamic.Codecs;
import org.joml.Vector2fc;

@Environment(value=EnvType.CLIENT)
public record UniformValue.Vec2fValue(Vector2fc value) implements UniformValue
{
    public static final Codec<UniformValue.Vec2fValue> CODEC = Codecs.VECTOR_2F.xmap(UniformValue.Vec2fValue::new, UniformValue.Vec2fValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putVec2(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putVec2();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.VEC2;
    }
}
