package net.minecraft.world.event;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public record GameEvent(int notificationRadius) {
   public static final RegistryEntry.Reference BLOCK_ACTIVATE = register("block_activate");
   public static final RegistryEntry.Reference BLOCK_ATTACH = register("block_attach");
   public static final RegistryEntry.Reference BLOCK_CHANGE = register("block_change");
   public static final RegistryEntry.Reference BLOCK_CLOSE = register("block_close");
   public static final RegistryEntry.Reference BLOCK_DEACTIVATE = register("block_deactivate");
   public static final RegistryEntry.Reference BLOCK_DESTROY = register("block_destroy");
   public static final RegistryEntry.Reference BLOCK_DETACH = register("block_detach");
   public static final RegistryEntry.Reference BLOCK_OPEN = register("block_open");
   public static final RegistryEntry.Reference BLOCK_PLACE = register("block_place");
   public static final RegistryEntry.Reference CONTAINER_CLOSE = register("container_close");
   public static final RegistryEntry.Reference CONTAINER_OPEN = register("container_open");
   public static final RegistryEntry.Reference DRINK = register("drink");
   public static final RegistryEntry.Reference EAT = register("eat");
   public static final RegistryEntry.Reference ELYTRA_GLIDE = register("elytra_glide");
   public static final RegistryEntry.Reference ENTITY_DAMAGE = register("entity_damage");
   public static final RegistryEntry.Reference ENTITY_DIE = register("entity_die");
   public static final RegistryEntry.Reference ENTITY_DISMOUNT = register("entity_dismount");
   public static final RegistryEntry.Reference ENTITY_INTERACT = register("entity_interact");
   public static final RegistryEntry.Reference ENTITY_MOUNT = register("entity_mount");
   public static final RegistryEntry.Reference ENTITY_PLACE = register("entity_place");
   public static final RegistryEntry.Reference ENTITY_ACTION = register("entity_action");
   public static final RegistryEntry.Reference EQUIP = register("equip");
   public static final RegistryEntry.Reference EXPLODE = register("explode");
   public static final RegistryEntry.Reference FLAP = register("flap");
   public static final RegistryEntry.Reference FLUID_PICKUP = register("fluid_pickup");
   public static final RegistryEntry.Reference FLUID_PLACE = register("fluid_place");
   public static final RegistryEntry.Reference HIT_GROUND = register("hit_ground");
   public static final RegistryEntry.Reference INSTRUMENT_PLAY = register("instrument_play");
   public static final RegistryEntry.Reference ITEM_INTERACT_FINISH = register("item_interact_finish");
   public static final RegistryEntry.Reference ITEM_INTERACT_START = register("item_interact_start");
   public static final RegistryEntry.Reference JUKEBOX_PLAY = register("jukebox_play", 10);
   public static final RegistryEntry.Reference JUKEBOX_STOP_PLAY = register("jukebox_stop_play", 10);
   public static final RegistryEntry.Reference LIGHTNING_STRIKE = register("lightning_strike");
   public static final RegistryEntry.Reference NOTE_BLOCK_PLAY = register("note_block_play");
   public static final RegistryEntry.Reference PRIME_FUSE = register("prime_fuse");
   public static final RegistryEntry.Reference PROJECTILE_LAND = register("projectile_land");
   public static final RegistryEntry.Reference PROJECTILE_SHOOT = register("projectile_shoot");
   public static final RegistryEntry.Reference SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
   public static final RegistryEntry.Reference SHEAR = register("shear");
   public static final RegistryEntry.Reference SHRIEK = register("shriek", 32);
   public static final RegistryEntry.Reference SPLASH = register("splash");
   public static final RegistryEntry.Reference STEP = register("step");
   public static final RegistryEntry.Reference SWIM = register("swim");
   public static final RegistryEntry.Reference TELEPORT = register("teleport");
   public static final RegistryEntry.Reference UNEQUIP = register("unequip");
   public static final RegistryEntry.Reference RESONATE_1 = register("resonate_1");
   public static final RegistryEntry.Reference RESONATE_2 = register("resonate_2");
   public static final RegistryEntry.Reference RESONATE_3 = register("resonate_3");
   public static final RegistryEntry.Reference RESONATE_4 = register("resonate_4");
   public static final RegistryEntry.Reference RESONATE_5 = register("resonate_5");
   public static final RegistryEntry.Reference RESONATE_6 = register("resonate_6");
   public static final RegistryEntry.Reference RESONATE_7 = register("resonate_7");
   public static final RegistryEntry.Reference RESONATE_8 = register("resonate_8");
   public static final RegistryEntry.Reference RESONATE_9 = register("resonate_9");
   public static final RegistryEntry.Reference RESONATE_10 = register("resonate_10");
   public static final RegistryEntry.Reference RESONATE_11 = register("resonate_11");
   public static final RegistryEntry.Reference RESONATE_12 = register("resonate_12");
   public static final RegistryEntry.Reference RESONATE_13 = register("resonate_13");
   public static final RegistryEntry.Reference RESONATE_14 = register("resonate_14");
   public static final RegistryEntry.Reference RESONATE_15 = register("resonate_15");
   public static final int DEFAULT_RANGE = 16;
   public static final Codec CODEC;

   public GameEvent(int range) {
      this.notificationRadius = range;
   }

   public static RegistryEntry registerAndGetDefault(Registry registry) {
      return BLOCK_ACTIVATE;
   }

   public int notificationRadius() {
      return this.notificationRadius;
   }

   private static RegistryEntry.Reference register(String id) {
      return register(id, 16);
   }

   private static RegistryEntry.Reference register(String id, int range) {
      return Registry.registerReference(Registries.GAME_EVENT, (Identifier)Identifier.ofVanilla(id), new GameEvent(range));
   }

   static {
      CODEC = RegistryFixedCodec.of(RegistryKeys.GAME_EVENT);
   }

   public static final class Message implements Comparable {
      private final RegistryEntry event;
      private final Vec3d emitterPos;
      private final Emitter emitter;
      private final GameEventListener listener;
      private final double distanceTraveled;

      public Message(RegistryEntry event, Vec3d emitterPos, Emitter emitter, GameEventListener listener, Vec3d listenerPos) {
         this.event = event;
         this.emitterPos = emitterPos;
         this.emitter = emitter;
         this.listener = listener;
         this.distanceTraveled = emitterPos.squaredDistanceTo(listenerPos);
      }

      public int compareTo(Message message) {
         return Double.compare(this.distanceTraveled, message.distanceTraveled);
      }

      public RegistryEntry getEvent() {
         return this.event;
      }

      public Vec3d getEmitterPos() {
         return this.emitterPos;
      }

      public Emitter getEmitter() {
         return this.emitter;
      }

      public GameEventListener getListener() {
         return this.listener;
      }

      // $FF: synthetic method
      public int compareTo(final Object other) {
         return this.compareTo((Message)other);
      }
   }

   public static record Emitter(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
      public Emitter(@Nullable Entity entity, @Nullable BlockState blockState) {
         this.sourceEntity = entity;
         this.affectedState = blockState;
      }

      public static Emitter of(@Nullable Entity sourceEntity) {
         return new Emitter(sourceEntity, (BlockState)null);
      }

      public static Emitter of(@Nullable BlockState affectedState) {
         return new Emitter((Entity)null, affectedState);
      }

      public static Emitter of(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
         return new Emitter(sourceEntity, affectedState);
      }

      @Nullable
      public Entity sourceEntity() {
         return this.sourceEntity;
      }

      @Nullable
      public BlockState affectedState() {
         return this.affectedState;
      }
   }
}
