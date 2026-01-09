package net.minecraft.client.texture.atlas;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public record UnstitchAtlasSource(Identifier resource, List regions, double divisorX, double divisorY) implements AtlasSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("resource").forGetter(UnstitchAtlasSource::resource), Codecs.nonEmptyList(UnstitchAtlasSource.Region.CODEC.listOf()).fieldOf("regions").forGetter(UnstitchAtlasSource::regions), Codec.DOUBLE.optionalFieldOf("divisor_x", 1.0).forGetter(UnstitchAtlasSource::divisorX), Codec.DOUBLE.optionalFieldOf("divisor_y", 1.0).forGetter(UnstitchAtlasSource::divisorY)).apply(instance, UnstitchAtlasSource::new);
   });

   public UnstitchAtlasSource(Identifier resource, List regions, double divisorX, double divisorY) {
      this.resource = resource;
      this.regions = regions;
      this.divisorX = divisorX;
      this.divisorY = divisorY;
   }

   public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
      Identifier identifier = RESOURCE_FINDER.toResourcePath(this.resource);
      Optional optional = resourceManager.getResource(identifier);
      if (optional.isPresent()) {
         AtlasSprite atlasSprite = new AtlasSprite(identifier, (Resource)optional.get(), this.regions.size());
         Iterator var6 = this.regions.iterator();

         while(var6.hasNext()) {
            Region region = (Region)var6.next();
            regions.add(region.sprite, (AtlasSource.SpriteRegion)(new SpriteRegion(atlasSprite, region, this.divisorX, this.divisorY)));
         }
      } else {
         LOGGER.warn("Missing sprite: {}", identifier);
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Identifier resource() {
      return this.resource;
   }

   public List regions() {
      return this.regions;
   }

   public double divisorX() {
      return this.divisorX;
   }

   public double divisorY() {
      return this.divisorY;
   }

   @Environment(EnvType.CLIENT)
   public static record Region(Identifier sprite, double x, double y, double width, double height) {
      final Identifier sprite;
      final double x;
      final double y;
      final double width;
      final double height;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("sprite").forGetter(Region::sprite), Codec.DOUBLE.fieldOf("x").forGetter(Region::x), Codec.DOUBLE.fieldOf("y").forGetter(Region::y), Codec.DOUBLE.fieldOf("width").forGetter(Region::width), Codec.DOUBLE.fieldOf("height").forGetter(Region::height)).apply(instance, Region::new);
      });

      public Region(Identifier identifier, double d, double e, double f, double g) {
         this.sprite = identifier;
         this.x = d;
         this.y = e;
         this.width = f;
         this.height = g;
      }

      public Identifier sprite() {
         return this.sprite;
      }

      public double x() {
         return this.x;
      }

      public double y() {
         return this.y;
      }

      public double width() {
         return this.width;
      }

      public double height() {
         return this.height;
      }
   }

   @Environment(EnvType.CLIENT)
   static class SpriteRegion implements AtlasSource.SpriteRegion {
      private final AtlasSprite sprite;
      private final Region region;
      private final double divisorX;
      private final double divisorY;

      SpriteRegion(AtlasSprite sprite, Region region, double divisorX, double divisorY) {
         this.sprite = sprite;
         this.region = region;
         this.divisorX = divisorX;
         this.divisorY = divisorY;
      }

      public SpriteContents apply(SpriteOpener spriteOpener) {
         try {
            NativeImage nativeImage = this.sprite.read();
            double d = (double)nativeImage.getWidth() / this.divisorX;
            double e = (double)nativeImage.getHeight() / this.divisorY;
            int i = MathHelper.floor(this.region.x * d);
            int j = MathHelper.floor(this.region.y * e);
            int k = MathHelper.floor(this.region.width * d);
            int l = MathHelper.floor(this.region.height * e);
            NativeImage nativeImage2 = new NativeImage(NativeImage.Format.RGBA, k, l, false);
            nativeImage.copyRect(nativeImage2, i, j, 0, 0, k, l, false, false);
            SpriteContents var12 = new SpriteContents(this.region.sprite, new SpriteDimensions(k, l), nativeImage2, ResourceMetadata.NONE);
            return var12;
         } catch (Exception var16) {
            UnstitchAtlasSource.LOGGER.error("Failed to unstitch region {}", this.region.sprite, var16);
         } finally {
            this.sprite.close();
         }

         return MissingSprite.createSpriteContents();
      }

      public void close() {
         this.sprite.close();
      }

      // $FF: synthetic method
      public Object apply(final Object opener) {
         return this.apply((SpriteOpener)opener);
      }
   }
}
