package net.minecraft.server.function;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.util.Identifier;

public class LazyContainer {
   public static final Codec CODEC;
   private final Identifier id;
   private boolean initialized;
   private Optional function = Optional.empty();

   public LazyContainer(Identifier id) {
      this.id = id;
   }

   public Optional get(CommandFunctionManager commandFunctionManager) {
      if (!this.initialized) {
         this.function = commandFunctionManager.getFunction(this.id);
         this.initialized = true;
      }

      return this.function;
   }

   public Identifier getId() {
      return this.id;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         boolean var10000;
         if (o instanceof LazyContainer) {
            LazyContainer lazyContainer = (LazyContainer)o;
            if (this.getId().equals(lazyContainer.getId())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   static {
      CODEC = Identifier.CODEC.xmap(LazyContainer::new, LazyContainer::getId);
   }
}
