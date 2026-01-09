package net.minecraft.world.event.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public record Vibration(RegistryEntry gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid, @Nullable Entity entity) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(GameEvent.CODEC.fieldOf("game_event").forGetter(Vibration::gameEvent), Codec.floatRange(0.0F, Float.MAX_VALUE).fieldOf("distance").forGetter(Vibration::distance), Vec3d.CODEC.fieldOf("pos").forGetter(Vibration::pos), Uuids.INT_STREAM_CODEC.lenientOptionalFieldOf("source").forGetter((vibration) -> {
         return Optional.ofNullable(vibration.uuid());
      }), Uuids.INT_STREAM_CODEC.lenientOptionalFieldOf("projectile_owner").forGetter((vibration) -> {
         return Optional.ofNullable(vibration.projectileOwnerUuid());
      })).apply(instance, (event, distance, pos, uuid, projectileOwnerUuid) -> {
         return new Vibration(event, distance, pos, (UUID)uuid.orElse((Object)null), (UUID)projectileOwnerUuid.orElse((Object)null));
      });
   });

   public Vibration(RegistryEntry gameEvent, float distance, Vec3d pos, @Nullable UUID uuid, @Nullable UUID projectileOwnerUuid) {
      this(gameEvent, distance, pos, uuid, projectileOwnerUuid, (Entity)null);
   }

   public Vibration(RegistryEntry gameEvent, float distance, Vec3d pos, @Nullable Entity entity) {
      this(gameEvent, distance, pos, entity == null ? null : entity.getUuid(), getOwnerUuid(entity), entity);
   }

   public Vibration(RegistryEntry registryEntry, float f, Vec3d vec3d, @Nullable UUID uUID, @Nullable UUID uUID2, @Nullable Entity entity) {
      this.gameEvent = registryEntry;
      this.distance = f;
      this.pos = vec3d;
      this.uuid = uUID;
      this.projectileOwnerUuid = uUID2;
      this.entity = entity;
   }

   @Nullable
   private static UUID getOwnerUuid(@Nullable Entity entity) {
      if (entity instanceof ProjectileEntity projectileEntity) {
         if (projectileEntity.getOwner() != null) {
            return projectileEntity.getOwner().getUuid();
         }
      }

      return null;
   }

   public Optional getEntity(ServerWorld world) {
      return Optional.ofNullable(this.entity).or(() -> {
         Optional var10000 = Optional.ofNullable(this.uuid);
         Objects.requireNonNull(world);
         return var10000.map(world::getEntity);
      });
   }

   public Optional getOwner(ServerWorld world) {
      return this.getEntity(world).filter((entity) -> {
         return entity instanceof ProjectileEntity;
      }).map((entity) -> {
         return (ProjectileEntity)entity;
      }).map(ProjectileEntity::getOwner).or(() -> {
         Optional var10000 = Optional.ofNullable(this.projectileOwnerUuid);
         Objects.requireNonNull(world);
         return var10000.map(world::getEntity);
      });
   }

   public RegistryEntry gameEvent() {
      return this.gameEvent;
   }

   public float distance() {
      return this.distance;
   }

   public Vec3d pos() {
      return this.pos;
   }

   @Nullable
   public UUID uuid() {
      return this.uuid;
   }

   @Nullable
   public UUID projectileOwnerUuid() {
      return this.projectileOwnerUuid;
   }

   @Nullable
   public Entity entity() {
      return this.entity;
   }
}
