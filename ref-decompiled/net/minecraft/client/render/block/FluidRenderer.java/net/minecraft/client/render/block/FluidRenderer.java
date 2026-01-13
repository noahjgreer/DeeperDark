/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TranslucentBlock;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
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

@Environment(value=EnvType.CLIENT)
public class FluidRenderer {
    private static final float FLUID_HEIGHT = 0.8888889f;
    private final Sprite field_64568;
    private final Sprite field_64569;
    private final Sprite field_64570;
    private final Sprite field_64571;
    private final Sprite waterOverlaySprite;

    public FluidRenderer(SpriteHolder spriteHolder) {
        this.field_64568 = spriteHolder.getSprite(ModelBaker.LAVA_STILL);
        this.field_64569 = spriteHolder.getSprite(ModelBaker.LAVA_FLOW);
        this.field_64570 = spriteHolder.getSprite(ModelBaker.WATER_STILL);
        this.field_64571 = spriteHolder.getSprite(ModelBaker.WATER_FLOW);
        this.waterOverlaySprite = spriteHolder.getSprite(ModelBaker.WATER_OVERLAY);
    }

    private static boolean isSameFluid(FluidState a, FluidState b) {
        return b.getFluid().matchesType(a.getFluid());
    }

    private static boolean isSideCovered(Direction side, float height, BlockState state) {
        VoxelShape voxelShape = state.getCullingFace(side.getOpposite());
        if (voxelShape == VoxelShapes.empty()) {
            return false;
        }
        if (voxelShape == VoxelShapes.fullCube()) {
            boolean bl = height == 1.0f;
            return side != Direction.UP || bl;
        }
        VoxelShape voxelShape2 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, height, 1.0);
        return VoxelShapes.isSideCovered(voxelShape2, voxelShape, side);
    }

    private static boolean shouldSkipRendering(Direction side, float height, BlockState state) {
        return FluidRenderer.isSideCovered(side, height, state);
    }

    private static boolean isOppositeSideCovered(BlockState state, Direction side) {
        return FluidRenderer.isSideCovered(side.getOpposite(), 1.0f, state);
    }

    public static boolean shouldRenderSide(FluidState fluid, BlockState state, Direction side, FluidState fluidFromSide) {
        return !FluidRenderer.isOppositeSideCovered(state, side) && !FluidRenderer.isSameFluid(fluid, fluidFromSide);
    }

    public void render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
        float ai;
        float ae;
        float ad;
        float ac;
        float ab;
        float aa;
        float z;
        float x;
        float w;
        float v;
        float u;
        float t;
        float s;
        float r;
        float q;
        float p;
        float o;
        boolean bl = fluidState.isIn(FluidTags.LAVA);
        Sprite sprite = bl ? this.field_64568 : this.field_64570;
        Sprite sprite2 = bl ? this.field_64569 : this.field_64571;
        int i = bl ? 0xFFFFFF : BiomeColors.getWaterColor(world, pos);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
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
        boolean bl2 = !FluidRenderer.isSameFluid(fluidState, fluidState3);
        boolean bl3 = FluidRenderer.shouldRenderSide(fluidState, blockState, Direction.DOWN, fluidState2) && !FluidRenderer.shouldSkipRendering(Direction.DOWN, 0.8888889f, blockState2);
        boolean bl4 = FluidRenderer.shouldRenderSide(fluidState, blockState, Direction.NORTH, fluidState4);
        boolean bl5 = FluidRenderer.shouldRenderSide(fluidState, blockState, Direction.SOUTH, fluidState5);
        boolean bl6 = FluidRenderer.shouldRenderSide(fluidState, blockState, Direction.WEST, fluidState6);
        boolean bl7 = FluidRenderer.shouldRenderSide(fluidState, blockState, Direction.EAST, fluidState7);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return;
        }
        float j = world.getBrightness(Direction.DOWN, true);
        float k = world.getBrightness(Direction.UP, true);
        float l = world.getBrightness(Direction.NORTH, true);
        float m = world.getBrightness(Direction.WEST, true);
        Fluid fluid = fluidState.getFluid();
        float n = this.getFluidHeight(world, fluid, pos, blockState, fluidState);
        if (n >= 1.0f) {
            o = 1.0f;
            p = 1.0f;
            q = 1.0f;
            r = 1.0f;
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
        s = pos.getX() & 0xF;
        t = pos.getY() & 0xF;
        u = pos.getZ() & 0xF;
        v = 0.001f;
        float f2 = w = bl3 ? 0.001f : 0.0f;
        if (bl2 && !FluidRenderer.shouldSkipRendering(Direction.UP, Math.min(Math.min(p, r), Math.min(q, o)), blockState3)) {
            float ah;
            float ag;
            float y;
            p -= 0.001f;
            r -= 0.001f;
            q -= 0.001f;
            o -= 0.001f;
            Vec3d vec3d = fluidState.getVelocity(world, pos);
            if (vec3d.x == 0.0 && vec3d.z == 0.0) {
                x = sprite.getFrameU(0.0f);
                y = sprite.getFrameV(0.0f);
                z = x;
                aa = sprite.getFrameV(1.0f);
                ab = sprite.getFrameU(1.0f);
                ac = aa;
                ad = ab;
                ae = y;
            } else {
                float af = (float)MathHelper.atan2(vec3d.z, vec3d.x) - 1.5707964f;
                ag = MathHelper.sin(af) * 0.25f;
                ah = MathHelper.cos(af) * 0.25f;
                ai = 0.5f;
                x = sprite2.getFrameU(0.5f + (-ah - ag));
                y = sprite2.getFrameV(0.5f + (-ah + ag));
                z = sprite2.getFrameU(0.5f + (-ah + ag));
                aa = sprite2.getFrameV(0.5f + (ah + ag));
                ab = sprite2.getFrameU(0.5f + (ah + ag));
                ac = sprite2.getFrameV(0.5f + (ah - ag));
                ad = sprite2.getFrameU(0.5f + (ah - ag));
                ae = sprite2.getFrameV(0.5f + (-ah - ag));
            }
            int aj = this.getLight(world, pos);
            ag = k * f;
            ah = k * g;
            ai = k * h;
            this.vertex(vertexConsumer, s + 0.0f, t + p, u + 0.0f, ag, ah, ai, x, y, aj);
            this.vertex(vertexConsumer, s + 0.0f, t + r, u + 1.0f, ag, ah, ai, z, aa, aj);
            this.vertex(vertexConsumer, s + 1.0f, t + q, u + 1.0f, ag, ah, ai, ab, ac, aj);
            this.vertex(vertexConsumer, s + 1.0f, t + o, u + 0.0f, ag, ah, ai, ad, ae, aj);
            if (fluidState.canFlowTo(world, pos.up())) {
                this.vertex(vertexConsumer, s + 0.0f, t + p, u + 0.0f, ag, ah, ai, x, y, aj);
                this.vertex(vertexConsumer, s + 1.0f, t + o, u + 0.0f, ag, ah, ai, ad, ae, aj);
                this.vertex(vertexConsumer, s + 1.0f, t + q, u + 1.0f, ag, ah, ai, ab, ac, aj);
                this.vertex(vertexConsumer, s + 0.0f, t + r, u + 1.0f, ag, ah, ai, z, aa, aj);
            }
        }
        if (bl3) {
            x = sprite.getMinU();
            z = sprite.getMaxU();
            ab = sprite.getMinV();
            ad = sprite.getMaxV();
            int ak = this.getLight(world, pos.down());
            aa = j * f;
            ac = j * g;
            ae = j * h;
            this.vertex(vertexConsumer, s, t + w, u + 1.0f, aa, ac, ae, x, ad, ak);
            this.vertex(vertexConsumer, s, t + w, u, aa, ac, ae, x, ab, ak);
            this.vertex(vertexConsumer, s + 1.0f, t + w, u, aa, ac, ae, z, ab, ak);
            this.vertex(vertexConsumer, s + 1.0f, t + w, u + 1.0f, aa, ac, ae, z, ad, ak);
        }
        int al = this.getLight(world, pos);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            Block block;
            float am;
            float y;
            if (!(switch (direction) {
                case Direction.NORTH -> {
                    ad = p;
                    y = o;
                    aa = s;
                    ae = s + 1.0f;
                    ac = u + 0.001f;
                    am = u + 0.001f;
                    yield bl4;
                }
                case Direction.SOUTH -> {
                    ad = q;
                    y = r;
                    aa = s + 1.0f;
                    ae = s;
                    ac = u + 1.0f - 0.001f;
                    am = u + 1.0f - 0.001f;
                    yield bl5;
                }
                case Direction.WEST -> {
                    ad = r;
                    y = p;
                    aa = s + 0.001f;
                    ae = s + 0.001f;
                    ac = u + 1.0f;
                    am = u;
                    yield bl6;
                }
                default -> {
                    ad = o;
                    y = q;
                    aa = s + 1.0f - 0.001f;
                    ae = s + 1.0f - 0.001f;
                    ac = u;
                    am = u + 1.0f;
                    yield bl7;
                }
            }) || FluidRenderer.shouldSkipRendering(direction, Math.max(ad, y), world.getBlockState(pos.offset(direction)))) continue;
            BlockPos blockPos = pos.offset(direction);
            Sprite sprite3 = sprite2;
            if (!bl && ((block = world.getBlockState(blockPos).getBlock()) instanceof TranslucentBlock || block instanceof LeavesBlock)) {
                sprite3 = this.waterOverlaySprite;
            }
            ai = sprite3.getFrameU(0.0f);
            float an = sprite3.getFrameU(0.5f);
            float ao = sprite3.getFrameV((1.0f - ad) * 0.5f);
            float ap = sprite3.getFrameV((1.0f - y) * 0.5f);
            float aq = sprite3.getFrameV(0.5f);
            float ar = direction.getAxis() == Direction.Axis.Z ? l : m;
            float as = k * ar * f;
            float at = k * ar * g;
            float au = k * ar * h;
            this.vertex(vertexConsumer, aa, t + ad, ac, as, at, au, ai, ao, al);
            this.vertex(vertexConsumer, ae, t + y, am, as, at, au, an, ap, al);
            this.vertex(vertexConsumer, ae, t + w, am, as, at, au, an, aq, al);
            this.vertex(vertexConsumer, aa, t + w, ac, as, at, au, ai, aq, al);
            if (sprite3 == this.waterOverlaySprite) continue;
            this.vertex(vertexConsumer, aa, t + w, ac, as, at, au, ai, aq, al);
            this.vertex(vertexConsumer, ae, t + w, am, as, at, au, an, aq, al);
            this.vertex(vertexConsumer, ae, t + y, am, as, at, au, an, ap, al);
            this.vertex(vertexConsumer, aa, t + ad, ac, as, at, au, ai, ao, al);
        }
    }

    private float calculateFluidHeight(BlockRenderView world, Fluid fluid, float originHeight, float northSouthHeight, float eastWestHeight, BlockPos pos) {
        if (eastWestHeight >= 1.0f || northSouthHeight >= 1.0f) {
            return 1.0f;
        }
        float[] fs = new float[2];
        if (eastWestHeight > 0.0f || northSouthHeight > 0.0f) {
            float f = this.getFluidHeight(world, fluid, pos);
            if (f >= 1.0f) {
                return 1.0f;
            }
            this.addHeight(fs, f);
        }
        this.addHeight(fs, originHeight);
        this.addHeight(fs, eastWestHeight);
        this.addHeight(fs, northSouthHeight);
        return fs[0] / fs[1];
    }

    private void addHeight(float[] weightedAverageHeight, float height) {
        if (height >= 0.8f) {
            weightedAverageHeight[0] = weightedAverageHeight[0] + height * 10.0f;
            weightedAverageHeight[1] = weightedAverageHeight[1] + 10.0f;
        } else if (height >= 0.0f) {
            weightedAverageHeight[0] = weightedAverageHeight[0] + height;
            weightedAverageHeight[1] = weightedAverageHeight[1] + 1.0f;
        }
    }

    private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return this.getFluidHeight(world, fluid, pos, blockState, blockState.getFluidState());
    }

    private float getFluidHeight(BlockRenderView world, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.matchesType(fluidState.getFluid())) {
            BlockState blockState2 = world.getBlockState(pos.up());
            if (fluid.matchesType(blockState2.getFluidState().getFluid())) {
                return 1.0f;
            }
            return fluidState.getHeight();
        }
        if (!blockState.isSolid()) {
            return 0.0f;
        }
        return -1.0f;
    }

    private void vertex(VertexConsumer vertexConsumer, float x, float y, float z, float red, float green, float blue, float u, float v, int light) {
        vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0f).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f);
    }

    private int getLight(BlockRenderView world, BlockPos pos) {
        int i = WorldRenderer.getLightmapCoordinates(world, pos);
        int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int m = i >> 16 & 0xFF;
        int n = j >> 16 & 0xFF;
        return (k > l ? k : l) | (m > n ? m : n) << 16;
    }
}
