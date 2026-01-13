/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPressurePlateBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.PressurePlateBlock
 *  net.minecraft.block.PressurePlateBlock$1
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class PressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final MapCodec<PressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType), (App)PressurePlateBlock.createSettingsCodec()).apply((Applicative)instance, PressurePlateBlock::new));
    public static final BooleanProperty POWERED = Properties.POWERED;

    public MapCodec<PressurePlateBlock> getCodec() {
        return CODEC;
    }

    public PressurePlateBlock(BlockSetType type, AbstractBlock.Settings settings) {
        super(settings, type);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected int getRedstoneOutput(BlockState state) {
        return (Boolean)state.get((Property)POWERED) != false ? 15 : 0;
    }

    protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
        return (BlockState)state.with((Property)POWERED, (Comparable)Boolean.valueOf(rsOut > 0));
    }

    protected int getRedstoneOutput(World world, BlockPos pos) {
        Class<Entity> class_ = switch (1.field_11360[this.blockSetType.pressurePlateSensitivity().ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> Entity.class;
            case 2 -> LivingEntity.class;
        };
        return PressurePlateBlock.getEntityCount((World)world, (Box)BOX.offset(pos), class_) > 0 ? 15 : 0;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWERED});
    }
}

