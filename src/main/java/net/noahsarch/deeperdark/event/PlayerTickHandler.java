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
    private static final ResourceKey<Level> THE_SLIP = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("minecraft", "the_slip"));

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                // Check if player is in The Slip dimension
                net.minecraft.world.level.Level world = ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld();
                if (world.dimension().equals(THE_SLIP)) {
                    // Handle freezing mechanics
                    handleFreezingMechanics(player);

                    // Handle elytra freezing while flying
                    handleElytraDamage(player);
                } else if (world.dimension().equals(Level.NETHER)) {
                    // Check if player is on the Nether roof (above Y=127)
                    if (player.getY() > 127.0) {
                        if (handleElytraDamage(player)) {
                            // Play sizzling sound occasionally
                            if (player.tickCount % 2 == 0) {
                                ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                                        SoundEvents.CAMPFIRE_CRACKLE, SoundSource.PLAYERS, 5f, 1.7f);
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
            int currentFrozen = player.getTicksFrozen();
            if (currentFrozen > 0) {
                player.setTicksFrozen(Math.max(0, currentFrozen - 2));
            }
        } else {
            // Increase frozen ticks to simulate being in powdered snow
            int currentFrozen = player.getTicksFrozen();
            int minFreezeDamage = player.getTicksRequiredToFreeze();

            // Keep incrementing to allow damage. Cap at a reasonable max.
            if (currentFrozen < minFreezeDamage + 100) {
                player.setTicksFrozen(currentFrozen + 3);
            }

            // Manually trigger freeze damage when threshold is exceeded
            // Damage every 40 ticks (2 seconds) like vanilla freezing
            if (currentFrozen >= minFreezeDamage && player.tickCount % 40 == 0) {
                net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel)
                    ((net.noahsarch.deeperdark.duck.EntityAccessor)player).deeperdark$getWorld();
                player.hurtServer(serverLevel, serverLevel.damageSources().freeze(), 1.0F);
            }
        }
    }

    private static boolean handleElytraDamage(ServerPlayer player) {
        // Check if player is actively flying with elytra (using Pose.FALL_FLYING)
        if (player.hasPose(Pose.FALL_FLYING)) {
            ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);

            // Verify they have an elytra equipped
            if (chestplate.getItem() == Items.ELYTRA) {
                // Check if the elytra is unbreakable (creative mode or special item)
                if (chestplate.has(DataComponents.UNBREAKABLE)) {
                    return false; // Don't damage unbreakable elytras
                }

                // Damage the elytra rapidly (about 20 damage per second)
                if (chestplate.getDamageValue() >= chestplate.getMaxDamage() - 2) {
                    chestplate.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.CHEST);
                } else {
                    chestplate.hurtAndBreak(2, player, net.minecraft.world.entity.EquipmentSlot.CHEST);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isWearingFullLeatherArmor(ServerPlayer player) {
        ItemStack helmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);

        return helmet.getItem() == Items.LEATHER_HELMET &&
               chestplate.getItem() == Items.LEATHER_CHESTPLATE &&
               leggings.getItem() == Items.LEATHER_LEGGINGS &&
               boots.getItem() == Items.LEATHER_BOOTS;
    }
}
