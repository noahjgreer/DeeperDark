/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.block.Oxidizable
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.block.OxidizableDoorBlock
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/*
 * Exception performing whole class analysis ignored.
 */
public class OxidizableDoorBlock
extends DoorBlock
implements Oxidizable {
    public static final MapCodec<OxidizableDoorBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::getBlockSetType), (App)Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableDoorBlock::getDegradationLevel), (App)OxidizableDoorBlock.createSettingsCodec()).apply((Applicative)instance, OxidizableDoorBlock::new));
    private final Oxidizable.OxidationLevel oxidationLevel;

    public MapCodec<OxidizableDoorBlock> getCodec() {
        return CODEC;
    }

    public OxidizableDoorBlock(BlockSetType type, Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(type, settings);
        this.oxidationLevel = oxidationLevel;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get((Property)DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
            this.tickDegradation(state, world, pos, random);
        }
    }

    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock((Block)state.getBlock()).isPresent();
    }

    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    public /* synthetic */ Enum getDegradationLevel() {
        return this.getDegradationLevel();
    }
}

