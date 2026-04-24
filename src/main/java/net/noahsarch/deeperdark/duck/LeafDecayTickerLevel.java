package net.noahsarch.deeperdark.duck;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.ticks.LevelTicks;

public interface LeafDecayTickerLevel {
    LevelTicks<Block> deeperdark$getLeafDecayTicks();
}
