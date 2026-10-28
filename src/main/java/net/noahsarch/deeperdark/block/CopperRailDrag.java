package net.noahsarch.deeperdark.block;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public final class CopperRailDrag {
    // On top of vanilla's 0.997 for a ridden cart: a cart coasts a bit over a third as far as on iron rails.
    private static final double KEEP = 0.995;

    private CopperRailDrag() {
    }

    public static Vec3 apply(AbstractMinecart minecart, Vec3 movement) {
        return minecart.level().getBlockState(minecart.getCurrentBlockPosOrRailBelow()).is(ModBlocks.COPPER_RAIL)
                ? movement.multiply(KEEP, 1.0, KEEP)
                : movement;
    }
}
