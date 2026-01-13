/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.EndPortalBlock
 *  net.minecraft.block.Portal
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.EndPortalBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.s2c.play.PositionFlag
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.ServerWorldAccess
 *  net.minecraft.world.TeleportTarget
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProperties$SpawnPoint
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.gen.feature.EndPlatformFeature
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Portal;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import org.jspecify.annotations.Nullable;

public class EndPortalBlock
extends BlockWithEntity
implements Portal {
    public static final MapCodec<EndPortalBlock> CODEC = EndPortalBlock.createCodec(EndPortalBlock::new);
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)6.0, (double)12.0);

    public MapCodec<EndPortalBlock> getCodec() {
        return CODEC;
    }

    public EndPortalBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EndPortalBlockEntity(pos, state);
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        return state.getOutlineShape(world, pos);
    }

    /*
     * Enabled aggressive block sorting
     */
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (!entity.canUsePortals(false)) return;
        if (!world.isClient() && world.getRegistryKey() == World.END && entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
            if (!serverPlayerEntity.seenCredits) {
                serverPlayerEntity.detachForDimensionChange();
                return;
            }
        }
        entity.tryUsePortal((Portal)this, pos);
    }

    public @Nullable TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        Set set;
        float g;
        float f;
        WorldProperties.SpawnPoint spawnPoint = world.getSpawnPoint();
        RegistryKey registryKey = world.getRegistryKey();
        boolean bl = registryKey == World.END;
        RegistryKey registryKey2 = bl ? spawnPoint.getDimension() : World.END;
        BlockPos blockPos = bl ? spawnPoint.getPos() : ServerWorld.END_SPAWN_POS;
        ServerWorld serverWorld = world.getServer().getWorld(registryKey2);
        if (serverWorld == null) {
            return null;
        }
        Vec3d vec3d = blockPos.toBottomCenterPos();
        if (!bl) {
            EndPlatformFeature.generate((ServerWorldAccess)serverWorld, (BlockPos)BlockPos.ofFloored((Position)vec3d).down(), (boolean)true);
            f = Direction.WEST.getPositiveHorizontalDegrees();
            g = 0.0f;
            set = PositionFlag.combine((Set[])new Set[]{PositionFlag.DELTA, Set.of(PositionFlag.X_ROT)});
            if (entity instanceof ServerPlayerEntity) {
                vec3d = vec3d.subtract(0.0, 1.0, 0.0);
            }
        } else {
            f = spawnPoint.yaw();
            g = spawnPoint.pitch();
            set = PositionFlag.combine((Set[])new Set[]{PositionFlag.DELTA, PositionFlag.ROT});
            if (entity instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
                return serverPlayerEntity.getRespawnTarget(false, TeleportTarget.NO_OP);
            }
            vec3d = entity.getWorldSpawnPos(serverWorld, blockPos).toBottomCenterPos();
        }
        return new TeleportTarget(serverWorld, vec3d, Vec3d.ZERO, f, g, set, TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET));
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        double d = (double)pos.getX() + random.nextDouble();
        double e = (double)pos.getY() + 0.8;
        double f = (double)pos.getZ() + random.nextDouble();
        world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return ItemStack.EMPTY;
    }

    protected boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }

    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }
}

