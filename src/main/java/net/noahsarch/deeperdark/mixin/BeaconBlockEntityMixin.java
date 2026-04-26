package net.noahsarch.deeperdark.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    /**
     * Replaces the sphere-based AABB with a full-height cylinder so beacon effects
     * apply to players at any Y level within the horizontal range.
     *
     * Original: inflate(range) then expandTowards upward only — caps Y to beacon.y ± range below.
     * Replaced: same XZ bounds but spanning the full world build height.
     */
    @WrapOperation(
        method = "applyEffects",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/phys/AABB;expandTowards(DDD)Lnet/minecraft/world/phys/AABB;")
    )
    private static AABB deeperdark$cylinderAABB(
        AABB inflated, double dx, double dy, double dz,
        Operation<AABB> original,
        @Local(argsOnly = true) Level level
    ) {
        double worldMinY = level.getMinY();
        double worldMaxY = level.getMinY() + level.getHeight();
        return new AABB(inflated.minX, worldMinY, inflated.minZ,
                        inflated.maxX, worldMaxY, inflated.maxZ);
    }
}
