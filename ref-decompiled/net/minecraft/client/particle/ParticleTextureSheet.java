package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ParticleTextureSheet(String name, @Nullable RenderLayer renderType) {
   public static final ParticleTextureSheet TERRAIN_SHEET;
   public static final ParticleTextureSheet PARTICLE_SHEET_OPAQUE;
   public static final ParticleTextureSheet PARTICLE_SHEET_TRANSLUCENT;
   public static final ParticleTextureSheet CUSTOM;
   public static final ParticleTextureSheet NO_RENDER;

   public ParticleTextureSheet(String string, @Nullable RenderLayer renderLayer) {
      this.name = string;
      this.renderType = renderLayer;
   }

   public String name() {
      return this.name;
   }

   @Nullable
   public RenderLayer renderType() {
      return this.renderType;
   }

   static {
      TERRAIN_SHEET = new ParticleTextureSheet("TERRAIN_SHEET", RenderLayer.getTranslucentParticle(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE));
      PARTICLE_SHEET_OPAQUE = new ParticleTextureSheet("PARTICLE_SHEET_OPAQUE", RenderLayer.getOpaqueParticle(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE));
      PARTICLE_SHEET_TRANSLUCENT = new ParticleTextureSheet("PARTICLE_SHEET_TRANSLUCENT", RenderLayer.getTranslucentParticle(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE));
      CUSTOM = new ParticleTextureSheet("CUSTOM", (RenderLayer)null);
      NO_RENDER = new ParticleTextureSheet("NO_RENDER", (RenderLayer)null);
   }
}
