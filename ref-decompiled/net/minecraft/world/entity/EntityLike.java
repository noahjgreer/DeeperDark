package net.minecraft.world.entity;

import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public interface EntityLike extends UniquelyIdentifiable {
   int getId();

   BlockPos getBlockPos();

   Box getBoundingBox();

   void setChangeListener(EntityChangeListener changeListener);

   Stream streamSelfAndPassengers();

   Stream streamPassengersAndSelf();

   void setRemoved(Entity.RemovalReason reason);

   boolean shouldSave();

   boolean isPlayer();
}
