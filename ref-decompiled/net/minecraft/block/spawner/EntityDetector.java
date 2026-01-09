package net.minecraft.block.spawner;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public interface EntityDetector {
   EntityDetector SURVIVAL_PLAYERS = (world, selector, center, radius, spawner) -> {
      return selector.getPlayers(world, (player) -> {
         return player.getBlockPos().isWithinDistance(center, radius) && !player.isCreative() && !player.isSpectator();
      }).stream().filter((entity) -> {
         return !spawner || hasLineOfSight(world, center.toCenterPos(), entity.getEyePos());
      }).map(Entity::getUuid).toList();
   };
   EntityDetector NON_SPECTATOR_PLAYERS = (world, selector, center, radius, spawner) -> {
      return selector.getPlayers(world, (player) -> {
         return player.getBlockPos().isWithinDistance(center, radius) && !player.isSpectator();
      }).stream().filter((entity) -> {
         return !spawner || hasLineOfSight(world, center.toCenterPos(), entity.getEyePos());
      }).map(Entity::getUuid).toList();
   };
   EntityDetector SHEEP = (world, selector, center, radius, spawner) -> {
      Box box = (new Box(center)).expand(radius);
      return selector.getEntities(world, EntityType.SHEEP, box, LivingEntity::isAlive).stream().filter((entity) -> {
         return !spawner || hasLineOfSight(world, center.toCenterPos(), entity.getEyePos());
      }).map(Entity::getUuid).toList();
   };

   List detect(ServerWorld world, Selector selector, BlockPos center, double radius, boolean spawner);

   private static boolean hasLineOfSight(World world, Vec3d pos, Vec3d entityEyePos) {
      BlockHitResult blockHitResult = world.raycast(new RaycastContext(entityEyePos, pos, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
      return blockHitResult.getBlockPos().equals(BlockPos.ofFloored(pos)) || blockHitResult.getType() == HitResult.Type.MISS;
   }

   public interface Selector {
      Selector IN_WORLD = new Selector() {
         public List getPlayers(ServerWorld world, Predicate predicate) {
            return world.getPlayers(predicate);
         }

         public List getEntities(ServerWorld world, TypeFilter typeFilter, Box box, Predicate predicate) {
            return world.getEntitiesByType(typeFilter, box, predicate);
         }
      };

      List getPlayers(ServerWorld world, Predicate predicate);

      List getEntities(ServerWorld world, TypeFilter typeFilter, Box box, Predicate predicate);

      static Selector ofPlayer(PlayerEntity player) {
         return ofPlayers(List.of(player));
      }

      static Selector ofPlayers(final List players) {
         return new Selector() {
            public List getPlayers(ServerWorld world, Predicate predicate) {
               return players.stream().filter(predicate).toList();
            }

            public List getEntities(ServerWorld world, TypeFilter typeFilter, Box box, Predicate predicate) {
               Stream var10000 = players.stream();
               Objects.requireNonNull(typeFilter);
               return var10000.map(typeFilter::downcast).filter(Objects::nonNull).filter(predicate).toList();
            }
         };
      }
   }
}
