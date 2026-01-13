package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.noahsarch.deeperdark.util.MobEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {

    @Inject(method = "createArrowProjectile", at = @At("RETURN"))
    private void onCreateArrowProjectile(ItemStack arrow, float damageModifier, ItemStack shotFrom, CallbackInfoReturnable<PersistentProjectileEntity> cir) {
        // Check if this entity actually implements MobEntityExtension before casting
        if (!(this instanceof MobEntityExtension)) {
            return;
        }

        MobEntityExtension self = (MobEntityExtension) this;
        RegistryEntry<StatusEffect> effect = self.deeperdark$getStoredEffect();

        if (effect != null) {
            PersistentProjectileEntity projectile = cir.getReturnValue();
            if (projectile instanceof ArrowEntity arrowEntity) {
                arrowEntity.addEffect(new StatusEffectInstance(effect, 140, 0));
            }
        }
    }
}

