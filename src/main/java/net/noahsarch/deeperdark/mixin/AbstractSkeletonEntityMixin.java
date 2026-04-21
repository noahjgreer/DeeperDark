package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.noahsarch.deeperdark.util.MobEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSkeleton.class)
public class AbstractSkeletonEntityMixin {

    @Inject(method = "createArrowProjectile", at = @At("RETURN"))
    private void onCreateArrowProjectile(ItemStack arrow, float damageModifier, ItemStack shotFrom, CallbackInfoReturnable<AbstractArrow> cir) {
        // Check if this entity actually implements MobEntityExtension before casting
        if (!(this instanceof MobEntityExtension)) {
            return;
        }

        MobEntityExtension self = (MobEntityExtension) this;
        Holder<MobEffect> effect = self.deeperdark$getStoredEffect();

        if (effect != null) {
            AbstractArrow projectile = cir.getReturnValue();
            if (projectile instanceof Arrow arrowEntity) {
                arrowEntity.addEffect(new MobEffectInstance(effect, 140, 0));
            }
        }
    }
}

