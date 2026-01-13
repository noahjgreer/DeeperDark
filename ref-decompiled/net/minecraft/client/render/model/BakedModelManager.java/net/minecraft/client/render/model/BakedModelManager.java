/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.item.ItemAssetsLoader;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.render.model.ModelGrouper;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.GeneratedItemModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class BakedModelManager
implements ResourceReloader,
FabricBakedModelManager {
    public static final Identifier BLOCK_OR_ITEM = Identifier.ofVanilla("block_or_item");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder MODELS_FINDER = ResourceFinder.json("models");
    private Map<Identifier, ItemModel> bakedItemModels = Map.of();
    private Map<Identifier, ItemAsset.Properties> itemProperties = Map.of();
    private final AtlasManager atlasManager;
    private final PlayerSkinCache skinCache;
    private final BlockModels blockModelCache;
    private final BlockColors colorMap;
    private LoadedEntityModels entityModels = LoadedEntityModels.EMPTY;
    private LoadedBlockEntityModels blockEntityModels = LoadedBlockEntityModels.EMPTY;
    private ModelBaker.BlockItemModels missingModels;
    private Object2IntMap<BlockState> modelGroups = Object2IntMaps.emptyMap();

    public BakedModelManager(BlockColors colorMap, AtlasManager atlasManager, PlayerSkinCache skinCache) {
        this.colorMap = colorMap;
        this.atlasManager = atlasManager;
        this.skinCache = skinCache;
        this.blockModelCache = new BlockModels(this);
    }

    public BlockStateModel getMissingModel() {
        return this.missingModels.block();
    }

    public ItemModel getItemModel(Identifier id) {
        return this.bakedItemModels.getOrDefault(id, this.missingModels.item());
    }

    public ItemAsset.Properties getItemProperties(Identifier id) {
        return this.itemProperties.getOrDefault(id, ItemAsset.Properties.DEFAULT);
    }

    public BlockModels getBlockModels() {
        return this.blockModelCache;
    }

    @Override
    public final CompletableFuture<Void> reload(ResourceReloader.Store store, Executor executor, ResourceReloader.Synchronizer synchronizer, Executor executor2) {
        ResourceManager resourceManager = store.getResourceManager();
        CompletableFuture<LoadedEntityModels> completableFuture = CompletableFuture.supplyAsync(LoadedEntityModels::copy, executor);
        CompletionStage completableFuture2 = completableFuture.thenApplyAsync(entityModels -> LoadedBlockEntityModels.fromModels(new SpecialModelRenderer.BakeContext.Simple((LoadedEntityModels)entityModels, this.atlasManager, this.skinCache)), executor);
        CompletableFuture<Map<Identifier, UnbakedModel>> completableFuture3 = BakedModelManager.reloadModels(resourceManager, executor);
        CompletableFuture<BlockStatesLoader.LoadedModels> completableFuture4 = BlockStatesLoader.load(resourceManager, executor);
        CompletableFuture<ItemAssetsLoader.Result> completableFuture5 = ItemAssetsLoader.load(resourceManager, executor);
        CompletionStage completableFuture6 = CompletableFuture.allOf(completableFuture3, completableFuture4, completableFuture5).thenApplyAsync(async -> BakedModelManager.collect((Map)completableFuture3.join(), (BlockStatesLoader.LoadedModels)completableFuture4.join(), (ItemAssetsLoader.Result)completableFuture5.join()), executor);
        CompletionStage completableFuture7 = completableFuture4.thenApplyAsync(definition -> BakedModelManager.group(this.colorMap, definition), executor);
        AtlasManager.Stitch stitch = store.getOrThrow(AtlasManager.stitchKey);
        CompletableFuture<SpriteLoader.StitchResult> completableFuture8 = stitch.getPreparations(Atlases.BLOCKS);
        CompletableFuture<SpriteLoader.StitchResult> completableFuture9 = stitch.getPreparations(Atlases.ITEMS);
        return ((CompletableFuture)((CompletableFuture)CompletableFuture.allOf(new CompletableFuture[]{completableFuture8, completableFuture9, completableFuture6, completableFuture7, completableFuture4, completableFuture5, completableFuture, completableFuture2, completableFuture3}).thenComposeAsync(arg_0 -> this.method_65753(completableFuture8, completableFuture9, (CompletableFuture)completableFuture6, (CompletableFuture)completableFuture7, completableFuture3, completableFuture, completableFuture4, completableFuture5, (CompletableFuture)completableFuture2, executor, arg_0), executor)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(this::upload, executor2);
    }

    private static CompletableFuture<Map<Identifier, UnbakedModel>> reloadModels(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> MODELS_FINDER.findResources(resourceManager), executor).thenCompose(models2 -> {
            ArrayList<CompletableFuture<@Nullable Pair>> list = new ArrayList<CompletableFuture<Pair>>(models2.size());
            for (Map.Entry entry : models2.entrySet()) {
                list.add(CompletableFuture.supplyAsync(() -> {
                    Pair pair;
                    block8: {
                        Identifier identifier = MODELS_FINDER.toResourceId((Identifier)entry.getKey());
                        @Nullable BufferedReader reader = ((Resource)entry.getValue()).getReader();
                        try {
                            pair = Pair.of((Object)identifier, (Object)JsonUnbakedModel.deserialize(reader));
                            if (reader == null) break block8;
                        }
                        catch (Throwable throwable) {
                            try {
                                if (reader != null) {
                                    try {
                                        ((Reader)reader).close();
                                    }
                                    catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            }
                            catch (Exception exception) {
                                LOGGER.error("Failed to load model {}", entry.getKey(), (Object)exception);
                                return null;
                            }
                        }
                        ((Reader)reader).close();
                    }
                    return pair;
                }, executor));
            }
            return Util.combineSafe(list).thenApply(models -> models.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond)));
        });
    }

    private static Models collect(Map<Identifier, UnbakedModel> modelMap, BlockStatesLoader.LoadedModels stateDefinition, ItemAssetsLoader.Result result) {
        try (ScopedProfiler scopedProfiler = Profilers.get().scoped("dependencies");){
            ReferencedModelsCollector referencedModelsCollector = new ReferencedModelsCollector(modelMap, MissingModel.create());
            referencedModelsCollector.addSpecialModel(GeneratedItemModel.GENERATED, new GeneratedItemModel());
            stateDefinition.models().values().forEach(referencedModelsCollector::resolve);
            result.contents().values().forEach(asset -> referencedModelsCollector.resolve(asset.model()));
            Models models = new Models(referencedModelsCollector.getMissingModel(), referencedModelsCollector.collectModels());
            return models;
        }
    }

    private static CompletableFuture<BakingResult> bake(final SpriteLoader.StitchResult blocksResult, final SpriteLoader.StitchResult itemsResult, ModelBaker baker, Object2IntMap<BlockState> groups, LoadedEntityModels entityModels, LoadedBlockEntityModels blockEntityModels, Executor executor) {
        final Multimap multimap = Multimaps.synchronizedMultimap((Multimap)HashMultimap.create());
        final Multimap multimap2 = Multimaps.synchronizedMultimap((Multimap)HashMultimap.create());
        return baker.bake(new ErrorCollectingSpriteGetter(){
            private final Sprite missingBlockSprite;
            private final Sprite missingItemSprite;
            {
                this.missingBlockSprite = blocksResult.missing();
                this.missingItemSprite = itemsResult.missing();
            }

            @Override
            public Sprite get(SpriteIdentifier id, SimpleModel model) {
                Sprite sprite;
                Identifier identifier = id.getAtlasId();
                boolean bl = identifier.equals(BLOCK_OR_ITEM);
                boolean bl2 = identifier.equals(SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE);
                boolean bl3 = identifier.equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
                if ((bl || bl2) && (sprite = itemsResult.getSprite(id.getTextureId())) != null) {
                    return sprite;
                }
                if ((bl || bl3) && (sprite = blocksResult.getSprite(id.getTextureId())) != null) {
                    return sprite;
                }
                multimap.put((Object)model.name(), (Object)id);
                return bl2 ? this.missingItemSprite : this.missingBlockSprite;
            }

            @Override
            public Sprite getMissing(String name, SimpleModel model) {
                multimap2.put((Object)model.name(), (Object)name);
                return this.missingBlockSprite;
            }
        }, executor).thenApply(bakedModels -> {
            multimap.asMap().forEach((modelName, sprites) -> LOGGER.warn("Missing textures in model {}:\n{}", modelName, (Object)sprites.stream().sorted(SpriteIdentifier.COMPARATOR).map(spriteId -> "    " + String.valueOf(spriteId.getAtlasId()) + ":" + String.valueOf(spriteId.getTextureId())).collect(Collectors.joining("\n"))));
            multimap2.asMap().forEach((modelName, textureIds) -> LOGGER.warn("Missing texture references in model {}:\n{}", modelName, (Object)textureIds.stream().sorted().map(textureId -> "    " + textureId).collect(Collectors.joining("\n"))));
            Map<BlockState, BlockStateModel> map = BakedModelManager.toStateMap(bakedModels.blockStateModels(), bakedModels.missingModels().block());
            return new BakingResult((ModelBaker.BakedModels)bakedModels, groups, map, entityModels, blockEntityModels);
        });
    }

    private static Map<BlockState, BlockStateModel> toStateMap(Map<BlockState, BlockStateModel> blockStateModels, BlockStateModel missingModel) {
        try (ScopedProfiler scopedProfiler = Profilers.get().scoped("block state dispatch");){
            IdentityHashMap<BlockState, BlockStateModel> map = new IdentityHashMap<BlockState, BlockStateModel>(blockStateModels);
            for (Block block : Registries.BLOCK) {
                block.getStateManager().getStates().forEach(state -> {
                    if (blockStateModels.putIfAbsent((BlockState)state, missingModel) == null) {
                        LOGGER.warn("Missing model for variant: '{}'", state);
                    }
                });
            }
            IdentityHashMap<BlockState, BlockStateModel> identityHashMap = map;
            return identityHashMap;
        }
    }

    private static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.LoadedModels definition) {
        try (ScopedProfiler scopedProfiler = Profilers.get().scoped("block groups");){
            Object2IntMap<BlockState> object2IntMap = ModelGrouper.group(colors, definition);
            return object2IntMap;
        }
    }

    private void upload(BakingResult bakingResult) {
        ModelBaker.BakedModels bakedModels = bakingResult.bakedModels;
        this.bakedItemModels = bakedModels.itemStackModels();
        this.itemProperties = bakedModels.itemProperties();
        this.modelGroups = bakingResult.modelGroups;
        this.missingModels = bakedModels.missingModels();
        this.blockModelCache.setModels(bakingResult.modelCache);
        this.blockEntityModels = bakingResult.specialBlockModelRenderer;
        this.entityModels = bakingResult.entityModelSet;
    }

    public boolean shouldRerender(BlockState from, BlockState to) {
        int j;
        if (from == to) {
            return false;
        }
        int i = this.modelGroups.getInt((Object)from);
        if (i != -1 && i == (j = this.modelGroups.getInt((Object)to))) {
            FluidState fluidState2;
            FluidState fluidState = from.getFluidState();
            return fluidState != (fluidState2 = to.getFluidState());
        }
        return true;
    }

    public LoadedBlockEntityModels getBlockEntityModelsSupplier() {
        return this.blockEntityModels;
    }

    public Supplier<LoadedEntityModels> getEntityModelsSupplier() {
        return () -> this.entityModels;
    }

    private /* synthetic */ CompletionStage method_65753(CompletableFuture completableFuture, CompletableFuture completableFuture2, CompletableFuture completableFuture3, CompletableFuture completableFuture4, CompletableFuture completableFuture5, CompletableFuture completableFuture6, CompletableFuture completableFuture7, CompletableFuture completableFuture8, CompletableFuture completableFuture9, Executor executor, Void v) {
        SpriteLoader.StitchResult stitchResult = (SpriteLoader.StitchResult)completableFuture.join();
        SpriteLoader.StitchResult stitchResult2 = (SpriteLoader.StitchResult)completableFuture2.join();
        Models models = (Models)completableFuture3.join();
        Object2IntMap object2IntMap = (Object2IntMap)completableFuture4.join();
        Sets.SetView set = Sets.difference(((Map)completableFuture5.join()).keySet(), models.models.keySet());
        if (!set.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", (Object)set.stream().sorted().map(id -> "\t" + String.valueOf(id) + "\n").collect(Collectors.joining()));
        }
        ModelBaker modelBaker = new ModelBaker((LoadedEntityModels)completableFuture6.join(), this.atlasManager, this.skinCache, ((BlockStatesLoader.LoadedModels)completableFuture7.join()).models(), ((ItemAssetsLoader.Result)completableFuture8.join()).contents(), models.models(), models.missing());
        return BakedModelManager.bake(stitchResult, stitchResult2, modelBaker, (Object2IntMap<BlockState>)object2IntMap, (LoadedEntityModels)completableFuture6.join(), (LoadedBlockEntityModels)completableFuture9.join(), executor);
    }

    @Environment(value=EnvType.CLIENT)
    static final class Models
    extends Record {
        private final BakedSimpleModel missing;
        final Map<Identifier, BakedSimpleModel> models;

        Models(BakedSimpleModel missing, Map<Identifier, BakedSimpleModel> models) {
            this.missing = missing;
            this.models = models;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Models.class, "missing;models", "missing", "models"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Models.class, "missing;models", "missing", "models"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Models.class, "missing;models", "missing", "models"}, this, object);
        }

        public BakedSimpleModel missing() {
            return this.missing;
        }

        public Map<Identifier, BakedSimpleModel> models() {
            return this.models;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static final class BakingResult
    extends Record {
        final ModelBaker.BakedModels bakedModels;
        final Object2IntMap<BlockState> modelGroups;
        final Map<BlockState, BlockStateModel> modelCache;
        final LoadedEntityModels entityModelSet;
        final LoadedBlockEntityModels specialBlockModelRenderer;

        BakingResult(ModelBaker.BakedModels bakedModels, Object2IntMap<BlockState> modelGroups, Map<BlockState, BlockStateModel> modelCache, LoadedEntityModels entityModelSet, LoadedBlockEntityModels specialBlockModelRenderer) {
            this.bakedModels = bakedModels;
            this.modelGroups = modelGroups;
            this.modelCache = modelCache;
            this.entityModelSet = entityModelSet;
            this.specialBlockModelRenderer = specialBlockModelRenderer;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakingResult.class, "bakedModels;modelGroups;modelCache;entityModelSet;specialBlockModelRenderer", "bakedModels", "modelGroups", "modelCache", "entityModelSet", "specialBlockModelRenderer"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakingResult.class, "bakedModels;modelGroups;modelCache;entityModelSet;specialBlockModelRenderer", "bakedModels", "modelGroups", "modelCache", "entityModelSet", "specialBlockModelRenderer"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakingResult.class, "bakedModels;modelGroups;modelCache;entityModelSet;specialBlockModelRenderer", "bakedModels", "modelGroups", "modelCache", "entityModelSet", "specialBlockModelRenderer"}, this, object);
        }

        public ModelBaker.BakedModels bakedModels() {
            return this.bakedModels;
        }

        public Object2IntMap<BlockState> modelGroups() {
            return this.modelGroups;
        }

        public Map<BlockState, BlockStateModel> modelCache() {
            return this.modelCache;
        }

        public LoadedEntityModels entityModelSet() {
            return this.entityModelSet;
        }

        public LoadedBlockEntityModels specialBlockModelRenderer() {
            return this.specialBlockModelRenderer;
        }
    }
}
