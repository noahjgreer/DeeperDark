package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ThrownSplashPotion.class)
public abstract class PotionEntityMixin extends ThrowableItemProjectile {

    protected PotionEntityMixin(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "onHit", at = @At("HEAD"))
    private void onHit(HitResult hitResult, CallbackInfo ci) {
        Level world = ((EntityAccessor)(Object)this).deeperdark$getWorld();
        if (!world.isClientSide()) {
            ItemStack itemStack = this.getItem();
            PotionContents potionContentsComponent = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (potionContentsComponent.is(Potions.WATER)) {
                 AABB box = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
                 List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, box, entity -> entity.isOnFire());
                 for (LivingEntity entity : list) {
                     entity.clearFire();
                 }
            }
        }
    }
}
