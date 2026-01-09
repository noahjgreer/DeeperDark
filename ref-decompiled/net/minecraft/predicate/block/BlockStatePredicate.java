package net.minecraft.predicate.block;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

public class BlockStatePredicate implements Predicate {
   public static final Predicate ANY = (state) -> {
      return true;
   };
   private final StateManager manager;
   private final Map propertyTests = Maps.newHashMap();

   private BlockStatePredicate(StateManager manager) {
      this.manager = manager;
   }

   public static BlockStatePredicate forBlock(Block block) {
      return new BlockStatePredicate(block.getStateManager());
   }

   public boolean test(@Nullable BlockState blockState) {
      if (blockState != null && blockState.getBlock().equals(this.manager.getOwner())) {
         if (this.propertyTests.isEmpty()) {
            return true;
         } else {
            Iterator var2 = this.propertyTests.entrySet().iterator();

            Map.Entry entry;
            do {
               if (!var2.hasNext()) {
                  return true;
               }

               entry = (Map.Entry)var2.next();
            } while(this.testProperty(blockState, (Property)entry.getKey(), (Predicate)entry.getValue()));

            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean testProperty(BlockState blockState, Property property, Predicate predicate) {
      Comparable comparable = blockState.get(property);
      return predicate.test(comparable);
   }

   public BlockStatePredicate with(Property property, Predicate predicate) {
      if (!this.manager.getProperties().contains(property)) {
         String var10002 = String.valueOf(this.manager);
         throw new IllegalArgumentException(var10002 + " cannot support property " + String.valueOf(property));
      } else {
         this.propertyTests.put(property, predicate);
         return this;
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable final Object state) {
      return this.test((BlockState)state);
   }
}
