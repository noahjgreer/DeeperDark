/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.texture.atlas;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static interface AtlasSource.SpriteRegions {
    default public void add(Identifier id, Resource resource) {
        this.add(id, opener -> opener.loadSprite(id, resource));
    }

    public void add(Identifier var1, AtlasSource.SpriteRegion var2);

    public void removeIf(Predicate<Identifier> var1);
}
