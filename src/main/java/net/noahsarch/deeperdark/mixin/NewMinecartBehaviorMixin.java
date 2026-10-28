package net.noahsarch.deeperdark.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.block.CopperRailDrag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NewMinecartBehavior.class)
public class NewMinecartBehaviorMixin {

    // Wrapping the call rather than AbstractMinecart's method also covers container carts, which override it.
    @WrapOperation(
        method = "calculateTrackSpeed",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;applyNaturalSlowdown(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
    )
    private Vec3 deeperdark$copperRailDrag(AbstractMinecart minecart, Vec3 movement, Operation<Vec3> original) {
        return CopperRailDrag.apply(minecart, original.call(minecart, movement));
    }
}
