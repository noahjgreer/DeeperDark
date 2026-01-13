/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
class AtlasLoader.1
implements AtlasSource.SpriteRegions {
    final /* synthetic */ Map field_41389;

    AtlasLoader.1() {
        this.field_41389 = map;
    }

    @Override
    public void add(Identifier arg, AtlasSource.SpriteRegion region) {
        AtlasSource.SpriteRegion spriteRegion = this.field_41389.put(arg, region);
        if (spriteRegion != null) {
            spriteRegion.close();
        }
    }

    @Override
    public void removeIf(Predicate<Identifier> predicate) {
        Iterator iterator = this.field_41389.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if (!predicate.test((Identifier)entry.getKey())) continue;
            ((AtlasSource.SpriteRegion)entry.getValue()).close();
            iterator.remove();
        }
    }
}
