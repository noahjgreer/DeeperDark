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
import java.util.List;
import net.minecraft.server.dedicated.management.RpcDiscover;
import net.minecraft.server.dedicated.management.RpcMethodInfo;

public record RpcDiscover.Document(String jsonRpcProtocolVersion, RpcDiscover.Info discoverInfo, List<RpcMethodInfo.Entry<?, ?>> methods, RpcDiscover.Components components) {
    public static final MapCodec<RpcDiscover.Document> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("openrpc").forGetter(RpcDiscover.Document::jsonRpcProtocolVersion), (App)RpcDiscover.Info.CODEC.codec().fieldOf("info").forGetter(RpcDiscover.Document::discoverInfo), (App)Codec.list(RpcMethodInfo.Entry.CODEC).fieldOf("methods").forGetter(RpcDiscover.Document::methods), (App)RpcDiscover.Components.CODEC.codec().fieldOf("components").forGetter(RpcDiscover.Document::components)).apply((Applicative)instance, RpcDiscover.Document::new));
}
