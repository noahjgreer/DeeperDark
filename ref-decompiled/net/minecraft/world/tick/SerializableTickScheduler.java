package net.minecraft.world.tick;

import java.util.List;

public interface SerializableTickScheduler {
   List collectTicks(long time);
}
