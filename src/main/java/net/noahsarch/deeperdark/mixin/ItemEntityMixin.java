package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Unique
    private Vec3d deeperdark$lastVelocity = Vec3d.ZERO;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$applyBorderForce(CallbackInfo ci) {
        if (!((EntityAccessor)(Object)this).deeperdark$getWorld().isClient()) {
            net.noahsarch.deeperdark.event.WorldBorderHandler.applyBorderForce(this);
        }
    }

    /**
     * Store velocity at the start of tick to detect explosion changes
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$storeVelocityBeforeTick(CallbackInfo ci) {
        this.deeperdark$lastVelocity = this.getVelocity();
    }

    /**
     * After tick completes, check if velocity changed significantly (likely from explosion)
     * and multiply it to make items fly farther (if enabled in config)
     */
    @Inject(method = "tick", at = @At("RETURN"))
    private void deeperdark$amplifyExplosionVelocity(CallbackInfo ci) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        // Only apply velocity multiplier if enabled in config
        if (!config.explosionItemKnockbackEnabled) {
            return;
        }

        if (!this.getEntityWorld().isClient()) {
            Vec3d currentVelocity = this.getVelocity();
            Vec3d velocityChange = currentVelocity.subtract(this.deeperdark$lastVelocity);

            // If velocity changed significantly in one tick, it's likely from an explosion
            // (or other external force). Amplify it!
            double changeSquared = velocityChange.lengthSquared();
            if (changeSquared > 0.01) { // Threshold to detect explosion knockback
                // Multiply the velocity change by the configured multiplier
                double multiplier = config.explosionItemKnockbackMultiplier;
                Vec3d amplifiedChange = velocityChange.multiply(multiplier);
                this.setVelocity(this.deeperdark$lastVelocity.add(amplifiedChange));
                this.velocityDirty = true;
            }
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void deeperdark$preventExplosionDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(false);
        }
    }

    /**
     * Allow items to be affected by explosion knockback (velocity) while preventing damage.
     * By default, ItemEntity.isImmuneToExplosion returns true, which causes explosions to skip
     * items entirely (no damage AND no velocity). We override this to return false so the explosion
     * applies velocity, but our damage injection above prevents the actual damage.
     */
    @Inject(method = "isImmuneToExplosion", at = @At("HEAD"), cancellable = true)
    private void deeperdark$allowExplosionKnockback(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        // Return false to allow explosion physics to apply (velocity/knockback)
        // The damage is still prevented by the deeperdark$preventExplosionDamage injection
        cir.setReturnValue(false);
    }
}
