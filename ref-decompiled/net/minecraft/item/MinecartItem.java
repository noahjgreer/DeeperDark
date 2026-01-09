package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MinecartItem extends Item {
   private final EntityType type;

   public MinecartItem(EntityType type, Item.Settings settings) {
      super(settings);
      this.type = type;
   }

   public ActionResult useOnBlock(ItemUsageContext context) {
      World world = context.getWorld();
      BlockPos blockPos = context.getBlockPos();
      BlockState blockState = world.getBlockState(blockPos);
      if (!blockState.isIn(BlockTags.RAILS)) {
         return ActionResult.FAIL;
      } else {
         ItemStack itemStack = context.getStack();
         RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? (RailShape)blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
         double d = 0.0;
         if (railShape.isAscending()) {
            d = 0.5;
         }

         Vec3d vec3d = new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5);
         AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, vec3d.x, vec3d.y, vec3d.z, this.type, SpawnReason.DISPENSER, itemStack, context.getPlayer());
         if (abstractMinecartEntity == null) {
            return ActionResult.FAIL;
         } else {
            if (AbstractMinecartEntity.areMinecartImprovementsEnabled(world)) {
               List list = world.getOtherEntities((Entity)null, abstractMinecartEntity.getBoundingBox());
               Iterator var12 = list.iterator();

               while(var12.hasNext()) {
                  Entity entity = (Entity)var12.next();
                  if (entity instanceof AbstractMinecartEntity) {
                     return ActionResult.FAIL;
                  }
               }
            }

            if (world instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)world;
               serverWorld.spawnEntity(abstractMinecartEntity);
               serverWorld.emitGameEvent(GameEvent.ENTITY_PLACE, blockPos, GameEvent.Emitter.of(context.getPlayer(), serverWorld.getBlockState(blockPos.down())));
            }

            itemStack.decrement(1);
            return ActionResult.SUCCESS;
         }
      }
   }
}
