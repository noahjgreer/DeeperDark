package net.minecraft.client.render.model;

import com.mojang.logging.LogUtils;
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
import net.minecraft.client.item.ItemAsset;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.MissingItemModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.AsyncHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelBaker {
   public static final SpriteIdentifier FIRE_0;
   public static final SpriteIdentifier FIRE_1;
   public static final SpriteIdentifier LAVA_FLOW;
   public static final SpriteIdentifier WATER_FLOW;
   public static final SpriteIdentifier WATER_OVERLAY;
   public static final SpriteIdentifier BANNER_BASE;
   public static final SpriteIdentifier SHIELD_BASE;
   public static final SpriteIdentifier SHIELD_BASE_NO_PATTERN;
   public static final int MAX_BLOCK_DESTRUCTION_STAGE = 10;
   public static final List BLOCK_DESTRUCTION_STAGES;
   public static final List BLOCK_DESTRUCTION_STAGE_TEXTURES;
   public static final List BLOCK_DESTRUCTION_RENDER_LAYERS;
   static final Logger LOGGER;
   private final LoadedEntityModels entityModels;
   private final Map blockModels;
   private final Map itemAssets;
   final Map simpleModels;
   final BakedSimpleModel missingModel;

   public ModelBaker(LoadedEntityModels entityModels, Map blockModels, Map itemModels, Map simpleModels, BakedSimpleModel missingModel) {
      this.entityModels = entityModels;
      this.blockModels = blockModels;
      this.itemAssets = itemModels;
      this.simpleModels = simpleModels;
      this.missingModel = missingModel;
   }

   public CompletableFuture bake(ErrorCollectingSpriteGetter spriteGetter, Executor executor) {
      BlockItemModels blockItemModels = ModelBaker.BlockItemModels.bake(this.missingModel, spriteGetter);
      BakerImpl bakerImpl = new BakerImpl(spriteGetter);
      CompletableFuture completableFuture = AsyncHelper.mapValues(this.blockModels, (state, unbaked) -> {
         try {
            return unbaked.bake(state, bakerImpl);
         } catch (Exception var4) {
            LOGGER.warn("Unable to bake model: '{}': {}", state, var4);
            return null;
         }
      }, executor);
      CompletableFuture completableFuture2 = AsyncHelper.mapValues(this.itemAssets, (state, asset) -> {
         try {
            return asset.model().bake(new ItemModel.BakeContext(bakerImpl, this.entityModels, blockItemModels.item, asset.registrySwapper()));
         } catch (Exception var6) {
            LOGGER.warn("Unable to bake item model: '{}'", state, var6);
            return null;
         }
      }, executor);
      Map map = new HashMap(this.itemAssets.size());
      this.itemAssets.forEach((id, asset) -> {
         ItemAsset.Properties properties = asset.properties();
         if (!properties.equals(ItemAsset.Properties.DEFAULT)) {
            map.put(id, properties);
         }

      });
      return completableFuture.thenCombine(completableFuture2, (blockStateModels, itemModels) -> {
         return new BakedModels(blockItemModels, blockStateModels, itemModels, map);
      });
   }

   static {
      FIRE_0 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_0"));
      FIRE_1 = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/fire_1"));
      LAVA_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/lava_flow"));
      WATER_FLOW = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_flow"));
      WATER_OVERLAY = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Identifier.ofVanilla("block/water_overlay"));
      BANNER_BASE = new SpriteIdentifier(TexturedRenderLayers.BANNER_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/banner_base"));
      SHIELD_BASE = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base"));
      SHIELD_BASE_NO_PATTERN = new SpriteIdentifier(TexturedRenderLayers.SHIELD_PATTERNS_ATLAS_TEXTURE, Identifier.ofVanilla("entity/shield_base_nopattern"));
      BLOCK_DESTRUCTION_STAGES = (List)IntStream.range(0, 10).mapToObj((stage) -> {
         return Identifier.ofVanilla("block/destroy_stage_" + stage);
      }).collect(Collectors.toList());
      BLOCK_DESTRUCTION_STAGE_TEXTURES = (List)BLOCK_DESTRUCTION_STAGES.stream().map((id) -> {
         return id.withPath((path) -> {
            return "textures/" + path + ".png";
         });
      }).collect(Collectors.toList());
      BLOCK_DESTRUCTION_RENDER_LAYERS = (List)BLOCK_DESTRUCTION_STAGE_TEXTURES.stream().map(RenderLayer::getBlockBreaking).collect(Collectors.toList());
      LOGGER = LogUtils.getLogger();
   }

   @Environment(EnvType.CLIENT)
   public static record BlockItemModels(BlockStateModel block, ItemModel item) {
      final ItemModel item;

      public BlockItemModels(BlockStateModel blockStateModel, ItemModel itemModel) {
         this.block = blockStateModel;
         this.item = itemModel;
      }

      public static BlockItemModels bake(BakedSimpleModel model, final ErrorCollectingSpriteGetter errorCollectingSpriteGetter) {
         Baker baker = new Baker() {
            public BakedSimpleModel getModel(Identifier id) {
               throw new IllegalStateException("Missing model can't have dependencies, but asked for " + String.valueOf(id));
            }

            public Object compute(Baker.ResolvableCacheKey key) {
               return key.compute(this);
            }

            public ErrorCollectingSpriteGetter getSpriteGetter() {
               return errorCollectingSpriteGetter;
            }
         };
         ModelTextures modelTextures = model.getTextures();
         boolean bl = model.getAmbientOcclusion();
         boolean bl2 = model.getGuiLight().isSide();
         ModelTransformation modelTransformation = model.getTransformations();
         BakedGeometry bakedGeometry = model.bakeGeometry(modelTextures, baker, ModelRotation.X0_Y0);
         Sprite sprite = model.getParticleTexture(modelTextures, baker);
         BlockStateModel blockStateModel = new SimpleBlockStateModel(new GeometryBakedModel(bakedGeometry, bl, sprite));
         ItemModel itemModel = new MissingItemModel(bakedGeometry.getAllQuads(), new ModelSettings(bl2, sprite, modelTransformation));
         return new BlockItemModels(blockStateModel, itemModel);
      }

      public BlockStateModel block() {
         return this.block;
      }

      public ItemModel item() {
         return this.item;
      }
   }

   @Environment(EnvType.CLIENT)
   class BakerImpl implements Baker {
      private final ErrorCollectingSpriteGetter spriteGetter;
      private final Map cache = new ConcurrentHashMap();
      private final Function cacheValueFunction = (key) -> {
         return key.compute(this);
      };

      BakerImpl(final ErrorCollectingSpriteGetter spriteGetter) {
         this.spriteGetter = spriteGetter;
      }

      public ErrorCollectingSpriteGetter getSpriteGetter() {
         return this.spriteGetter;
      }

      public BakedSimpleModel getModel(Identifier id) {
         BakedSimpleModel bakedSimpleModel = (BakedSimpleModel)ModelBaker.this.simpleModels.get(id);
         if (bakedSimpleModel == null) {
            ModelBaker.LOGGER.warn("Requested a model that was not discovered previously: {}", id);
            return ModelBaker.this.missingModel;
         } else {
            return bakedSimpleModel;
         }
      }

      public Object compute(Baker.ResolvableCacheKey key) {
         return this.cache.computeIfAbsent(key, this.cacheValueFunction);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record BakedModels(BlockItemModels missingModels, Map blockStateModels, Map itemStackModels, Map itemProperties) {
      public BakedModels(BlockItemModels blockItemModels, Map map, Map map2, Map map3) {
         this.missingModels = blockItemModels;
         this.blockStateModels = map;
         this.itemStackModels = map2;
         this.itemProperties = map3;
      }

      public BlockItemModels missingModels() {
         return this.missingModels;
      }

      public Map blockStateModels() {
         return this.blockStateModels;
      }

      public Map itemStackModels() {
         return this.itemStackModels;
      }

      public Map itemProperties() {
         return this.itemProperties;
      }
   }
}
