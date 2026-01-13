/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.item.ItemAsset
 *  net.minecraft.client.item.ItemAsset$Properties
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.item.model.ItemModel$BakeContext
 *  net.minecraft.client.render.model.BakedSimpleModel
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.Baker$Vec3fInterner
 *  net.minecraft.client.render.model.BlockStateModel$UnbakedGrouped
 *  net.minecraft.client.render.model.ErrorCollectingSpriteGetter
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.render.model.ModelBaker$BakedModels
 *  net.minecraft.client.render.model.ModelBaker$BakerImpl
 *  net.minecraft.client.render.model.ModelBaker$BlockItemModels
 *  net.minecraft.client.render.model.ModelBaker$Vec3fInternerImpl
 *  net.minecraft.client.texture.PlayerSkinCache
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.thread.AsyncHelper
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
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
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.AsyncHelper;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ModelBaker {
    public static final SpriteIdentifier FIRE_0 = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("fire_0");
    public static final SpriteIdentifier FIRE_1 = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("fire_1");
    public static final SpriteIdentifier LAVA_STILL = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("lava_still");
    public static final SpriteIdentifier LAVA_FLOW = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("lava_flow");
    public static final SpriteIdentifier WATER_STILL = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("water_still");
    public static final SpriteIdentifier WATER_FLOW = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("water_flow");
    public static final SpriteIdentifier WATER_OVERLAY = TexturedRenderLayers.BLOCK_SPRITE_MAPPER.mapVanilla("water_overlay");
    public static final SpriteIdentifier BANNER_BASE = new SpriteIdentifier(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla((String)"entity/banner_base"));
    public static final SpriteIdentifier SHIELD_BASE = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla((String)"entity/shield_base"));
    public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla((String)"entity/shield_base_nopattern"));
    public static final int MAX_BLOCK_DESTRUCTION_STAGE = 10;
    public static final List<Identifier> BLOCK_DESTRUCTION_STAGES = IntStream.range(0, 10).mapToObj(stage -> Identifier.ofVanilla((String)("block/destroy_stage_" + stage))).collect(Collectors.toList());
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
        BlockItemModels blockItemModels = BlockItemModels.bake((BakedSimpleModel)this.missingModel, (ErrorCollectingSpriteGetter)spriteGetter, (Baker.Vec3fInterner)vec3fInternerImpl);
        BakerImpl bakerImpl = new BakerImpl(this, spriteGetter, (Baker.Vec3fInterner)vec3fInternerImpl, blockItemModels);
        CompletableFuture completableFuture = AsyncHelper.mapValues((Map)this.blockModels, (state, unbaked) -> {
            try {
                return unbaked.bake(state, (Baker)bakerImpl);
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", state, (Object)exception);
                return null;
            }
        }, (Executor)executor);
        CompletableFuture completableFuture2 = AsyncHelper.mapValues((Map)this.itemAssets, (state, asset) -> {
            try {
                return asset.model().bake(new ItemModel.BakeContext((Baker)bakerImpl, this.entityModels, this.spriteHolder, this.skinCache, blockItemModels.item, asset.registrySwapper()));
            }
            catch (Exception exception) {
                LOGGER.warn("Unable to bake item model: '{}'", state, (Object)exception);
                return null;
            }
        }, (Executor)executor);
        HashMap map = new HashMap(this.itemAssets.size());
        this.itemAssets.forEach((id, asset) -> {
            ItemAsset.Properties properties = asset.properties();
            if (!properties.equals((Object)ItemAsset.Properties.DEFAULT)) {
                map.put(id, properties);
            }
        });
        return completableFuture.thenCombine((CompletionStage)completableFuture2, (blockStateModels, itemModels) -> new BakedModels(blockItemModels, blockStateModels, itemModels, map));
    }
}

