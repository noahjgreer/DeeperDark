package net.minecraft.client.texture.atlas;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.SpriteOpener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public record PalettedPermutationsAtlasSource(List textures, Identifier paletteKey, Map permutations, String separator) implements AtlasSource {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final String DEFAULT_SEPARATOR = "_";
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(PalettedPermutationsAtlasSource::textures), Identifier.CODEC.fieldOf("palette_key").forGetter(PalettedPermutationsAtlasSource::paletteKey), Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(PalettedPermutationsAtlasSource::permutations), Codec.STRING.optionalFieldOf("separator", "_").forGetter(PalettedPermutationsAtlasSource::separator)).apply(instance, PalettedPermutationsAtlasSource::new);
   });

   public PalettedPermutationsAtlasSource(List textures, Identifier paletteKey, Map permutations) {
      this(textures, paletteKey, permutations, "_");
   }

   public PalettedPermutationsAtlasSource(List textures, Identifier identifier, Map permutations, String string) {
      this.textures = textures;
      this.paletteKey = identifier;
      this.permutations = permutations;
      this.separator = string;
   }

   public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
      Supplier supplier = Suppliers.memoize(() -> {
         return open(resourceManager, this.paletteKey);
      });
      Map map = new HashMap();
      this.permutations.forEach((key, texture) -> {
         map.put(key, Suppliers.memoize(() -> {
            return toMapper((int[])supplier.get(), open(resourceManager, texture));
         }));
      });
      Iterator var5 = this.textures.iterator();

      while(true) {
         while(var5.hasNext()) {
            Identifier identifier = (Identifier)var5.next();
            Identifier identifier2 = RESOURCE_FINDER.toResourcePath(identifier);
            Optional optional = resourceManager.getResource(identifier2);
            if (optional.isEmpty()) {
               LOGGER.warn("Unable to find texture {}", identifier2);
            } else {
               AtlasSprite atlasSprite = new AtlasSprite(identifier2, (Resource)optional.get(), map.size());
               Iterator var10 = map.entrySet().iterator();

               while(var10.hasNext()) {
                  Map.Entry entry = (Map.Entry)var10.next();
                  String var10001 = this.separator;
                  Identifier identifier3 = identifier.withSuffixedPath(var10001 + (String)entry.getKey());
                  regions.add(identifier3, (AtlasSource.SpriteRegion)(new PalettedSpriteRegion(atlasSprite, (Supplier)entry.getValue(), identifier3)));
               }
            }
         }

         return;
      }
   }

   private static IntUnaryOperator toMapper(int[] from, int[] to) {
      if (to.length != from.length) {
         LOGGER.warn("Palette mapping has different sizes: {} and {}", from.length, to.length);
         throw new IllegalArgumentException();
      } else {
         Int2IntMap int2IntMap = new Int2IntOpenHashMap(to.length);

         for(int i = 0; i < from.length; ++i) {
            int j = from[i];
            if (ColorHelper.getAlpha(j) != 0) {
               int2IntMap.put(ColorHelper.zeroAlpha(j), to[i]);
            }
         }

         return (color) -> {
            int i = ColorHelper.getAlpha(color);
            if (i == 0) {
               return color;
            } else {
               int j = ColorHelper.zeroAlpha(color);
               int k = int2IntMap.getOrDefault(j, ColorHelper.fullAlpha(j));
               int l = ColorHelper.getAlpha(k);
               return ColorHelper.withAlpha(i * l / 255, k);
            }
         };
      }
   }

   private static int[] open(ResourceManager resourceManager, Identifier texture) {
      Optional optional = resourceManager.getResource(RESOURCE_FINDER.toResourcePath(texture));
      if (optional.isEmpty()) {
         LOGGER.error("Failed to load palette image {}", texture);
         throw new IllegalArgumentException();
      } else {
         try {
            InputStream inputStream = ((Resource)optional.get()).getInputStream();

            int[] var5;
            try {
               NativeImage nativeImage = NativeImage.read(inputStream);

               try {
                  var5 = nativeImage.copyPixelsArgb();
               } catch (Throwable var9) {
                  if (nativeImage != null) {
                     try {
                        nativeImage.close();
                     } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                     }
                  }

                  throw var9;
               }

               if (nativeImage != null) {
                  nativeImage.close();
               }
            } catch (Throwable var10) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return var5;
         } catch (Exception var11) {
            LOGGER.error("Couldn't load texture {}", texture, var11);
            throw new IllegalArgumentException();
         }
      }
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public List textures() {
      return this.textures;
   }

   public Identifier paletteKey() {
      return this.paletteKey;
   }

   public Map permutations() {
      return this.permutations;
   }

   public String separator() {
      return this.separator;
   }

   @Environment(EnvType.CLIENT)
   private static record PalettedSpriteRegion(AtlasSprite baseImage, Supplier palette, Identifier permutationLocation) implements AtlasSource.SpriteRegion {
      PalettedSpriteRegion(AtlasSprite atlasSprite, Supplier supplier, Identifier identifier) {
         this.baseImage = atlasSprite;
         this.palette = supplier;
         this.permutationLocation = identifier;
      }

      @Nullable
      public SpriteContents apply(SpriteOpener spriteOpener) {
         SpriteContents var3;
         try {
            NativeImage nativeImage = this.baseImage.read().applyToCopy((IntUnaryOperator)this.palette.get());
            var3 = new SpriteContents(this.permutationLocation, new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight()), nativeImage, ResourceMetadata.NONE);
            return var3;
         } catch (IllegalArgumentException | IOException var7) {
            PalettedPermutationsAtlasSource.LOGGER.error("unable to apply palette to {}", this.permutationLocation, var7);
            var3 = null;
         } finally {
            this.baseImage.close();
         }

         return var3;
      }

      public void close() {
         this.baseImage.close();
      }

      public AtlasSprite baseImage() {
         return this.baseImage;
      }

      public Supplier palette() {
         return this.palette;
      }

      public Identifier permutationLocation() {
         return this.permutationLocation;
      }

      // $FF: synthetic method
      @Nullable
      public Object apply(final Object opener) {
         return this.apply((SpriteOpener)opener);
      }
   }
}
