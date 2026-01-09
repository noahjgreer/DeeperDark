package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClosableFactory;
import net.minecraft.client.util.Handle;

@Environment(EnvType.CLIENT)
public interface FramePass {
   Handle addRequiredResource(String name, ClosableFactory factory);

   void dependsOn(Handle handle);

   Handle transfer(Handle handle);

   void addRequired(FramePass pass);

   void markToBeVisited();

   void setRenderer(Runnable renderer);
}
