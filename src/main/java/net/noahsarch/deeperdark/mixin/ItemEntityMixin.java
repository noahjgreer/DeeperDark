package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.event.HopperTrinketHandler;
import net.noahsarch.deeperdark.event.ItemMagnetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow private int pickupDelay;
    @Shadow private java.util.UUID target;

    @Shadow public abstract ItemStack getItem();

    @Unique
    private Vec3 deeperdark$lastVelocity = Vec3.ZERO;

    public ItemEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$applyBorderForce(CallbackInfo ci) {
        if (!((EntityAccessor)(Object)this).deeperdark$getWorld().isClientSide()) {
            net.noahsarch.deeperdark.event.WorldBorderHandler.applyBorderForce(this);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$storeVelocityBeforeTick(CallbackInfo ci) {
        this.deeperdark$lastVelocity = this.getDeltaMovement();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void deeperdark$amplifyExplosionVelocity(CallbackInfo ci) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        if (!config.explosionItemKnockbackEnabled) {
            return;
        }

        if (!this.level().isClientSide()) {
            Vec3 currentVelocity = this.getDeltaMovement();
            Vec3 velocityChange = currentVelocity.subtract(this.deeperdark$lastVelocity);

            double changeSquared = velocityChange.lengthSqr();
            if (changeSquared > 0.01) {
                double multiplier = config.explosionItemKnockbackMultiplier;
                Vec3 amplifiedChange = velocityChange.scale(multiplier);
                this.setDeltaMovement(this.deeperdark$lastVelocity.add(amplifiedChange));
                this.hurtMarked = true;
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void deeperdark$preventExplosionDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "ignoreExplosion", at = @At("HEAD"), cancellable = true)
    private void deeperdark$allowExplosionKnockback(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void deeperdark$clearDroppedFlag(Player player, CallbackInfo ci) {
        ItemMagnetHandler.clearDroppedItem(((Entity)(Object)this).getUUID());
    }

    /**
     * Hopper trinket: divert the pickup into matching container items the player is carrying.
     * Anything left over falls through to vanilla, which adds it to the inventory as usual;
     * if the whole stack was routed we finish the pickup ourselves and cancel.
     */
    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void deeperdark$routeToCollarHopper(Player player, CallbackInfo ci) {
        if (this.level().isClientSide()) return;
        // Mirror vanilla's pickup gate so a delayed or reserved drop isn't siphoned early.
        if (this.pickupDelay != 0) return;
        if (this.target != null && !this.target.equals(player.getUUID())) return;

        ItemStack stack = this.getItem();
        if (stack.isEmpty()) return;

        Item item = stack.getItem();
        int moved = HopperTrinketHandler.route(player, stack);
        if (moved == 0) return;

        player.awardStat(Stats.ITEM_PICKED_UP.get(item), moved);
        if (stack.isEmpty()) {
            // Whole stack absorbed: play out the rest of the vanilla pickup and stop here.
            // Injector order at HEAD isn't guaranteed, so clear the magnet flag before cancelling.
            ItemMagnetHandler.clearDroppedItem(this.getUUID());
            player.take((ItemEntity) (Object) this, moved);
            player.onItemPickup((ItemEntity) (Object) this);
            this.discard();
            ci.cancel();
        }
        // Partial: vanilla continues with the shrunken stack and handles take/stats for the rest.
    }
}
