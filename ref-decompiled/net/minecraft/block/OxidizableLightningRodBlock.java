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
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.LightningRodBlock
 *  net.minecraft.block.Oxidizable
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.block.OxidizableLightningRodBlock
 *  net.minecraft.server.world.ServerWorld
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
import net.minecraft.block.BlockState;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/*
 * Exception performing whole class analysis ignored.
 */
public class OxidizableLightningRodBlock
extends LightningRodBlock
implements Oxidizable {
    public static final MapCodec<OxidizableLightningRodBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(OxidizableLightningRodBlock::getDegradationLevel), (App)OxidizableLightningRodBlock.createSettingsCodec()).apply((Applicative)instance, OxidizableLightningRodBlock::new));
    private final Oxidizable.OxidationLevel oxidationLevel;

    public MapCodec<OxidizableLightningRodBlock> getCodec() {
        return CODEC;
    }

    public OxidizableLightningRodBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(settings);
        this.oxidationLevel = oxidationLevel;
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
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

