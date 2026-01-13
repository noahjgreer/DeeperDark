/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.dedicated.management.RpcMethodInfo;
import net.minecraft.util.Identifier;

public record RpcMethodInfo.Entry<Params, Result>(Identifier name, RpcMethodInfo<Params, Result> contents) {
    public static final Codec<RpcMethodInfo.Entry<?, ?>> CODEC = RpcMethodInfo.Entry.createCodec();

    public static <Params, Result> Codec<RpcMethodInfo.Entry<Params, Result>> createCodec() {
        return RecordCodecBuilder.create(instance -> instance.group((App)Identifier.CODEC.fieldOf("name").forGetter(RpcMethodInfo.Entry::name), (App)RpcMethodInfo.createCodec().forGetter(RpcMethodInfo.Entry::contents)).apply((Applicative)instance, RpcMethodInfo.Entry::new));
    }
}
