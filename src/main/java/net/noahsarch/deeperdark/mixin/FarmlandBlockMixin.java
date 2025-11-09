package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    private void deeperdark$preventTrampleWithFeatherFalling(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        // Check if the entity is a player
        if (entity instanceof PlayerEntity player) {
            // Get the boots item stack
            ItemStack boots = player.getEquippedStack(EquipmentSlot.FEET);

            // Check if the boots have Feather Falling enchantment (any level)
            if (!boots.isEmpty()) {
                ItemEnchantmentsComponent enchantments = boots.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

                // Check if Feather Falling is present
                boolean hasFeatherFalling = false;
                for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
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

