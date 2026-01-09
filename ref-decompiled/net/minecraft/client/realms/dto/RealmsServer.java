package net.minecraft.client.realms.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.util.DontSerialize;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServer extends ValueObject implements RealmsSerializable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_PARENT = -1;
   public static final Text REALM_CLOSED_TEXT = Text.translatable("mco.play.button.realm.closed");
   @SerializedName("id")
   public long id = -1L;
   @Nullable
   @SerializedName("remoteSubscriptionId")
   public String remoteSubscriptionId;
   @Nullable
   @SerializedName("name")
   public String name;
   @SerializedName("motd")
   public String description = "";
   @SerializedName("state")
   public State state;
   @Nullable
   @SerializedName("owner")
   public String owner;
   @SerializedName("ownerUUID")
   @JsonAdapter(UUIDTypeAdapter.class)
   public UUID ownerUUID;
   @SerializedName("players")
   public List players;
   @SerializedName("slots")
   private List emptySlots;
   @DontSerialize
   public Map slots;
   @SerializedName("expired")
   public boolean expired;
   @SerializedName("expiredTrial")
   public boolean expiredTrial;
   @SerializedName("daysLeft")
   public int daysLeft;
   @SerializedName("worldType")
   public WorldType worldType;
   @SerializedName("isHardcore")
   public boolean hardcore;
   @SerializedName("gameMode")
   public int gameMode;
   @SerializedName("activeSlot")
   public int activeSlot;
   @Nullable
   @SerializedName("minigameName")
   public String minigameName;
   @SerializedName("minigameId")
   public int minigameId;
   @Nullable
   @SerializedName("minigameImage")
   public String minigameImage;
   @SerializedName("parentWorldId")
   public long parentWorldId;
   @Nullable
   @SerializedName("parentWorldName")
   public String parentWorldName;
   @SerializedName("activeVersion")
   public String activeVersion;
   @SerializedName("compatibility")
   public Compatibility compatibility;
   @Nullable
   @SerializedName("regionSelectionPreference")
   public RealmsRegionSelectionPreference regionSelectionPreference;

   public RealmsServer() {
      this.state = RealmsServer.State.CLOSED;
      this.ownerUUID = Util.NIL_UUID;
      this.players = Lists.newArrayList();
      this.emptySlots = getEmptySlots();
      this.slots = new HashMap();
      this.expiredTrial = false;
      this.worldType = RealmsServer.WorldType.NORMAL;
      this.hardcore = false;
      this.gameMode = -1;
      this.activeSlot = -1;
      this.minigameId = -1;
      this.parentWorldId = -1L;
      this.activeVersion = "";
      this.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
   }

   public String getDescription() {
      return this.description;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getMinigameName() {
      return this.minigameName;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public static RealmsServer parse(CheckedGson gson, String json) {
      try {
         RealmsServer realmsServer = (RealmsServer)gson.fromJson(json, RealmsServer.class);
         if (realmsServer == null) {
            LOGGER.error("Could not parse McoServer: {}", json);
            return new RealmsServer();
         } else {
            replaceNullsWithDefaults(realmsServer);
            return realmsServer;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServer: {}", var3.getMessage());
         return new RealmsServer();
      }
   }

   public static void replaceNullsWithDefaults(RealmsServer server) {
      if (server.players == null) {
         server.players = Lists.newArrayList();
      }

      if (server.emptySlots == null) {
         server.emptySlots = getEmptySlots();
      }

      if (server.slots == null) {
         server.slots = new HashMap();
      }

      if (server.worldType == null) {
         server.worldType = RealmsServer.WorldType.NORMAL;
      }

      if (server.activeVersion == null) {
         server.activeVersion = "";
      }

      if (server.compatibility == null) {
         server.compatibility = RealmsServer.Compatibility.UNVERIFIABLE;
      }

      if (server.regionSelectionPreference == null) {
         server.regionSelectionPreference = RealmsRegionSelectionPreference.DEFAULT;
      }

      sortInvited(server);
      populateSlots(server);
   }

   private static void sortInvited(RealmsServer server) {
      server.players.sort((a, b) -> {
         return ComparisonChain.start().compareFalseFirst(b.isAccepted(), a.isAccepted()).compare(a.getName().toLowerCase(Locale.ROOT), b.getName().toLowerCase(Locale.ROOT)).result();
      });
   }

   private static void populateSlots(RealmsServer server) {
      server.emptySlots.forEach((slot) -> {
         server.slots.put(slot.slotId, slot);
      });

      for(int i = 1; i <= 3; ++i) {
         if (!server.slots.containsKey(i)) {
            server.slots.put(i, RealmsSlot.create(i));
         }
      }

   }

   private static List getEmptySlots() {
      List list = new ArrayList();
      list.add(RealmsSlot.create(1));
      list.add(RealmsSlot.create(2));
      list.add(RealmsSlot.create(3));
      return list;
   }

   public boolean isCompatible() {
      return this.compatibility.isCompatible();
   }

   public boolean needsUpgrade() {
      return this.compatibility.needsUpgrade();
   }

   public boolean needsDowngrade() {
      return this.compatibility.needsDowngrade();
   }

   public boolean shouldAllowPlay() {
      boolean bl = !this.expired && this.state == RealmsServer.State.OPEN;
      return bl && (this.isCompatible() || this.needsUpgrade() || this.isPlayerOwner());
   }

   private boolean isPlayerOwner() {
      return MinecraftClient.getInstance().uuidEquals(this.ownerUUID);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.name, this.description, this.state, this.owner, this.expired});
   }

   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (o == this) {
         return true;
      } else if (o.getClass() != this.getClass()) {
         return false;
      } else {
         RealmsServer realmsServer = (RealmsServer)o;
         return (new EqualsBuilder()).append(this.id, realmsServer.id).append(this.name, realmsServer.name).append(this.description, realmsServer.description).append(this.state, realmsServer.state).append(this.owner, realmsServer.owner).append(this.expired, realmsServer.expired).append(this.worldType, this.worldType).isEquals();
      }
   }

   public RealmsServer clone() {
      RealmsServer realmsServer = new RealmsServer();
      realmsServer.id = this.id;
      realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
      realmsServer.name = this.name;
      realmsServer.description = this.description;
      realmsServer.state = this.state;
      realmsServer.owner = this.owner;
      realmsServer.players = this.players;
      realmsServer.emptySlots = this.emptySlots.stream().map(RealmsSlot::clone).toList();
      realmsServer.slots = this.cloneSlots(this.slots);
      realmsServer.expired = this.expired;
      realmsServer.expiredTrial = this.expiredTrial;
      realmsServer.daysLeft = this.daysLeft;
      realmsServer.worldType = this.worldType;
      realmsServer.hardcore = this.hardcore;
      realmsServer.gameMode = this.gameMode;
      realmsServer.ownerUUID = this.ownerUUID;
      realmsServer.minigameName = this.minigameName;
      realmsServer.activeSlot = this.activeSlot;
      realmsServer.minigameId = this.minigameId;
      realmsServer.minigameImage = this.minigameImage;
      realmsServer.parentWorldName = this.parentWorldName;
      realmsServer.parentWorldId = this.parentWorldId;
      realmsServer.activeVersion = this.activeVersion;
      realmsServer.compatibility = this.compatibility;
      realmsServer.regionSelectionPreference = this.regionSelectionPreference != null ? this.regionSelectionPreference.clone() : null;
      return realmsServer;
   }

   public Map cloneSlots(Map slots) {
      Map map = Maps.newHashMap();
      Iterator var3 = slots.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         map.put((Integer)entry.getKey(), new RealmsSlot((Integer)entry.getKey(), ((RealmsSlot)entry.getValue()).options.clone(), ((RealmsSlot)entry.getValue()).settings));
      }

      return map;
   }

   public boolean isPrerelease() {
      return this.parentWorldId != -1L;
   }

   public boolean isMinigame() {
      return this.worldType == RealmsServer.WorldType.MINIGAME;
   }

   public String getWorldName(int slotId) {
      if (this.name == null) {
         return ((RealmsSlot)this.slots.get(slotId)).options.getSlotName(slotId);
      } else {
         String var10000 = this.name;
         return var10000 + " (" + ((RealmsSlot)this.slots.get(slotId)).options.getSlotName(slotId) + ")";
      }
   }

   public ServerInfo createServerInfo(String address) {
      return new ServerInfo((String)Objects.requireNonNullElse(this.name, "unknown server"), address, ServerInfo.ServerType.REALM);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }

   @Environment(EnvType.CLIENT)
   public static enum State {
      CLOSED,
      OPEN,
      UNINITIALIZED;

      // $FF: synthetic method
      private static State[] method_36848() {
         return new State[]{CLOSED, OPEN, UNINITIALIZED};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum WorldType {
      NORMAL,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;

      // $FF: synthetic method
      private static WorldType[] method_36849() {
         return new WorldType[]{NORMAL, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
      }
   }

   @Environment(EnvType.CLIENT)
   public static enum Compatibility {
      UNVERIFIABLE,
      INCOMPATIBLE,
      RELEASE_TYPE_INCOMPATIBLE,
      NEEDS_DOWNGRADE,
      NEEDS_UPGRADE,
      COMPATIBLE;

      public boolean isCompatible() {
         return this == COMPATIBLE;
      }

      public boolean needsUpgrade() {
         return this == NEEDS_UPGRADE;
      }

      public boolean needsDowngrade() {
         return this == NEEDS_DOWNGRADE;
      }

      // $FF: synthetic method
      private static Compatibility[] method_54368() {
         return new Compatibility[]{UNVERIFIABLE, INCOMPATIBLE, RELEASE_TYPE_INCOMPATIBLE, NEEDS_DOWNGRADE, NEEDS_UPGRADE, COMPATIBLE};
      }
   }

   @Environment(EnvType.CLIENT)
   public static class McoServerComparator implements Comparator {
      private final String refOwner;

      public McoServerComparator(String owner) {
         this.refOwner = owner;
      }

      public int compare(RealmsServer realmsServer, RealmsServer realmsServer2) {
         return ComparisonChain.start().compareTrueFirst(realmsServer.isPrerelease(), realmsServer2.isPrerelease()).compareTrueFirst(realmsServer.state == RealmsServer.State.UNINITIALIZED, realmsServer2.state == RealmsServer.State.UNINITIALIZED).compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial).compareTrueFirst(Objects.equals(realmsServer.owner, this.refOwner), Objects.equals(realmsServer2.owner, this.refOwner)).compareFalseFirst(realmsServer.expired, realmsServer2.expired).compareTrueFirst(realmsServer.state == RealmsServer.State.OPEN, realmsServer2.state == RealmsServer.State.OPEN).compare(realmsServer.id, realmsServer2.id).result();
      }

      // $FF: synthetic method
      public int compare(final Object one, final Object two) {
         return this.compare((RealmsServer)one, (RealmsServer)two);
      }
   }
}
