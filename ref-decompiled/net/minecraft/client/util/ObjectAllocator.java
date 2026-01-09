package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ObjectAllocator {
   ObjectAllocator TRIVIAL = new ObjectAllocator() {
      public Object acquire(ClosableFactory factory) {
         Object object = factory.create();
         factory.prepare(object);
         return object;
      }

      public void release(ClosableFactory factory, Object value) {
         factory.close(value);
      }
   };

   Object acquire(ClosableFactory factory);

   void release(ClosableFactory factory, Object value);
}
