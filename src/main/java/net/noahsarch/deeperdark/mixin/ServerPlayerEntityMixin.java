package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void deeperdark$copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        // If player died (not alive) and keepInventory is on
        if (!alive && self.getServer() != null && self.getServer().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            // Halve the XP level
            self.experienceLevel = oldPlayer.experienceLevel / 2;
            // Keep progress
            self.experienceProgress = oldPlayer.experienceProgress;
        }
    }
}
