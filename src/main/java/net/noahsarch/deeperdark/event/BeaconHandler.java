package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.world.ServerWorld;
import net.noahsarch.deeperdark.state.ActiveBeaconState;

public class BeaconHandler {

    public static void register() {
         ServerTickEvents.END_SERVER_TICK.register(server -> {
             for (ServerWorld world : server.getWorlds()) {
                 ActiveBeaconState.get(world).tick(world);
             }
         });

         ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
             // Check all worlds because beacons in any world could be tracking this player
             for (ServerWorld world : server.getWorlds()) {
                 ActiveBeaconState.get(world).onPlayerJoin(handler.player);
             }
             net.noahsarch.deeperdark.event.PlayerTickHandler.onPlayerJoin(handler.player);
         });
    }
}
