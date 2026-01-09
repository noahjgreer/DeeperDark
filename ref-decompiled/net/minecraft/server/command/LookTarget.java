package net.minecraft.server.command;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface LookTarget {
   void look(ServerCommandSource source, Entity entity);

   public static record LookAtPosition(Vec3d position) implements LookTarget {
      public LookAtPosition(Vec3d vec3d) {
         this.position = vec3d;
      }

      public void look(ServerCommandSource source, Entity entity) {
         entity.lookAt(source.getEntityAnchor(), this.position);
      }

      public Vec3d position() {
         return this.position;
      }
   }

   public static record LookAtEntity(Entity entity, EntityAnchorArgumentType.EntityAnchor anchor) implements LookTarget {
      public LookAtEntity(Entity entity, EntityAnchorArgumentType.EntityAnchor entityAnchor) {
         this.entity = entity;
         this.anchor = entityAnchor;
      }

      public void look(ServerCommandSource source, Entity entity) {
         if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.lookAtEntity(source.getEntityAnchor(), this.entity, this.anchor);
         } else {
            entity.lookAt(source.getEntityAnchor(), this.anchor.positionAt(this.entity));
         }

      }

      public Entity entity() {
         return this.entity;
      }

      public EntityAnchorArgumentType.EntityAnchor anchor() {
         return this.anchor;
      }
   }
}
