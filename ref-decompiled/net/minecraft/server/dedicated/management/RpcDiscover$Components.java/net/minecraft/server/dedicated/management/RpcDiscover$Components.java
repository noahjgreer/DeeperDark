/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server.dedicated.management;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.server.dedicated.management.schema.RpcSchema;

public record RpcDiscover.Components(Map<String, RpcSchema<?>> schemas) {
    public static final MapCodec<RpcDiscover.Components> CODEC = RpcDiscover.Components.createCodec();

    private static MapCodec<RpcDiscover.Components> createCodec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.unboundedMap((Codec)Codec.STRING, RpcSchema.CODEC).fieldOf("schemas").forGetter(RpcDiscover.Components::schemas)).apply((Applicative)instance, RpcDiscover.Components::new));
    }
}
