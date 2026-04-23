package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
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
}
