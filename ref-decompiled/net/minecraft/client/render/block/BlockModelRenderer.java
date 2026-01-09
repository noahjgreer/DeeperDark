package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class BlockModelRenderer implements FabricBlockModelRenderer {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockColors colors;
   private static final int BRIGHTNESS_CACHE_MAX_SIZE = 100;
   static final ThreadLocal BRIGHTNESS_CACHE = ThreadLocal.withInitial(BrightnessCache::new);

   public BlockModelRenderer(BlockColors colors) {
      this.colors = colors;
   }

   public void render(BlockRenderView world, List parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
      if (!parts.isEmpty()) {
         boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && ((BlockModelPart)parts.getFirst()).useAmbientOcclusion();
         matrices.translate(state.getModelOffset(pos));

         try {
            if (bl) {
               this.renderSmooth(world, parts, state, pos, matrices, vertexConsumer, cull, overlay);
            } else {
               this.renderFlat(world, parts, state, pos, matrices, vertexConsumer, cull, overlay);
            }

         } catch (Throwable var13) {
            CrashReport crashReport = CrashReport.create(var13, "Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(crashReportSection, world, pos, state);
            crashReportSection.add("Using AO", (Object)bl);
            throw new CrashException(crashReport);
         }
      }
   }

   private static boolean shouldDrawFace(BlockRenderView world, BlockState state, boolean cull, Direction side, BlockPos pos) {
      if (!cull) {
         return true;
      } else {
         BlockState blockState = world.getBlockState(pos);
         return Block.shouldDrawSide(state, blockState, side);
      }
   }

   public void renderSmooth(BlockRenderView world, List parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
      AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
      int i = 0;
      int j = 0;
      Iterator var12 = parts.iterator();

      while(var12.hasNext()) {
         BlockModelPart blockModelPart = (BlockModelPart)var12.next();
         Direction[] var14 = DIRECTIONS;
         int var15 = var14.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            Direction direction = var14[var16];
            int k = 1 << direction.ordinal();
            boolean bl = (i & k) == 1;
            boolean bl2 = (j & k) == 1;
            if (!bl || bl2) {
               List list = blockModelPart.getQuads(direction);
               if (!list.isEmpty()) {
                  if (!bl) {
                     bl2 = shouldDrawFace(world, state, cull, direction, ambientOcclusionCalculator.pos.set(pos, (Direction)direction));
                     i |= k;
                     if (bl2) {
                        j |= k;
                     }
                  }

                  if (bl2) {
                     this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list, ambientOcclusionCalculator, overlay);
                  }
               }
            }
         }

         List list2 = blockModelPart.getQuads((Direction)null);
         if (!list2.isEmpty()) {
            this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list2, ambientOcclusionCalculator, overlay);
         }
      }

   }

   public void renderFlat(BlockRenderView world, List parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
      LightmapCache lightmapCache = new LightmapCache();
      int i = 0;
      int j = 0;
      Iterator var12 = parts.iterator();

      while(var12.hasNext()) {
         BlockModelPart blockModelPart = (BlockModelPart)var12.next();
         Direction[] var14 = DIRECTIONS;
         int var15 = var14.length;

         for(int var16 = 0; var16 < var15; ++var16) {
            Direction direction = var14[var16];
            int k = 1 << direction.ordinal();
            boolean bl = (i & k) == 1;
            boolean bl2 = (j & k) == 1;
            if (!bl || bl2) {
               List list = blockModelPart.getQuads(direction);
               if (!list.isEmpty()) {
                  BlockPos blockPos = lightmapCache.pos.set(pos, (Direction)direction);
                  if (!bl) {
                     bl2 = shouldDrawFace(world, state, cull, direction, blockPos);
                     i |= k;
                     if (bl2) {
                        j |= k;
                     }
                  }

                  if (bl2) {
                     int l = lightmapCache.brightnessCache.getInt(state, world, blockPos);
                     this.renderQuadsFlat(world, state, pos, l, overlay, false, matrices, vertexConsumer, list, lightmapCache);
                  }
               }
            }
         }

         List list2 = blockModelPart.getQuads((Direction)null);
         if (!list2.isEmpty()) {
            this.renderQuadsFlat(world, state, pos, -1, overlay, true, matrices, vertexConsumer, list2, lightmapCache);
         }
      }

   }

   private void renderQuadsSmooth(BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, List quads, AmbientOcclusionCalculator ambientOcclusionCalculator, int overlay) {
      Iterator var9 = quads.iterator();

      while(var9.hasNext()) {
         BakedQuad bakedQuad = (BakedQuad)var9.next();
         getQuadDimensions(world, state, pos, bakedQuad.vertexData(), bakedQuad.face(), ambientOcclusionCalculator);
         ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.face(), bakedQuad.shade());
         this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, ambientOcclusionCalculator, overlay);
      }

   }

   private void renderQuad(BlockRenderView world, BlockState state, BlockPos pos, VertexConsumer vertexConsumer, MatrixStack.Entry matrixEntry, BakedQuad quad, LightmapCache lightmap, int light) {
      int i = quad.tintIndex();
      float f;
      float g;
      float h;
      if (i != -1) {
         int j;
         if (lightmap.lastTintIndex == i) {
            j = lightmap.colorOfLastTintIndex;
         } else {
            j = this.colors.getColor(state, world, pos, i);
            lightmap.lastTintIndex = i;
            lightmap.colorOfLastTintIndex = j;
         }

         f = ColorHelper.getRedFloat(j);
         g = ColorHelper.getGreenFloat(j);
         h = ColorHelper.getBlueFloat(j);
      } else {
         f = 1.0F;
         g = 1.0F;
         h = 1.0F;
      }

      vertexConsumer.quad(matrixEntry, quad, lightmap.fs, f, g, h, 1.0F, lightmap.is, light, true);
   }

   private static void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, LightmapCache lightmap) {
      float f = 32.0F;
      float g = 32.0F;
      float h = 32.0F;
      float i = -32.0F;
      float j = -32.0F;
      float k = -32.0F;

      float m;
      for(int l = 0; l < 4; ++l) {
         m = Float.intBitsToFloat(vertexData[l * 8]);
         float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
         float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
         f = Math.min(f, m);
         g = Math.min(g, n);
         h = Math.min(h, o);
         i = Math.max(i, m);
         j = Math.max(j, n);
         k = Math.max(k, o);
      }

      if (lightmap instanceof AmbientOcclusionCalculator ambientOcclusionCalculator) {
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.WEST.index] = f;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.EAST.index] = i;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.DOWN.index] = g;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.UP.index] = j;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.NORTH.index] = h;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.SOUTH.index] = k;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.FLIP_WEST.index] = 1.0F - f;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.FLIP_EAST.index] = 1.0F - i;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.FLIP_DOWN.index] = 1.0F - g;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.FLIP_UP.index] = 1.0F - j;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.FLIP_NORTH.index] = 1.0F - h;
         ambientOcclusionCalculator.field_58158[BlockModelRenderer.NeighborOrientation.FLIP_SOUTH.index] = 1.0F - k;
      }

      float p = 1.0E-4F;
      m = 0.9999F;
      boolean var10001;
      switch (face) {
         case DOWN:
         case UP:
            var10001 = f >= 1.0E-4F || h >= 1.0E-4F || i <= 0.9999F || k <= 0.9999F;
            break;
         case NORTH:
         case SOUTH:
            var10001 = f >= 1.0E-4F || g >= 1.0E-4F || i <= 0.9999F || j <= 0.9999F;
            break;
         case WEST:
         case EAST:
            var10001 = g >= 1.0E-4F || h >= 1.0E-4F || j <= 0.9999F || k <= 0.9999F;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      lightmap.field_58161 = var10001;
      switch (face) {
         case DOWN:
            var10001 = g == j && (g < 1.0E-4F || state.isFullCube(world, pos));
            break;
         case UP:
            var10001 = g == j && (j > 0.9999F || state.isFullCube(world, pos));
            break;
         case NORTH:
            var10001 = h == k && (h < 1.0E-4F || state.isFullCube(world, pos));
            break;
         case SOUTH:
            var10001 = h == k && (k > 0.9999F || state.isFullCube(world, pos));
            break;
         case WEST:
            var10001 = f == i && (f < 1.0E-4F || state.isFullCube(world, pos));
            break;
         case EAST:
            var10001 = f == i && (i > 0.9999F || state.isFullCube(world, pos));
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      lightmap.field_58160 = var10001;
   }

   private void renderQuadsFlat(BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List quads, LightmapCache lightmap) {
      Iterator var11 = quads.iterator();

      while(var11.hasNext()) {
         BakedQuad bakedQuad = (BakedQuad)var11.next();
         if (useWorldLight) {
            getQuadDimensions(world, state, pos, bakedQuad.vertexData(), bakedQuad.face(), lightmap);
            BlockPos blockPos = lightmap.field_58160 ? lightmap.pos.set(pos, (Direction)bakedQuad.face()) : pos;
            light = lightmap.brightnessCache.getInt(state, world, (BlockPos)blockPos);
         }

         float f = world.getBrightness(bakedQuad.face(), bakedQuad.shade());
         lightmap.fs[0] = f;
         lightmap.fs[1] = f;
         lightmap.fs[2] = f;
         lightmap.fs[3] = f;
         lightmap.is[0] = light;
         lightmap.is[1] = light;
         lightmap.is[2] = light;
         lightmap.is[3] = light;
         this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, lightmap, overlay);
      }

   }

   public static void render(MatrixStack.Entry entry, VertexConsumer vertexConsumer, BlockStateModel model, float red, float green, float blue, int light, int overlay) {
      Iterator var8 = model.getParts(Random.create(42L)).iterator();

      while(var8.hasNext()) {
         BlockModelPart blockModelPart = (BlockModelPart)var8.next();
         Direction[] var10 = DIRECTIONS;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            Direction direction = var10[var12];
            renderQuads(entry, vertexConsumer, red, green, blue, blockModelPart.getQuads(direction), light, overlay);
         }

         renderQuads(entry, vertexConsumer, red, green, blue, blockModelPart.getQuads((Direction)null), light, overlay);
      }

   }

   private static void renderQuads(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float red, float green, float blue, List quads, int light, int overlay) {
      BakedQuad bakedQuad;
      float f;
      float g;
      float h;
      for(Iterator var8 = quads.iterator(); var8.hasNext(); vertexConsumer.quad(entry, bakedQuad, f, g, h, 1.0F, light, overlay)) {
         bakedQuad = (BakedQuad)var8.next();
         if (bakedQuad.hasTint()) {
            f = MathHelper.clamp(red, 0.0F, 1.0F);
            g = MathHelper.clamp(green, 0.0F, 1.0F);
            h = MathHelper.clamp(blue, 0.0F, 1.0F);
         } else {
            f = 1.0F;
            g = 1.0F;
            h = 1.0F;
         }
      }

   }

   public static void enableBrightnessCache() {
      ((BrightnessCache)BRIGHTNESS_CACHE.get()).enable();
   }

   public static void disableBrightnessCache() {
      ((BrightnessCache)BRIGHTNESS_CACHE.get()).disable();
   }

   @Environment(EnvType.CLIENT)
   static class AmbientOcclusionCalculator extends LightmapCache {
      final float[] field_58158;

      public AmbientOcclusionCalculator() {
         this.field_58158 = new float[BlockModelRenderer.NeighborOrientation.SIZE];
      }

      public void apply(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, boolean bl) {
         BlockPos blockPos = this.field_58160 ? pos.offset(direction) : pos;
         NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
         BlockPos.Mutable mutable = this.pos;
         mutable.set(blockPos, (Direction)neighborData.faces[0]);
         BlockState blockState = world.getBlockState(mutable);
         int i = this.brightnessCache.getInt(blockState, world, mutable);
         float f = this.brightnessCache.getFloat(blockState, world, mutable);
         mutable.set(blockPos, (Direction)neighborData.faces[1]);
         BlockState blockState2 = world.getBlockState(mutable);
         int j = this.brightnessCache.getInt(blockState2, world, mutable);
         float g = this.brightnessCache.getFloat(blockState2, world, mutable);
         mutable.set(blockPos, (Direction)neighborData.faces[2]);
         BlockState blockState3 = world.getBlockState(mutable);
         int k = this.brightnessCache.getInt(blockState3, world, mutable);
         float h = this.brightnessCache.getFloat(blockState3, world, mutable);
         mutable.set(blockPos, (Direction)neighborData.faces[3]);
         BlockState blockState4 = world.getBlockState(mutable);
         int l = this.brightnessCache.getInt(blockState4, world, mutable);
         float m = this.brightnessCache.getFloat(blockState4, world, mutable);
         BlockState blockState5 = world.getBlockState(mutable.set(blockPos, (Direction)neighborData.faces[0]).move(direction));
         boolean bl2 = !blockState5.shouldBlockVision(world, mutable) || blockState5.getOpacity() == 0;
         BlockState blockState6 = world.getBlockState(mutable.set(blockPos, (Direction)neighborData.faces[1]).move(direction));
         boolean bl3 = !blockState6.shouldBlockVision(world, mutable) || blockState6.getOpacity() == 0;
         BlockState blockState7 = world.getBlockState(mutable.set(blockPos, (Direction)neighborData.faces[2]).move(direction));
         boolean bl4 = !blockState7.shouldBlockVision(world, mutable) || blockState7.getOpacity() == 0;
         BlockState blockState8 = world.getBlockState(mutable.set(blockPos, (Direction)neighborData.faces[3]).move(direction));
         boolean bl5 = !blockState8.shouldBlockVision(world, mutable) || blockState8.getOpacity() == 0;
         float n;
         int o;
         BlockState blockState9;
         if (!bl4 && !bl2) {
            n = f;
            o = i;
         } else {
            mutable.set(blockPos, (Direction)neighborData.faces[0]).move(neighborData.faces[2]);
            blockState9 = world.getBlockState(mutable);
            n = this.brightnessCache.getFloat(blockState9, world, mutable);
            o = this.brightnessCache.getInt(blockState9, world, mutable);
         }

         float p;
         int q;
         if (!bl5 && !bl2) {
            p = f;
            q = i;
         } else {
            mutable.set(blockPos, (Direction)neighborData.faces[0]).move(neighborData.faces[3]);
            blockState9 = world.getBlockState(mutable);
            p = this.brightnessCache.getFloat(blockState9, world, mutable);
            q = this.brightnessCache.getInt(blockState9, world, mutable);
         }

         float r;
         int s;
         if (!bl4 && !bl3) {
            r = f;
            s = i;
         } else {
            mutable.set(blockPos, (Direction)neighborData.faces[1]).move(neighborData.faces[2]);
            blockState9 = world.getBlockState(mutable);
            r = this.brightnessCache.getFloat(blockState9, world, mutable);
            s = this.brightnessCache.getInt(blockState9, world, mutable);
         }

         float t;
         int u;
         if (!bl5 && !bl3) {
            t = f;
            u = i;
         } else {
            mutable.set(blockPos, (Direction)neighborData.faces[1]).move(neighborData.faces[3]);
            blockState9 = world.getBlockState(mutable);
            t = this.brightnessCache.getFloat(blockState9, world, mutable);
            u = this.brightnessCache.getInt(blockState9, world, mutable);
         }

         int v = this.brightnessCache.getInt(state, world, pos);
         mutable.set(pos, (Direction)direction);
         BlockState blockState10 = world.getBlockState(mutable);
         if (this.field_58160 || !blockState10.isOpaqueFullCube()) {
            v = this.brightnessCache.getInt(blockState10, world, mutable);
         }

         float w = this.field_58160 ? this.brightnessCache.getFloat(world.getBlockState(blockPos), world, blockPos) : this.brightnessCache.getFloat(world.getBlockState(pos), world, pos);
         Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
         float x;
         float y;
         float z;
         float aa;
         if (this.field_58161 && neighborData.nonCubicWeight) {
            x = (m + f + p + w) * 0.25F;
            y = (h + f + n + w) * 0.25F;
            z = (h + g + r + w) * 0.25F;
            aa = (m + g + t + w) * 0.25F;
            float ab = this.field_58158[neighborData.field_4192[0].index] * this.field_58158[neighborData.field_4192[1].index];
            float ac = this.field_58158[neighborData.field_4192[2].index] * this.field_58158[neighborData.field_4192[3].index];
            float ad = this.field_58158[neighborData.field_4192[4].index] * this.field_58158[neighborData.field_4192[5].index];
            float ae = this.field_58158[neighborData.field_4192[6].index] * this.field_58158[neighborData.field_4192[7].index];
            float af = this.field_58158[neighborData.field_4185[0].index] * this.field_58158[neighborData.field_4185[1].index];
            float ag = this.field_58158[neighborData.field_4185[2].index] * this.field_58158[neighborData.field_4185[3].index];
            float ah = this.field_58158[neighborData.field_4185[4].index] * this.field_58158[neighborData.field_4185[5].index];
            float ai = this.field_58158[neighborData.field_4185[6].index] * this.field_58158[neighborData.field_4185[7].index];
            float aj = this.field_58158[neighborData.field_4180[0].index] * this.field_58158[neighborData.field_4180[1].index];
            float ak = this.field_58158[neighborData.field_4180[2].index] * this.field_58158[neighborData.field_4180[3].index];
            float al = this.field_58158[neighborData.field_4180[4].index] * this.field_58158[neighborData.field_4180[5].index];
            float am = this.field_58158[neighborData.field_4180[6].index] * this.field_58158[neighborData.field_4180[7].index];
            float an = this.field_58158[neighborData.field_4188[0].index] * this.field_58158[neighborData.field_4188[1].index];
            float ao = this.field_58158[neighborData.field_4188[2].index] * this.field_58158[neighborData.field_4188[3].index];
            float ap = this.field_58158[neighborData.field_4188[4].index] * this.field_58158[neighborData.field_4188[5].index];
            float aq = this.field_58158[neighborData.field_4188[6].index] * this.field_58158[neighborData.field_4188[7].index];
            this.fs[translation.firstCorner] = Math.clamp(x * ab + y * ac + z * ad + aa * ae, 0.0F, 1.0F);
            this.fs[translation.secondCorner] = Math.clamp(x * af + y * ag + z * ah + aa * ai, 0.0F, 1.0F);
            this.fs[translation.thirdCorner] = Math.clamp(x * aj + y * ak + z * al + aa * am, 0.0F, 1.0F);
            this.fs[translation.fourthCorner] = Math.clamp(x * an + y * ao + z * ap + aa * aq, 0.0F, 1.0F);
            int ar = getAmbientOcclusionBrightness(l, i, q, v);
            int as = getAmbientOcclusionBrightness(k, i, o, v);
            int at = getAmbientOcclusionBrightness(k, j, s, v);
            int au = getAmbientOcclusionBrightness(l, j, u, v);
            this.is[translation.firstCorner] = getBrightness(ar, as, at, au, ab, ac, ad, ae);
            this.is[translation.secondCorner] = getBrightness(ar, as, at, au, af, ag, ah, ai);
            this.is[translation.thirdCorner] = getBrightness(ar, as, at, au, aj, ak, al, am);
            this.is[translation.fourthCorner] = getBrightness(ar, as, at, au, an, ao, ap, aq);
         } else {
            x = (m + f + p + w) * 0.25F;
            y = (h + f + n + w) * 0.25F;
            z = (h + g + r + w) * 0.25F;
            aa = (m + g + t + w) * 0.25F;
            this.is[translation.firstCorner] = getAmbientOcclusionBrightness(l, i, q, v);
            this.is[translation.secondCorner] = getAmbientOcclusionBrightness(k, i, o, v);
            this.is[translation.thirdCorner] = getAmbientOcclusionBrightness(k, j, s, v);
            this.is[translation.fourthCorner] = getAmbientOcclusionBrightness(l, j, u, v);
            this.fs[translation.firstCorner] = x;
            this.fs[translation.secondCorner] = y;
            this.fs[translation.thirdCorner] = z;
            this.fs[translation.fourthCorner] = aa;
         }

         x = world.getBrightness(direction, bl);

         for(int av = 0; av < this.fs.length; ++av) {
            float[] var10000 = this.fs;
            var10000[av] *= x;
         }

      }

      private static int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
         if (i == 0) {
            i = l;
         }

         if (j == 0) {
            j = l;
         }

         if (k == 0) {
            k = l;
         }

         return i + j + k + l >> 2 & 16711935;
      }

      private static int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
         int n = (int)((float)(i >> 16 & 255) * f + (float)(j >> 16 & 255) * g + (float)(k >> 16 & 255) * h + (float)(l >> 16 & 255) * m) & 255;
         int o = (int)((float)(i & 255) * f + (float)(j & 255) * g + (float)(k & 255) * h + (float)(l & 255) * m) & 255;
         return n << 16 | o;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class LightmapCache {
      public final BlockPos.Mutable pos = new BlockPos.Mutable();
      public boolean field_58160;
      public boolean field_58161;
      public final float[] fs = new float[4];
      public final int[] is = new int[4];
      public int lastTintIndex = -1;
      public int colorOfLastTintIndex;
      public final BrightnessCache brightnessCache;

      LightmapCache() {
         this.brightnessCache = (BrightnessCache)BlockModelRenderer.BRIGHTNESS_CACHE.get();
      }
   }

   @Environment(EnvType.CLIENT)
   private static class BrightnessCache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap intCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
         Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int newN) {
            }
         };
         long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
         return long2IntLinkedOpenHashMap;
      });
      private final Long2FloatLinkedOpenHashMap floatCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
         Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
            protected void rehash(int newN) {
            }
         };
         long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
         return long2FloatLinkedOpenHashMap;
      });
      private final WorldRenderer.BrightnessGetter brightnessCache = (world, pos) -> {
         long l = pos.asLong();
         int i = this.intCache.get(l);
         if (i != Integer.MAX_VALUE) {
            return i;
         } else {
            int j = WorldRenderer.BrightnessGetter.DEFAULT.packedBrightness(world, pos);
            if (this.intCache.size() == 100) {
               this.intCache.removeFirstInt();
            }

            this.intCache.put(l, j);
            return j;
         }
      };

      public void enable() {
         this.enabled = true;
      }

      public void disable() {
         this.enabled = false;
         this.intCache.clear();
         this.floatCache.clear();
      }

      public int getInt(BlockState state, BlockRenderView world, BlockPos pos) {
         return WorldRenderer.getLightmapCoordinates(this.enabled ? this.brightnessCache : WorldRenderer.BrightnessGetter.DEFAULT, world, state, pos);
      }

      public float getFloat(BlockState state, BlockRenderView blockView, BlockPos pos) {
         long l = pos.asLong();
         float f;
         if (this.enabled) {
            f = this.floatCache.get(l);
            if (!Float.isNaN(f)) {
               return f;
            }
         }

         f = state.getAmbientOcclusionLightLevel(blockView, pos);
         if (this.enabled) {
            if (this.floatCache.size() == 100) {
               this.floatCache.removeFirstFloat();
            }

            this.floatCache.put(l, f);
         }

         return f;
      }
   }

   @Environment(EnvType.CLIENT)
   protected static enum NeighborOrientation {
      DOWN(0),
      UP(1),
      NORTH(2),
      SOUTH(3),
      WEST(4),
      EAST(5),
      FLIP_DOWN(6),
      FLIP_UP(7),
      FLIP_NORTH(8),
      FLIP_SOUTH(9),
      FLIP_WEST(10),
      FLIP_EAST(11);

      public static final int SIZE = values().length;
      final int index;

      private NeighborOrientation(final int index) {
         this.index = index;
      }

      // $FF: synthetic method
      private static NeighborOrientation[] method_36919() {
         return new NeighborOrientation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST, FLIP_DOWN, FLIP_UP, FLIP_NORTH, FLIP_SOUTH, FLIP_WEST, FLIP_EAST};
      }
   }

   @Environment(EnvType.CLIENT)
   protected static enum NeighborData {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.SOUTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.SOUTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.WEST}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_WEST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.WEST, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.WEST}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.EAST}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_EAST, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.EAST, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.SOUTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.SOUTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.FLIP_DOWN, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.DOWN, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.NORTH, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_NORTH, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.NORTH}, new NeighborOrientation[]{BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.SOUTH, BlockModelRenderer.NeighborOrientation.FLIP_UP, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.FLIP_SOUTH, BlockModelRenderer.NeighborOrientation.UP, BlockModelRenderer.NeighborOrientation.SOUTH});

      final Direction[] faces;
      final boolean nonCubicWeight;
      final NeighborOrientation[] field_4192;
      final NeighborOrientation[] field_4185;
      final NeighborOrientation[] field_4180;
      final NeighborOrientation[] field_4188;
      private static final NeighborData[] VALUES = (NeighborData[])Util.make(new NeighborData[6], (values) -> {
         values[Direction.DOWN.getIndex()] = DOWN;
         values[Direction.UP.getIndex()] = UP;
         values[Direction.NORTH.getIndex()] = NORTH;
         values[Direction.SOUTH.getIndex()] = SOUTH;
         values[Direction.WEST.getIndex()] = WEST;
         values[Direction.EAST.getIndex()] = EAST;
      });

      private NeighborData(final Direction[] faces, final float f, final boolean nonCubicWeight, final NeighborOrientation[] neighborOrientations, final NeighborOrientation[] neighborOrientations2, final NeighborOrientation[] neighborOrientations3, final NeighborOrientation[] neighborOrientations4) {
         this.faces = faces;
         this.nonCubicWeight = nonCubicWeight;
         this.field_4192 = neighborOrientations;
         this.field_4185 = neighborOrientations2;
         this.field_4180 = neighborOrientations3;
         this.field_4188 = neighborOrientations4;
      }

      public static NeighborData getData(Direction direction) {
         return VALUES[direction.getIndex()];
      }

      // $FF: synthetic method
      private static NeighborData[] method_36917() {
         return new NeighborData[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }
   }

   @Environment(EnvType.CLIENT)
   private static enum Translation {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      final int firstCorner;
      final int secondCorner;
      final int thirdCorner;
      final int fourthCorner;
      private static final Translation[] VALUES = (Translation[])Util.make(new Translation[6], (values) -> {
         values[Direction.DOWN.getIndex()] = DOWN;
         values[Direction.UP.getIndex()] = UP;
         values[Direction.NORTH.getIndex()] = NORTH;
         values[Direction.SOUTH.getIndex()] = SOUTH;
         values[Direction.WEST.getIndex()] = WEST;
         values[Direction.EAST.getIndex()] = EAST;
      });

      private Translation(final int firstCorner, final int secondCorner, final int thirdCorner, final int fourthCorner) {
         this.firstCorner = firstCorner;
         this.secondCorner = secondCorner;
         this.thirdCorner = thirdCorner;
         this.fourthCorner = fourthCorner;
      }

      public static Translation getTranslations(Direction direction) {
         return VALUES[direction.getIndex()];
      }

      // $FF: synthetic method
      private static Translation[] method_36918() {
         return new Translation[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
      }
   }
}
