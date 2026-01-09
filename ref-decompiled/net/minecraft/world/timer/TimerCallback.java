package net.minecraft.world.timer;

import com.mojang.serialization.MapCodec;

public interface TimerCallback {
   void call(Object server, Timer events, long time);

   MapCodec getCodec();
}
