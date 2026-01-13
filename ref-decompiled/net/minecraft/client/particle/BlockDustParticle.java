/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BlockDustParticle
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.particle.BlockStateParticleEffect
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.BlockRenderView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockDustParticle
extends BillboardParticle {
    private final BillboardParticle.RenderType renderType;
    private final BlockPos blockPos;
    private final float sampleU;
    private final float sampleV;

    public BlockDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state) {
        this(world, x, y, z, velocityX, velocityY, velocityZ, state, BlockPos.ofFloored((double)x, (double)y, (double)z));
    }

    public BlockDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
        this.blockPos = blockPos;
        this.gravityStrength = 1.0f;
        this.red = 0.6f;
        this.green = 0.6f;
        this.blue = 0.6f;
        if (!state.isOf(Blocks.GRASS_BLOCK)) {
            int i = MinecraftClient.getInstance().getBlockColors().getColor(state, (BlockRenderView)world, blockPos, 0);
            this.red *= (float)(i >> 16 & 0xFF) / 255.0f;
            this.green *= (float)(i >> 8 & 0xFF) / 255.0f;
            this.blue *= (float)(i & 0xFF) / 255.0f;
        }
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
        this.renderType = this.sprite.getAtlasId().equals((Object)SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE) ? BillboardParticle.RenderType.BLOCK_ATLAS_TRANSLUCENT : BillboardParticle.RenderType.ITEM_ATLAS_TRANSLUCENT;
    }

    public BillboardParticle.RenderType getRenderType() {
        return this.renderType;
    }

    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f);
    }

    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f);
    }

    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f);
    }

    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f);
    }

    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        if (i == 0 && this.world.isChunkLoaded(this.blockPos)) {
            return WorldRenderer.getLightmapCoordinates((BlockRenderView)this.world, (BlockPos)this.blockPos);
        }
        return i;
    }

    static @Nullable BlockDustParticle create(BlockStateParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        BlockState blockState = parameters.getBlockState();
        if (blockState.isAir() || blockState.isOf(Blocks.MOVING_PISTON) || !blockState.hasBlockBreakParticles()) {
            return null;
        }
        return new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, blockState);
    }
}

