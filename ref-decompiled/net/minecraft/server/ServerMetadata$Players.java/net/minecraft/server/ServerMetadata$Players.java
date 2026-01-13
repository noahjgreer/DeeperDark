/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.server;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.PlayerConfigEntry;

public record ServerMetadata.Players(int max, int online, List<PlayerConfigEntry> sample) {
    public static final Codec<ServerMetadata.Players> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("max").forGetter(ServerMetadata.Players::max), (App)Codec.INT.fieldOf("online").forGetter(ServerMetadata.Players::online), (App)PlayerConfigEntry.CODEC.listOf().lenientOptionalFieldOf("sample", List.of()).forGetter(ServerMetadata.Players::sample)).apply((Applicative)instance, ServerMetadata.Players::new));
}
