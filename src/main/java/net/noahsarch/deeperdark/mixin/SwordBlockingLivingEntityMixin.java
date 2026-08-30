package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.ported.SwordBlockingPort;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The authoritative gate for sword blocking.
 *
 * <p>{@code LivingEntity#getItemBlockingWith} is the single point the game consults for both
 * {@code isBlocking()} and {@code applyItemBlocking}, so clearing it here retracts the block
 * everywhere at once - damage reduction, the third-person pose, mob AI and knockback. This
 * catches the cases {@link SwordBlockingItemMixin} cannot, most notably a player who raises a
 * sword and only then swaps a shield into the off-hand.
 */
@Mixin(LivingEntity.class)
public class SwordBlockingLivingEntityMixin {

    @Inject(method = "getItemBlockingWith", at = @At("RETURN"), cancellable = true)
    private void deeperdark$gateSwordBlocking(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack blockingWith = cir.getReturnValue();
        if (blockingWith == null || !SwordBlockingPort.isBlockingSword(blockingWith)) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        if (!SwordBlockingPort.canSwordBlock(self, blockingWith)) {
            cir.setReturnValue(null);
        }
    }
}
