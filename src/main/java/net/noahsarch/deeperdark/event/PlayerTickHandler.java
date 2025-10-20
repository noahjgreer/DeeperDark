package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PlayerTickHandler {
    private static final RegistryKey<World> THE_SLIP = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("minecraft", "the_slip"));

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Check if player is in The Slip dimension
                if (player.getWorld().getRegistryKey().equals(THE_SLIP)) {
                    // Check if player is wearing full leather armor
                    if (isWearingFullLeatherArmor(player)) {
                        // If wearing full leather armor, decrease frozen ticks (thaw out)
                        int currentFrozen = player.getFrozenTicks();
                        if (currentFrozen > 0) {
                            player.setFrozenTicks(Math.max(0, currentFrozen - 2));
                        }
                    } else {
                        // Increase frozen ticks to simulate being in powdered snow
                        int currentFrozen = player.getFrozenTicks();
                        int minFreezeDamage = player.getMinFreezeDamageTicks(); // This is 140 by default

                        // Keep incrementing to allow damage. Cap at a reasonable max.
                        if (currentFrozen < minFreezeDamage + 100) {
                            player.setFrozenTicks(currentFrozen + 3);
                        }

                        // Manually trigger freeze damage when threshold is exceeded
                        // Damage every 40 ticks (2 seconds) like vanilla freezing
                        if (currentFrozen >= minFreezeDamage && player.age % 40 == 0) {
                            player.serverDamage(player.getWorld().getDamageSources().freeze(), 1.0F);
                        }
                    }
                }
            }
        });
    }

    private static boolean isWearingFullLeatherArmor(ServerPlayerEntity player) {
        ItemStack helmet = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.FEET);

        return helmet.isOf(Items.LEATHER_HELMET) &&
               chestplate.isOf(Items.LEATHER_CHESTPLATE) &&
               leggings.isOf(Items.LEATHER_LEGGINGS) &&
               boots.isOf(Items.LEATHER_BOOTS);
    }
}
