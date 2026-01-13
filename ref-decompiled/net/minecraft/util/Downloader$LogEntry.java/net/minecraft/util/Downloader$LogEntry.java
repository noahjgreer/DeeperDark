/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.Downloader;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;

record Downloader.LogEntry(UUID id, String url, Instant time, Optional<String> hash, Either<String, Downloader.FileInfo> errorOrFileInfo) {
    public static final Codec<Downloader.LogEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Uuids.STRING_CODEC.fieldOf("id").forGetter(Downloader.LogEntry::id), (App)Codec.STRING.fieldOf("url").forGetter(Downloader.LogEntry::url), (App)Codecs.INSTANT.fieldOf("time").forGetter(Downloader.LogEntry::time), (App)Codec.STRING.optionalFieldOf("hash").forGetter(Downloader.LogEntry::hash), (App)Codec.mapEither((MapCodec)Codec.STRING.fieldOf("error"), (MapCodec)Downloader.FileInfo.CODEC.fieldOf("file")).forGetter(Downloader.LogEntry::errorOrFileInfo)).apply((Applicative)instance, Downloader.LogEntry::new));
}
