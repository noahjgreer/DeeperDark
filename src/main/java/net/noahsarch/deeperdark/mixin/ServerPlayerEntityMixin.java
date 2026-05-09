package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;

import net.noahsarch.deeperdark.component.CollarFuelData;
import net.noahsarch.deeperdark.component.ModComponents;
import net.noahsarch.deeperdark.duck.CollarHolder;
import net.noahsarch.deeperdark.duck.ServerPlayerAccessor;
import net.noahsarch.deeperdark.item.CollarItem;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerAccessor {

    @Shadow @org.spongepowered.asm.mixin.Final private MinecraftServer server;

    @Override
    public MinecraftServer deeperdark$getServer() {
        return this.server;
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void deeperdark$cancelFuelProtectedDamage(ServerLevel level, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        ItemStack collar = ((CollarHolder) self).deeperdark$getCollarItem();
        if (collar.isEmpty() || !(collar.getItem() instanceof CollarItem ci) || ci.getTier() == null) return;
        CollarFuelData fuel = collar.getOrDefault(ModComponents.COLLAR_FUEL, CollarFuelData.EMPTY);

        if (source.is(DamageTypes.DROWN) && fuel.waterTicks() > 0) {
            cir.setReturnValue(false);
            return;
        }
        if (source.is(DamageTypeTags.IS_FIRE) && fuel.fireTicks() > 0) {
            // Ensure fire ticks are active so CollarEvents sees isOnFire() == true and drains fuel.
            // Without this, standing in a fire block may not set fire ticks before the damage call.
            if (self.getRemainingFireTicks() <= 0) {
                self.igniteForSeconds(1);
            }
            cir.setReturnValue(false);
        }
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

        // Copy (or drop) the collar on respawn
        ItemStack oldCollar = ((CollarHolder) oldPlayer).deeperdark$getCollarItem();
        if (!oldCollar.isEmpty()) {
            boolean keepInv = alive || (world != null && world.getGameRules().get(GameRules.KEEP_INVENTORY));
            if (keepInv) {
                ((CollarHolder) self).deeperdark$setCollarItem(oldCollar.copy());
            } else {
                oldPlayer.spawnAtLocation(world, oldCollar);
            }
            ((CollarHolder) oldPlayer).deeperdark$setCollarItem(ItemStack.EMPTY);
        }
    }
}
