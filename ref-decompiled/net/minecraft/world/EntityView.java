package net.minecraft.world;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public interface EntityView {
   List getOtherEntities(@Nullable Entity except, Box box, Predicate predicate);

   List getEntitiesByType(TypeFilter filter, Box box, Predicate predicate);

   default List getEntitiesByClass(Class entityClass, Box box, Predicate predicate) {
      return this.getEntitiesByType(TypeFilter.instanceOf(entityClass), box, predicate);
   }

   List getPlayers();

   default List getOtherEntities(@Nullable Entity except, Box box) {
      return this.getOtherEntities(except, box, EntityPredicates.EXCEPT_SPECTATOR);
   }

   default boolean doesNotIntersectEntities(@Nullable Entity except, VoxelShape shape) {
      if (shape.isEmpty()) {
         return true;
      } else {
         Iterator var3 = this.getOtherEntities(except, shape.getBoundingBox()).iterator();

         Entity entity;
         do {
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return true;
                     }

                     entity = (Entity)var3.next();
                  } while(entity.isRemoved());
               } while(!entity.intersectionChecked);
            } while(except != null && entity.isConnectedThroughVehicle(except));
         } while(!VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND));

         return false;
      }
   }

   default List getNonSpectatingEntities(Class entityClass, Box box) {
      return this.getEntitiesByClass(entityClass, box, EntityPredicates.EXCEPT_SPECTATOR);
   }

   default List getEntityCollisions(@Nullable Entity entity, Box box) {
      if (box.getAverageSideLength() < 1.0E-7) {
         return List.of();
      } else {
         Predicate var10000;
         if (entity == null) {
            var10000 = EntityPredicates.CAN_COLLIDE;
         } else {
            var10000 = EntityPredicates.EXCEPT_SPECTATOR;
            Objects.requireNonNull(entity);
            var10000 = var10000.and(entity::collidesWith);
         }

         Predicate predicate = var10000;
         List list = this.getOtherEntities(entity, box.expand(1.0E-7), predicate);
         if (list.isEmpty()) {
            return List.of();
         } else {
            ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(list.size());
            Iterator var6 = list.iterator();

            while(var6.hasNext()) {
               Entity entity2 = (Entity)var6.next();
               builder.add(VoxelShapes.cuboid(entity2.getBoundingBox()));
            }

            return builder.build();
         }
      }
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, @Nullable Predicate targetPredicate) {
      double d = -1.0;
      PlayerEntity playerEntity = null;
      Iterator var13 = this.getPlayers().iterator();

      while(true) {
         PlayerEntity playerEntity2;
         double e;
         do {
            do {
               do {
                  if (!var13.hasNext()) {
                     return playerEntity;
                  }

                  playerEntity2 = (PlayerEntity)var13.next();
               } while(targetPredicate != null && !targetPredicate.test(playerEntity2));

               e = playerEntity2.squaredDistanceTo(x, y, z);
            } while(!(maxDistance < 0.0) && !(e < maxDistance * maxDistance));
         } while(d != -1.0 && !(e < d));

         d = e;
         playerEntity = playerEntity2;
      }
   }

   @Nullable
   default PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
      return this.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), maxDistance, false);
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, boolean ignoreCreative) {
      Predicate predicate = ignoreCreative ? EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicates.EXCEPT_SPECTATOR;
      return this.getClosestPlayer(x, y, z, maxDistance, predicate);
   }

   default boolean isPlayerInRange(double x, double y, double z, double range) {
      Iterator var9 = this.getPlayers().iterator();

      double d;
      do {
         PlayerEntity playerEntity;
         do {
            do {
               if (!var9.hasNext()) {
                  return false;
               }

               playerEntity = (PlayerEntity)var9.next();
            } while(!EntityPredicates.EXCEPT_SPECTATOR.test(playerEntity));
         } while(!EntityPredicates.VALID_LIVING_ENTITY.test(playerEntity));

         d = playerEntity.squaredDistanceTo(x, y, z);
      } while(!(range < 0.0) && !(d < range * range));

      return true;
   }

   @Nullable
   default PlayerEntity getPlayerByUuid(UUID uuid) {
      for(int i = 0; i < this.getPlayers().size(); ++i) {
         PlayerEntity playerEntity = (PlayerEntity)this.getPlayers().get(i);
         if (uuid.equals(playerEntity.getUuid())) {
            return playerEntity;
         }
      }

      return null;
   }
}
