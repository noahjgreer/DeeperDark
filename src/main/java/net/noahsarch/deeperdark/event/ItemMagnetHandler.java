package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.item.ItemMagnetItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemMagnetHandler {

    private static final int ACTIVATION_DURATION = 30;

    private static final Map<UUID, Integer> activationTicks = new HashMap<>();
    private static final Map<UUID, InteractionHand> activationHand = new HashMap<>();

    public static void activateMagnet(UUID playerUUID, InteractionHand hand) {
        activationTicks.put(playerUUID, ACTIVATION_DURATION);
        activationHand.put(playerUUID, hand);
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                tickLevel(level);
            }
        });
    }

    private static void tickLevel(ServerLevel level) {
        // Player-held magnets
        for (ServerPlayer player : level.players()) {
            handlePlayer(level, player);
        }

        // Dropped magnet items and item frames containing magnets
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();
                if (stack.getItem() instanceof ItemMagnetItem magnet) {
                    pullItemsToward(level, itemEntity.position(), magnet.getMagnetType(), false, itemEntity.getUUID());
                }
            } else if (entity instanceof ItemFrame frame) {
                ItemStack stack = frame.getItem();
                if (stack.getItem() instanceof ItemMagnetItem magnet) {
                    pullItemsToward(level, frame.position(), magnet.getMagnetType(), false, null);
                }
            }
        }
    }

    private static void handlePlayer(ServerLevel level, ServerPlayer player) {
        UUID uuid = player.getUUID();

        boolean activating = false;
        InteractionHand activeHand = null;
        Integer ticks = activationTicks.get(uuid);
        if (ticks != null) {
            if (ticks > 0) {
                activating = true;
                activeHand = activationHand.get(uuid);
                activationTicks.put(uuid, ticks - 1);
            } else {
                activationTicks.remove(uuid);
                activationHand.remove(uuid);
            }
        }

        ItemStack mainhand = player.getMainHandItem();
        if (mainhand.getItem() instanceof ItemMagnetItem magnet) {
            boolean isActivating = activating && activeHand == InteractionHand.MAIN_HAND;
            pullItemsToward(level, player.position(), magnet.getMagnetType(), isActivating, null);
        }

        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof ItemMagnetItem magnet) {
            boolean isActivating = activating && activeHand == InteractionHand.OFF_HAND;
            pullItemsToward(level, player.position(), magnet.getMagnetType(), isActivating, null);
        }
    }

    private static void pullItemsToward(ServerLevel level, Vec3 center, ItemMagnetItem.MagnetType type, boolean activating, UUID excludeEntityId) {
        DeeperDarkConfig.ItemMagnetVariantConfig cfg = getVariantConfig(DeeperDarkConfig.get(), type);
        double radius = cfg.radius;
        double passiveStrength = cfg.passiveStrength;

        AABB searchBox = new AABB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius
        );

        List<ItemEntity> nearby = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity item : nearby) {
            if (excludeEntityId != null && item.getUUID().equals(excludeEntityId)) continue;

            Vec3 itemPos = item.position();
            Vec3 diff = center.subtract(itemPos);
            double dist = diff.length();

            if (dist < 0.5 || dist > radius) continue;

            Vec3 dir = diff.normalize();

            Vec3 newVel;
            if (activating) {
                // Same strong pull for all materials — items fly toward the player
                newVel = dir.scale(1.5);
            } else {
                // Directly set velocity toward magnet, scaled by proximity (squared falloff)
                double t = 1.0 - (dist / radius);
                newVel = dir.scale(passiveStrength * t * t * 0.5);
            }

            item.setDeltaMovement(newVel);
        }
    }

    private static DeeperDarkConfig.ItemMagnetVariantConfig getVariantConfig(DeeperDarkConfig.ConfigInstance cfg, ItemMagnetItem.MagnetType type) {
        return switch (type) {
            case COPPER -> cfg.itemMagnet.copper;
            case IRON -> cfg.itemMagnet.iron;
            case GOLDEN -> cfg.itemMagnet.gold;
            case DIAMOND -> cfg.itemMagnet.diamond;
            case NETHERITE -> cfg.itemMagnet.netherite;
        };
    }
}
