/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.block.BlockModelRenderer
 *  net.minecraft.client.render.block.BlockModelRenderer$1
 *  net.minecraft.client.render.block.BlockModelRenderer$AmbientOcclusionCalculator
 *  net.minecraft.client.render.block.BlockModelRenderer$BrightnessCache
 *  net.minecraft.client.render.block.BlockModelRenderer$LightmapCache
 *  net.minecraft.client.render.block.BlockModelRenderer$NeighborOrientation
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.BlockStateModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.util.crash.CrashException
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockRenderView
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.HeightLimitView
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.block;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.render.FabricBlockModelRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import org.joml.Vector3fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class BlockModelRenderer
implements FabricBlockModelRenderer {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockColors colors;
    private static final int BRIGHTNESS_CACHE_MAX_SIZE = 100;
    static final ThreadLocal<BrightnessCache> BRIGHTNESS_CACHE = ThreadLocal.withInitial(BrightnessCache::new);

    public BlockModelRenderer(BlockColors colors) {
        this.colors = colors;
    }

    public void render(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
        if (parts.isEmpty()) {
            return;
        }
        boolean bl = MinecraftClient.isAmbientOcclusionEnabled() && state.getLuminance() == 0 && parts.getFirst().useAmbientOcclusion();
        matrices.translate(state.getModelOffset(pos));
        try {
            if (bl) {
                this.renderSmooth(world, parts, state, pos, matrices, vertexConsumer, cull, overlay);
            } else {
                this.renderFlat(world, parts, state, pos, matrices, vertexConsumer, cull, overlay);
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create((Throwable)throwable, (String)"Tesselating block model");
            CrashReportSection crashReportSection = crashReport.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo((CrashReportSection)crashReportSection, (HeightLimitView)world, (BlockPos)pos, (BlockState)state);
            crashReportSection.add("Using AO", (Object)bl);
            throw new CrashException(crashReport);
        }
    }

    private static boolean shouldDrawFace(BlockRenderView world, BlockState state, boolean cull, Direction side, BlockPos pos) {
        if (!cull) {
            return true;
        }
        BlockState blockState = world.getBlockState(pos);
        return Block.shouldDrawSide((BlockState)state, (BlockState)blockState, (Direction)side);
    }

    public void renderSmooth(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
        AmbientOcclusionCalculator ambientOcclusionCalculator = new AmbientOcclusionCalculator();
        int i = 0;
        int j = 0;
        for (BlockModelPart blockModelPart : parts) {
            for (Direction direction : DIRECTIONS) {
                List list;
                boolean bl2;
                int k = 1 << direction.ordinal();
                boolean bl = (i & k) == 1;
                boolean bl3 = bl2 = (j & k) == 1;
                if (bl && !bl2 || (list = blockModelPart.getQuads(direction)).isEmpty()) continue;
                if (!bl) {
                    bl2 = BlockModelRenderer.shouldDrawFace((BlockRenderView)world, (BlockState)state, (boolean)cull, (Direction)direction, (BlockPos)ambientOcclusionCalculator.pos.set((Vec3i)pos, direction));
                    i |= k;
                    if (bl2) {
                        j |= k;
                    }
                }
                if (!bl2) continue;
                this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list, ambientOcclusionCalculator, overlay);
            }
            List list2 = blockModelPart.getQuads(null);
            if (list2.isEmpty()) continue;
            this.renderQuadsSmooth(world, state, pos, matrices, vertexConsumer, list2, ambientOcclusionCalculator, overlay);
        }
    }

    public void renderFlat(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay) {
        LightmapCache lightmapCache = new LightmapCache();
        int i = 0;
        int j = 0;
        for (BlockModelPart blockModelPart : parts) {
            for (Direction direction : DIRECTIONS) {
                List list;
                boolean bl2;
                int k = 1 << direction.ordinal();
                boolean bl = (i & k) == 1;
                boolean bl3 = bl2 = (j & k) == 1;
                if (bl && !bl2 || (list = blockModelPart.getQuads(direction)).isEmpty()) continue;
                BlockPos.Mutable blockPos = lightmapCache.pos.set((Vec3i)pos, direction);
                if (!bl) {
                    bl2 = BlockModelRenderer.shouldDrawFace((BlockRenderView)world, (BlockState)state, (boolean)cull, (Direction)direction, (BlockPos)blockPos);
                    i |= k;
                    if (bl2) {
                        j |= k;
                    }
                }
                if (!bl2) continue;
                int l = lightmapCache.brightnessCache.getInt(state, world, (BlockPos)blockPos);
                this.renderQuadsFlat(world, state, pos, l, overlay, false, matrices, vertexConsumer, list, lightmapCache);
            }
            List list2 = blockModelPart.getQuads(null);
            if (list2.isEmpty()) continue;
            this.renderQuadsFlat(world, state, pos, -1, overlay, true, matrices, vertexConsumer, list2, lightmapCache);
        }
    }

    private void renderQuadsSmooth(BlockRenderView world, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, AmbientOcclusionCalculator ambientOcclusionCalculator, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            BlockModelRenderer.getQuadDimensions((BlockRenderView)world, (BlockState)state, (BlockPos)pos, (BakedQuad)bakedQuad, (LightmapCache)ambientOcclusionCalculator);
            ambientOcclusionCalculator.apply(world, state, pos, bakedQuad.face(), bakedQuad.shade());
            this.renderQuad(world, state, pos, vertexConsumer, matrices.peek(), bakedQuad, (LightmapCache)ambientOcclusionCalculator, overlay);
        }
    }

    private void renderQuad(BlockRenderView world, BlockState state, BlockPos pos, VertexConsumer vertexConsumer, MatrixStack.Entry matrixEntry, BakedQuad quad, LightmapCache lightmap, int light) {
        float h;
        float g;
        float f;
        int i = quad.tintIndex();
        if (i != -1) {
            int j;
            if (lightmap.lastTintIndex == i) {
                j = lightmap.colorOfLastTintIndex;
            } else {
                j = this.colors.getColor(state, world, pos, i);
                lightmap.lastTintIndex = i;
                lightmap.colorOfLastTintIndex = j;
            }
            f = ColorHelper.getRedFloat((int)j);
            g = ColorHelper.getGreenFloat((int)j);
            h = ColorHelper.getBlueFloat((int)j);
        } else {
            f = 1.0f;
            g = 1.0f;
            h = 1.0f;
        }
        vertexConsumer.quad(matrixEntry, quad, lightmap.fs, f, g, h, 1.0f, lightmap.is, light);
    }

    private static void getQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, BakedQuad bakedQuad, LightmapCache lightmapCache) {
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (int l = 0; l < 4; ++l) {
            Vector3fc vector3fc = bakedQuad.getPosition(l);
            float m = vector3fc.x();
            float n = vector3fc.y();
            float o = vector3fc.z();
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }
        if (lightmapCache instanceof AmbientOcclusionCalculator) {
            AmbientOcclusionCalculator ambientOcclusionCalculator = (AmbientOcclusionCalculator)lightmapCache;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.WEST.index] = f;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.EAST.index] = i;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.DOWN.index] = g;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.UP.index] = j;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.NORTH.index] = h;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.SOUTH.index] = k;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_WEST.index] = 1.0f - f;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_EAST.index] = 1.0f - i;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_DOWN.index] = 1.0f - g;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_UP.index] = 1.0f - j;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_NORTH.index] = 1.0f - h;
            ambientOcclusionCalculator.field_58158[NeighborOrientation.FLIP_SOUTH.index] = 1.0f - k;
        }
        float p = 1.0E-4f;
        float q = 0.9999f;
        lightmapCache.field_58161 = switch (1.field_4197[bakedQuad.face().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1, 2 -> {
                if (f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f) {
                    yield true;
                }
                yield false;
            }
            case 3, 4 -> {
                if (f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f) {
                    yield true;
                }
                yield false;
            }
            case 5, 6 -> g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f;
        };
        lightmapCache.field_58160 = switch (1.field_4197[bakedQuad.face().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> {
                if (g == j && (g < 1.0E-4f || state.isFullCube((BlockView)world, pos))) {
                    yield true;
                }
                yield false;
            }
            case 2 -> {
                if (g == j && (j > 0.9999f || state.isFullCube((BlockView)world, pos))) {
                    yield true;
                }
                yield false;
            }
            case 3 -> {
                if (h == k && (h < 1.0E-4f || state.isFullCube((BlockView)world, pos))) {
                    yield true;
                }
                yield false;
            }
            case 4 -> {
                if (h == k && (k > 0.9999f || state.isFullCube((BlockView)world, pos))) {
                    yield true;
                }
                yield false;
            }
            case 5 -> {
                if (f == i && (f < 1.0E-4f || state.isFullCube((BlockView)world, pos))) {
                    yield true;
                }
                yield false;
            }
            case 6 -> f == i && (i > 0.9999f || state.isFullCube((BlockView)world, pos));
        };
    }

    private void renderQuadsFlat(BlockRenderView world, BlockState state, BlockPos pos, int light, int overlay, boolean useWorldLight, MatrixStack matrices, VertexConsumer vertexConsumer, List<BakedQuad> quads, LightmapCache lightmap) {
        for (BakedQuad bakedQuad : quads) {
            float f;
            if (useWorldLight) {
                BlockModelRenderer.getQuadDimensions((BlockRenderView)world, (BlockState)state, (BlockPos)pos, (BakedQuad)bakedQuad, (LightmapCache)lightmap);
                BlockPos blockPos = lightmap.field_58160 ? lightmap.pos.set((Vec3i)pos, bakedQuad.face()) : pos;
                light = lightmap.brightnessCache.getInt(state, world, blockPos);
            }
            lightmap.fs[0] = f = world.getBrightness(bakedQuad.face(), bakedQuad.shade());
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
        for (BlockModelPart blockModelPart : model.getParts(Random.create((long)42L))) {
            for (Direction direction : DIRECTIONS) {
                BlockModelRenderer.renderQuads((MatrixStack.Entry)entry, (VertexConsumer)vertexConsumer, (float)red, (float)green, (float)blue, (List)blockModelPart.getQuads(direction), (int)light, (int)overlay);
            }
            BlockModelRenderer.renderQuads((MatrixStack.Entry)entry, (VertexConsumer)vertexConsumer, (float)red, (float)green, (float)blue, (List)blockModelPart.getQuads(null), (int)light, (int)overlay);
        }
    }

    private static void renderQuads(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float red, float green, float blue, List<BakedQuad> quads, int light, int overlay) {
        for (BakedQuad bakedQuad : quads) {
            float h;
            float g;
            float f;
            if (bakedQuad.hasTint()) {
                f = MathHelper.clamp((float)red, (float)0.0f, (float)1.0f);
                g = MathHelper.clamp((float)green, (float)0.0f, (float)1.0f);
                h = MathHelper.clamp((float)blue, (float)0.0f, (float)1.0f);
            } else {
                f = 1.0f;
                g = 1.0f;
                h = 1.0f;
            }
            vertexConsumer.quad(entry, bakedQuad, f, g, h, 1.0f, light, overlay);
        }
    }

    public static void enableBrightnessCache() {
        ((BrightnessCache)BRIGHTNESS_CACHE.get()).enable();
    }

    public static void disableBrightnessCache() {
        ((BrightnessCache)BRIGHTNESS_CACHE.get()).disable();
    }
}

