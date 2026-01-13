/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.EndGatewayBlock
 *  net.minecraft.block.Portal
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.EndGatewayBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.s2c.play.PositionFlag
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.TeleportTarget
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Portal;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class EndGatewayBlock
extends BlockWithEntity
implements Portal {
    public static final MapCodec<EndGatewayBlock> CODEC = EndGatewayBlock.createCodec(EndGatewayBlock::new);

    public MapCodec<EndGatewayBlock> getCodec() {
        return CODEC;
    }

    public EndGatewayBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EndGatewayBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return EndGatewayBlock.validateTicker(type, (BlockEntityType)BlockEntityType.END_GATEWAY, (BlockEntityTicker)(world.isClient() ? EndGatewayBlockEntity::clientTick : EndGatewayBlockEntity::serverTick));
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof EndGatewayBlockEntity)) {
            return;
        }
        int i = ((EndGatewayBlockEntity)blockEntity).getDrawnSidesCount();
        for (int j = 0; j < i; ++j) {
            double d = (double)pos.getX() + random.nextDouble();
            double e = (double)pos.getY() + random.nextDouble();
            double f = (double)pos.getZ() + random.nextDouble();
            double g = (random.nextDouble() - 0.5) * 0.5;
            double h = (random.nextDouble() - 0.5) * 0.5;
            double k = (random.nextDouble() - 0.5) * 0.5;
            int l = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
                f = (double)pos.getZ() + 0.5 + 0.25 * (double)l;
                k = random.nextFloat() * 2.0f * (float)l;
            } else {
                d = (double)pos.getX() + 0.5 + 0.25 * (double)l;
                g = random.nextFloat() * 2.0f * (float)l;
            }
            world.addParticleClient((ParticleEffect)ParticleTypes.PORTAL, d, e, f, g, h, k);
        }
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return ItemStack.EMPTY;
    }

    protected boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (entity.canUsePortals(false)) {
            EndGatewayBlockEntity endGatewayBlockEntity;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!world.isClient() && blockEntity instanceof EndGatewayBlockEntity && !(endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity).needsCooldownBeforeTeleporting()) {
                entity.tryUsePortal((Portal)this, pos);
                EndGatewayBlockEntity.startTeleportCooldown((World)world, (BlockPos)pos, (BlockState)state, (EndGatewayBlockEntity)endGatewayBlockEntity);
            }
        }
    }

    public @Nullable TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof EndGatewayBlockEntity)) {
            return null;
        }
        EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
        Vec3d vec3d = endGatewayBlockEntity.getOrCreateExitPortalPos(world, pos);
        if (vec3d == null) {
            return null;
        }
        if (entity instanceof EnderPearlEntity) {
            return new TeleportTarget(world, vec3d, Vec3d.ZERO, 0.0f, 0.0f, Set.of(), TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
        }
        return new TeleportTarget(world, vec3d, Vec3d.ZERO, 0.0f, 0.0f, PositionFlag.combine((Set[])new Set[]{PositionFlag.DELTA, PositionFlag.ROT}), TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}

