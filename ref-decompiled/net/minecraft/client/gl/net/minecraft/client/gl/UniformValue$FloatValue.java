/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;

@Environment(value=EnvType.CLIENT)
public record UniformValue.FloatValue(float value) implements UniformValue
{
    public static final Codec<UniformValue.FloatValue> CODEC = Codec.FLOAT.xmap(UniformValue.FloatValue::new, UniformValue.FloatValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putFloat(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putFloat();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.FLOAT;
    }
}
