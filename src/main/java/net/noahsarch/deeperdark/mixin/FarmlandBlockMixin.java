package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void deeperdark$preventTrampleWithFeatherFalling(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (entity instanceof Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

            if (!boots.isEmpty()) {
                ItemEnchantments enchantments = boots.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

                boolean hasFeatherFalling = false;
                for (Holder<Enchantment> enchantment : enchantments.keySet()) {
                    if (enchantment.is(Enchantments.FEATHER_FALLING)) {
                        hasFeatherFalling = true;
                        break;
                    }
                }

                if (hasFeatherFalling) {
                    entity.causeFallDamage(fallDistance, 1.0f, world.damageSources().fall());
                    ci.cancel();
                }
            }
        }
    }
}
