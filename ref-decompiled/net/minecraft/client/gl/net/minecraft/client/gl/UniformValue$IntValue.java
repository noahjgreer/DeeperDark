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
public record UniformValue.IntValue(int value) implements UniformValue
{
    public static final Codec<UniformValue.IntValue> CODEC = Codec.INT.xmap(UniformValue.IntValue::new, UniformValue.IntValue::value);

    @Override
    public void write(Std140Builder builder) {
        builder.putInt(this.value);
    }

    @Override
    public void addSize(Std140SizeCalculator calculator) {
        calculator.putInt();
    }

    @Override
    public UniformValue.Type getType() {
        return UniformValue.Type.INT;
    }
}
