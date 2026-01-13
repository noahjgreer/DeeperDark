package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.noahsarch.deeperdark.DeeperDarkConfig;

public class WorldBorderHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSpectator() || player.isCreative()) continue;
                applyBorderForce(player);
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
            // Mobs logic: Prevent spawning outside and cleanup far outliers
            if (entity instanceof net.minecraft.entity.mob.MobEntity) {
                // If just spawned (low age) and outside, remove immediately
                if (entity.age < 200) {
                    entity.discard();
                    return;
                }
                // If significantly outside (teleported/glitched/spawned far), remove
                if (distX > radius + 32 || distZ > radius + 32) {
                    entity.discard();
                    return;
                }
            }

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
