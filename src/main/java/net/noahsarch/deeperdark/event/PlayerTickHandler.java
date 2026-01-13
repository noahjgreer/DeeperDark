package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityPose;

public class PlayerTickHandler {
    private static final RegistryKey<World> THE_SLIP = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("minecraft", "the_slip"));

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Check if player is in The Slip dimension
                net.minecraft.world.World world = ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld();
                if (world.getRegistryKey().equals(THE_SLIP)) {
                    // Handle freezing mechanics
                    handleFreezingMechanics(player);

                    // Handle elytra freezing while flying
                    handleElytraDamage(player);
                } else if (world.getRegistryKey().equals(World.NETHER)) {
                    // Check if player is on the Nether roof (above Y=127)
                    if (player.getY() > 127.0) {
                        if (handleElytraDamage(player)) {
                            // Play sizzling sound occasionally
                            if (player.age % 2 == 0) {
                                ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.PLAYERS, 5f, 1.7f);
                            }
                        }
                    }
                }
            }
        });
    }

    private static void handleFreezingMechanics(ServerPlayerEntity player) {
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
                player.damage((net.minecraft.server.world.ServerWorld)((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld(), player.getDamageSources().freeze(), 1.0F);
            }
        }
    }

    private static boolean handleElytraDamage(ServerPlayerEntity player) {
        // Check if player is actively flying with elytra (using EntityPose.GLIDING)
        if (player.isInPose(EntityPose.GLIDING)) {
            ItemStack chestplate = player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST);

            // Verify they have an elytra equipped
            if (chestplate.isOf(Items.ELYTRA)) {
                // Check if the elytra is unbreakable (creative mode or special item)
                if (chestplate.contains(DataComponentTypes.UNBREAKABLE)) {
                    return false; // Don't damage unbreakable elytras
                }

                // Damage the elytra rapidly (about 20 damage per second)
                // At 432 max durability, this gives roughly 5 seconds of flight (100 ticks)
                // We'll damage it every tick for maximum effect
                // If the durability is at 2, damage it only by 1 to avoid breaking it immediately
                if (chestplate.getDamage() >= chestplate.getMaxDamage() - 2) {
                    chestplate.damage(1, player, net.minecraft.entity.EquipmentSlot.CHEST);
                } else {
                    chestplate.damage(2, player, net.minecraft.entity.EquipmentSlot.CHEST);
                }
                return true;
            }
        }
        return false;
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
