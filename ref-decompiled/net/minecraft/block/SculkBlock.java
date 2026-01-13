/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.ExperienceDroppingBlock
 *  net.minecraft.block.SculkBlock
 *  net.minecraft.block.SculkShriekerBlock
 *  net.minecraft.block.SculkSpreadable
 *  net.minecraft.block.entity.SculkSpreadManager
 *  net.minecraft.block.entity.SculkSpreadManager$Cursor
 *  net.minecraft.fluid.Fluid
 *  net.minecraft.fluid.Fluids
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.intprovider.ConstantIntProvider
 *  net.minecraft.util.math.intprovider.IntProvider
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.SculkShriekerBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;

/*
 * Exception performing whole class analysis ignored.
 */
public class SculkBlock
extends ExperienceDroppingBlock
implements SculkSpreadable {
    public static final MapCodec<SculkBlock> CODEC = SculkBlock.createCodec(SculkBlock::new);

    public MapCodec<SculkBlock> getCodec() {
        return CODEC;
    }

    public SculkBlock(AbstractBlock.Settings settings) {
        super((IntProvider)ConstantIntProvider.create((int)1), settings);
    }

    public int spread(SculkSpreadManager.Cursor cursor, WorldAccess world, BlockPos catalystPos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
        int i = cursor.getCharge();
        if (i == 0 || random.nextInt(spreadManager.getSpreadChance()) != 0) {
            return i;
        }
        BlockPos blockPos = cursor.getPos();
        boolean bl = blockPos.isWithinDistance((Vec3i)catalystPos, (double)spreadManager.getMaxDistance());
        if (bl || !SculkBlock.shouldNotDecay((WorldAccess)world, (BlockPos)blockPos)) {
            if (random.nextInt(spreadManager.getDecayChance()) != 0) {
                return i;
            }
            return i - (bl ? 1 : SculkBlock.getDecay((SculkSpreadManager)spreadManager, (BlockPos)blockPos, (BlockPos)catalystPos, (int)i));
        }
        int j = spreadManager.getExtraBlockChance();
        if (random.nextInt(j) < i) {
            BlockPos blockPos2 = blockPos.up();
            BlockState blockState = this.getExtraBlockState(world, blockPos2, random, spreadManager.isWorldGen());
            world.setBlockState(blockPos2, blockState, 3);
            world.playSound(null, blockPos, blockState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return Math.max(0, i - j);
    }

    private static int getDecay(SculkSpreadManager spreadManager, BlockPos cursorPos, BlockPos catalystPos, int charge) {
        int i = spreadManager.getMaxDistance();
        float f = MathHelper.square((float)((float)Math.sqrt(cursorPos.getSquaredDistance((Vec3i)catalystPos)) - (float)i));
        int j = MathHelper.square((int)(24 - i));
        float g = Math.min(1.0f, f / (float)j);
        return Math.max(1, (int)((float)charge * g * 0.5f));
    }

    private BlockState getExtraBlockState(WorldAccess world, BlockPos pos, Random random, boolean allowShrieker) {
        BlockState blockState = random.nextInt(11) == 0 ? (BlockState)Blocks.SCULK_SHRIEKER.getDefaultState().with((Property)SculkShriekerBlock.CAN_SUMMON, (Comparable)Boolean.valueOf(allowShrieker)) : Blocks.SCULK_SENSOR.getDefaultState();
        if (blockState.contains((Property)Properties.WATERLOGGED) && !world.getFluidState(pos).isEmpty()) {
            return (BlockState)blockState.with((Property)Properties.WATERLOGGED, (Comparable)Boolean.valueOf(true));
        }
        return blockState;
    }

    private static boolean shouldNotDecay(WorldAccess world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.up());
        if (!(blockState.isAir() || blockState.isOf(Blocks.WATER) && blockState.getFluidState().isOf((Fluid)Fluids.WATER))) {
            return false;
        }
        int i = 0;
        for (BlockPos blockPos : BlockPos.iterate((BlockPos)pos.add(-4, 0, -4), (BlockPos)pos.add(4, 2, 4))) {
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isOf(Blocks.SCULK_SENSOR) || blockState2.isOf(Blocks.SCULK_SHRIEKER)) {
                ++i;
            }
            if (i <= 2) continue;
            return false;
        }
        return true;
    }

    public boolean shouldConvertToSpreadable() {
        return false;
    }
}

