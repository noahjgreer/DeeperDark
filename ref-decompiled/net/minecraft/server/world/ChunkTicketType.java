package net.minecraft.server.world;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public record ChunkTicketType(long expiryTicks, boolean persist, Use use) {
   public static final long NO_EXPIRATION = 0L;
   public static final ChunkTicketType START;
   public static final ChunkTicketType DRAGON;
   public static final ChunkTicketType PLAYER_LOADING;
   public static final ChunkTicketType PLAYER_SIMULATION;
   public static final ChunkTicketType FORCED;
   public static final ChunkTicketType PORTAL;
   public static final ChunkTicketType ENDER_PEARL;
   public static final ChunkTicketType UNKNOWN;

   public ChunkTicketType(long l, boolean bl, Use use) {
      this.expiryTicks = l;
      this.persist = bl;
      this.use = use;
   }

   private static ChunkTicketType register(String id, long expiryTicks, boolean persist, Use use) {
      return (ChunkTicketType)Registry.register(Registries.TICKET_TYPE, (String)id, new ChunkTicketType(expiryTicks, persist, use));
   }

   public boolean isForLoading() {
      return this.use == ChunkTicketType.Use.LOADING || this.use == ChunkTicketType.Use.LOADING_AND_SIMULATION;
   }

   public boolean isForSimulation() {
      return this.use == ChunkTicketType.Use.SIMULATION || this.use == ChunkTicketType.Use.LOADING_AND_SIMULATION;
   }

   public boolean canExpire() {
      return this.expiryTicks != 0L;
   }

   public long expiryTicks() {
      return this.expiryTicks;
   }

   public boolean persist() {
      return this.persist;
   }

   public Use use() {
      return this.use;
   }

   static {
      START = register("start", 0L, false, ChunkTicketType.Use.LOADING_AND_SIMULATION);
      DRAGON = register("dragon", 0L, false, ChunkTicketType.Use.LOADING_AND_SIMULATION);
      PLAYER_LOADING = register("player_loading", 0L, false, ChunkTicketType.Use.LOADING);
      PLAYER_SIMULATION = register("player_simulation", 0L, false, ChunkTicketType.Use.SIMULATION);
      FORCED = register("forced", 0L, true, ChunkTicketType.Use.LOADING_AND_SIMULATION);
      PORTAL = register("portal", 300L, true, ChunkTicketType.Use.LOADING_AND_SIMULATION);
      ENDER_PEARL = register("ender_pearl", 40L, false, ChunkTicketType.Use.LOADING_AND_SIMULATION);
      UNKNOWN = register("unknown", 1L, false, ChunkTicketType.Use.LOADING);
   }

   public static enum Use {
      LOADING,
      SIMULATION,
      LOADING_AND_SIMULATION;

      // $FF: synthetic method
      private static Use[] method_66029() {
         return new Use[]{LOADING, SIMULATION, LOADING_AND_SIMULATION};
      }
   }
}
