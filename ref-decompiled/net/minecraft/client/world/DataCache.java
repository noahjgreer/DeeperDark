package net.minecraft.client.world;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DataCache {
   private final Function dataFunction;
   @Nullable
   private CacheContext context;
   @Nullable
   private Object data;

   public DataCache(Function dataFunction) {
      this.dataFunction = dataFunction;
   }

   public Object compute(CacheContext context) {
      if (context == this.context && this.data != null) {
         return this.data;
      } else {
         Object object = this.dataFunction.apply(context);
         this.data = object;
         this.context = context;
         context.registerForCleaning(this);
         return object;
      }
   }

   public void clean() {
      this.data = null;
      this.context = null;
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface CacheContext {
      void registerForCleaning(DataCache dataCache);
   }
}
