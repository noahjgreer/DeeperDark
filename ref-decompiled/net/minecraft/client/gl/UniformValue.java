/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.buffers.Std140SizeCalculator
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.UniformValue
 *  net.minecraft.client.gl.UniformValue$Type
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.UniformValue;

@Environment(value=EnvType.CLIENT)
public interface UniformValue {
    public static final Codec<UniformValue> CODEC = Type.CODEC.dispatch(UniformValue::getType, val -> val.mapCodec);

    public void write(Std140Builder var1);

    public void addSize(Std140SizeCalculator var1);

    public Type getType();
}

