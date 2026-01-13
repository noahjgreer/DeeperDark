package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$applyBorderForce(CallbackInfo ci) {
        if (!((EntityAccessor)(Object)this).deeperdark$getWorld().isClient()) {
            net.noahsarch.deeperdark.event.WorldBorderHandler.applyBorderForce(this);
        }
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void deeperdark$preventExplosionDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            cir.setReturnValue(false);
        }
    }
}
