package net.noahsarch.deeperdark.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.util.ActiveSpongeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(World.class)
public class WorldMixin implements ActiveSpongeTracker {
    @Unique
    private final Set<BlockPos> deeperdark$activeSponges = new HashSet<>();

    @Override
    public Set<BlockPos> getActiveSponges() {
        return deeperdark$activeSponges;
    }

    @Override
    public void addActiveSponge(BlockPos pos) {
        // Store immutable pos because BlockPos passed might be mutable
        deeperdark$activeSponges.add(pos.toImmutable());
    }

    @Override
    public void removeActiveSponge(BlockPos pos) {
        deeperdark$activeSponges.remove(pos);
    }
}

