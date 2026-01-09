package net.minecraft.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public interface EntityLookupView extends EntityView {
   ServerWorld toServerWorld();

   @Nullable
   default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity) {
      return (PlayerEntity)this.getClosestEntity(this.getPlayers(), targetPredicate, entity, entity.getX(), entity.getY(), entity.getZ());
   }

   @Nullable
   default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, LivingEntity entity, double x, double y, double z) {
      return (PlayerEntity)this.getClosestEntity(this.getPlayers(), targetPredicate, entity, x, y, z);
   }

   @Nullable
   default PlayerEntity getClosestPlayer(TargetPredicate targetPredicate, double x, double y, double z) {
      return (PlayerEntity)this.getClosestEntity(this.getPlayers(), targetPredicate, (LivingEntity)null, x, y, z);
   }

   @Nullable
   default LivingEntity getClosestEntity(Class clazz, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z, Box box) {
      return this.getClosestEntity(this.getEntitiesByClass(clazz, box, (potentialEntity) -> {
         return true;
      }), targetPredicate, entity, x, y, z);
   }

   @Nullable
   default LivingEntity getClosestEntity(List entities, TargetPredicate targetPredicate, @Nullable LivingEntity entity, double x, double y, double z) {
      double d = -1.0;
      LivingEntity livingEntity = null;
      Iterator var13 = entities.iterator();

      while(true) {
         LivingEntity livingEntity2;
         double e;
         do {
            do {
               if (!var13.hasNext()) {
                  return livingEntity;
               }

               livingEntity2 = (LivingEntity)var13.next();
            } while(!targetPredicate.test(this.toServerWorld(), entity, livingEntity2));

            e = livingEntity2.squaredDistanceTo(x, y, z);
         } while(d != -1.0 && !(e < d));

         d = e;
         livingEntity = livingEntity2;
      }
   }

   default List getPlayers(TargetPredicate targetPredicate, LivingEntity entity, Box box) {
      List list = new ArrayList();
      Iterator var5 = this.getPlayers().iterator();

      while(var5.hasNext()) {
         PlayerEntity playerEntity = (PlayerEntity)var5.next();
         if (box.contains(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ()) && targetPredicate.test(this.toServerWorld(), entity, playerEntity)) {
            list.add(playerEntity);
         }
      }

      return list;
   }

   default List getTargets(Class clazz, TargetPredicate targetPredicate, LivingEntity entity, Box box) {
      List list = this.getEntitiesByClass(clazz, box, (entityx) -> {
         return true;
      });
      List list2 = new ArrayList();
      Iterator var7 = list.iterator();

      while(var7.hasNext()) {
         LivingEntity livingEntity = (LivingEntity)var7.next();
         if (targetPredicate.test(this.toServerWorld(), entity, livingEntity)) {
            list2.add(livingEntity);
         }
      }

      return list2;
   }
}
