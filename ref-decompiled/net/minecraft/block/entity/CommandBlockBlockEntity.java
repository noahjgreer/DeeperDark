/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CommandBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.CommandBlockBlockEntity
 *  net.minecraft.block.entity.CommandBlockBlockEntity$Type
 *  net.minecraft.component.ComponentMap$Builder
 *  net.minecraft.component.ComponentsAccess
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.state.property.Property
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.text.Text
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.CommandBlockExecutor
 */
package net.minecraft.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.state.property.Property;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CommandBlockExecutor;

public class CommandBlockBlockEntity
extends BlockEntity {
    private static final boolean DEFAULT_POWERED = false;
    private static final boolean DEFAULT_AUTO = false;
    private static final boolean DEFAULT_CONDITION_MET = false;
    private boolean powered = false;
    private boolean auto = false;
    private boolean conditionMet = false;
    private final CommandBlockExecutor commandExecutor = new /* Unavailable Anonymous Inner Class!! */;

    public CommandBlockBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.COMMAND_BLOCK, pos, state);
    }

    protected void writeData(WriteView view) {
        super.writeData(view);
        this.commandExecutor.writeData(view);
        view.putBoolean("powered", this.isPowered());
        view.putBoolean("conditionMet", this.isConditionMet());
        view.putBoolean("auto", this.isAuto());
    }

    protected void readData(ReadView view) {
        super.readData(view);
        this.commandExecutor.readData(view);
        this.powered = view.getBoolean("powered", false);
        this.conditionMet = view.getBoolean("conditionMet", false);
        this.setAuto(view.getBoolean("auto", false));
    }

    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public boolean isAuto() {
        return this.auto;
    }

    public void setAuto(boolean auto) {
        boolean bl = this.auto;
        this.auto = auto;
        if (!bl && auto && !this.powered && this.world != null && this.getCommandBlockType() != Type.SEQUENCE) {
            this.scheduleAutoTick();
        }
    }

    public void updateCommandBlock() {
        Type type = this.getCommandBlockType();
        if (type == Type.AUTO && (this.powered || this.auto) && this.world != null) {
            this.scheduleAutoTick();
        }
    }

    private void scheduleAutoTick() {
        Block block = this.getCachedState().getBlock();
        if (block instanceof CommandBlock) {
            this.updateConditionMet();
            this.world.scheduleBlockTick(this.pos, block, 1);
        }
    }

    public boolean isConditionMet() {
        return this.conditionMet;
    }

    public boolean updateConditionMet() {
        this.conditionMet = true;
        if (this.isConditionalCommandBlock()) {
            BlockEntity blockEntity;
            BlockPos blockPos = this.pos.offset(((Direction)this.world.getBlockState(this.pos).get((Property)CommandBlock.FACING)).getOpposite());
            this.conditionMet = this.world.getBlockState(blockPos).getBlock() instanceof CommandBlock ? (blockEntity = this.world.getBlockEntity(blockPos)) instanceof CommandBlockBlockEntity && ((CommandBlockBlockEntity)blockEntity).getCommandExecutor().getSuccessCount() > 0 : false;
        }
        return this.conditionMet;
    }

    public Type getCommandBlockType() {
        BlockState blockState = this.getCachedState();
        if (blockState.isOf(Blocks.COMMAND_BLOCK)) {
            return Type.REDSTONE;
        }
        if (blockState.isOf(Blocks.REPEATING_COMMAND_BLOCK)) {
            return Type.AUTO;
        }
        if (blockState.isOf(Blocks.CHAIN_COMMAND_BLOCK)) {
            return Type.SEQUENCE;
        }
        return Type.REDSTONE;
    }

    public boolean isConditionalCommandBlock() {
        BlockState blockState = this.world.getBlockState(this.getPos());
        if (blockState.getBlock() instanceof CommandBlock) {
            return (Boolean)blockState.get((Property)CommandBlock.CONDITIONAL);
        }
        return false;
    }

    protected void readComponents(ComponentsAccess components) {
        super.readComponents(components);
        this.commandExecutor.setCustomName((Text)components.get(DataComponentTypes.CUSTOM_NAME));
    }

    protected void addComponents(ComponentMap.Builder builder) {
        super.addComponents(builder);
        builder.add(DataComponentTypes.CUSTOM_NAME, (Object)this.commandExecutor.getCustomName());
    }

    public void removeFromCopiedStackData(WriteView view) {
        super.removeFromCopiedStackData(view);
        view.remove("CustomName");
        view.remove("conditionMet");
        view.remove("powered");
    }
}

