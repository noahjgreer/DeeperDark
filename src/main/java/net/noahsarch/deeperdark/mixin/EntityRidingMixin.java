package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(net.minecraft.world.entity.Entity.class)
public class EntityRidingMixin {

    // Entity.startRiding() has a server-side guard: if the vehicle's EntityType has
    // canSerialize() == false it returns false immediately. EntityType.PLAYER uses noSave()
    // so canSerialize() == false, which silently blocks player-on-player riding server-side.
    // Redirect that call so that the Player entity type passes the check.
    @Redirect(
        method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z")
    )
    private boolean deeperdark$allowPlayerVehicle(EntityType<?> type) {
        return type.canSerialize() || type == EntityType.PLAYER;
    }
}
