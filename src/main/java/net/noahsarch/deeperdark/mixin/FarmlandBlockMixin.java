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

    /**
     * Prevent farmland from being trampled if the player has Feather Falling enchantment on their boots
     */
    @Inject(method = "onLandedUpon", at = @At("HEAD"), cancellable = true)
    private void deeperdark$preventTrampleWithFeatherFalling(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        // Check if the entity is a player
        if (entity instanceof Player player) {
            // Get the boots item stack
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

            // Check if the boots have Feather Falling enchantment (any level)
            if (!boots.isEmpty()) {
                ItemEnchantments enchantments = boots.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.DEFAULT);

                // Check if Feather Falling is present
                boolean hasFeatherFalling = false;
                for (Holder<Enchantment> enchantment : enchantments.getEnchantments()) {
                    if (enchantment.matchesKey(Enchantments.FEATHER_FALLING)) {
                        hasFeatherFalling = true;
                        break;
                    }
                }

                if (hasFeatherFalling) {
                    // Player has Feather Falling - only call the super method to handle fall damage,
                    // but don't let the farmland trampling logic execute
                    entity.handleFallDamage(fallDistance, 1.0f, world.getDamageSources().fall());
                    ci.cancel();
                }
            }
        }
    }
}

