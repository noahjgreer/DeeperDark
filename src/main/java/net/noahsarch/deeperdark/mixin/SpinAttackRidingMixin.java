package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(LivingEntity.class)
public class SpinAttackRidingMixin {

    // The riptide spin attack sweeps the player's AABB between old and new positions and
    // attacks the first LivingEntity it finds, then immediately cancels the spin attack and
    // reverses velocity (scale -0.2). The riding partner is always inside this swept box.
    // Filter them out so the spin attack (and riptide flight) can complete normally.
    @ModifyVariable(method = "checkAutoSpinAttack", at = @At("STORE"), ordinal = 0)
    private List<Entity> deeperdark$skipRidingPartnersFromSpinAttack(List<Entity> entities) {
        LivingEntity self = (LivingEntity)(Object)this;
        if (!self.isPassenger() && !self.isVehicle()) return entities;
        return entities.stream()
                .filter(e -> !e.isPassengerOfSameVehicle(self))
                .collect(Collectors.toList());
    }
}
