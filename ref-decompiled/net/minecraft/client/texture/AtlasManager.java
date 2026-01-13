/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.resource.metadata.GuiResourceMetadata
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.AtlasManager
 *  net.minecraft.client.texture.AtlasManager$CompletableEntry
 *  net.minecraft.client.texture.AtlasManager$Entry
 *  net.minecraft.client.texture.AtlasManager$Metadata
 *  net.minecraft.client.texture.AtlasManager$Stitch
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.texture.SpriteLoader$StitchResult
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.ResourceReloader
 *  net.minecraft.resource.ResourceReloader$Key
 *  net.minecraft.resource.ResourceReloader$Store
 *  net.minecraft.resource.ResourceReloader$Synchronizer
 *  net.minecraft.util.Atlases
 *  net.minecraft.util.Identifier
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.resource.metadata.GuiResourceMetadata;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class AtlasManager
implements ResourceReloader,
SpriteHolder,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<Metadata> ATLAS_METADATA = List.of(new Metadata(TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE, Atlases.ARMOR_TRIMS, false), new Metadata(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Atlases.BANNER_PATTERNS, false), new Metadata(TexturedRenderLayers.BEDS_ATLAS_TEXTURE, Atlases.BEDS, false), new Metadata(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Atlases.BLOCKS, true), new Metadata(SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE, Atlases.ITEMS, false), new Metadata(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, Atlases.CHESTS, false), new Metadata(TexturedRenderLayers.DECORATED_POT_ATLAS_TEXTURE, Atlases.DECORATED_POT, false), new Metadata(TexturedRenderLayers.GUI_ATLAS_TEXTURE, Atlases.GUI, false, Set.of(GuiResourceMetadata.SERIALIZER)), new Metadata(TexturedRenderLayers.MAP_DECORATIONS_ATLAS_TEXTURE, Atlases.MAP_DECORATIONS, false), new Metadata(TexturedRenderLayers.PAINTINGS_ATLAS_TEXTURE, Atlases.PAINTINGS, false), new Metadata(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, Atlases.PARTICLES, false), new Metadata(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Atlases.SHIELD_PATTERNS, false), new Metadata(TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, Atlases.SHULKER_BOXES, false), new Metadata(TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, Atlases.SIGNS, false), new Metadata(TexturedRenderLayers.CELESTIALS_ATLAS_TEXTURE, Atlases.CELESTIALS, false));
    public static final ResourceReloader.Key<Stitch> stitchKey = new ResourceReloader.Key();
    private final Map<Identifier, Entry> entriesByTextureId = new HashMap();
    private final Map<Identifier, Entry> entriesByDefinitionId = new HashMap();
    private Map<SpriteIdentifier, Sprite> sprites = Map.of();
    private int mipmapLevels;

    public AtlasManager(TextureManager textureManager, int mipmapLevels) {
        for (Metadata metadata : ATLAS_METADATA) {
            SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture(metadata.textureId);
            textureManager.registerTexture(metadata.textureId, (AbstractTexture)spriteAtlasTexture);
            Entry entry = new Entry(spriteAtlasTexture, metadata);
            this.entriesByTextureId.put(metadata.textureId, entry);
            this.entriesByDefinitionId.put(metadata.definitionId, entry);
        }
        this.mipmapLevels = mipmapLevels;
    }

    public SpriteAtlasTexture getAtlasTexture(Identifier id) {
        Entry entry = (Entry)this.entriesByDefinitionId.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("Invalid atlas id: " + String.valueOf(id));
        }
        return entry.atlas();
    }

    public void acceptAtlasTextures(BiConsumer<Identifier, SpriteAtlasTexture> consumer) {
        this.entriesByDefinitionId.forEach((definitionId, entry) -> consumer.accept((Identifier)definitionId, entry.atlas));
    }

    public void setMipmapLevels(int mipmapLevels) {
        this.mipmapLevels = mipmapLevels;
    }

    @Override
    public void close() {
        this.sprites = Map.of();
        this.entriesByDefinitionId.values().forEach(Entry::close);
        this.entriesByDefinitionId.clear();
        this.entriesByTextureId.clear();
    }

    public Sprite getSprite(SpriteIdentifier id) {
        Sprite sprite = (Sprite)this.sprites.get(id);
        if (sprite != null) {
            return sprite;
        }
        Identifier identifier = id.getAtlasId();
        Entry entry = (Entry)this.entriesByTextureId.get(identifier);
        if (entry == null) {
            throw new IllegalArgumentException("Invalid atlas texture id: " + String.valueOf(identifier));
        }
        return entry.atlas().getMissingSprite();
    }

    public void prepareSharedState(ResourceReloader.Store store) {
        int i = this.entriesByDefinitionId.size();
        ArrayList list = new ArrayList(i);
        HashMap map = new HashMap(i);
        ArrayList list2 = new ArrayList(i);
        this.entriesByDefinitionId.forEach((textureId, metadata) -> {
            CompletableFuture completableFuture = new CompletableFuture();
            map.put(textureId, completableFuture);
            list.add(new CompletableEntry(metadata, completableFuture));
            list2.add(completableFuture.thenCompose(SpriteLoader.StitchResult::readyForUpload));
        });
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf((CompletableFuture[])list2.toArray(CompletableFuture[]::new));
        store.put(stitchKey, (Object)new Stitch(list, map, completableFuture));
    }

    public CompletableFuture<Void> reload(ResourceReloader.Store store, Executor executor, ResourceReloader.Synchronizer synchronizer, Executor executor2) {
        Stitch stitch = (Stitch)store.getOrThrow(stitchKey);
        ResourceManager resourceManager = store.getResourceManager();
        stitch.entries.forEach(entry -> entry.entry.load(resourceManager, executor, this.mipmapLevels).whenComplete((stitchResult, throwable) -> {
            if (stitchResult != null) {
                completableEntry.preparations.complete(stitchResult);
            } else {
                completableEntry.preparations.completeExceptionally((Throwable)throwable);
            }
        }));
        return ((CompletableFuture)stitch.readyForUpload.thenCompose(arg_0 -> ((ResourceReloader.Synchronizer)synchronizer).whenPrepared(arg_0))).thenAcceptAsync(v -> this.logDuplicates(stitch), executor2);
    }

    private void logDuplicates(Stitch stitch) {
        this.sprites = stitch.createSpriteMap();
        HashMap map = new HashMap();
        this.sprites.forEach((id, sprite) -> {
            Sprite sprite2;
            if (!id.getTextureId().equals((Object)MissingSprite.getMissingSpriteId()) && (sprite2 = map.putIfAbsent(id.getTextureId(), sprite)) != null) {
                LOGGER.warn("Duplicate sprite {} from atlas {}, already defined in atlas {}. This will be rejected in a future version", new Object[]{id.getTextureId(), id.getAtlasId(), sprite2.getAtlasId()});
            }
        });
    }
}

