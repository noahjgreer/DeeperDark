/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.HorizontalFacingBlock
 *  net.minecraft.block.VaultBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.VaultBlockEntity
 *  net.minecraft.block.entity.VaultBlockEntity$Client
 *  net.minecraft.block.entity.VaultBlockEntity$Server
 *  net.minecraft.block.enums.VaultState
 *  net.minecraft.block.vault.VaultClientData
 *  net.minecraft.block.vault.VaultConfig
 *  net.minecraft.block.vault.VaultServerData
 *  net.minecraft.block.vault.VaultSharedData
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.BlockMirror
 *  net.minecraft.util.BlockRotation
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.enums.VaultState;
import net.minecraft.block.vault.VaultClientData;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class VaultBlock
extends BlockWithEntity {
    public static final MapCodec<VaultBlock> CODEC = VaultBlock.createCodec(VaultBlock::new);
    public static final Property<VaultState> VAULT_STATE = Properties.VAULT_STATE;
    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty OMINOUS = Properties.OMINOUS;

    public MapCodec<VaultBlock> getCodec() {
        return CODEC;
    }

    public VaultBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)FACING, (Comparable)Direction.NORTH)).with(VAULT_STATE, (Comparable)VaultState.INACTIVE)).with((Property)OMINOUS, (Comparable)Boolean.valueOf(false)));
    }

    public ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isEmpty() || state.get(VAULT_STATE) != VaultState.ACTIVE) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            BlockEntity blockEntity = serverWorld.getBlockEntity(pos);
            if (!(blockEntity instanceof VaultBlockEntity)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            VaultBlockEntity vaultBlockEntity = (VaultBlockEntity)blockEntity;
            VaultBlockEntity.Server.tryUnlock((ServerWorld)serverWorld, (BlockPos)pos, (BlockState)state, (VaultConfig)vaultBlockEntity.getConfig(), (VaultServerData)vaultBlockEntity.getServerData(), (VaultSharedData)vaultBlockEntity.getSharedData(), (PlayerEntity)player, (ItemStack)stack);
        }
        return ActionResult.SUCCESS_SERVER;
    }

    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VaultBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, VAULT_STATE, OMINOUS});
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        BlockEntityTicker blockEntityTicker;
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            blockEntityTicker = VaultBlock.validateTicker(type, (BlockEntityType)BlockEntityType.VAULT, (worldx, pos, statex, blockEntity) -> VaultBlockEntity.Server.tick((ServerWorld)serverWorld, (BlockPos)pos, (BlockState)statex, (VaultConfig)blockEntity.getConfig(), (VaultServerData)blockEntity.getServerData(), (VaultSharedData)blockEntity.getSharedData()));
        } else {
            blockEntityTicker = VaultBlock.validateTicker(type, (BlockEntityType)BlockEntityType.VAULT, (worldx, pos, statex, blockEntity) -> VaultBlockEntity.Client.tick((World)worldx, (BlockPos)pos, (BlockState)statex, (VaultClientData)blockEntity.getClientData(), (VaultSharedData)blockEntity.getSharedData()));
        }
        return blockEntityTicker;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with((Property)FACING, (Comparable)ctx.getHorizontalPlayerFacing().getOpposite());
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with((Property)FACING, (Comparable)rotation.rotate((Direction)state.get((Property)FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get((Property)FACING)));
    }
}

