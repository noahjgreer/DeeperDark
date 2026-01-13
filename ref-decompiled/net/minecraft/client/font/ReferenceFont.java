/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.FontLoader
 *  net.minecraft.client.font.FontLoader$Loadable
 *  net.minecraft.client.font.FontLoader$Reference
 *  net.minecraft.client.font.FontType
 *  net.minecraft.client.font.ReferenceFont
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.font;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;
import net.minecraft.client.font.FontType;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record ReferenceFont(Identifier id) implements FontLoader
{
    private final Identifier id;
    public static final MapCodec<ReferenceFont> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("id").forGetter(ReferenceFont::id)).apply((Applicative)instance, ReferenceFont::new));

    public ReferenceFont(Identifier id) {
        this.id = id;
    }

    public FontType getType() {
        return FontType.REFERENCE;
    }

    public Either<FontLoader.Loadable, FontLoader.Reference> build() {
        return Either.right((Object)new FontLoader.Reference(this.id));
    }

    public Identifier id() {
        return this.id;
    }
}

