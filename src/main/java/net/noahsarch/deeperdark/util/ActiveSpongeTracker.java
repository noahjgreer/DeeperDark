package net.noahsarch.deeperdark.util;

import net.minecraft.core.BlockPos;
import java.util.Set;

public interface ActiveSpongeTracker {
    public Set<BlockPos> getActiveSponges();
    public void addActiveSponge(BlockPos pos);
    public void removeActiveSponge(BlockPos pos);
}

