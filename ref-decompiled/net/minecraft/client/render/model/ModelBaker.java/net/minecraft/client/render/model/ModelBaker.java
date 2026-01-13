/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Interner
 *  com.google.common.collect.Interners
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.GeometryBakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleBlockStateModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.AsyncHelper;
import org.joml.Vector3fc;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ModelBaker {
    public static final SpriteIdentifier FIRE_0 = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("fire_0");
    public static final SpriteIdentifier FIRE_1 = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("fire_1");
    public static final SpriteIdentifier LAVA_STILL = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("lava_still");
    public static final SpriteIdentifier LAVA_FLOW = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("lava_flow");
    public static final SpriteIdentifier WATER_STILL = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("water_still");
    public static final SpriteIdentifier WATER_FLOW = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("water_flow");
    public static final SpriteIdentifier WATER_OVERLAY = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("water_overlay");
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/banner_base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base"));
    public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base_nopattern"));
    public static final int MAX_BLOCK_DESTRUCTION_STAGE = 10;
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(stage -> Identifier.ofVanilla("block/destroy_stage_" + stage)).collect(Collectors.toList());
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGE_TEXTURES = BLOCK_DESTRUCTION_STAGES.stream().map(id -> id.withPath(path -> "textures/" + path + ".png")).collect(Collectors.toList());
    public static final List<RenderLayer> BLOCK_DESTRUCTION_RENDER_LAYERS = BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayers::crumbling).collect(Collectors.toList());
    static final Logger LOGGER = LogUtils.getLogger();
    private final LoadedEntityModels entityModels;
    private final SpriteHolder spriteHolder;
    private final PlayerSkinCache skinCache;
    private final Map<BlockState, BlockStateModel.UnbakedGrouped> blockModels;
    private final Map<Identifier, ItemAsset> itemAssets;
    final Map<Identifier, BakedSimpleModel> simpleModels;
    final BakedSimpleModel missingModel;

    public ModelBaker(LoadedEntityModels entityModels, SpriteHolder spriteHolder, PlayerSkinCache skinCache, Map<BlockState, BlockStateModel.UnbakedGrouped> blockModels, Map<Identifier, ItemAsset> itemAssets, Map<Identifier, BakedSimpleModel> simpleModels, BakedSimpleModel missingModel) {
        this.entityModels = entityModels;
        this.spriteHolder = spriteHolder;
        this.skinCache = skinCache;
        this.blockModels = blockModels;
        this.itemAssets = itemAssets;
        this.simpleModels = simpleModels;
        this.missingModel = missingModel;
    }

    public CompletableFuture<BakedModels> bake(ErrorCollectingSpriteGetter spriteGetter, Executor executor) {
        Vec3fInternerImpl vec3fInternerImpl = new Vec3fInternerImpl();
        BlockItemModels blockItemModels = BlockItemModels.bake(this.missingModel, spriteGetter, vec3fInternerImpl);
        BakerImpl bakerImpl = new BakerImpl(spriteGetter, vec3fInternerImpl, blockItemModels);
        CompletableFuture<Map<BlockState, BlockStateModel>> completableFuture = AsyncHelper.mapValues(this.blockModels, (state, unbaked) -> {
            try {
                return unbaked.bake((BlockState)state, bakerImpl);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", state, (Object)exception);
                return null;
            }
        }, executor);
        CompletableFuture<Map<Identifier, ItemModel>> completableFuture2 = AsyncHelper.mapValues(this.itemAssets, (state, asset) -> {
            try {
                return asset.model().bake(new ItemModel.BakeContext(bakerImpl, this.entityModels, this.spriteHolder, this.skinCache, blockItemModels.item, asset.registrySwapper()));
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake item model: '{}'", state, (Object)exception);
                return null;
            }
        }, executor);
        HashMap map = new HashMap(this.itemAssets.size());
        this.itemAssets.forEach((id, asset) -> {
            ItemAsset.Properties properties = asset.properties();
            if (!properties.equals(ItemAsset.Properties.DEFAULT)) {
                map.put(id, properties);
            }
        });
        return completableFuture.thenCombine(completableFuture2, (blockStateModels, itemModels) -> new BakedModels(blockItemModels, (Map<BlockState, BlockStateModel>)blockStateModels, (Map<Identifier, ItemModel>)itemModels, map));
    }

    @Environment(value=EnvType.CLIENT)
    static class Vec3fInternerImpl
    implements Baker.Vec3fInterner {
        private final Interner<Vector3fc> INTERNER = Interners.newStrongInterner();

        Vec3fInternerImpl() {
        }

        @Override
        public Vector3fc intern(Vector3fc vec) {
            return (Vector3fc)this.INTERNER.intern((Object)vec);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static final class BlockItemModels
    extends Record {
        final BlockModelPart blockPart;
        private final BlockStateModel block;
        final ItemModel item;

        public BlockItemModels(BlockModelPart blockPart, BlockStateModel block, ItemModel item) {
            this.blockPart = blockPart;
            this.block = block;
            this.item = item;
        }

        public static BlockItemModels bake(BakedSimpleModel model, final ErrorCollectingSpriteGetter errorCollectingSpriteGetter, final Baker.Vec3fInterner vec3fInterner) {
            Baker baker = new Baker(){

                @Override
                public BakedSimpleModel getModel(Identifier id) {
                    throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(id));
                }

                @Override
                public BlockModelPart getBlockPart() {
                    throw new IllegalStateException();
                }

                @Override
                public <T> T compute(Baker.ResolvableCacheKey<T> key) {
                    return key.compute(this);
                }

                @Override
                public ErrorCollectingSpriteGetter getSpriteGetter() {
                    return errorCollectingSpriteGetter;
                }

                @Override
                public Baker.Vec3fInterner getVec3fInterner() {
                    return vec3fInterner;
                }
            };
            ModelTextures modelTextures = model.getTextures();
            boolean bl = model.getAmbientOcclusion();
            boolean bl2 = model.getGuiLight().isSide();
            ModelTransformation modelTransformation = model.getTransformations();
            BakedGeometry bakedGeometry = model.bakeGeometry(modelTextures, baker, ModelRotation.IDENTITY);
            Sprite sprite = model.getParticleTexture(modelTextures, baker);
            GeometryBakedModel geometryBakedModel = new GeometryBakedModel(bakedGeometry, bl, sprite);
            SimpleBlockStateModel blockStateModel = new SimpleBlockStateModel(geometryBakedModel);
            MissingItemModel itemModel = new MissingItemModel(bakedGeometry.getAllQuads(), new ModelSettings(bl2, sprite, modelTransformation));
            return new BlockItemModels(geometryBakedModel, blockStateModel, itemModel);
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockItemModels.class, "blockPart;block;item", "blockPart", "block", "item"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockItemModels.class, "blockPart;block;item", "blockPart", "block", "item"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockItemModels.class, "blockPart;block;item", "blockPart", "block", "item"}, this, object);
        }

        public BlockModelPart blockPart() {
            return this.blockPart;
        }

        public BlockStateModel block() {
            return this.block;
        }

        public ItemModel item() {
            return this.item;
        }
    }

    @Environment(value=EnvType.CLIENT)
    class BakerImpl
    implements Baker {
        private final ErrorCollectingSpriteGetter spriteGetter;
        private final Baker.Vec3fInterner interner;
        private final BlockItemModels blockItemModels;
        private final Map<Baker.ResolvableCacheKey<Object>, Object> cache = new ConcurrentHashMap<Baker.ResolvableCacheKey<Object>, Object>();
        private final Function<Baker.ResolvableCacheKey<Object>, Object> cacheValueFunction = key -> key.compute(this);

        BakerImpl(ErrorCollectingSpriteGetter spriteGetter, Baker.Vec3fInterner interner, BlockItemModels blockItemModels) {
            this.spriteGetter = spriteGetter;
            this.interner = interner;
            this.blockItemModels = blockItemModels;
        }

        @Override
        public BlockModelPart getBlockPart() {
            return this.blockItemModels.blockPart;
        }

        @Override
        public ErrorCollectingSpriteGetter getSpriteGetter() {
            return this.spriteGetter;
        }

        @Override
        public Baker.Vec3fInterner getVec3fInterner() {
            return this.interner;
        }

        @Override
        public BakedSimpleModel getModel(Identifier id) {
            BakedSimpleModel bakedSimpleModel = ModelBaker.this.simpleModels.get(id);
            if (bakedSimpleModel == null) {
                LOGGER.warn("Requested a model that was not discovered previously: {}", (Object)id);
                return ModelBaker.this.missingModel;
            }
            return bakedSimpleModel;
        }

        @Override
        public <T> T compute(Baker.ResolvableCacheKey<T> key) {
            return (T)this.cache.computeIfAbsent(key, this.cacheValueFunction);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record BakedModels(BlockItemModels missingModels, Map<BlockState, BlockStateModel> blockStateModels, Map<Identifier, ItemModel> itemStackModels, Map<Identifier, ItemAsset.Properties> itemProperties) {
    }
}
