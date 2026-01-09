package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class CrackParticle extends SpriteBillboardParticle {
   private final float sampleU;
   private final float sampleV;

   CrackParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, ItemRenderState itemRenderState) {
      this(world, x, y, z, itemRenderState);
      this.velocityX *= 0.10000000149011612;
      this.velocityY *= 0.10000000149011612;
      this.velocityZ *= 0.10000000149011612;
      this.velocityX += velocityX;
      this.velocityY += velocityY;
      this.velocityZ += velocityZ;
   }

   public ParticleTextureSheet getType() {
      return ParticleTextureSheet.TERRAIN_SHEET;
   }

   protected CrackParticle(ClientWorld world, double x, double y, double z, ItemRenderState itemRenderState) {
      super(world, x, y, z, 0.0, 0.0, 0.0);
      Sprite sprite = itemRenderState.getParticleSprite(this.random);
      if (sprite != null) {
         this.setSprite(sprite);
      } else {
         this.setSprite((Sprite)MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(MissingSprite.getMissingSpriteId()));
      }

      this.gravityStrength = 1.0F;
      this.scale /= 2.0F;
      this.sampleU = this.random.nextFloat() * 3.0F;
      this.sampleV = this.random.nextFloat() * 3.0F;
   }

   protected float getMinU() {
      return this.sprite.getFrameU((this.sampleU + 1.0F) / 4.0F);
   }

   protected float getMaxU() {
      return this.sprite.getFrameU(this.sampleU / 4.0F);
   }

   protected float getMinV() {
      return this.sprite.getFrameV(this.sampleV / 4.0F);
   }

   protected float getMaxV() {
      return this.sprite.getFrameV((this.sampleV + 1.0F) / 4.0F);
   }

   @Environment(EnvType.CLIENT)
   public static class SnowballFactory extends Factory {
      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new CrackParticle(clientWorld, d, e, f, this.getItemRenderState(new ItemStack(Items.SNOWBALL), clientWorld));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class CobwebFactory extends Factory {
      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new CrackParticle(clientWorld, d, e, f, this.getItemRenderState(new ItemStack(Items.COBWEB), clientWorld));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class SlimeballFactory extends Factory {
      public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new CrackParticle(clientWorld, d, e, f, this.getItemRenderState(new ItemStack(Items.SLIME_BALL), clientWorld));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((SimpleParticleType)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }

   @Environment(EnvType.CLIENT)
   public static class ItemFactory extends Factory {
      public Particle createParticle(ItemStackParticleEffect itemStackParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         return new CrackParticle(clientWorld, d, e, f, g, h, i, this.getItemRenderState(itemStackParticleEffect.getItemStack(), clientWorld));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleEffect particleEffect, final ClientWorld clientWorld, final double d, final double e, final double f, final double g, final double h, final double i) {
         return this.createParticle((ItemStackParticleEffect)particleEffect, clientWorld, d, e, f, g, h, i);
      }
   }

   @Environment(EnvType.CLIENT)
   public abstract static class Factory implements ParticleFactory {
      private final ItemRenderState itemRenderState = new ItemRenderState();

      protected ItemRenderState getItemRenderState(ItemStack stack, ClientWorld world) {
         MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(this.itemRenderState, stack, ItemDisplayContext.GROUND, world, (LivingEntity)null, 0);
         return this.itemRenderState;
      }
   }
}
