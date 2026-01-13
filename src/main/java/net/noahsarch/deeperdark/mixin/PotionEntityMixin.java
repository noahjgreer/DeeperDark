package net.noahsarch.deeperdark.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PotionEntity.class)
public abstract class PotionEntityMixin extends ThrownItemEntity {

    protected PotionEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        World world = ((EntityAccessor)(Object)this).deeperdark$getWorld();
        if (!world.isClient()) {
            ItemStack itemStack = this.getStack();
            PotionContentsComponent potionContentsComponent = itemStack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
            if (potionContentsComponent.matches(Potions.WATER)) {
                 Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
                 List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isOnFire);
                 for (LivingEntity entity : list) {
                     entity.extinguishWithSound();
                 }
            }
        }
    }
}
