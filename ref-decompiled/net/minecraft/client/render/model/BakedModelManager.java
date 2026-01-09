package net.minecraft.client.render.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.item.ItemAssetsLoader;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.json.GeneratedItemModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.atlas.Atlases;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BakedModelManager implements ResourceReloader, AutoCloseable, FabricBakedModelManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceFinder MODELS_FINDER = ResourceFinder.json("models");
   private static final Map LAYERS_TO_LOADERS;
   private Map bakedItemModels = Map.of();
   private Map itemProperties = Map.of();
   private final SpriteAtlasManager atlasManager;
   private final BlockModels blockModelCache;
   private final BlockColors colorMap;
   private LoadedEntityModels entityModels;
   private LoadedBlockEntityModels blockEntityModels;
   private int mipmapLevels;
   private ModelBaker.BlockItemModels missingModels;
   private Object2IntMap modelGroups;

   public BakedModelManager(TextureManager textureManager, BlockColors colorMap, int mipmapLevels) {
      this.entityModels = LoadedEntityModels.EMPTY;
      this.blockEntityModels = LoadedBlockEntityModels.EMPTY;
      this.modelGroups = Object2IntMaps.emptyMap();
      this.colorMap = colorMap;
      this.mipmapLevels = mipmapLevels;
      this.blockModelCache = new BlockModels(this);
      this.atlasManager = new SpriteAtlasManager(LAYERS_TO_LOADERS, textureManager);
   }

   public BlockStateModel getMissingModel() {
      return this.missingModels.block();
   }

   public ItemModel getItemModel(Identifier id) {
      return (ItemModel)this.bakedItemModels.getOrDefault(id, this.missingModels.item());
   }

   public ItemAsset.Properties getItemProperties(Identifier id) {
      return (ItemAsset.Properties)this.itemProperties.getOrDefault(id, ItemAsset.Properties.DEFAULT);
   }

   public BlockModels getBlockModels() {
      return this.blockModelCache;
   }

   public final CompletableFuture reload(ResourceReloader.Synchronizer synchronizer, ResourceManager resourceManager, Executor executor, Executor executor2) {
      CompletableFuture completableFuture = CompletableFuture.supplyAsync(LoadedEntityModels::copy, executor);
      CompletableFuture completableFuture2 = completableFuture.thenApplyAsync(LoadedBlockEntityModels::fromModels, executor);
      CompletableFuture completableFuture3 = reloadModels(resourceManager, executor);
      CompletableFuture completableFuture4 = BlockStatesLoader.load(resourceManager, executor);
      CompletableFuture completableFuture5 = ItemAssetsLoader.load(resourceManager, executor);
      CompletableFuture completableFuture6 = CompletableFuture.allOf(completableFuture3, completableFuture4, completableFuture5).thenApplyAsync((async) -> {
         return collect((Map)completableFuture3.join(), (BlockStatesLoader.LoadedModels)completableFuture4.join(), (ItemAssetsLoader.Result)completableFuture5.join());
      }, executor);
      CompletableFuture completableFuture7 = completableFuture4.thenApplyAsync((definition) -> {
         return group(this.colorMap, definition);
      }, executor);
      Map map = this.atlasManager.reload(resourceManager, this.mipmapLevels, executor);
      CompletableFuture var10000 = CompletableFuture.allOf((CompletableFuture[])Stream.concat(map.values().stream(), Stream.of(completableFuture6, completableFuture7, completableFuture4, completableFuture5, completableFuture, completableFuture2, completableFuture3)).toArray((i) -> {
         return new CompletableFuture[i];
      })).thenComposeAsync((async) -> {
         Map map2 = Util.transformMapValues(map, CompletableFuture::join);
         Models models = (Models)completableFuture6.join();
         Object2IntMap object2IntMap = (Object2IntMap)completableFuture7.join();
         Set set = Sets.difference(((Map)completableFuture3.join()).keySet(), models.models.keySet());
         if (!set.isEmpty()) {
            LOGGER.debug("Unreferenced models: \n{}", set.stream().sorted().map((id) -> {
               return "\t" + String.valueOf(id) + "\n";
            }).collect(Collectors.joining()));
         }

         ModelBaker modelBaker = new ModelBaker((LoadedEntityModels)completableFuture.join(), ((BlockStatesLoader.LoadedModels)completableFuture4.join()).models(), ((ItemAssetsLoader.Result)completableFuture5.join()).contents(), models.models(), models.missing());
         return bake(map2, modelBaker, object2IntMap, (LoadedEntityModels)completableFuture.join(), (LoadedBlockEntityModels)completableFuture2.join(), executor);
      }, executor).thenCompose((result) -> {
         return result.readyForUpload.thenApply((void_) -> {
            return result;
         });
      });
      Objects.requireNonNull(synchronizer);
      return var10000.thenCompose(synchronizer::whenPrepared).thenAcceptAsync((bakingResult) -> {
         this.upload(bakingResult, Profilers.get());
      }, executor2);
   }

   private static CompletableFuture reloadModels(ResourceManager resourceManager, Executor executor) {
      return CompletableFuture.supplyAsync(() -> {
         return MODELS_FINDER.findResources(resourceManager);
      }, executor).thenCompose((models) -> {
         List list = new ArrayList(models.size());
         Iterator var3 = models.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry entry = (Map.Entry)var3.next();
            list.add(CompletableFuture.supplyAsync(() -> {
               Identifier identifier = MODELS_FINDER.toResourceId((Identifier)entry.getKey());

               try {
                  Reader reader = ((Resource)entry.getValue()).getReader();

                  Pair var3;
                  try {
                     var3 = Pair.of(identifier, JsonUnbakedModel.deserialize(reader));
                  } catch (Throwable var6) {
                     if (reader != null) {
                        try {
                           reader.close();
                        } catch (Throwable var5) {
                           var6.addSuppressed(var5);
                        }
                     }

                     throw var6;
                  }

                  if (reader != null) {
                     reader.close();
                  }

                  return var3;
               } catch (Exception var7) {
                  LOGGER.error("Failed to load model {}", entry.getKey(), var7);
                  return null;
               }
            }, executor));
         }

         return Util.combineSafe(list).thenApply((modelsx) -> {
            return (Map)modelsx.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));
         });
      });
   }

   private static Models collect(Map modelMap, BlockStatesLoader.LoadedModels stateDefinition, ItemAssetsLoader.Result result) {
      ScopedProfiler scopedProfiler = Profilers.get().scoped("dependencies");

      Models var5;
      try {
         ReferencedModelsCollector referencedModelsCollector = new ReferencedModelsCollector(modelMap, MissingModel.create());
         referencedModelsCollector.addSpecialModel(GeneratedItemModel.GENERATED, new GeneratedItemModel());
         Collection var10000 = stateDefinition.models().values();
         Objects.requireNonNull(referencedModelsCollector);
         var10000.forEach(referencedModelsCollector::resolve);
         result.contents().values().forEach((asset) -> {
            referencedModelsCollector.resolve((ResolvableModel)asset.model());
         });
         var5 = new Models(referencedModelsCollector.getMissingModel(), referencedModelsCollector.collectModels());
      } catch (Throwable var7) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

      return var5;
   }

   private static CompletableFuture bake(final Map atlasMap, ModelBaker baker, Object2IntMap blockStates, LoadedEntityModels entityModels, LoadedBlockEntityModels blockEntityModels, Executor executor) {
      CompletableFuture completableFuture = CompletableFuture.allOf((CompletableFuture[])atlasMap.values().stream().map(SpriteAtlasManager.AtlasPreparation::whenComplete).toArray((i) -> {
         return new CompletableFuture[i];
      }));
      final Multimap multimap = Multimaps.synchronizedMultimap(HashMultimap.create());
      final Multimap multimap2 = Multimaps.synchronizedMultimap(HashMultimap.create());
      return baker.bake(new ErrorCollectingSpriteGetter() {
         private final Sprite missingSprite;

         {
            this.missingSprite = ((SpriteAtlasManager.AtlasPreparation)atlasMap.get(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)).getMissingSprite();
         }

         public Sprite get(SpriteIdentifier id, SimpleModel model) {
            SpriteAtlasManager.AtlasPreparation atlasPreparation = (SpriteAtlasManager.AtlasPreparation)atlasMap.get(id.getAtlasId());
            Sprite sprite = atlasPreparation.getSprite(id.getTextureId());
            if (sprite != null) {
               return sprite;
            } else {
               multimap.put(model.name(), id);
               return atlasPreparation.getMissingSprite();
            }
         }

         public Sprite getMissing(String name, SimpleModel model) {
            multimap2.put(model.name(), name);
            return this.missingSprite;
         }
      }, executor).thenApply((models) -> {
         multimap.asMap().forEach((modelName, sprites) -> {
            LOGGER.warn("Missing textures in model {}:\n{}", modelName, sprites.stream().sorted(SpriteIdentifier.COMPARATOR).map((spriteId) -> {
               String var10000 = String.valueOf(spriteId.getAtlasId());
               return "    " + var10000 + ":" + String.valueOf(spriteId.getTextureId());
            }).collect(Collectors.joining("\n")));
         });
         multimap2.asMap().forEach((modelName, textureIds) -> {
            LOGGER.warn("Missing texture references in model {}:\n{}", modelName, textureIds.stream().sorted().map((string) -> {
               return "    " + string;
            }).collect(Collectors.joining("\n")));
         });
         Map map2 = toStateMap(models.blockStateModels(), models.missingModels().block());
         return new BakingResult(models, blockStates, map2, atlasMap, entityModels, blockEntityModels, completableFuture);
      });
   }

   private static Map toStateMap(Map blockStateModels, BlockStateModel missingModel) {
      ScopedProfiler scopedProfiler = Profilers.get().scoped("block state dispatch");

      IdentityHashMap var8;
      try {
         Map map = new IdentityHashMap(blockStateModels);
         Iterator var4 = Registries.BLOCK.iterator();

         while(true) {
            if (!var4.hasNext()) {
               var8 = map;
               break;
            }

            Block block = (Block)var4.next();
            block.getStateManager().getStates().forEach((state) -> {
               if (blockStateModels.putIfAbsent(state, missingModel) == null) {
                  LOGGER.warn("Missing model for variant: '{}'", state);
               }

            });
         }
      } catch (Throwable var7) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

      return var8;
   }

   private static Object2IntMap group(BlockColors colors, BlockStatesLoader.LoadedModels definition) {
      ScopedProfiler scopedProfiler = Profilers.get().scoped("block groups");

      Object2IntMap var3;
      try {
         var3 = ModelGrouper.group(colors, definition);
      } catch (Throwable var6) {
         if (scopedProfiler != null) {
            try {
               scopedProfiler.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (scopedProfiler != null) {
         scopedProfiler.close();
      }

      return var3;
   }

   private void upload(BakingResult bakingResult, Profiler profiler) {
      profiler.push("upload");
      bakingResult.atlasPreparations.values().forEach(SpriteAtlasManager.AtlasPreparation::upload);
      ModelBaker.BakedModels bakedModels = bakingResult.bakedModels;
      this.bakedItemModels = bakedModels.itemStackModels();
      this.itemProperties = bakedModels.itemProperties();
      this.modelGroups = bakingResult.modelGroups;
      this.missingModels = bakedModels.missingModels();
      profiler.swap("cache");
      this.blockModelCache.setModels(bakingResult.modelCache);
      this.blockEntityModels = bakingResult.specialBlockModelRenderer;
      this.entityModels = bakingResult.entityModelSet;
      profiler.pop();
   }

   public boolean shouldRerender(BlockState from, BlockState to) {
      if (from == to) {
         return false;
      } else {
         int i = this.modelGroups.getInt(from);
         if (i != -1) {
            int j = this.modelGroups.getInt(to);
            if (i == j) {
               FluidState fluidState = from.getFluidState();
               FluidState fluidState2 = to.getFluidState();
               return fluidState != fluidState2;
            }
         }

         return true;
      }
   }

   public SpriteAtlasTexture getAtlas(Identifier id) {
      return this.atlasManager.getAtlas(id);
   }

   public void close() {
      this.atlasManager.close();
   }

   public void setMipmapLevels(int mipmapLevels) {
      this.mipmapLevels = mipmapLevels;
   }

   public Supplier getBlockEntityModelsSupplier() {
      return () -> {
         return this.blockEntityModels;
      };
   }

   public Supplier getEntityModelsSupplier() {
      return () -> {
         return this.entityModels;
      };
   }

   static {
      LAYERS_TO_LOADERS = Map.of(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Atlases.BANNER_PATTERNS, TexturedRenderLayers.BEDS_ATLAS_TEXTURE, Atlases.BEDS, TexturedRenderLayers.CHEST_ATLAS_TEXTURE, Atlases.CHESTS, TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Atlases.SHIELD_PATTERNS, TexturedRenderLayers.SIGNS_ATLAS_TEXTURE, Atlases.SIGNS, TexturedRenderLayers.SHULKER_BOXES_ATLAS_TEXTURE, Atlases.SHULKER_BOXES, TexturedRenderLayers.ARMOR_TRIMS_ATLAS_TEXTURE, Atlases.ARMOR_TRIMS, TexturedRenderLayers.DECORATED_POT_ATLAS_TEXTURE, Atlases.DECORATED_POT, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Atlases.BLOCKS);
   }

   @Environment(EnvType.CLIENT)
   private static record Models(BakedSimpleModel missing, Map models) {
      final Map models;

      Models(BakedSimpleModel bakedSimpleModel, Map map) {
         this.missing = bakedSimpleModel;
         this.models = map;
      }

      public BakedSimpleModel missing() {
         return this.missing;
      }

      public Map models() {
         return this.models;
      }
   }

   @Environment(EnvType.CLIENT)
   private static record BakingResult(ModelBaker.BakedModels bakedModels, Object2IntMap modelGroups, Map modelCache, Map atlasPreparations, LoadedEntityModels entityModelSet, LoadedBlockEntityModels specialBlockModelRenderer, CompletableFuture readyForUpload) {
      final ModelBaker.BakedModels bakedModels;
      final Object2IntMap modelGroups;
      final Map modelCache;
      final Map atlasPreparations;
      final LoadedEntityModels entityModelSet;
      final LoadedBlockEntityModels specialBlockModelRenderer;
      final CompletableFuture readyForUpload;

      BakingResult(ModelBaker.BakedModels bakedModels, Object2IntMap object2IntMap, Map map, Map map2, LoadedEntityModels loadedEntityModels, LoadedBlockEntityModels loadedBlockEntityModels, CompletableFuture completableFuture) {
         this.bakedModels = bakedModels;
         this.modelGroups = object2IntMap;
         this.modelCache = map;
         this.atlasPreparations = map2;
         this.entityModelSet = loadedEntityModels;
         this.specialBlockModelRenderer = loadedBlockEntityModels;
         this.readyForUpload = completableFuture;
      }

      public ModelBaker.BakedModels bakedModels() {
         return this.bakedModels;
      }

      public Object2IntMap modelGroups() {
         return this.modelGroups;
      }

      public Map modelCache() {
         return this.modelCache;
      }

      public Map atlasPreparations() {
         return this.atlasPreparations;
      }

      public LoadedEntityModels entityModelSet() {
         return this.entityModelSet;
      }

      public LoadedBlockEntityModels specialBlockModelRenderer() {
         return this.specialBlockModelRenderer;
      }

      public CompletableFuture readyForUpload() {
         return this.readyForUpload;
      }
   }
}
