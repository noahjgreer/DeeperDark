package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.item.ItemMagnetItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemMagnetHandler {

    private static final int ACTIVATION_DURATION = 30;

    // Ticks to block magnet after eating ends. Resets to this value on each suppressed
    // call (RMB still held), so it only counts down after the player releases RMB.
    // Must be > 4 (the client's right-click repeat rate) so holding never drains it to 0.
    private static final int POST_EAT_HOLD_TICKS = 8;

    // Maps item entity UUID → player UUID who dropped it. Persists until pickup.
    private static final Map<UUID, UUID> playerDroppedItems = new ConcurrentHashMap<>();

    public static void markDroppedByPlayer(UUID itemEntityUUID, UUID playerUUID) {
        playerDroppedItems.put(itemEntityUUID, playerUUID);
    }

    public static void clearDroppedItem(UUID itemEntityUUID) {
        playerDroppedItems.remove(itemEntityUUID);
    }

    public static boolean isDroppedByPlayer(UUID itemEntityUUID, UUID playerUUID) {
        return playerUUID.equals(playerDroppedItems.get(itemEntityUUID));
    }

    private static final Map<UUID, Integer> activationTicks = new HashMap<>();
    private static final Map<UUID, InteractionHand> activationHand = new HashMap<>();
    private static final Map<UUID, Boolean> prevUsingItem = new HashMap<>();
    // Per-player countdown: positive = suppressed; reset to POST_EAT_HOLD_TICKS on each
    // suppressed activateMagnet call; counts down to 0 only after RMB is released.
    private static final Map<UUID, Integer> postEatCooldown = new HashMap<>();

    /**
     * Called from ItemMagnetItem.use() when the player right-clicks with a magnet.
     * Returns true if activation was allowed (caller should apply damage/cooldown/sound),
     * or false if suppressed because the player just finished eating while holding RMB.
     */
    public static boolean activateMagnet(UUID playerUUID, InteractionHand hand) {
        if (postEatCooldown.containsKey(playerUUID)) {
            // RMB still held after eating — reset the timer to prevent it draining to 0
            postEatCooldown.put(playerUUID, POST_EAT_HOLD_TICKS);
            return false;
        }
        activationTicks.put(playerUUID, ACTIVATION_DURATION);
        activationHand.put(playerUUID, hand);
        return true;
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerLevel level : server.getAllLevels()) {
                tickLevel(level);
            }
        });
    }

    private static void tickLevel(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            handlePlayer(level, player);
        }

        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();
                if (stack.getItem() instanceof ItemMagnetItem magnet) {
                    pullItemsToward(level, itemEntity.position(), magnet.getMagnetType(), false, itemEntity.getUUID(), null);
                }
            } else if (entity instanceof ItemFrame frame) {
                ItemStack stack = frame.getItem();
                if (stack.getItem() instanceof ItemMagnetItem magnet) {
                    pullItemsToward(level, frame.position(), magnet.getMagnetType(), false, null, null);
                }
            }
        }
    }

    private static void handlePlayer(ServerLevel level, ServerPlayer player) {
        UUID uuid = player.getUUID();

        // Detect eating completion: isUsingItem true → false
        boolean isUsingNow = player.isUsingItem();
        Boolean wasUsing = prevUsingItem.put(uuid, isUsingNow);
        if (Boolean.TRUE.equals(wasUsing) && !isUsingNow) {
            postEatCooldown.put(uuid, POST_EAT_HOLD_TICKS);
            activationTicks.remove(uuid);
            activationHand.remove(uuid);
        }

        // Decrement post-eat suppression timer each tick
        Integer remaining = postEatCooldown.get(uuid);
        if (remaining != null) {
            if (remaining <= 0) {
                postEatCooldown.remove(uuid);
            } else {
                postEatCooldown.put(uuid, remaining - 1);
            }
        }

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
            pullItemsToward(level, player.position(), magnet.getMagnetType(), isActivating, null, uuid);
        }

        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof ItemMagnetItem magnet) {
            boolean isActivating = activating && activeHand == InteractionHand.OFF_HAND;
            pullItemsToward(level, player.position(), magnet.getMagnetType(), isActivating, null, uuid);
        }
    }

    private static void pullItemsToward(ServerLevel level, Vec3 center, ItemMagnetItem.MagnetType type, boolean activating, UUID excludeEntityId, UUID magnetHolderUUID) {
        DeeperDarkConfig.ItemMagnetVariantConfig cfg = getVariantConfig(DeeperDarkConfig.get(), type);
        double radius = cfg.radius;
        double passiveStrength = cfg.passiveStrength;
        double xpRadius = radius * 1.5;

        AABB searchBox = new AABB(
                center.x - xpRadius, center.y - xpRadius, center.z - xpRadius,
                center.x + xpRadius, center.y + xpRadius, center.z + xpRadius
        );

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, searchBox)) {
            if (excludeEntityId != null && item.getUUID().equals(excludeEntityId)) continue;
            // Skip items deliberately dropped by the magnet holder — lets them give items to others.
            // The flag persists (unlike vanilla's thrower field which clears after 40 ticks) until pickup.
            if (magnetHolderUUID != null && magnetHolderUUID.equals(playerDroppedItems.get(item.getUUID()))) continue;

            Vec3 diff = center.subtract(item.position());
            double dist = diff.length();
            if (dist < 0.5 || dist > radius) continue;

            Vec3 dir = diff.normalize();
            Vec3 newVel = activating ? dir.scale(3.0)
                    : dir.scale(passiveStrength * sqFalloff(dist, radius) * 0.5);
            item.setDeltaMovement(newVel);
            item.hurtMarked = true;
        }

        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, searchBox)) {
            Vec3 diff = center.subtract(orb.position());
            double dist = diff.length();
            if (dist < 0.5 || dist > xpRadius) continue;

            Vec3 dir = diff.normalize();
            Vec3 newVel = activating ? dir.scale(4.5)
                    : dir.scale(passiveStrength * sqFalloff(dist, xpRadius) * 0.75);
            orb.setDeltaMovement(newVel);
            orb.hurtMarked = true;
        }
    }

    private static double sqFalloff(double dist, double radius) {
        double t = 1.0 - (dist / radius);
        return t * t;
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
