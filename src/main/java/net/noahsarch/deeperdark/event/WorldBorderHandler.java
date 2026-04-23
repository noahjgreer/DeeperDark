package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.noahsarch.deeperdark.DeeperDarkConfig;

import java.util.ArrayList;
import java.util.List;

public class WorldBorderHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

            // Handle players
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.isSpectator()) continue;

                if (config.pushSurvivalModeOnly && player.isCreative()) continue;

                if (player.getVehicle() != null) {
                    net.minecraft.world.entity.Entity vehicle = player.getVehicle();
                    if (vehicle != null && vehicle.getControllingPassenger() == player) {
                        applyBorderForce(vehicle);
                    }
                } else {
                    applyBorderForce(player);
                }
            }

            // Handle mobs in all worlds
            for (ServerLevel world : server.getAllLevels()) {
                List<Mob> mobs = new ArrayList<>();
                world.getAllEntities().forEach(entity -> {
                    if (entity instanceof Mob mob) {
                        mobs.add(mob);
                    }
                });

                for (Mob mob : mobs) {
                    if (!mob.isRemoved()) {
                        boolean handled = false;
                        if (mob.getVehicle() != null) {
                            net.minecraft.world.entity.Entity vehicle = mob.getVehicle();
                            if (vehicle != null && vehicle.getControllingPassenger() == mob) {
                                applyBorderForce(vehicle);
                                handled = true;
                            }
                        }

                        if (!handled) {
                            handleMobBorder(mob);
                        }
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

    private static void handleMobBorder(Mob mob) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

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

        if (distX > radius + 16 || distZ > radius + 16) {
            mob.discard();
            return;
        }

        if (mob.tickCount < 200 && (distX > radius + 2 || distZ > radius + 2)) {
            mob.discard();
            return;
        }

        if (distX > radius || distZ > radius) {
            double overlapX = Math.max(0, distX - radius);
            double overlapZ = Math.max(0, distZ - radius);

            double strengthX = overlapX > 0 ? Math.min(overlapX * 0.05 * forceMult, 0.5) : 0;
            double strengthZ = overlapZ > 0 ? Math.min(overlapZ * 0.05 * forceMult, 0.5) : 0;

            mob.push(-Math.signum(dx) * strengthX, 0, -Math.signum(dz) * strengthZ);
            mob.hurtMarked = true;
        }
    }

    public static void applyBorderForce(net.minecraft.world.entity.Entity entity) {
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

            entity.push(-Math.signum(dx) * strengthX, 0, -Math.signum(dz) * strengthZ);
            entity.hurtMarked = true;

            if (entity instanceof ServerPlayer player) {
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
            } else if (entity.level() instanceof ServerLevel serverWorld) {
                serverWorld.getChunkSource().sendToTrackingPlayers(entity, new ClientboundSetEntityMotionPacket(entity));
            }
        }
    }
}
