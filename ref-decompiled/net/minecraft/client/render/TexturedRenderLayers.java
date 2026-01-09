package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.DecoratedPotPattern;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.SpriteMapper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TexturedRenderLayers {
   public static final Identifier SHULKER_BOXES_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/shulker_boxes.png");
   public static final Identifier BEDS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/beds.png");
   public static final Identifier BANNER_PATTERNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/banner_patterns.png");
   public static final Identifier SHIELD_PATTERNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/shield_patterns.png");
   public static final Identifier SIGNS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/signs.png");
   public static final Identifier CHEST_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/chest.png");
   public static final Identifier ARMOR_TRIMS_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/armor_trims.png");
   public static final Identifier DECORATED_POT_ATLAS_TEXTURE = Identifier.ofVanilla("textures/atlas/decorated_pot.png");
   private static final RenderLayer SHULKER_BOXES_RENDER_LAYER;
   private static final RenderLayer BEDS_RENDER_LAYER;
   private static final RenderLayer BANNER_PATTERNS_RENDER_LAYER;
   private static final RenderLayer SHIELD_PATTERNS_RENDER_LAYER;
   private static final RenderLayer SIGN_RENDER_LAYER;
   private static final RenderLayer CHEST_RENDER_LAYER;
   private static final RenderLayer ARMOR_TRIMS_RENDER_LAYER;
   private static final RenderLayer ARMOR_TRIMS_DECAL_RENDER_LAYER;
   private static final RenderLayer ENTITY_SOLID;
   private static final RenderLayer ENTITY_CUTOUT;
   private static final RenderLayer ITEM_ENTITY_TRANSLUCENT_CULL;
   public static final SpriteMapper ITEM_SPRITE_MAPPER;
   public static final SpriteMapper BLOCK_SPRITE_MAPPER;
   public static final SpriteMapper BANNER_PATTERN_SPRITE_MAPPER;
   public static final SpriteMapper SHIELD_PATTERN_SPRITE_MAPPER;
   public static final SpriteMapper CHEST_SPRITE_MAPPER;
   public static final SpriteMapper DECORATED_POT_SPRITE_MAPPER;
   public static final SpriteMapper BED_SPRITE_MAPPER;
   public static final SpriteMapper SHULKER_SPRITE_MAPPER;
   public static final SpriteMapper SIGN_SPRITE_MAPPER;
   public static final SpriteMapper HANGING_SIGN_SPRITE_MAPPER;
   public static final SpriteIdentifier SHULKER_TEXTURE_ID;
   public static final List COLORED_SHULKER_BOXES_TEXTURES;
   public static final Map SIGN_TYPE_TEXTURES;
   public static final Map HANGING_SIGN_TYPE_TEXTURES;
   public static final SpriteIdentifier BANNER_BASE;
   public static final SpriteIdentifier SHIELD_BASE;
   private static final Map BANNER_PATTERN_TEXTURES;
   private static final Map SHIELD_PATTERN_TEXTURES;
   public static final Map DECORATED_POT_PATTERN_TEXTURES;
   public static final SpriteIdentifier DECORATED_POT_BASE;
   public static final SpriteIdentifier DECORATED_POT_SIDE;
   private static final SpriteIdentifier[] BED_TEXTURES;
   public static final SpriteIdentifier TRAPPED_CHEST;
   public static final SpriteIdentifier TRAPPED_CHEST_LEFT;
   public static final SpriteIdentifier TRAPPED_CHEST_RIGHT;
   public static final SpriteIdentifier CHRISTMAS_CHEST;
   public static final SpriteIdentifier CHRISTMAS_CHEST_LEFT;
   public static final SpriteIdentifier CHRISTMAS_CHEST_RIGHT;
   public static final SpriteIdentifier CHEST;
   public static final SpriteIdentifier CHEST_LEFT;
   public static final SpriteIdentifier CHEST_RIGHT;
   public static final SpriteIdentifier ENDER_CHEST;

   public static RenderLayer getBannerPatterns() {
      return BANNER_PATTERNS_RENDER_LAYER;
   }

   public static RenderLayer getShieldPatterns() {
      return SHIELD_PATTERNS_RENDER_LAYER;
   }

   public static RenderLayer getBeds() {
      return BEDS_RENDER_LAYER;
   }

   public static RenderLayer getShulkerBoxes() {
      return SHULKER_BOXES_RENDER_LAYER;
   }

   public static RenderLayer getSign() {
      return SIGN_RENDER_LAYER;
   }

   public static RenderLayer getHangingSign() {
      return SIGN_RENDER_LAYER;
   }

   public static RenderLayer getChest() {
      return CHEST_RENDER_LAYER;
   }

   public static RenderLayer getArmorTrims(boolean decal) {
      return decal ? ARMOR_TRIMS_DECAL_RENDER_LAYER : ARMOR_TRIMS_RENDER_LAYER;
   }

   public static RenderLayer getEntitySolid() {
      return ENTITY_SOLID;
   }

   public static RenderLayer getEntityCutout() {
      return ENTITY_CUTOUT;
   }

   public static RenderLayer getItemEntityTranslucentCull() {
      return ITEM_ENTITY_TRANSLUCENT_CULL;
   }

   public static SpriteIdentifier getBedTextureId(DyeColor color) {
      return BED_TEXTURES[color.getIndex()];
   }

   public static Identifier createColorId(DyeColor color) {
      return Identifier.ofVanilla(color.getId());
   }

   public static SpriteIdentifier createBedTextureId(DyeColor color) {
      return BED_SPRITE_MAPPER.map(createColorId(color));
   }

   public static SpriteIdentifier getShulkerBoxTextureId(DyeColor color) {
      return (SpriteIdentifier)COLORED_SHULKER_BOXES_TEXTURES.get(color.getIndex());
   }

   public static Identifier createShulkerId(DyeColor color) {
      return Identifier.ofVanilla("shulker_" + color.getId());
   }

   public static SpriteIdentifier createShulkerBoxTextureId(DyeColor color) {
      return SHULKER_SPRITE_MAPPER.map(createShulkerId(color));
   }

   private static SpriteIdentifier createSignTextureId(WoodType type) {
      return SIGN_SPRITE_MAPPER.mapVanilla(type.name());
   }

   private static SpriteIdentifier createHangingSignTextureId(WoodType type) {
      return HANGING_SIGN_SPRITE_MAPPER.mapVanilla(type.name());
   }

   public static SpriteIdentifier getSignTextureId(WoodType signType) {
      return (SpriteIdentifier)SIGN_TYPE_TEXTURES.get(signType);
   }

   public static SpriteIdentifier getHangingSignTextureId(WoodType signType) {
      return (SpriteIdentifier)HANGING_SIGN_TYPE_TEXTURES.get(signType);
   }

   public static SpriteIdentifier getBannerPatternTextureId(RegistryEntry pattern) {
      Map var10000 = BANNER_PATTERN_TEXTURES;
      Identifier var10001 = ((BannerPattern)pattern.value()).assetId();
      SpriteMapper var10002 = BANNER_PATTERN_SPRITE_MAPPER;
      Objects.requireNonNull(var10002);
      return (SpriteIdentifier)var10000.computeIfAbsent(var10001, var10002::map);
   }

   public static SpriteIdentifier getShieldPatternTextureId(RegistryEntry pattern) {
      Map var10000 = SHIELD_PATTERN_TEXTURES;
      Identifier var10001 = ((BannerPattern)pattern.value()).assetId();
      SpriteMapper var10002 = SHIELD_PATTERN_SPRITE_MAPPER;
      Objects.requireNonNull(var10002);
      return (SpriteIdentifier)var10000.computeIfAbsent(var10001, var10002::map);
   }

   @Nullable
   public static SpriteIdentifier getDecoratedPotPatternTextureId(@Nullable RegistryKey potPatternKey) {
      return potPatternKey == null ? null : (SpriteIdentifier)DECORATED_POT_PATTERN_TEXTURES.get(potPatternKey);
   }

   public static SpriteIdentifier getChestTextureId(BlockEntity blockEntity, ChestType type, boolean christmas) {
      if (blockEntity instanceof EnderChestBlockEntity) {
         return ENDER_CHEST;
      } else if (christmas) {
         return getChestTextureId(type, CHRISTMAS_CHEST, CHRISTMAS_CHEST_LEFT, CHRISTMAS_CHEST_RIGHT);
      } else {
         return blockEntity instanceof TrappedChestBlockEntity ? getChestTextureId(type, TRAPPED_CHEST, TRAPPED_CHEST_LEFT, TRAPPED_CHEST_RIGHT) : getChestTextureId(type, CHEST, CHEST_LEFT, CHEST_RIGHT);
      }
   }

   private static SpriteIdentifier getChestTextureId(ChestType type, SpriteIdentifier single, SpriteIdentifier left, SpriteIdentifier right) {
      switch (type) {
         case LEFT:
            return left;
         case RIGHT:
            return right;
         case SINGLE:
         default:
            return single;
      }
   }

   static {
      SHULKER_BOXES_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SHULKER_BOXES_ATLAS_TEXTURE);
      BEDS_RENDER_LAYER = RenderLayer.getEntitySolid(BEDS_ATLAS_TEXTURE);
      BANNER_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(BANNER_PATTERNS_ATLAS_TEXTURE);
      SHIELD_PATTERNS_RENDER_LAYER = RenderLayer.getEntityNoOutline(SHIELD_PATTERNS_ATLAS_TEXTURE);
      SIGN_RENDER_LAYER = RenderLayer.getEntityCutoutNoCull(SIGNS_ATLAS_TEXTURE);
      CHEST_RENDER_LAYER = RenderLayer.getEntityCutout(CHEST_ATLAS_TEXTURE);
      ARMOR_TRIMS_RENDER_LAYER = RenderLayer.getArmorCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
      ARMOR_TRIMS_DECAL_RENDER_LAYER = RenderLayer.createArmorDecalCutoutNoCull(ARMOR_TRIMS_ATLAS_TEXTURE);
      ENTITY_SOLID = RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
      ENTITY_CUTOUT = RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
      ITEM_ENTITY_TRANSLUCENT_CULL = RenderLayer.getItemEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
      ITEM_SPRITE_MAPPER = new SpriteMapper(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, "item");
      BLOCK_SPRITE_MAPPER = new SpriteMapper(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, "block");
      BANNER_PATTERN_SPRITE_MAPPER = new SpriteMapper(BANNER_PATTERNS_ATLAS_TEXTURE, "entity/banner");
      SHIELD_PATTERN_SPRITE_MAPPER = new SpriteMapper(SHIELD_PATTERNS_ATLAS_TEXTURE, "entity/shield");
      CHEST_SPRITE_MAPPER = new SpriteMapper(CHEST_ATLAS_TEXTURE, "entity/chest");
      DECORATED_POT_SPRITE_MAPPER = new SpriteMapper(DECORATED_POT_ATLAS_TEXTURE, "entity/decorated_pot");
      BED_SPRITE_MAPPER = new SpriteMapper(BEDS_ATLAS_TEXTURE, "entity/bed");
      SHULKER_SPRITE_MAPPER = new SpriteMapper(SHULKER_BOXES_ATLAS_TEXTURE, "entity/shulker");
      SIGN_SPRITE_MAPPER = new SpriteMapper(SIGNS_ATLAS_TEXTURE, "entity/signs");
      HANGING_SIGN_SPRITE_MAPPER = new SpriteMapper(SIGNS_ATLAS_TEXTURE, "entity/signs/hanging");
      SHULKER_TEXTURE_ID = SHULKER_SPRITE_MAPPER.mapVanilla("shulker");
      COLORED_SHULKER_BOXES_TEXTURES = (List)Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getIndex)).map(TexturedRenderLayers::createShulkerBoxTextureId).collect(ImmutableList.toImmutableList());
      SIGN_TYPE_TEXTURES = (Map)WoodType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createSignTextureId));
      HANGING_SIGN_TYPE_TEXTURES = (Map)WoodType.stream().collect(Collectors.toMap(Function.identity(), TexturedRenderLayers::createHangingSignTextureId));
      BANNER_BASE = BANNER_PATTERN_SPRITE_MAPPER.mapVanilla("base");
      SHIELD_BASE = SHIELD_PATTERN_SPRITE_MAPPER.mapVanilla("base");
      BANNER_PATTERN_TEXTURES = new HashMap();
      SHIELD_PATTERN_TEXTURES = new HashMap();
      DECORATED_POT_PATTERN_TEXTURES = (Map)Registries.DECORATED_POT_PATTERN.streamEntries().collect(Collectors.toMap(RegistryEntry.Reference::registryKey, (pattern) -> {
         return DECORATED_POT_SPRITE_MAPPER.map(((DecoratedPotPattern)pattern.value()).assetId());
      }));
      DECORATED_POT_BASE = DECORATED_POT_SPRITE_MAPPER.mapVanilla("decorated_pot_base");
      DECORATED_POT_SIDE = DECORATED_POT_SPRITE_MAPPER.mapVanilla("decorated_pot_side");
      BED_TEXTURES = (SpriteIdentifier[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getIndex)).map(TexturedRenderLayers::createBedTextureId).toArray((i) -> {
         return new SpriteIdentifier[i];
      });
      TRAPPED_CHEST = CHEST_SPRITE_MAPPER.mapVanilla("trapped");
      TRAPPED_CHEST_LEFT = CHEST_SPRITE_MAPPER.mapVanilla("trapped_left");
      TRAPPED_CHEST_RIGHT = CHEST_SPRITE_MAPPER.mapVanilla("trapped_right");
      CHRISTMAS_CHEST = CHEST_SPRITE_MAPPER.mapVanilla("christmas");
      CHRISTMAS_CHEST_LEFT = CHEST_SPRITE_MAPPER.mapVanilla("christmas_left");
      CHRISTMAS_CHEST_RIGHT = CHEST_SPRITE_MAPPER.mapVanilla("christmas_right");
      CHEST = CHEST_SPRITE_MAPPER.mapVanilla("normal");
      CHEST_LEFT = CHEST_SPRITE_MAPPER.mapVanilla("normal_left");
      CHEST_RIGHT = CHEST_SPRITE_MAPPER.mapVanilla("normal_right");
      ENDER_CHEST = CHEST_SPRITE_MAPPER.mapVanilla("ender");
   }
}
