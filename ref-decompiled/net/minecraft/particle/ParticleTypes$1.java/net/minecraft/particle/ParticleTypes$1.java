/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.particle;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.particle.ParticleType;

static class ParticleTypes.1
extends ParticleType<T> {
    final /* synthetic */ Function field_25126;
    final /* synthetic */ Function field_48457;

    ParticleTypes.1(boolean bl, Function function, Function function2) {
        this.field_25126 = function;
        this.field_48457 = function2;
        super(bl);
    }

    @Override
    public MapCodec<T> getCodec() {
        return (MapCodec)this.field_25126.apply(this);
    }

    @Override
    public PacketCodec<? super RegistryByteBuf, T> getPacketCodec() {
        return (PacketCodec)this.field_48457.apply(this);
    }
}
