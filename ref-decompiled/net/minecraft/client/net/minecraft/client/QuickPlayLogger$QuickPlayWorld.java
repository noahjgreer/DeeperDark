/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.QuickPlayLogger;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
record QuickPlayLogger.QuickPlayWorld(QuickPlayLogger.WorldType type, String id, String name) {
    public static final MapCodec<QuickPlayLogger.QuickPlayWorld> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)QuickPlayLogger.WorldType.CODEC.fieldOf("type").forGetter(QuickPlayLogger.QuickPlayWorld::type), (App)Codecs.ESCAPED_STRING.fieldOf("id").forGetter(QuickPlayLogger.QuickPlayWorld::id), (App)Codec.STRING.fieldOf("name").forGetter(QuickPlayLogger.QuickPlayWorld::name)).apply((Applicative)instance, QuickPlayLogger.QuickPlayWorld::new));
}
