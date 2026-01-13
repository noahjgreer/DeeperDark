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
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontLoader;

@Environment(value=EnvType.CLIENT)
static final class FontManager.Providers
extends Record {
    final List<FontLoader.Provider> providers;
    public static final Codec<FontManager.Providers> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)FontLoader.Provider.CODEC.listOf().fieldOf("providers").forGetter(FontManager.Providers::providers)).apply((Applicative)instance, FontManager.Providers::new));

    private FontManager.Providers(List<FontLoader.Provider> providers) {
        this.providers = providers;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontManager.Providers.class, "providers", "providers"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontManager.Providers.class, "providers", "providers"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontManager.Providers.class, "providers", "providers"}, this, object);
    }

    public List<FontLoader.Provider> providers() {
        return this.providers;
    }
}
