package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;

import net.noahsarch.deeperdark.duck.ServerPlayerAccessor;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {

    @Shadow @org.spongepowered.asm.mixin.Final private MinecraftServer server;

    @Override
    public MinecraftServer deeperdark$getServer() {
        return this.server;
    }

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void deeperdark$copyFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        ServerLevel world = (ServerLevel) ((net.noahsarch.deeperdark.duck.EntityAccessor)self).deeperdark$getWorld();
        if (!alive && world != null && world.getGameRules().get(GameRules.KEEP_INVENTORY)) {
            // Halve the XP level
            self.experienceLevel = oldPlayer.experienceLevel / 2;
            // Keep progress
            self.experienceProgress = oldPlayer.experienceProgress;
        }
    }
}
