/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontFilterType;

@Environment(value=EnvType.CLIENT)
public static class FontFilterType.FilterMap {
    private final Map<FontFilterType, Boolean> activeFilters;
    public static final Codec<FontFilterType.FilterMap> CODEC = Codec.unboundedMap(CODEC, (Codec)Codec.BOOL).xmap(FontFilterType.FilterMap::new, filterType -> filterType.activeFilters);
    public static final FontFilterType.FilterMap NO_FILTER = new FontFilterType.FilterMap(Map.of());

    public FontFilterType.FilterMap(Map<FontFilterType, Boolean> activeFilters) {
        this.activeFilters = activeFilters;
    }

    public boolean isAllowed(Set<FontFilterType> activeFilters) {
        for (Map.Entry<FontFilterType, Boolean> entry : this.activeFilters.entrySet()) {
            if (activeFilters.contains(entry.getKey()) == entry.getValue().booleanValue()) continue;
            return false;
        }
        return true;
    }

    public FontFilterType.FilterMap apply(FontFilterType.FilterMap activeFilters) {
        HashMap<FontFilterType, Boolean> map = new HashMap<FontFilterType, Boolean>(activeFilters.activeFilters);
        map.putAll(this.activeFilters);
        return new FontFilterType.FilterMap(Map.copyOf(map));
    }
}
