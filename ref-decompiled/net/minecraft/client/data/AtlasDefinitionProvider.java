package net.minecraft.client.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BellBlockEntityRenderer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.Atlases;
import net.minecraft.client.texture.atlas.DirectoryAtlasSource;
import net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource;
import net.minecraft.client.texture.atlas.SingleAtlasSource;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.equipment.trim.ArmorTrimAssets;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AtlasDefinitionProvider implements DataProvider {
   private static final Identifier TRIM_PALETTES_ID = Identifier.ofVanilla("trims/color_palettes/trim_palette");
   private static final Map TRIM_ASSET_SUFFIX_TO_COLOR_PALETTE = (Map)streamTrimAssets().collect(Collectors.toMap(ArmorTrimAssets.AssetId::suffix, (assetId) -> {
      return Identifier.ofVanilla("trims/color_palettes/" + assetId.suffix());
   }));
   private static final List ARMOR_TRIM_PATTERN_KEYS;
   private static final List EQUIPMENT_MODEL_LAYER_TYPES;
   private final DataOutput.PathResolver pathResolver;

   public AtlasDefinitionProvider(DataOutput output) {
      this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "atlases");
   }

   private static List getArmorTrimTextures() {
      List list = new ArrayList(ARMOR_TRIM_PATTERN_KEYS.size() * EQUIPMENT_MODEL_LAYER_TYPES.size());
      Iterator var1 = ARMOR_TRIM_PATTERN_KEYS.iterator();

      while(var1.hasNext()) {
         RegistryKey registryKey = (RegistryKey)var1.next();
         Identifier identifier = ArmorTrimPatterns.getId(registryKey);
         Iterator var4 = EQUIPMENT_MODEL_LAYER_TYPES.iterator();

         while(var4.hasNext()) {
            EquipmentModel.LayerType layerType = (EquipmentModel.LayerType)var4.next();
            list.add(identifier.withPath((trimPatternPath) -> {
               String var10000 = layerType.getTrimsDirectory();
               return var10000 + "/" + trimPatternPath;
            }));
         }
      }

      return list;
   }

   private static AtlasSource createSingleAtlasSource(SpriteIdentifier spriteId) {
      return new SingleAtlasSource(spriteId.getTextureId());
   }

   private static AtlasSource createDirectoryAtlasSource(SpriteMapper spriteMapper) {
      return new DirectoryAtlasSource(spriteMapper.prefix(), spriteMapper.prefix() + "/");
   }

   private static List createAtlasSources(SpriteMapper spriteMapper) {
      return List.of(createDirectoryAtlasSource(spriteMapper));
   }

   private static List createAtlasSources(String directorySource) {
      return List.of(new DirectoryAtlasSource(directorySource, ""));
   }

   private static Stream streamTrimAssets() {
      return ItemModelGenerator.TRIM_MATERIALS.stream().map(ItemModelGenerator.TrimMaterial::assets).flatMap((assets) -> {
         return Stream.concat(Stream.of(assets.base()), assets.overrides().values().stream());
      }).sorted(Comparator.comparing(ArmorTrimAssets.AssetId::suffix));
   }

   private static List createArmorTrimsAtlasSources() {
      return List.of(new PalettedPermutationsAtlasSource(getArmorTrimTextures(), TRIM_PALETTES_ID, TRIM_ASSET_SUFFIX_TO_COLOR_PALETTE));
   }

   private static List createBlocksAtlasSources() {
      return List.of(createDirectoryAtlasSource(TexturedRenderLayers.BLOCK_SPRITE_MAPPER), createDirectoryAtlasSource(TexturedRenderLayers.ITEM_SPRITE_MAPPER), createDirectoryAtlasSource(ConduitBlockEntityRenderer.SPRITE_MAPPER), createSingleAtlasSource(BellBlockEntityRenderer.BELL_BODY_TEXTURE), createSingleAtlasSource(TexturedRenderLayers.DECORATED_POT_SIDE), createSingleAtlasSource(EnchantingTableBlockEntityRenderer.BOOK_TEXTURE), new PalettedPermutationsAtlasSource(List.of(ItemModelGenerator.HELMET_TRIM_ID_PREFIX, ItemModelGenerator.CHESTPLATE_TRIM_ID_PREFIX, ItemModelGenerator.LEGGINGS_TRIM_ID_PREFIX, ItemModelGenerator.BOOTS_TRIM_ID_PREFIX), TRIM_PALETTES_ID, TRIM_ASSET_SUFFIX_TO_COLOR_PALETTE));
   }

   private static List createBannerPatternsAtlasSources() {
      return List.of(createSingleAtlasSource(ModelBaker.BANNER_BASE), createDirectoryAtlasSource(TexturedRenderLayers.BANNER_PATTERN_SPRITE_MAPPER));
   }

   private static List createShieldAtlasSources() {
      return List.of(createSingleAtlasSource(ModelBaker.SHIELD_BASE), createSingleAtlasSource(ModelBaker.SHIELD_BASE_NO_PATTERN), createDirectoryAtlasSource(TexturedRenderLayers.SHIELD_PATTERN_SPRITE_MAPPER));
   }

   private static List createGuiAtlasSources() {
      return List.of(new DirectoryAtlasSource("gui/sprites", ""), new DirectoryAtlasSource("mob_effect", "mob_effect/"));
   }

   public CompletableFuture run(DataWriter writer) {
      return CompletableFuture.allOf(this.runForAtlas(writer, Atlases.ARMOR_TRIMS, createArmorTrimsAtlasSources()), this.runForAtlas(writer, Atlases.BANNER_PATTERNS, createBannerPatternsAtlasSources()), this.runForAtlas(writer, Atlases.BEDS, createAtlasSources(TexturedRenderLayers.BED_SPRITE_MAPPER)), this.runForAtlas(writer, Atlases.BLOCKS, createBlocksAtlasSources()), this.runForAtlas(writer, Atlases.CHESTS, createAtlasSources(TexturedRenderLayers.CHEST_SPRITE_MAPPER)), this.runForAtlas(writer, Atlases.DECORATED_POT, createAtlasSources(TexturedRenderLayers.DECORATED_POT_SPRITE_MAPPER)), this.runForAtlas(writer, Atlases.GUI, createGuiAtlasSources()), this.runForAtlas(writer, Atlases.MAP_DECORATIONS, createAtlasSources("map/decorations")), this.runForAtlas(writer, Atlases.PAINTINGS, createAtlasSources("painting")), this.runForAtlas(writer, Atlases.PARTICLES, createAtlasSources("particle")), this.runForAtlas(writer, Atlases.SHIELD_PATTERNS, createShieldAtlasSources()), this.runForAtlas(writer, Atlases.SHULKER_BOXES, createAtlasSources(TexturedRenderLayers.SHULKER_SPRITE_MAPPER)), this.runForAtlas(writer, Atlases.SIGNS, createAtlasSources(TexturedRenderLayers.SIGN_SPRITE_MAPPER)));
   }

   private CompletableFuture runForAtlas(DataWriter writer, Identifier atlasId, List atlasSources) {
      return DataProvider.writeCodecToPath(writer, AtlasSourceManager.LIST_CODEC, atlasSources, this.pathResolver.resolveJson(atlasId));
   }

   public String getName() {
      return "Atlas Definitions";
   }

   static {
      ARMOR_TRIM_PATTERN_KEYS = List.of(ArmorTrimPatterns.SENTRY, ArmorTrimPatterns.DUNE, ArmorTrimPatterns.COAST, ArmorTrimPatterns.WILD, ArmorTrimPatterns.WARD, ArmorTrimPatterns.EYE, ArmorTrimPatterns.VEX, ArmorTrimPatterns.TIDE, ArmorTrimPatterns.SNOUT, ArmorTrimPatterns.RIB, ArmorTrimPatterns.SPIRE, ArmorTrimPatterns.WAYFINDER, ArmorTrimPatterns.SHAPER, ArmorTrimPatterns.SILENCE, ArmorTrimPatterns.RAISER, ArmorTrimPatterns.HOST, ArmorTrimPatterns.FLOW, ArmorTrimPatterns.BOLT);
      EQUIPMENT_MODEL_LAYER_TYPES = List.of(EquipmentModel.LayerType.HUMANOID, EquipmentModel.LayerType.HUMANOID_LEGGINGS);
   }
}
