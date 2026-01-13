/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.texture;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.resource.metadata.GuiResourceMetadata;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
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
    private final Map<Identifier, Entry> entriesByTextureId = new HashMap<Identifier, Entry>();
    private final Map<Identifier, Entry> entriesByDefinitionId = new HashMap<Identifier, Entry>();
    private Map<SpriteIdentifier, Sprite> sprites = Map.of();
    private int mipmapLevels;

    public AtlasManager(TextureManager textureManager, int mipmapLevels) {
        for (Metadata metadata : ATLAS_METADATA) {
            SpriteAtlasTexture spriteAtlasTexture = new SpriteAtlasTexture(metadata.textureId);
            textureManager.registerTexture(metadata.textureId, spriteAtlasTexture);
            Entry entry = new Entry(spriteAtlasTexture, metadata);
            this.entriesByTextureId.put(metadata.textureId, entry);
            this.entriesByDefinitionId.put(metadata.definitionId, entry);
        }
        this.mipmapLevels = mipmapLevels;
    }

    public SpriteAtlasTexture getAtlasTexture(Identifier id) {
        Entry entry = this.entriesByDefinitionId.get(id);
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

    @Override
    public Sprite getSprite(SpriteIdentifier id) {
        Sprite sprite = this.sprites.get(id);
        if (sprite != null) {
            return sprite;
        }
        Identifier identifier = id.getAtlasId();
        Entry entry = this.entriesByTextureId.get(identifier);
        if (entry == null) {
            throw new IllegalArgumentException("Invalid atlas texture id: " + String.valueOf(identifier));
        }
        return entry.atlas().getMissingSprite();
    }

    @Override
    public void prepareSharedState(ResourceReloader.Store store) {
        int i = this.entriesByDefinitionId.size();
        ArrayList<CompletableEntry> list = new ArrayList<CompletableEntry>(i);
        HashMap<Identifier, CompletableFuture<SpriteLoader.StitchResult>> map = new HashMap<Identifier, CompletableFuture<SpriteLoader.StitchResult>>(i);
        ArrayList list2 = new ArrayList(i);
        this.entriesByDefinitionId.forEach((textureId, metadata) -> {
            CompletableFuture<SpriteLoader.StitchResult> completableFuture = new CompletableFuture<SpriteLoader.StitchResult>();
            map.put((Identifier)textureId, completableFuture);
            list.add(new CompletableEntry((Entry)metadata, completableFuture));
            list2.add(completableFuture.thenCompose(SpriteLoader.StitchResult::readyForUpload));
        });
        CompletableFuture<Void> completableFuture = CompletableFuture.allOf((CompletableFuture[])list2.toArray(CompletableFuture[]::new));
        store.put(stitchKey, new Stitch(list, map, completableFuture));
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Store store, Executor executor, ResourceReloader.Synchronizer synchronizer, Executor executor2) {
        Stitch stitch = store.getOrThrow(stitchKey);
        ResourceManager resourceManager = store.getResourceManager();
        stitch.entries.forEach(entry -> entry.entry.load(resourceManager, executor, this.mipmapLevels).whenComplete((stitchResult, throwable) -> {
            if (stitchResult != null) {
                completableEntry.preparations.complete((SpriteLoader.StitchResult)stitchResult);
            } else {
                completableEntry.preparations.completeExceptionally((Throwable)throwable);
            }
        }));
        return ((CompletableFuture)stitch.readyForUpload.thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(v -> this.logDuplicates(stitch), executor2);
    }

    private void logDuplicates(Stitch stitch) {
        this.sprites = stitch.createSpriteMap();
        HashMap map = new HashMap();
        this.sprites.forEach((id, sprite) -> {
            Sprite sprite2;
            if (!id.getTextureId().equals(MissingSprite.getMissingSpriteId()) && (sprite2 = map.putIfAbsent(id.getTextureId(), sprite)) != null) {
                LOGGER.warn("Duplicate sprite {} from atlas {}, already defined in atlas {}. This will be rejected in a future version", new Object[]{id.getTextureId(), id.getAtlasId(), sprite2.getAtlasId()});
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public static final class Metadata
    extends Record {
        final Identifier textureId;
        final Identifier definitionId;
        final boolean createMipmaps;
        final Set<ResourceMetadataSerializer<?>> additionalMetadata;

        public Metadata(Identifier textureId, Identifier definitionId, boolean createMipmaps) {
            this(textureId, definitionId, createMipmaps, Set.of());
        }

        public Metadata(Identifier textureId, Identifier definitionId, boolean createMipmaps, Set<ResourceMetadataSerializer<?>> additionalMetadata) {
            this.textureId = textureId;
            this.definitionId = definitionId;
            this.createMipmaps = createMipmaps;
            this.additionalMetadata = additionalMetadata;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Metadata.class, "textureId;definitionLocation;createMipmaps;additionalMetadata", "textureId", "definitionId", "createMipmaps", "additionalMetadata"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Metadata.class, "textureId;definitionLocation;createMipmaps;additionalMetadata", "textureId", "definitionId", "createMipmaps", "additionalMetadata"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Metadata.class, "textureId;definitionLocation;createMipmaps;additionalMetadata", "textureId", "definitionId", "createMipmaps", "additionalMetadata"}, this, object);
        }

        public Identifier textureId() {
            return this.textureId;
        }

        public Identifier definitionId() {
            return this.definitionId;
        }

        public boolean createMipmaps() {
            return this.createMipmaps;
        }

        public Set<ResourceMetadataSerializer<?>> additionalMetadata() {
            return this.additionalMetadata;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class Entry
    extends Record
    implements AutoCloseable {
        final SpriteAtlasTexture atlas;
        final Metadata metadata;

        Entry(SpriteAtlasTexture atlas, Metadata metadata) {
            this.atlas = atlas;
            this.metadata = metadata;
        }

        @Override
        public void close() {
            this.atlas.clear();
        }

        CompletableFuture<SpriteLoader.StitchResult> load(ResourceManager manager, Executor executor, int mipLevel) {
            return SpriteLoader.fromAtlas(this.atlas).load(manager, this.metadata.definitionId, this.metadata.createMipmaps ? mipLevel : 0, executor, this.metadata.additionalMetadata);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "atlas;config", "atlas", "metadata"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "atlas;config", "atlas", "metadata"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "atlas;config", "atlas", "metadata"}, this, object);
        }

        public SpriteAtlasTexture atlas() {
            return this.atlas;
        }

        public Metadata metadata() {
            return this.metadata;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Stitch {
        final List<CompletableEntry> entries;
        private final Map<Identifier, CompletableFuture<SpriteLoader.StitchResult>> preparations;
        final CompletableFuture<?> readyForUpload;

        Stitch(List<CompletableEntry> entries, Map<Identifier, CompletableFuture<SpriteLoader.StitchResult>> preparations, CompletableFuture<?> readyForUpload) {
            this.entries = entries;
            this.preparations = preparations;
            this.readyForUpload = readyForUpload;
        }

        public Map<SpriteIdentifier, Sprite> createSpriteMap() {
            HashMap<SpriteIdentifier, Sprite> map = new HashMap<SpriteIdentifier, Sprite>();
            this.entries.forEach(entry -> entry.fillSpriteMap(map));
            return map;
        }

        public CompletableFuture<SpriteLoader.StitchResult> getPreparations(Identifier atlasTextureId) {
            return Objects.requireNonNull(this.preparations.get(atlasTextureId));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class CompletableEntry
    extends Record {
        final Entry entry;
        final CompletableFuture<SpriteLoader.StitchResult> preparations;

        CompletableEntry(Entry entry, CompletableFuture<SpriteLoader.StitchResult> preparations) {
            this.entry = entry;
            this.preparations = preparations;
        }

        public void fillSpriteMap(Map<SpriteIdentifier, Sprite> sprites) {
            SpriteLoader.StitchResult stitchResult = this.preparations.join();
            this.entry.atlas.create(stitchResult);
            stitchResult.sprites().forEach((id, sprite) -> sprites.put(new SpriteIdentifier(this.entry.metadata.textureId, (Identifier)id), (Sprite)sprite));
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CompletableEntry.class, "entry;preparations", "entry", "preparations"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CompletableEntry.class, "entry;preparations", "entry", "preparations"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CompletableEntry.class, "entry;preparations", "entry", "preparations"}, this, object);
        }

        public Entry entry() {
            return this.entry;
        }

        public CompletableFuture<SpriteLoader.StitchResult> preparations() {
            return this.preparations;
        }
    }
}
