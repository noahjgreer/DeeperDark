/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.Scaling;

@Environment(value=EnvType.CLIENT)
public record Scaling.Stretch() implements Scaling
{
    public static final MapCodec<Scaling.Stretch> CODEC = MapCodec.unit(Scaling.Stretch::new);

    @Override
    public Scaling.Type getType() {
        return Scaling.Type.STRETCH;
    }
}
