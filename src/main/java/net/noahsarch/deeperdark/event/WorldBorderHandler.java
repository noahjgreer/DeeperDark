package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.ArrayList;
import java.util.List;

public class WorldBorderHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

            // Handle players
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSpectator()) continue;

                // If pushSurvivalModeOnly is true, skip creative players
                if (config.pushSurvivalModeOnly && player.isCreative()) continue;

                applyBorderForce(player);
            }

            // Handle mobs in all worlds
            for (ServerWorld world : server.getWorlds()) {
                // Collect all mobs first to avoid concurrent modification
                List<MobEntity> mobs = new ArrayList<>();
                world.iterateEntities().forEach(entity -> {
                    if (entity instanceof MobEntity mob) {
                        mobs.add(mob);
                    }
                });

                // Now process them safely
                for (MobEntity mob : mobs) {
                    if (!mob.isRemoved()) { // Check if still exists
                        handleMobBorder(mob);
                    }
                }
            }
        });
    }

    public static boolean isSafe(double x, double z) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        double radius = config.safeRadius;
        int originX = config.originX;
        int originZ = config.originZ;

        double distX = Math.abs(x - originX);
        double distZ = Math.abs(z - originZ);

        return distX <= radius && distZ <= radius;
    }

    /**
     * Handle mobs near or outside the border
     */
    private static void handleMobBorder(MobEntity mob) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        // If entity spawning is allowed, don't push or delete mobs
        if (config.allowEntitySpawning) {
            return;
        }

        double radius = config.safeRadius;
        int originX = config.originX;
        int originZ = config.originZ;
        double forceMult = config.forceMultiplier;

        double dx = mob.getX() - originX;
        double dz = mob.getZ() - originZ;

        double distX = Math.abs(dx);
        double distZ = Math.abs(dz);

        // Delete mobs more than 16 blocks outside the border
        if (distX > radius + 16 || distZ > radius + 16) {
            mob.discard(); // Discard, not kill - no drops, no death message
            return;
        }

        // If just spawned (low age) and outside, remove immediately
        if (mob.age < 200 && (distX > radius || distZ > radius)) {
            mob.discard();
            return;
        }

        // Push mobs back if they're outside or close to the border
        if (distX > radius || distZ > radius) {
            double overlapX = Math.max(0, distX - radius);
            double overlapZ = Math.max(0, distZ - radius);

            // Gentler push for mobs - use a linear or mild exponential force
            double strengthX = overlapX > 0 ? Math.min(overlapX * 0.05 * forceMult, 0.5) : 0;
            double strengthZ = overlapZ > 0 ? Math.min(overlapZ * 0.05 * forceMult, 0.5) : 0;

            mob.addVelocity(-Math.signum(dx) * strengthX, 0, -Math.signum(dz) * strengthZ);
            mob.velocityDirty = true;
        }
    }

    public static void applyBorderForce(net.minecraft.entity.Entity entity) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        double radius = config.safeRadius;
        int originX = config.originX;
        int originZ = config.originZ;
        double forceMult = config.forceMultiplier;

        double dx = entity.getX() - originX;
        double dz = entity.getZ() - originZ;

        double distX = Math.abs(dx);
        double distZ = Math.abs(dz);

        if (distX > radius || distZ > radius) {

            double overlapX = Math.max(0, distX - radius);
            double overlapZ = Math.max(0, distZ - radius);

            double strengthX = overlapX > 0 ? (Math.exp(overlapX * 0.1) - 1) * 0.2 * forceMult : 0;
            double strengthZ = overlapZ > 0 ? (Math.exp(overlapZ * 0.1) - 1) * 0.2 * forceMult : 0;

            if (strengthX > 4.0) strengthX = 4.0;
            if (strengthZ > 4.0) strengthZ = 4.0;

            entity.addVelocity(-Math.signum(dx) * strengthX, 0, -Math.signum(dz) * strengthZ);
            entity.velocityDirty = true;

            if (entity instanceof ServerPlayerEntity player) {
                player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
            }
        }
    }
}
