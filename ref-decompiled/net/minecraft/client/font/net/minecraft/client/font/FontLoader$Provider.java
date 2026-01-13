/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontFilterType;
import net.minecraft.client.font.FontLoader;

@Environment(value=EnvType.CLIENT)
public record FontLoader.Provider(FontLoader definition, FontFilterType.FilterMap filter) {
    public static final Codec<FontLoader.Provider> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)CODEC.forGetter(FontLoader.Provider::definition), (App)FontFilterType.FilterMap.CODEC.optionalFieldOf("filter", (Object)FontFilterType.FilterMap.NO_FILTER).forGetter(FontLoader.Provider::filter)).apply((Applicative)instance, FontLoader.Provider::new));
}
