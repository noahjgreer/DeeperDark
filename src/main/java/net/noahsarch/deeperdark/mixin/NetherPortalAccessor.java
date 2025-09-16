package net.noahsarch.deeperdark.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NetherPortal.class)
public interface NetherPortalAccessor {
    @Accessor("lowerCorner")
    BlockPos getLowerCorner();
}
