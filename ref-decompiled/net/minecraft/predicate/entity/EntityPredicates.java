package net.minecraft.predicate.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.scoreboard.AbstractTeam;

public final class EntityPredicates {
   public static final Predicate VALID_ENTITY = Entity::isAlive;
   public static final Predicate VALID_LIVING_ENTITY = (entity) -> {
      return entity.isAlive() && entity instanceof LivingEntity;
   };
   public static final Predicate NOT_MOUNTED = (entity) -> {
      return entity.isAlive() && !entity.hasPassengers() && !entity.hasVehicle();
   };
   public static final Predicate VALID_INVENTORIES = (entity) -> {
      return entity instanceof Inventory && entity.isAlive();
   };
   public static final Predicate EXCEPT_CREATIVE_OR_SPECTATOR = (entity) -> {
      boolean var10000;
      if (entity instanceof PlayerEntity playerEntity) {
         if (entity.isSpectator() || playerEntity.isCreative()) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   };
   public static final Predicate EXCEPT_SPECTATOR = (entity) -> {
      return !entity.isSpectator();
   };
   public static final Predicate CAN_COLLIDE;
   public static final Predicate CAN_HIT;

   private EntityPredicates() {
   }

   public static Predicate maxDistance(double x, double y, double z, double max) {
      double d = max * max;
      return (entity) -> {
         return entity != null && entity.squaredDistanceTo(x, y, z) <= d;
      };
   }

   public static Predicate canBePushedBy(Entity entity) {
      AbstractTeam abstractTeam = entity.getScoreboardTeam();
      AbstractTeam.CollisionRule collisionRule = abstractTeam == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam.getCollisionRule();
      return (Predicate)(collisionRule == AbstractTeam.CollisionRule.NEVER ? Predicates.alwaysFalse() : EXCEPT_SPECTATOR.and((entityx) -> {
         if (!entityx.isPushable()) {
            return false;
         } else {
            if (entity.getWorld().isClient) {
               label62: {
                  if (entityx instanceof PlayerEntity) {
                     PlayerEntity playerEntity = (PlayerEntity)entityx;
                     if (playerEntity.isMainPlayer()) {
                        break label62;
                     }
                  }

                  return false;
               }
            }

            AbstractTeam abstractTeam2 = entityx.getScoreboardTeam();
            AbstractTeam.CollisionRule collisionRule2 = abstractTeam2 == null ? AbstractTeam.CollisionRule.ALWAYS : abstractTeam2.getCollisionRule();
            if (collisionRule2 == AbstractTeam.CollisionRule.NEVER) {
               return false;
            } else {
               boolean bl = abstractTeam != null && abstractTeam.isEqual(abstractTeam2);
               if ((collisionRule == AbstractTeam.CollisionRule.PUSH_OWN_TEAM || collisionRule2 == AbstractTeam.CollisionRule.PUSH_OWN_TEAM) && bl) {
                  return false;
               } else {
                  return collisionRule != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS && collisionRule2 != AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS || bl;
               }
            }
         }
      }));
   }

   public static Predicate rides(Entity entity) {
      return (testedEntity) -> {
         while(true) {
            if (testedEntity.hasVehicle()) {
               testedEntity = testedEntity.getVehicle();
               if (testedEntity != entity) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   static {
      CAN_COLLIDE = EXCEPT_SPECTATOR.and((entity) -> {
         return entity.isCollidable((Entity)null);
      });
      CAN_HIT = EXCEPT_SPECTATOR.and(Entity::canHit);
   }
}
