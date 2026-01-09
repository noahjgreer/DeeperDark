package net.minecraft.resource;

public interface LifecycledResourceManager extends ResourceManager, AutoCloseable {
   void close();
}
