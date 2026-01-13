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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
static class BlockModelRenderer.AmbientOcclusionCalculator
extends BlockModelRenderer.LightmapCache {
    final float[] field_58158 = new float[BlockModelRenderer.NeighborOrientation.SIZE];

    public void apply(BlockRenderView world, BlockState state, BlockPos pos, Direction direction, boolean bl) {
        float x;
        int u;
        float t;
        int s;
        float r;
        int q;
        float p;
        int o;
        float n;
        BlockState blockState9;
        boolean bl5;
        BlockPos blockPos = this.field_58160 ? pos.offset(direction) : pos;
        BlockModelRenderer.NeighborData neighborData = BlockModelRenderer.NeighborData.getData(direction);
        BlockPos.Mutable mutable = this.pos;
        mutable.set((Vec3i)blockPos, neighborData.faces[0]);
        BlockState blockState = world.getBlockState(mutable);
        int i = this.brightnessCache.getInt(blockState, world, mutable);
        float f = this.brightnessCache.getFloat(blockState, world, mutable);
        mutable.set((Vec3i)blockPos, neighborData.faces[1]);
        BlockState blockState2 = world.getBlockState(mutable);
        int j = this.brightnessCache.getInt(blockState2, world, mutable);
        float g = this.brightnessCache.getFloat(blockState2, world, mutable);
        mutable.set((Vec3i)blockPos, neighborData.faces[2]);
        BlockState blockState3 = world.getBlockState(mutable);
        int k = this.brightnessCache.getInt(blockState3, world, mutable);
        float h = this.brightnessCache.getFloat(blockState3, world, mutable);
        mutable.set((Vec3i)blockPos, neighborData.faces[3]);
        BlockState blockState4 = world.getBlockState(mutable);
        int l = this.brightnessCache.getInt(blockState4, world, mutable);
        float m = this.brightnessCache.getFloat(blockState4, world, mutable);
        BlockState blockState5 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[0]).move(direction));
        boolean bl2 = !blockState5.shouldBlockVision(world, mutable) || blockState5.getOpacity() == 0;
        BlockState blockState6 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[1]).move(direction));
        boolean bl3 = !blockState6.shouldBlockVision(world, mutable) || blockState6.getOpacity() == 0;
        BlockState blockState7 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[2]).move(direction));
        boolean bl4 = !blockState7.shouldBlockVision(world, mutable) || blockState7.getOpacity() == 0;
        BlockState blockState8 = world.getBlockState(mutable.set((Vec3i)blockPos, neighborData.faces[3]).move(direction));
        boolean bl6 = bl5 = !blockState8.shouldBlockVision(world, mutable) || blockState8.getOpacity() == 0;
        if (bl4 || bl2) {
            mutable.set((Vec3i)blockPos, neighborData.faces[0]).move(neighborData.faces[2]);
            blockState9 = world.getBlockState(mutable);
            n = this.brightnessCache.getFloat(blockState9, world, mutable);
            o = this.brightnessCache.getInt(blockState9, world, mutable);
        } else {
            n = f;
            o = i;
        }
        if (bl5 || bl2) {
            mutable.set((Vec3i)blockPos, neighborData.faces[0]).move(neighborData.faces[3]);
            blockState9 = world.getBlockState(mutable);
            p = this.brightnessCache.getFloat(blockState9, world, mutable);
            q = this.brightnessCache.getInt(blockState9, world, mutable);
        } else {
            p = f;
            q = i;
        }
        if (bl4 || bl3) {
            mutable.set((Vec3i)blockPos, neighborData.faces[1]).move(neighborData.faces[2]);
            blockState9 = world.getBlockState(mutable);
            r = this.brightnessCache.getFloat(blockState9, world, mutable);
            s = this.brightnessCache.getInt(blockState9, world, mutable);
        } else {
            r = f;
            s = i;
        }
        if (bl5 || bl3) {
            mutable.set((Vec3i)blockPos, neighborData.faces[1]).move(neighborData.faces[3]);
            blockState9 = world.getBlockState(mutable);
            t = this.brightnessCache.getFloat(blockState9, world, mutable);
            u = this.brightnessCache.getInt(blockState9, world, mutable);
        } else {
            t = f;
            u = i;
        }
        int v = this.brightnessCache.getInt(state, world, pos);
        mutable.set((Vec3i)pos, direction);
        BlockState blockState10 = world.getBlockState(mutable);
        if (this.field_58160 || !blockState10.isOpaqueFullCube()) {
            v = this.brightnessCache.getInt(blockState10, world, mutable);
        }
        float w = this.field_58160 ? this.brightnessCache.getFloat(world.getBlockState(blockPos), world, blockPos) : this.brightnessCache.getFloat(world.getBlockState(pos), world, pos);
        BlockModelRenderer.Translation translation = BlockModelRenderer.Translation.getTranslations(direction);
        if (!this.field_58161 || !neighborData.nonCubicWeight) {
            x = (m + f + p + w) * 0.25f;
            y = (h + f + n + w) * 0.25f;
            z = (h + g + r + w) * 0.25f;
            aa = (m + g + t + w) * 0.25f;
            this.is[translation.firstCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, i, q, v);
            this.is[translation.secondCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, i, o, v);
            this.is[translation.thirdCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, j, s, v);
            this.is[translation.fourthCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, j, u, v);
            this.fs[translation.firstCorner] = x;
            this.fs[translation.secondCorner] = y;
            this.fs[translation.thirdCorner] = z;
            this.fs[translation.fourthCorner] = aa;
        } else {
            x = (m + f + p + w) * 0.25f;
            y = (h + f + n + w) * 0.25f;
            z = (h + g + r + w) * 0.25f;
            aa = (m + g + t + w) * 0.25f;
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
            this.fs[translation.firstCorner] = Math.clamp(x * ab + y * ac + z * ad + aa * ae, 0.0f, 1.0f);
            this.fs[translation.secondCorner] = Math.clamp(x * af + y * ag + z * ah + aa * ai, 0.0f, 1.0f);
            this.fs[translation.thirdCorner] = Math.clamp(x * aj + y * ak + z * al + aa * am, 0.0f, 1.0f);
            this.fs[translation.fourthCorner] = Math.clamp(x * an + y * ao + z * ap + aa * aq, 0.0f, 1.0f);
            int ar = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, i, q, v);
            int as = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, i, o, v);
            int at = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(k, j, s, v);
            int au = BlockModelRenderer.AmbientOcclusionCalculator.getAmbientOcclusionBrightness(l, j, u, v);
            this.is[translation.firstCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getBrightness(ar, as, at, au, ab, ac, ad, ae);
            this.is[translation.secondCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getBrightness(ar, as, at, au, af, ag, ah, ai);
            this.is[translation.thirdCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getBrightness(ar, as, at, au, aj, ak, al, am);
            this.is[translation.fourthCorner] = BlockModelRenderer.AmbientOcclusionCalculator.getBrightness(ar, as, at, au, an, ao, ap, aq);
        }
        x = world.getBrightness(direction, bl);
        int av = 0;
        while (av < this.fs.length) {
            int n2 = av++;
            this.fs[n2] = this.fs[n2] * x;
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
        return i + j + k + l >> 2 & 0xFF00FF;
    }

    private static int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
        int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
        int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
        return n << 16 | o;
    }
}
