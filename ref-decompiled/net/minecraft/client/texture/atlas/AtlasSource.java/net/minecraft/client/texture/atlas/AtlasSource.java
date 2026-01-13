/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.texture.atlas;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface AtlasSource {
    public static final ResourceFinder RESOURCE_FINDER = new ResourceFinder("textures", ".png");

    public void load(ResourceManager var1, SpriteRegions var2);

    public MapCodec<? extends AtlasSource> getCodec();

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteRegion
    extends SpriteSource {
        default public void close() {
        }
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface SpriteSource {
        public @Nullable SpriteContents load(SpriteOpener var1);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface SpriteRegions {
        default public void add(Identifier id, Resource resource) {
            this.add(id, opener -> opener.loadSprite(id, resource));
        }

        public void add(Identifier var1, SpriteRegion var2);

        public void removeIf(Predicate<Identifier> var1);
    }
}
