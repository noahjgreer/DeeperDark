package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.noahsarch.deeperdark.state.ActiveBeaconState;

public class BeaconHandler {

    public static void register() {
         ServerTickEvents.END_SERVER_TICK.register(server -> {
             for (ServerWorld world : server.getWorlds()) {
                 ActiveBeaconState.get(world).tick(world);
             }
         });
    }
}

