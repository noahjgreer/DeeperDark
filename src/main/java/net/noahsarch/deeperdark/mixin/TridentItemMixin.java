package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    // Vanilla guards riptide with !player.isPassenger() so it doesn't activate on horses/boats.
    // When riding another player we DO want riptide to work, so pretend the player isn't a
    // passenger in that specific call.
    @Redirect(
        method = "releaseUsing",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isPassenger()Z")
    )
    private boolean deeperdark$allowRiptideOnPlayerVehicle(Player player) {
        if (player.getVehicle() instanceof Player) return false;
        return player.isPassenger();
    }
}
