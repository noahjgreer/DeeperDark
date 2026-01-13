/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class FontManager.ProviderIndex
extends Record {
    private final Map<Identifier, List<Font.FontFilterPair>> fontSets;
    final List<Font> allProviders;

    FontManager.ProviderIndex(Map<Identifier, List<Font.FontFilterPair>> fontSets, List<Font> allProviders) {
        this.fontSets = fontSets;
        this.allProviders = allProviders;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{FontManager.ProviderIndex.class, "fontSets;allProviders", "fontSets", "allProviders"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{FontManager.ProviderIndex.class, "fontSets;allProviders", "fontSets", "allProviders"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{FontManager.ProviderIndex.class, "fontSets;allProviders", "fontSets", "allProviders"}, this, object);
    }

    public Map<Identifier, List<Font.FontFilterPair>> fontSets() {
        return this.fontSets;
    }

    public List<Font> allProviders() {
        return this.allProviders;
    }
}
