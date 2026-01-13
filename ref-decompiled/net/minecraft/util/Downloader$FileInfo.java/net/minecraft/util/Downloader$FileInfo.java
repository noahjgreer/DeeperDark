/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Downloader.FileInfo(String name, long size) {
    public static final Codec<Downloader.FileInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.STRING.fieldOf("name").forGetter(Downloader.FileInfo::name), (App)Codec.LONG.fieldOf("size").forGetter(Downloader.FileInfo::size)).apply((Applicative)instance, Downloader.FileInfo::new));
}
