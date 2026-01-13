/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.AbstractPressurePlateBlock
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.WeightedPressurePlateBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class WeightedPressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final MapCodec<WeightedPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.intRange((int)1, (int)1024).fieldOf("max_weight").forGetter(block -> block.weight), (App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType), (App)WeightedPressurePlateBlock.createSettingsCodec()).apply((Applicative)instance, WeightedPressurePlateBlock::new));
    public static final IntProperty POWER = Properties.POWER;
    private final int weight;

    public MapCodec<WeightedPressurePlateBlock> getCodec() {
        return CODEC;
    }

    public WeightedPressurePlateBlock(int weight, BlockSetType type, AbstractBlock.Settings settings) {
        super(settings, type);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with((Property)POWER, (Comparable)Integer.valueOf(0)));
        this.weight = weight;
    }

    protected int getRedstoneOutput(World world, BlockPos pos) {
        int i = Math.min(WeightedPressurePlateBlock.getEntityCount((World)world, (Box)BOX.offset(pos), Entity.class), this.weight);
        if (i > 0) {
            float f = (float)Math.min(this.weight, i) / (float)this.weight;
            return MathHelper.ceil((float)(f * 15.0f));
        }
        return 0;
    }

    protected int getRedstoneOutput(BlockState state) {
        return (Integer)state.get((Property)POWER);
    }

    protected BlockState setRedstoneOutput(BlockState state, int rsOut) {
        return (BlockState)state.with((Property)POWER, (Comparable)Integer.valueOf(rsOut));
    }

    protected int getTickRate() {
        return 10;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWER});
    }
}

