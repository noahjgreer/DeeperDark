package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Pose;

public class PlayerTickHandler {
    private static final ResourceKey<Level> THE_SLIP = ResourceKey.create(Registries.WORLD, Identifier.fromNamespaceAndPath("minecraft", "the_slip"));

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerManager().getPlayerList()) {
                // Check if player is in The Slip dimension
                net.minecraft.world.level.Level world = ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld();
                if (world.getRegistryKey().equals(THE_SLIP)) {
                    // Handle freezing mechanics
                    handleFreezingMechanics(player);

                    // Handle elytra freezing while flying
                    handleElytraDamage(player);
                } else if (world.getRegistryKey().equals(Level.NETHER)) {
                    // Check if player is on the Nether roof (above Y=127)
                    if (player.getY() > 127.0) {
                        if (handleElytraDamage(player)) {
                            // Play sizzling sound occasionally
                            if (player.age % 2 == 0) {
                                ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundSource.PLAYERS, 5f, 1.7f);
                            }
                        }
                    }
                }
            }
        });
    }

    private static void handleFreezingMechanics(ServerPlayer player) {
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
                player.damage((net.minecraft.server.level.ServerLevel)((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld(), player.getDamageSources().freeze(), 1.0F);
            }
        }
    }

    private static boolean handleElytraDamage(ServerPlayer player) {
        // Check if player is actively flying with elytra (using Pose.GLIDING)
        if (player.isInPose(Pose.GLIDING)) {
            ItemStack chestplate = player.getEquippedStack(net.minecraft.world.entity.EquipmentSlot.CHEST);

            // Verify they have an elytra equipped
            if (chestplate.isOf(Items.ELYTRA)) {
                // Check if the elytra is unbreakable (creative mode or special item)
                if (chestplate.contains(DataComponents.UNBREAKABLE)) {
                    return false; // Don't damage unbreakable elytras
                }

                // Damage the elytra rapidly (about 20 damage per second)
                // At 432 max durability, this gives roughly 5 seconds of flight (100 ticks)
                // We'll damage it every tick for maximum effect
                // If the durability is at 2, damage it only by 1 to avoid breaking it immediately
                if (chestplate.getDamage() >= chestplate.getMaxDamage() - 2) {
                    chestplate.damage(1, player, net.minecraft.world.entity.EquipmentSlot.CHEST);
                } else {
                    chestplate.damage(2, player, net.minecraft.world.entity.EquipmentSlot.CHEST);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isWearingFullLeatherArmor(ServerPlayer player) {
        ItemStack helmet = player.getEquippedStack(net.minecraft.world.entity.EquipmentSlot.HEAD);
        ItemStack chestplate = player.getEquippedStack(net.minecraft.world.entity.EquipmentSlot.CHEST);
        ItemStack leggings = player.getEquippedStack(net.minecraft.world.entity.EquipmentSlot.LEGS);
        ItemStack boots = player.getEquippedStack(net.minecraft.world.entity.EquipmentSlot.FEET);

        return helmet.isOf(Items.LEATHER_HELMET) &&
               chestplate.isOf(Items.LEATHER_CHESTPLATE) &&
               leggings.isOf(Items.LEATHER_LEGGINGS) &&
               boots.isOf(Items.LEATHER_BOOTS);
    }
}
