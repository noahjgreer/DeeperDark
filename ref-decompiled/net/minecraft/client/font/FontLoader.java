/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.FontLoader
 *  net.minecraft.client.font.FontLoader$Loadable
 *  net.minecraft.client.font.FontLoader$Reference
 *  net.minecraft.client.font.FontType
 */
package net.minecraft.client.font;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;

@Environment(value=EnvType.CLIENT)
public interface FontLoader {
    public static final MapCodec<FontLoader> CODEC = FontType.CODEC.dispatchMap(FontLoader::getType, FontType::getLoaderCodec);

    public FontType getType();

    public Either<Loadable, Reference> build();
}

