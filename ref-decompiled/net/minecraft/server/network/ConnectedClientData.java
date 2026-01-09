package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;

public record ConnectedClientData(GameProfile gameProfile, int latency, SyncedClientOptions syncedOptions, boolean transferred) {
   public ConnectedClientData(GameProfile gameProfile, int i, SyncedClientOptions syncedClientOptions, boolean bl) {
      this.gameProfile = gameProfile;
      this.latency = i;
      this.syncedOptions = syncedClientOptions;
      this.transferred = bl;
   }

   public static ConnectedClientData createDefault(GameProfile profile, boolean bl) {
      return new ConnectedClientData(profile, 0, SyncedClientOptions.createDefault(), bl);
   }

   public GameProfile gameProfile() {
      return this.gameProfile;
   }

   public int latency() {
      return this.latency;
   }

   public SyncedClientOptions syncedOptions() {
      return this.syncedOptions;
   }

   public boolean transferred() {
      return this.transferred;
   }
}
