package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public abstract class RenderLayer extends RenderPhase {
   private static final int field_32777 = 1048576;
   public static final int SOLID_BUFFER_SIZE = 4194304;
   public static final int CUTOUT_BUFFER_SIZE = 786432;
   public static final int DEFAULT_BUFFER_SIZE = 1536;
   private static final RenderLayer SOLID;
   private static final RenderLayer CUTOUT_MIPPED;
   private static final RenderLayer CUTOUT;
   private static final RenderLayer TRANSLUCENT_MOVING_BLOCK;
   private static final Function ARMOR_CUTOUT_NO_CULL;
   private static final Function ARMOR_TRANSLUCENT;
   private static final Function ENTITY_SOLID;
   private static final Function ENTITY_SOLID_Z_OFFSET_FORWARD;
   private static final Function ENTITY_CUTOUT;
   private static final BiFunction ENTITY_CUTOUT_NO_CULL;
   private static final BiFunction ENTITY_CUTOUT_NO_CULL_Z_OFFSET;
   private static final Function ITEM_ENTITY_TRANSLUCENT_CULL;
   private static final BiFunction ENTITY_TRANSLUCENT;
   private static final BiFunction ENTITY_TRANSLUCENT_EMISSIVE;
   private static final Function ENTITY_SMOOTH_CUTOUT;
   private static final BiFunction BEACON_BEAM;
   private static final Function ENTITY_DECAL;
   private static final Function ENTITY_NO_OUTLINE;
   private static final Function ENTITY_SHADOW;
   private static final Function ENTITY_ALPHA;
   private static final Function EYES;
   private static final RenderLayer LEASH;
   private static final RenderLayer WATER_MASK;
   private static final RenderLayer ARMOR_ENTITY_GLINT;
   private static final RenderLayer GLINT_TRANSLUCENT;
   private static final RenderLayer GLINT;
   private static final RenderLayer ENTITY_GLINT;
   private static final Function CRUMBLING;
   private static final Function TEXT;
   private static final RenderLayer TEXT_BACKGROUND;
   private static final Function TEXT_INTENSITY;
   private static final Function TEXT_POLYGON_OFFSET;
   private static final Function TEXT_INTENSITY_POLYGON_OFFSET;
   private static final Function TEXT_SEE_THROUGH;
   private static final RenderLayer TEXT_BACKGROUND_SEE_THROUGH;
   private static final Function TEXT_INTENSITY_SEE_THROUGH;
   private static final RenderLayer LIGHTNING;
   private static final RenderLayer DRAGON_RAYS;
   private static final RenderLayer DRAGON_RAYS_DEPTH;
   private static final RenderLayer TRIPWIRE;
   private static final RenderLayer END_PORTAL;
   private static final RenderLayer END_GATEWAY;
   public static final MultiPhase LINES;
   public static final MultiPhase SECONDARY_BLOCK_OUTLINE;
   public static final MultiPhase LINE_STRIP;
   private static final Function DEBUG_LINE_STRIP;
   private static final MultiPhase DEBUG_FILLED_BOX;
   private static final MultiPhase DEBUG_QUADS;
   private static final MultiPhase DEBUG_TRIANGLE_FAN;
   private static final MultiPhase DEBUG_STRUCTURE_QUADS;
   private static final MultiPhase DEBUG_SECTION_QUADS;
   private static final Function OPAQUE_PARTICLE;
   private static final Function TRANSLUCENT_PARTICLE;
   private static final Function WEATHER_ALL_MASK;
   private static final Function WEATHER_COLOR_MASK;
   private static final RenderLayer SUNRISE_SUNSET;
   private static final Function CELESTIAL;
   private static final Function BLOCK_SCREEN_EFFECT;
   private static final Function FIRE_SCREEN_EFFECT;
   private final int expectedBufferSize;
   private final boolean hasCrumbling;
   private final boolean translucent;

   public static RenderLayer getSolid() {
      return SOLID;
   }

   public static RenderLayer getCutoutMipped() {
      return CUTOUT_MIPPED;
   }

   public static RenderLayer getCutout() {
      return CUTOUT;
   }

   public static RenderLayer getTranslucentMovingBlock() {
      return TRANSLUCENT_MOVING_BLOCK;
   }

   public static RenderLayer getArmorCutoutNoCull(Identifier texture) {
      return (RenderLayer)ARMOR_CUTOUT_NO_CULL.apply(texture);
   }

   public static RenderLayer createArmorDecalCutoutNoCull(Identifier texture) {
      MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(true);
      return of("armor_decal_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL, multiPhaseParameters);
   }

   public static RenderLayer createArmorTranslucent(Identifier texture) {
      return (RenderLayer)ARMOR_TRANSLUCENT.apply(texture);
   }

   public static RenderLayer getEntitySolid(Identifier texture) {
      return (RenderLayer)ENTITY_SOLID.apply(texture);
   }

   public static RenderLayer getEntitySolidZOffsetForward(Identifier texture) {
      return (RenderLayer)ENTITY_SOLID_Z_OFFSET_FORWARD.apply(texture);
   }

   public static RenderLayer getEntityCutout(Identifier texture) {
      return (RenderLayer)ENTITY_CUTOUT.apply(texture);
   }

   public static RenderLayer getEntityCutoutNoCull(Identifier texture, boolean affectsOutline) {
      return (RenderLayer)ENTITY_CUTOUT_NO_CULL.apply(texture, affectsOutline);
   }

   public static RenderLayer getEntityCutoutNoCull(Identifier texture) {
      return getEntityCutoutNoCull(texture, true);
   }

   public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture, boolean affectsOutline) {
      return (RenderLayer)ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(texture, affectsOutline);
   }

   public static RenderLayer getEntityCutoutNoCullZOffset(Identifier texture) {
      return getEntityCutoutNoCullZOffset(texture, true);
   }

   public static RenderLayer getItemEntityTranslucentCull(Identifier texture) {
      return (RenderLayer)ITEM_ENTITY_TRANSLUCENT_CULL.apply(texture);
   }

   public static RenderLayer getEntityTranslucent(Identifier texture, boolean affectsOutline) {
      return (RenderLayer)ENTITY_TRANSLUCENT.apply(texture, affectsOutline);
   }

   public static RenderLayer getEntityTranslucent(Identifier texture) {
      return getEntityTranslucent(texture, true);
   }

   public static RenderLayer getEntityTranslucentEmissive(Identifier texture, boolean affectsOutline) {
      return (RenderLayer)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
   }

   public static RenderLayer getEntityTranslucentEmissive(Identifier texture) {
      return getEntityTranslucentEmissive(texture, true);
   }

   public static RenderLayer getEntitySmoothCutout(Identifier texture) {
      return (RenderLayer)ENTITY_SMOOTH_CUTOUT.apply(texture);
   }

   public static RenderLayer getBeaconBeam(Identifier texture, boolean translucent) {
      return (RenderLayer)BEACON_BEAM.apply(texture, translucent);
   }

   public static RenderLayer getEntityDecal(Identifier texture) {
      return (RenderLayer)ENTITY_DECAL.apply(texture);
   }

   public static RenderLayer getEntityNoOutline(Identifier texture) {
      return (RenderLayer)ENTITY_NO_OUTLINE.apply(texture);
   }

   public static RenderLayer getEntityShadow(Identifier texture) {
      return (RenderLayer)ENTITY_SHADOW.apply(texture);
   }

   public static RenderLayer getEntityAlpha(Identifier texture) {
      return (RenderLayer)ENTITY_ALPHA.apply(texture);
   }

   public static RenderLayer getEyes(Identifier texture) {
      return (RenderLayer)EYES.apply(texture);
   }

   public static RenderLayer getEntityTranslucentEmissiveNoOutline(Identifier texture) {
      return (RenderLayer)ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, false);
   }

   public static RenderLayer getBreezeWind(Identifier texture, float x, float y) {
      return of("breeze_wind", 1536, false, true, RenderPipelines.BREEZE_WIND, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).texturing(new RenderPhase.OffsetTexturing(x, y)).lightmap(ENABLE_LIGHTMAP).overlay(DISABLE_OVERLAY_COLOR).build(false));
   }

   public static RenderLayer getEnergySwirl(Identifier texture, float x, float y) {
      return of("energy_swirl", 1536, false, true, RenderPipelines.ENTITY_ENERGY_SWIRL, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).texturing(new RenderPhase.OffsetTexturing(x, y)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
   }

   public static RenderLayer getLeash() {
      return LEASH;
   }

   public static RenderLayer getWaterMask() {
      return WATER_MASK;
   }

   public static RenderLayer getOutline(Identifier texture) {
      return (RenderLayer)RenderLayer.MultiPhase.CULLING_LAYERS.apply(texture, false);
   }

   public static RenderLayer getArmorEntityGlint() {
      return ARMOR_ENTITY_GLINT;
   }

   public static RenderLayer getGlintTranslucent() {
      return GLINT_TRANSLUCENT;
   }

   public static RenderLayer getGlint() {
      return GLINT;
   }

   public static RenderLayer getEntityGlint() {
      return ENTITY_GLINT;
   }

   public static RenderLayer getBlockBreaking(Identifier texture) {
      return (RenderLayer)CRUMBLING.apply(texture);
   }

   public static RenderLayer getText(Identifier texture) {
      return (RenderLayer)TEXT.apply(texture);
   }

   public static RenderLayer getTextBackground() {
      return TEXT_BACKGROUND;
   }

   public static RenderLayer getTextIntensity(Identifier texture) {
      return (RenderLayer)TEXT_INTENSITY.apply(texture);
   }

   public static RenderLayer getTextPolygonOffset(Identifier texture) {
      return (RenderLayer)TEXT_POLYGON_OFFSET.apply(texture);
   }

   public static RenderLayer getTextIntensityPolygonOffset(Identifier texture) {
      return (RenderLayer)TEXT_INTENSITY_POLYGON_OFFSET.apply(texture);
   }

   public static RenderLayer getTextSeeThrough(Identifier texture) {
      return (RenderLayer)TEXT_SEE_THROUGH.apply(texture);
   }

   public static RenderLayer getTextBackgroundSeeThrough() {
      return TEXT_BACKGROUND_SEE_THROUGH;
   }

   public static RenderLayer getTextIntensitySeeThrough(Identifier texture) {
      return (RenderLayer)TEXT_INTENSITY_SEE_THROUGH.apply(texture);
   }

   public static RenderLayer getLightning() {
      return LIGHTNING;
   }

   public static RenderLayer getDragonRays() {
      return DRAGON_RAYS;
   }

   public static RenderLayer getDragonRaysDepth() {
      return DRAGON_RAYS_DEPTH;
   }

   public static RenderLayer getTripwire() {
      return TRIPWIRE;
   }

   public static RenderLayer getEndPortal() {
      return END_PORTAL;
   }

   public static RenderLayer getEndGateway() {
      return END_GATEWAY;
   }

   public static RenderLayer getLines() {
      return LINES;
   }

   public static RenderLayer getSecondaryBlockOutline() {
      return SECONDARY_BLOCK_OUTLINE;
   }

   public static RenderLayer getLineStrip() {
      return LINE_STRIP;
   }

   public static RenderLayer getDebugLineStrip(double lineWidth) {
      return (RenderLayer)DEBUG_LINE_STRIP.apply(lineWidth);
   }

   public static RenderLayer getDebugFilledBox() {
      return DEBUG_FILLED_BOX;
   }

   public static RenderLayer getDebugQuads() {
      return DEBUG_QUADS;
   }

   public static RenderLayer getDebugTriangleFan() {
      return DEBUG_TRIANGLE_FAN;
   }

   public static RenderLayer getDebugStructureQuads() {
      return DEBUG_STRUCTURE_QUADS;
   }

   public static RenderLayer getDebugSectionQuads() {
      return DEBUG_SECTION_QUADS;
   }

   public static RenderLayer getOpaqueParticle(Identifier texture) {
      return (RenderLayer)OPAQUE_PARTICLE.apply(texture);
   }

   public static RenderLayer getTranslucentParticle(Identifier texture) {
      return (RenderLayer)TRANSLUCENT_PARTICLE.apply(texture);
   }

   private static Function createWeather(RenderPipeline pipeline) {
      return Util.memoize((texture) -> {
         return of("weather", 1536, false, false, pipeline, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).target(WEATHER_TARGET).lightmap(ENABLE_LIGHTMAP).build(false));
      });
   }

   public static RenderLayer getWeather(Identifier texture, boolean allMask) {
      return (RenderLayer)(allMask ? WEATHER_ALL_MASK : WEATHER_COLOR_MASK).apply(texture);
   }

   public static RenderLayer getSunriseSunset() {
      return SUNRISE_SUNSET;
   }

   public static RenderLayer getCelestial(Identifier texture) {
      return (RenderLayer)CELESTIAL.apply(texture);
   }

   public static RenderLayer getBlockScreenEffect(Identifier texture) {
      return (RenderLayer)BLOCK_SCREEN_EFFECT.apply(texture);
   }

   public static RenderLayer getFireScreenEffect(Identifier texture) {
      return (RenderLayer)FIRE_SCREEN_EFFECT.apply(texture);
   }

   public RenderLayer(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
      super(name, begin, end);
      this.expectedBufferSize = size;
      this.hasCrumbling = hasCrumbling;
      this.translucent = translucent;
   }

   public static MultiPhase of(String name, int size, RenderPipeline pipeline, MultiPhaseParameters params) {
      return of(name, size, false, false, pipeline, params);
   }

   public static MultiPhase of(String name, int size, boolean hasCrumbling, boolean translucent, RenderPipeline pipeline, MultiPhaseParameters params) {
      return new MultiPhase(name, size, hasCrumbling, translucent, pipeline, params);
   }

   public abstract void draw(BuiltBuffer buffer);

   public int getExpectedBufferSize() {
      return this.expectedBufferSize;
   }

   public abstract VertexFormat getVertexFormat();

   public abstract VertexFormat.DrawMode getDrawMode();

   public Optional getAffectedOutline() {
      return Optional.empty();
   }

   public boolean isOutline() {
      return false;
   }

   public boolean hasCrumbling() {
      return this.hasCrumbling;
   }

   public boolean areVerticesNotShared() {
      return !this.getDrawMode().shareVertices;
   }

   public boolean isTranslucent() {
      return this.translucent;
   }

   static {
      SOLID = of("solid", 1536, true, false, RenderPipelines.SOLID, RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true));
      CUTOUT_MIPPED = of("cutout_mipped", 1536, true, false, RenderPipelines.CUTOUT_MIPPED, RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).build(true));
      CUTOUT = of("cutout", 1536, true, false, RenderPipelines.CUTOUT, RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).texture(BLOCK_ATLAS_TEXTURE).build(true));
      TRANSLUCENT_MOVING_BLOCK = of("translucent_moving_block", 786432, false, true, RenderPipelines.RENDERTYPE_TRANSLUCENT_MOVING_BLOCK, RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).target(ITEM_ENTITY_TARGET).build(true));
      ARMOR_CUTOUT_NO_CULL = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(true);
         return of("armor_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_CUTOUT_NO_CULL, multiPhaseParameters);
      });
      ARMOR_TRANSLUCENT = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(true);
         return of("armor_translucent", 1536, true, true, RenderPipelines.ARMOR_TRANSLUCENT, multiPhaseParameters);
      });
      ENTITY_SOLID = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
         return of("entity_solid", 1536, true, false, RenderPipelines.ENTITY_SOLID, multiPhaseParameters);
      });
      ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING_FORWARD).build(true);
         return of("entity_solid_z_offset_forward", 1536, true, false, RenderPipelines.ENTITY_SOLID_OFFSET_FORWARD, multiPhaseParameters);
      });
      ENTITY_CUTOUT = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
         return of("entity_cutout", 1536, true, false, RenderPipelines.ENTITY_CUTOUT, multiPhaseParameters);
      });
      ENTITY_CUTOUT_NO_CULL = Util.memoize((texture, affectsOutline) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(affectsOutline);
         return of("entity_cutout_no_cull", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL, multiPhaseParameters);
      });
      ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize((texture, affectsOutline) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(affectsOutline);
         return of("entity_cutout_no_cull_z_offset", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET, multiPhaseParameters);
      });
      ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).target(ITEM_ENTITY_TARGET).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
         return of("item_entity_translucent_cull", 1536, true, true, RenderPipelines.RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL, multiPhaseParameters);
      });
      ENTITY_TRANSLUCENT = Util.memoize((texture, affectsOutline) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(affectsOutline);
         return of("entity_translucent", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT, multiPhaseParameters);
      });
      ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((texture, affectsOutline) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).overlay(ENABLE_OVERLAY_COLOR).build(affectsOutline);
         return of("entity_translucent_emissive", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE, multiPhaseParameters);
      });
      ENTITY_SMOOTH_CUTOUT = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
         return of("entity_smooth_cutout", 1536, RenderPipelines.ENTITY_SMOOTH_CUTOUT, multiPhaseParameters);
      });
      BEACON_BEAM = Util.memoize((texture, affectsOutline) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).build(false);
         return of("beacon_beam", 1536, false, true, affectsOutline ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE, multiPhaseParameters);
      });
      ENTITY_DECAL = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false);
         return of("entity_decal", 1536, RenderPipelines.RENDERTYPE_ENTITY_DECAL, multiPhaseParameters);
      });
      ENTITY_NO_OUTLINE = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false);
         return of("entity_no_outline", 1536, false, true, RenderPipelines.ENTITY_NO_OUTLINE, multiPhaseParameters);
      });
      ENTITY_SHADOW = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).layering(VIEW_OFFSET_Z_LAYERING).build(false);
         return of("entity_shadow", 1536, false, false, RenderPipelines.RENDERTYPE_ENTITY_SHADOW, multiPhaseParameters);
      });
      ENTITY_ALPHA = Util.memoize((texture) -> {
         MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).build(true);
         return of("entity_alpha", 1536, RenderPipelines.RENDERTYPE_ENTITY_ALPHA, multiPhaseParameters);
      });
      EYES = Util.memoize((texture) -> {
         RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false);
         return of("eyes", 1536, false, true, RenderPipelines.ENTITY_EYES, RenderLayer.MultiPhaseParameters.builder().texture(texture2).build(false));
      });
      LEASH = of("leash", 1536, RenderPipelines.RENDERTYPE_LEASH, RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).lightmap(ENABLE_LIGHTMAP).build(false));
      WATER_MASK = of("water_mask", 1536, RenderPipelines.RENDERTYPE_WATER_MASK, RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).build(false));
      ARMOR_ENTITY_GLINT = of("armor_entity_glint", 1536, RenderPipelines.GLINT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ENTITY_ENCHANTMENT_GLINT, false)).texturing(ARMOR_ENTITY_GLINT_TEXTURING).layering(VIEW_OFFSET_Z_LAYERING).build(false));
      GLINT_TRANSLUCENT = of("glint_translucent", 1536, RenderPipelines.GLINT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ITEM_ENCHANTMENT_GLINT, false)).texturing(GLINT_TEXTURING).target(ITEM_ENTITY_TARGET).build(false));
      GLINT = of("glint", 1536, RenderPipelines.GLINT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ITEM_ENCHANTMENT_GLINT, false)).texturing(GLINT_TEXTURING).build(false));
      ENTITY_GLINT = of("entity_glint", 1536, RenderPipelines.GLINT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(ItemRenderer.ITEM_ENCHANTMENT_GLINT, false)).texturing(ENTITY_GLINT_TEXTURING).build(false));
      CRUMBLING = Util.memoize((texture) -> {
         RenderPhase.Texture texture2 = new RenderPhase.Texture(texture, false);
         return of("crumbling", 1536, false, true, RenderPipelines.RENDERTYPE_CRUMBLING, RenderLayer.MultiPhaseParameters.builder().texture(texture2).build(false));
      });
      TEXT = Util.memoize((texture) -> {
         return of("text", 786432, false, false, RenderPipelines.RENDERTYPE_TEXT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      TEXT_BACKGROUND = of("text_background", 1536, false, true, RenderPipelines.RENDERTYPE_TEXT_BG, RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).lightmap(ENABLE_LIGHTMAP).build(false));
      TEXT_INTENSITY = Util.memoize((texture) -> {
         return of("text_intensity", 786432, false, false, RenderPipelines.RENDERTYPE_TEXT_INTENSITY, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      TEXT_POLYGON_OFFSET = Util.memoize((texture) -> {
         return of("text_polygon_offset", 1536, false, true, RenderPipelines.RENDERTYPE_TEXT_POLYGON_OFFSET, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize((texture) -> {
         return of("text_intensity_polygon_offset", 1536, false, true, RenderPipelines.RENDERTYPE_TEXT_INTENSITY, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      TEXT_SEE_THROUGH = Util.memoize((texture) -> {
         return of("text_see_through", 1536, false, false, RenderPipelines.RENDERTYPE_TEXT_SEETHROUGH, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      TEXT_BACKGROUND_SEE_THROUGH = of("text_background_see_through", 1536, false, true, RenderPipelines.RENDERTYPE_TEXT_BG_SEETHROUGH, RenderLayer.MultiPhaseParameters.builder().texture(NO_TEXTURE).lightmap(ENABLE_LIGHTMAP).build(false));
      TEXT_INTENSITY_SEE_THROUGH = Util.memoize((texture) -> {
         return of("text_intensity_see_through", 1536, false, true, RenderPipelines.RENDERTYPE_TEXT_INTENSITY_SEETHROUGH, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      LIGHTNING = of("lightning", 1536, false, true, RenderPipelines.RENDERTYPE_LIGHTNING, RenderLayer.MultiPhaseParameters.builder().target(WEATHER_TARGET).build(false));
      DRAGON_RAYS = of("dragon_rays", 1536, false, false, RenderPipelines.RENDERTYPE_LIGHTNING_DRAGON_RAYS, RenderLayer.MultiPhaseParameters.builder().build(false));
      DRAGON_RAYS_DEPTH = of("dragon_rays_depth", 1536, false, false, RenderPipelines.POSITION_DRAGON_RAYS_DEPTH, RenderLayer.MultiPhaseParameters.builder().build(false));
      TRIPWIRE = of("tripwire", 1536, true, true, RenderPipelines.TRIPWIRE, RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).texture(MIPMAP_BLOCK_ATLAS_TEXTURE).target(WEATHER_TARGET).build(true));
      END_PORTAL = of("end_portal", 1536, false, false, RenderPipelines.END_PORTAL, RenderLayer.MultiPhaseParameters.builder().texture(RenderPhase.Textures.create().add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false).add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false).build()).build(false));
      END_GATEWAY = of("end_gateway", 1536, false, false, RenderPipelines.END_GATEWAY, RenderLayer.MultiPhaseParameters.builder().texture(RenderPhase.Textures.create().add(EndPortalBlockEntityRenderer.SKY_TEXTURE, false).add(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false).build()).build(false));
      LINES = of("lines", 1536, RenderPipelines.LINES, RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty())).layering(VIEW_OFFSET_Z_LAYERING).target(ITEM_ENTITY_TARGET).build(false));
      SECONDARY_BLOCK_OUTLINE = of("secondary_block_outline", 1536, RenderPipelines.SECOND_BLOCK_OUTLINE, RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(7.0))).layering(VIEW_OFFSET_Z_LAYERING).target(ITEM_ENTITY_TARGET).build(false));
      LINE_STRIP = of("line_strip", 1536, RenderPipelines.LINE_STRIP, RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.empty())).layering(VIEW_OFFSET_Z_LAYERING).target(ITEM_ENTITY_TARGET).build(false));
      DEBUG_LINE_STRIP = Util.memoize((lineWidth) -> {
         return of("debug_line_strip", 1536, RenderPipelines.DEBUG_LINE_STRIP, RenderLayer.MultiPhaseParameters.builder().lineWidth(new RenderPhase.LineWidth(OptionalDouble.of(lineWidth))).build(false));
      });
      DEBUG_FILLED_BOX = of("debug_filled_box", 1536, false, true, RenderPipelines.DEBUG_FILLED_BOX, RenderLayer.MultiPhaseParameters.builder().layering(VIEW_OFFSET_Z_LAYERING).build(false));
      DEBUG_QUADS = of("debug_quads", 1536, false, true, RenderPipelines.DEBUG_QUADS, RenderLayer.MultiPhaseParameters.builder().build(false));
      DEBUG_TRIANGLE_FAN = of("debug_triangle_fan", 1536, false, true, RenderPipelines.DEBUG_TRIANGLE_FAN, RenderLayer.MultiPhaseParameters.builder().build(false));
      DEBUG_STRUCTURE_QUADS = of("debug_structure_quads", 1536, false, true, RenderPipelines.DEBUG_STRUCTURE_QUADS, RenderLayer.MultiPhaseParameters.builder().build(false));
      DEBUG_SECTION_QUADS = of("debug_section_quads", 1536, false, true, RenderPipelines.DEBUG_SECTION_QUADS, RenderLayer.MultiPhaseParameters.builder().layering(VIEW_OFFSET_Z_LAYERING).build(false));
      OPAQUE_PARTICLE = Util.memoize((texture) -> {
         return of("opaque_particle", 1536, false, false, RenderPipelines.OPAQUE_PARTICLE, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      TRANSLUCENT_PARTICLE = Util.memoize((texture) -> {
         return of("translucent_particle", 1536, false, false, RenderPipelines.TRANSLUCENT_PARTICLE, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).target(PARTICLES_TARGET).lightmap(ENABLE_LIGHTMAP).build(false));
      });
      WEATHER_ALL_MASK = createWeather(RenderPipelines.WEATHER_DEPTH);
      WEATHER_COLOR_MASK = createWeather(RenderPipelines.WEATHER_NO_DEPTH);
      SUNRISE_SUNSET = of("sunrise_sunset", 1536, false, false, RenderPipelines.POSITION_COLOR_SUNRISE_SUNSET, RenderLayer.MultiPhaseParameters.builder().build(false));
      CELESTIAL = Util.memoize((texture) -> {
         return of("celestial", 1536, false, false, RenderPipelines.POSITION_TEX_COLOR_CELESTIAL, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).build(false));
      });
      BLOCK_SCREEN_EFFECT = Util.memoize((texture) -> {
         return of("block_screen_effect", 1536, false, false, RenderPipelines.BLOCK_SCREEN_EFFECT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).build(false));
      });
      FIRE_SCREEN_EFFECT = Util.memoize((texture) -> {
         return of("fire_screen_effect", 1536, false, false, RenderPipelines.FIRE_SCREEN_EFFECT, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).build(false));
      });
   }

   @Environment(EnvType.CLIENT)
   public static final class MultiPhaseParameters {
      final RenderPhase.TextureBase texture;
      final RenderPhase.Target target;
      final OutlineMode outlineMode;
      final ImmutableList phases;

      MultiPhaseParameters(RenderPhase.TextureBase texture, RenderPhase.Lightmap lightMap, RenderPhase.Overlay overlay, RenderPhase.Layering layering, RenderPhase.Target target, RenderPhase.Texturing texturing, RenderPhase.LineWidth lineWidth, OutlineMode outlineMode) {
         this.texture = texture;
         this.target = target;
         this.outlineMode = outlineMode;
         this.phases = ImmutableList.of(texture, lightMap, overlay, layering, target, texturing, lineWidth);
      }

      public String toString() {
         String var10000 = String.valueOf(this.phases);
         return "CompositeState[" + var10000 + ", outlineProperty=" + String.valueOf(this.outlineMode) + "]";
      }

      public static Builder builder() {
         return new Builder();
      }

      @Environment(EnvType.CLIENT)
      public static class Builder {
         private RenderPhase.TextureBase texture;
         private RenderPhase.Lightmap lightmap;
         private RenderPhase.Overlay overlay;
         private RenderPhase.Layering layering;
         private RenderPhase.Target target;
         private RenderPhase.Texturing texturing;
         private RenderPhase.LineWidth lineWidth;

         Builder() {
            this.texture = RenderPhase.NO_TEXTURE;
            this.lightmap = RenderPhase.DISABLE_LIGHTMAP;
            this.overlay = RenderPhase.DISABLE_OVERLAY_COLOR;
            this.layering = RenderPhase.NO_LAYERING;
            this.target = RenderPhase.MAIN_TARGET;
            this.texturing = RenderPhase.DEFAULT_TEXTURING;
            this.lineWidth = RenderPhase.FULL_LINE_WIDTH;
         }

         public Builder texture(RenderPhase.TextureBase texture) {
            this.texture = texture;
            return this;
         }

         public Builder lightmap(RenderPhase.Lightmap lightmap) {
            this.lightmap = lightmap;
            return this;
         }

         public Builder overlay(RenderPhase.Overlay overlay) {
            this.overlay = overlay;
            return this;
         }

         public Builder layering(RenderPhase.Layering layering) {
            this.layering = layering;
            return this;
         }

         public Builder target(RenderPhase.Target target) {
            this.target = target;
            return this;
         }

         public Builder texturing(RenderPhase.Texturing texturing) {
            this.texturing = texturing;
            return this;
         }

         public Builder lineWidth(RenderPhase.LineWidth lineWidth) {
            this.lineWidth = lineWidth;
            return this;
         }

         public MultiPhaseParameters build(boolean affectsOutline) {
            return this.build(affectsOutline ? RenderLayer.OutlineMode.AFFECTS_OUTLINE : RenderLayer.OutlineMode.NONE);
         }

         public MultiPhaseParameters build(OutlineMode outlineMode) {
            return new MultiPhaseParameters(this.texture, this.lightmap, this.overlay, this.layering, this.target, this.texturing, this.lineWidth, outlineMode);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public static final class MultiPhase extends RenderLayer {
      static final BiFunction CULLING_LAYERS = Util.memoize((texture, hasCulling) -> {
         return RenderLayer.of("outline", 1536, hasCulling ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false)).target(OUTLINE_TARGET).build(RenderLayer.OutlineMode.IS_OUTLINE));
      });
      private final MultiPhaseParameters phases;
      private final RenderPipeline pipeline;
      private final Optional affectedOutline;
      private final boolean outline;

      MultiPhase(String name, int size, boolean hasCrumbling, boolean translucent, RenderPipeline pipeline, MultiPhaseParameters phases) {
         super(name, size, hasCrumbling, translucent, () -> {
            phases.phases.forEach(RenderPhase::startDrawing);
         }, () -> {
            phases.phases.forEach(RenderPhase::endDrawing);
         });
         this.phases = phases;
         this.pipeline = pipeline;
         this.affectedOutline = phases.outlineMode == RenderLayer.OutlineMode.AFFECTS_OUTLINE ? phases.texture.getId().map((id) -> {
            return (RenderLayer)CULLING_LAYERS.apply(id, pipeline.isCull());
         }) : Optional.empty();
         this.outline = phases.outlineMode == RenderLayer.OutlineMode.IS_OUTLINE;
      }

      public Optional getAffectedOutline() {
         return this.affectedOutline;
      }

      public boolean isOutline() {
         return this.outline;
      }

      public VertexFormat getVertexFormat() {
         return this.pipeline.getVertexFormat();
      }

      public VertexFormat.DrawMode getDrawMode() {
         return this.pipeline.getVertexFormatMode();
      }

      public void draw(BuiltBuffer buffer) {
         this.startDrawing();
         GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), RenderSystem.getModelOffset(), RenderSystem.getTextureMatrix(), RenderSystem.getShaderLineWidth());
         BuiltBuffer var3 = buffer;

         try {
            GpuBuffer gpuBuffer = this.pipeline.getVertexFormat().uploadImmediateVertexBuffer(buffer.getBuffer());
            GpuBuffer gpuBuffer2;
            VertexFormat.IndexType indexType;
            if (buffer.getSortedBuffer() == null) {
               RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(buffer.getDrawParameters().mode());
               gpuBuffer2 = shapeIndexBuffer.getIndexBuffer(buffer.getDrawParameters().indexCount());
               indexType = shapeIndexBuffer.getIndexType();
            } else {
               gpuBuffer2 = this.pipeline.getVertexFormat().uploadImmediateIndexBuffer(buffer.getSortedBuffer());
               indexType = buffer.getDrawParameters().indexType();
            }

            Framebuffer framebuffer = this.phases.target.get();
            GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride != null ? RenderSystem.outputColorTextureOverride : framebuffer.getColorAttachmentView();
            GpuTextureView gpuTextureView2 = framebuffer.useDepthAttachment ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : framebuffer.getDepthAttachmentView()) : null;
            RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
               return "Immediate draw for " + this.getName();
            }, gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty());

            try {
               renderPass.setPipeline(this.pipeline);
               ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
               if (scissorState.method_72091()) {
                  renderPass.enableScissor(scissorState.method_72092(), scissorState.method_72093(), scissorState.method_72094(), scissorState.method_72095());
               }

               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
               renderPass.setVertexBuffer(0, gpuBuffer);

               for(int i = 0; i < 12; ++i) {
                  GpuTextureView gpuTextureView3 = RenderSystem.getShaderTexture(i);
                  if (gpuTextureView3 != null) {
                     renderPass.bindSampler("Sampler" + i, gpuTextureView3);
                  }
               }

               renderPass.setIndexBuffer(gpuBuffer2, indexType);
               renderPass.drawIndexed(0, 0, buffer.getDrawParameters().indexCount(), 1);
            } catch (Throwable var16) {
               if (renderPass != null) {
                  try {
                     renderPass.close();
                  } catch (Throwable var15) {
                     var16.addSuppressed(var15);
                  }
               }

               throw var16;
            }

            if (renderPass != null) {
               renderPass.close();
            }
         } catch (Throwable var17) {
            if (buffer != null) {
               try {
                  var3.close();
               } catch (Throwable var14) {
                  var17.addSuppressed(var14);
               }
            }

            throw var17;
         }

         if (buffer != null) {
            buffer.close();
         }

         this.endDrawing();
      }

      public String toString() {
         String var10000 = this.name;
         return "RenderType[" + var10000 + ":" + String.valueOf(this.phases) + "]";
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum OutlineMode {
      NONE("none"),
      IS_OUTLINE("is_outline"),
      AFFECTS_OUTLINE("affects_outline");

      private final String name;

      private OutlineMode(final String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static OutlineMode[] method_36916() {
         return new OutlineMode[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
      }
   }
}
