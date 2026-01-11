package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.noahsarch.deeperdark.DeeperDarkConfig;

public class WorldBorderHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
            double radius = config.safeRadius;
            int originX = config.originX;
            int originZ = config.originZ;
            double forceMult = config.forceMultiplier; // e.g., 0.05

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                // Ignore spectator mode
                if (player.isSpectator() || player.isCreative()) continue;

                double dx = player.getX() - originX;
                double dz = player.getZ() - originZ;

                double distX = Math.abs(dx);
                double distZ = Math.abs(dz);

                if (distX > radius || distZ > radius) {
                    double overlapX = Math.max(0, distX - radius);
                    double overlapZ = Math.max(0, distZ - radius);

                    // Calculate force magnitude based on overlap distance per axis
                    // Exponential force: (e^(overlap * factor) - 1) * multiplier
                    double strengthX = overlapX > 0 ? (Math.exp(overlapX * 0.1) - 1) * 0.2 * forceMult : 0;
                    double strengthZ = overlapZ > 0 ? (Math.exp(overlapZ * 0.1) - 1) * 0.2 * forceMult : 0;

                    // Limit strength to avoid flying glitches
                    if (strengthX > 4.0) strengthX = 4.0;
                    if (strengthZ > 4.0) strengthZ = 4.0;

                    // Apply velocity
                    // Note: setVelocity updates the server-side velocity.
                    // For players, we need to mark it as modified to sync to others.
                    player.addVelocity(-Math.signum(dx) * strengthX, 0, -Math.signum(dz) * strengthZ);
                    player.velocityModified = true;

                    // Force sync to the client so they feel the push immediately
                    player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
                }
            }
        });
    }
}
