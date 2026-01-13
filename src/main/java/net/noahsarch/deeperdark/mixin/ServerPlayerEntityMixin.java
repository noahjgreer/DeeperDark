package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.rule.GameRules;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;

import net.noahsarch.deeperdark.duck.ServerPlayerAccessor;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {

    @Shadow @org.spongepowered.asm.mixin.Final private MinecraftServer server;

    @Override
    public MinecraftServer deeperdark$getServer() {
        return this.server;
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void deeperdark$copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

        // If player died (not alive) and keepInventory is on
        ServerWorld world = (ServerWorld) ((net.noahsarch.deeperdark.duck.EntityAccessor)self).deeperdark$getWorld();
        if (!alive && world != null && world.getGameRules().getValue(GameRules.KEEP_INVENTORY)) {
            // Halve the XP level
            self.experienceLevel = oldPlayer.experienceLevel / 2;
            // Keep progress
            self.experienceProgress = oldPlayer.experienceProgress;
        }
    }
}
