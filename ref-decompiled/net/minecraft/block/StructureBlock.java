/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.OperatorBlock
 *  net.minecraft.block.StructureBlock
 *  net.minecraft.block.StructureBlock$1
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.StructureBlockBlockEntity
 *  net.minecraft.block.enums.StructureBlockMode
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.EnumProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.OperatorBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jspecify.annotations.Nullable;

public class StructureBlock
extends BlockWithEntity
implements OperatorBlock {
    public static final MapCodec<StructureBlock> CODEC = StructureBlock.createCodec(StructureBlock::new);
    public static final EnumProperty<StructureBlockMode> MODE = Properties.STRUCTURE_BLOCK_MODE;

    public MapCodec<StructureBlock> getCodec() {
        return CODEC;
    }

    public StructureBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)MODE, (Comparable)StructureBlockMode.LOAD));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StructureBlockBlockEntity(pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StructureBlockBlockEntity) {
            return ((StructureBlockBlockEntity)blockEntity).openScreen(player) ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        return ActionResult.PASS;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (world.isClient()) {
            return;
        }
        if (placer != null && (blockEntity = world.getBlockEntity(pos)) instanceof StructureBlockBlockEntity) {
            ((StructureBlockBlockEntity)blockEntity).setAuthor(placer);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{MODE});
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!(world instanceof ServerWorld)) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof StructureBlockBlockEntity)) {
            return;
        }
        StructureBlockBlockEntity structureBlockBlockEntity = (StructureBlockBlockEntity)blockEntity;
        boolean bl = world.isReceivingRedstonePower(pos);
        boolean bl2 = structureBlockBlockEntity.isPowered();
        if (bl && !bl2) {
            structureBlockBlockEntity.setPowered(true);
            this.doAction((ServerWorld)world, structureBlockBlockEntity);
        } else if (!bl && bl2) {
            structureBlockBlockEntity.setPowered(false);
        }
    }

    private void doAction(ServerWorld world, StructureBlockBlockEntity blockEntity) {
        switch (1.field_11587[blockEntity.getMode().ordinal()]) {
            case 1: {
                blockEntity.saveStructure(false);
                break;
            }
            case 2: {
                blockEntity.loadAndPlaceStructure(world);
                break;
            }
            case 3: {
                blockEntity.unloadStructure();
                break;
            }
            case 4: {
                break;
            }
        }
    }
}

