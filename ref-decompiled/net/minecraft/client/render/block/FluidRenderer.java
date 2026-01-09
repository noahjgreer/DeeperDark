package net.minecraft.client.render.block;

import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;

@Environment(EnvType.CLIENT)
public class FluidRenderer {
   private static final float FLUID_HEIGHT = 0.8888889F;
   private final Sprite[] lavaSprites = new Sprite[2];
   private final Sprite[] waterSprites = new Sprite[2];
   private Sprite waterOverlaySprite;

   protected void onResourceReload() {
      this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).particleSprite();
      this.lavaSprites[1] = ModelBaker.LAVA_FLOW.getSprite();
      this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).particleSprite();
      this.waterSprites[1] = ModelBaker.WATER_FLOW.getSprite();
      this.waterOverlaySprite = ModelBaker.WATER_OVERLAY.getSprite();
   }

   private static boolean isSameFluid(FluidState a, FluidState b) {
      return b.getFluid().matchesType(a.getFluid());
   }

   private static boolean isSideCovered(Direction side, float height, BlockState state) {
      VoxelShape voxelShape = state.getCullingFace(side.getOpposite());
      if (voxelShape == VoxelShapes.empty()) {
         return false;
      } else if (voxelShape == VoxelShapes.fullCube()) {
         boolean bl = height == 1.0F;
         return side != Direction.UP || bl;
      } else {
         VoxelShape voxelShape2 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)height, 1.0);
         return VoxelShapes.isSideCovered(voxelShape2, voxelShape, side);
      }
   }

   private static boolean shouldSkipRendering(Direction side, float height, BlockState state) {
      return isSideCovered(side, height, state);
   }

   private static boolean isOppositeSideCovered(BlockState state, Direction side) {
      return isSideCovered(side.getOpposite(), 1.0F, state);
   }

   public static boolean shouldRenderSide(FluidState fluid, BlockState state, Direction side, FluidState fluidFromSide) {
      return !isOppositeSideCovered(state, side) && !isSameFluid(fluid, fluidFromSide);
   }

   public void render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
      boolean bl = fluidState.isIn(FluidTags.LAVA);
      Sprite[] sprites = bl ? this.lavaSprites : this.waterSprites;
      int i = bl ? 16777215 : BiomeColors.getWaterColor(world, pos);
      float f = (float)(i >> 16 & 255) / 255.0F;
      float g = (float)(i >> 8 & 255) / 255.0F;
      float h = (float)(i & 255) / 255.0F;
      BlockState blockState2 = world.getBlockState(pos.offset(Direction.DOWN));
      FluidState fluidState2 = blockState2.getFluidState();
      BlockState blockState3 = world.getBlockState(pos.offset(Direction.UP));
      FluidState fluidState3 = blockState3.getFluidState();
      BlockState blockState4 = world.getBlockState(pos.offset(Direction.NORTH));
      FluidState fluidState4 = blockState4.getFluidState();
      BlockState blockState5 = world.getBlockState(pos.offset(Direction.SOUTH));
      FluidState fluidState5 = blockState5.getFluidState();
      BlockState blockState6 = world.getBlockState(pos.offset(Direction.WEST));
      FluidState fluidState6 = blockState6.getFluidState();
      BlockState blockState7 = world.getBlockState(pos.offset(Direction.EAST));
      FluidState fluidState7 = blockState7.getFluidState();
      boolean bl2 = !isSameFluid(fluidState, fluidState3);
      boolean bl3 = shouldRenderSide(fluidState, blockState, Direction.DOWN, fluidState2) && !shouldSkipRendering(Direction.DOWN, 0.8888889F, blockState2);
      boolean bl4 = shouldRenderSide(fluidState, blockState, Direction.NORTH, fluidState4);
      boolean bl5 = shouldRenderSide(fluidState, blockState, Direction.SOUTH, fluidState5);
      boolean bl6 = shouldRenderSide(fluidState, blockState, Direction.WEST, fluidState6);
      boolean bl7 = shouldRenderSide(fluidState, blockState, Direction.EAST, fluidState7);
      if (bl2 || bl3 || bl7 || bl6 || bl4 || bl5) {
         float j = world.getBrightness(Direction.DOWN, true);
         float k = world.getBrightness(Direction.UP, true);
         float l = world.getBrightness(Direction.NORTH, true);
         float m = world.getBrightness(Direction.WEST, true);
         Fluid fluid = fluidState.getFluid();
         float n = this.getFluidHeight(world, fluid, pos, blockState, fluidState);
         float o;
         float p;
         float q;
         float r;
         float s;
         float t;
         float u;
         float v;
         if (n >= 1.0F) {
            o = 1.0F;
            p = 1.0F;
            q = 1.0F;
            r = 1.0F;
         } else {
            s = this.getFluidHeight(world, fluid, pos.north(), blockState4, fluidState4);
            t = this.getFluidHeight(world, fluid, pos.south(), blockState5, fluidState5);
            u = this.getFluidHeight(world, fluid, pos.east(), blockState7, fluidState7);
            v = this.getFluidHeight(world, fluid, pos.west(), blockState6, fluidState6);
            o = this.calculateFluidHeight(world, fluid, n, s, u, pos.offset(Direction.NORTH).offset(Direction.EAST));
            p = this.calculateFluidHeight(world, fluid, n, s, v, pos.offset(Direction.NORTH).offset(Direction.WEST));
            q = this.calculateFluidHeight(world, fluid, n, t, u, pos.offset(Direction.SOUTH).offset(Direction.EAST));
            r = this.calculateFluidHeight(world, fluid, n, t, v, pos.offset(Direction.SOUTH).offset(Direction.WEST));
         }

         s = (float)(pos.getX() & 15);
         t = (float)(pos.getY() & 15);
         u = (float)(pos.getZ() & 15);
         v = 0.001F;
         float w = bl3 ? 0.001F : 0.0F;
         float x;
         float z;
         float ab;
         float ad;
         float y;
         float aa;
         float ac;
         float ae;
         float ah;
         float ai;
         float al;
         float am;
         if (bl2 && !shouldSkipRendering(Direction.UP, Math.min(Math.min(p, r), Math.min(q, o)), blockState3)) {
            p -= 0.001F;
            r -= 0.001F;
            q -= 0.001F;
            o -= 0.001F;
            Vec3d vec3d = fluidState.getVelocity(world, pos);
            Sprite sprite;
            float af;
            float ag;
            if (vec3d.x == 0.0 && vec3d.z == 0.0) {
               sprite = sprites[0];
               x = sprite.getFrameU(0.0F);
               y = sprite.getFrameV(0.0F);
               z = x;
               aa = sprite.getFrameV(1.0F);
               ab = sprite.getFrameU(1.0F);
               ac = aa;
               ad = ab;
               ae = y;
            } else {
               sprite = sprites[1];
               af = (float)MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707964F;
               ag = MathHelper.sin(af) * 0.25F;
               ah = MathHelper.cos(af) * 0.25F;
               ai = 0.5F;
               x = sprite.getFrameU(0.5F + (-ah - ag));
               y = sprite.getFrameV(0.5F + -ah + ag);
               z = sprite.getFrameU(0.5F + -ah + ag);
               aa = sprite.getFrameV(0.5F + ah + ag);
               ab = sprite.getFrameU(0.5F + ah + ag);
               ac = sprite.getFrameV(0.5F + (ah - ag));
               ad = sprite.getFrameU(0.5F + (ah - ag));
               ae = sprite.getFrameV(0.5F + (-ah - ag));
            }

            float aj = (x + z + ab + ad) / 4.0F;
            af = (y + aa + ac + ae) / 4.0F;
            ag = sprites[0].getUvScaleDelta();
            x = MathHelper.lerp(ag, x, aj);
            z = MathHelper.lerp(ag, z, aj);
            ab = MathHelper.lerp(ag, ab, aj);
            ad = MathHelper.lerp(ag, ad, aj);
            y = MathHelper.lerp(ag, y, af);
            aa = MathHelper.lerp(ag, aa, af);
            ac = MathHelper.lerp(ag, ac, af);
            ae = MathHelper.lerp(ag, ae, af);
            int ak = this.getLight(world, pos);
            ai = k * f;
            al = k * g;
            am = k * h;
            this.vertex(vertexConsumer, s + 0.0F, t + p, u + 0.0F, ai, al, am, x, y, ak);
            this.vertex(vertexConsumer, s + 0.0F, t + r, u + 1.0F, ai, al, am, z, aa, ak);
            this.vertex(vertexConsumer, s + 1.0F, t + q, u + 1.0F, ai, al, am, ab, ac, ak);
            this.vertex(vertexConsumer, s + 1.0F, t + o, u + 0.0F, ai, al, am, ad, ae, ak);
            if (fluidState.canFlowTo(world, pos.up())) {
               this.vertex(vertexConsumer, s + 0.0F, t + p, u + 0.0F, ai, al, am, x, y, ak);
               this.vertex(vertexConsumer, s + 1.0F, t + o, u + 0.0F, ai, al, am, ad, ae, ak);
               this.vertex(vertexConsumer, s + 1.0F, t + q, u + 1.0F, ai, al, am, ab, ac, ak);
               this.vertex(vertexConsumer, s + 0.0F, t + r, u + 1.0F, ai, al, am, z, aa, ak);
            }
         }

         if (bl3) {
            x = sprites[0].getMinU();
            z = sprites[0].getMaxU();
            ab = sprites[0].getMinV();
            ad = sprites[0].getMaxV();
            int an = this.getLight(world, pos.down());
            aa = j * f;
            ac = j * g;
            ae = j * h;
            this.vertex(vertexConsumer, s, t + w, u + 1.0F, aa, ac, ae, x, ad, an);
            this.vertex(vertexConsumer, s, t + w, u, aa, ac, ae, x, ab, an);
            this.vertex(vertexConsumer, s + 1.0F, t + w, u, aa, ac, ae, z, ab, an);
            this.vertex(vertexConsumer, s + 1.0F, t + w, u + 1.0F, aa, ac, ae, z, ad, an);
         }

         int ao = this.getLight(world, pos);
         Iterator var67 = Direction.Type.HORIZONTAL.iterator();

         while(true) {
            Direction direction;
            float ap;
            boolean bl8;
            do {
               do {
                  if (!var67.hasNext()) {
                     return;
                  }

                  direction = (Direction)var67.next();
                  switch (direction) {
                     case NORTH:
                        ad = p;
                        y = o;
                        aa = s;
                        ae = s + 1.0F;
                        ac = u + 0.001F;
                        ap = u + 0.001F;
                        bl8 = bl4;
                        break;
                     case SOUTH:
                        ad = q;
                        y = r;
                        aa = s + 1.0F;
                        ae = s;
                        ac = u + 1.0F - 0.001F;
                        ap = u + 1.0F - 0.001F;
                        bl8 = bl5;
                        break;
                     case WEST:
                        ad = r;
                        y = p;
                        aa = s + 0.001F;
                        ae = s + 0.001F;
                        ac = u + 1.0F;
                        ap = u;
                        bl8 = bl6;
                        break;
                     default:
                        ad = o;
                        y = q;
                        aa = s + 1.0F - 0.001F;
                        ae = s + 1.0F - 0.001F;
                        ac = u;
                        ap = u + 1.0F;
                        bl8 = bl7;
                  }
               } while(!bl8);
            } while(shouldSkipRendering(direction, Math.max(ad, y), world.getBlockState(pos.offset(direction))));

            BlockPos blockPos = pos.offset(direction);
            Sprite sprite2 = sprites[1];
            if (!bl) {
               Block block = world.getBlockState(blockPos).getBlock();
               if (block instanceof TranslucentBlock || block instanceof LeavesBlock) {
                  sprite2 = this.waterOverlaySprite;
               }
            }

            ah = sprite2.getFrameU(0.0F);
            ai = sprite2.getFrameU(0.5F);
            al = sprite2.getFrameV((1.0F - ad) * 0.5F);
            am = sprite2.getFrameV((1.0F - y) * 0.5F);
            float aq = sprite2.getFrameV(0.5F);
            float ar = direction.getAxis() == Direction.Axis.Z ? l : m;
            float as = k * ar * f;
            float at = k * ar * g;
            float au = k * ar * h;
            this.vertex(vertexConsumer, aa, t + ad, ac, as, at, au, ah, al, ao);
            this.vertex(vertexConsumer, ae, t + y, ap, as, at, au, ai, am, ao);
            this.vertex(vertexConsumer, ae, t + w, ap, as, at, au, ai, aq, ao);
            this.vertex(vertexConsumer, aa, t + w, ac, as, at, au, ah, aq, ao);
            if (sprite2 != this.waterOverlaySprite) {
               this.vertex(vertexConsumer, aa, t + w, ac, as, at, au, ah, aq, ao);
               this.vertex(vertexConsumer, ae, t + w, ap, as, at, au, ai, aq, ao);
               this.vertex(vertexConsumer, ae, t + y, ap, as, at, au, ai, am, ao);
               this.vertex(vertexConsumer, aa, t + ad, ac, as, at, au, ah, al, ao);
            }
         }
      }
   }

   private float calculateFluidHeight(BlockRenderView world, Fluid fluid, float originHeight, float northSouthHeight, float eastWestHeight, BlockPos pos) {
      if (!(eastWestHeight >= 1.0F) && !(northSouthHeight >= 1.0F)) {
         float[] fs = new float[2];
         if (eastWestHeight > 0.0F || northSouthHeight > 0.0F) {
            float f = this.getFluidHeight(world, fluid, pos);
            if (f >= 1.0F) {
               return 1.0F;
            }

            this.addHeight(fs, f);
         }

         this.addHeight(fs, originHeight);
         this.addHeight(fs, eastWestHeight);
         this.addHeight(fs, northSouthHeight);
         return fs[0] / fs[1];
      } else {
         return 1.0F;
      }
   }

   private void addHeight(float[] weightedAverageHeight, float height) {
      if (height >= 0.8F) {
         weightedAverageHeight[0] += height * 10.0F;
         weightedAverageHeight[1] += 10.0F;
      } else if (height >= 0.0F) {
         weightedAverageHeight[0] += height;
         int var10002 = weightedAverageHeight[1]++;
      }

   }

   private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos) {
      BlockState blockState = world.getBlockState(pos);
      return this.getFluidHeight(world, fluid, pos, blockState, blockState.getFluidState());
   }

   private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
      if (fluid.matchesType(fluidState.getFluid())) {
         BlockState blockState2 = world.getBlockState(pos.up());
         return fluid.matchesType(blockState2.getFluidState().getFluid()) ? 1.0F : fluidState.getHeight();
      } else {
         return !blockState.isSolid() ? 0.0F : -1.0F;
      }
   }

   private void vertex(VertexConsumer vertexConsumer, float x, float y, float z, float red, float green, float blue, float u, float v, int light) {
      vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0F).texture(u, v).light(light).normal(0.0F, 1.0F, 0.0F);
   }

   private int getLight(BlockRenderView world, BlockPos pos) {
      int i = WorldRenderer.getLightmapCoordinates(world, pos);
      int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
      int k = i & 255;
      int l = j & 255;
      int m = i >> 16 & 255;
      int n = j >> 16 & 255;
      return (k > l ? k : l) | (m > n ? m : n) << 16;
   }
}
