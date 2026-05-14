package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.duck.CollarHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public class BowItemMixin {

    @Inject(method = "releaseUsing", at = @At("RETURN"))
    private void deeperdark$consumeCollarArrow(
            ItemStack bowStack, Level level, LivingEntity entity, int remainingTime,
            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        if (!(entity instanceof Player player)) return;
        if (!(player instanceof CollarHolder ch) || !ch.deeperdark$isArrowFromCollar()) return;
        ch.deeperdark$setArrowFromCollar(false);

        if (!(level instanceof ServerLevel serverLevel)) return;

        // Infinity (or multishot extra arrows) returns ammoUse == 0 — don't consume.
        int ammoUse = EnchantmentHelper.processAmmoUse(serverLevel, bowStack, new ItemStack(net.minecraft.world.item.Items.ARROW), 1);
        if (ammoUse == 0) return;

        ItemStack collar = ch.deeperdark$getCollarItem();
        if (collar.isEmpty()) return;

        NonNullList<ItemStack> trinkets = NonNullList.withSize(5, ItemStack.EMPTY);
        collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(trinkets);

        for (int i = 0; i < trinkets.size(); i++) {
            ItemStack t = trinkets.get(i);
            if (!t.isEmpty() && ProjectileWeaponItem.ARROW_ONLY.test(t)) {
                trinkets.set(i, ItemStack.EMPTY);
                collar.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(trinkets));
                ch.deeperdark$setCollarItem(collar);
                break;
            }
        }
    }
}
