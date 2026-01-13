/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.annotations.JsonAdapter
 *  com.google.gson.annotations.SerializedName
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UUIDTypeAdapter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ServerInfo
 *  net.minecraft.client.network.ServerInfo$ServerType
 *  net.minecraft.client.realms.CheckedGson
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.dto.PlayerInfo
 *  net.minecraft.client.realms.dto.RealmsRegionSelectionPreference
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$Compatibility
 *  net.minecraft.client.realms.dto.RealmsServer$State
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.dto.RealmsSlot
 *  net.minecraft.client.realms.dto.ValueObject
 *  net.minecraft.client.realms.util.DontSerialize
 *  net.minecraft.text.Text
 *  net.minecraft.util.Util
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsSlot;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.DontSerialize;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class RealmsServer
extends ValueObject
implements RealmsSerializable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_PARENT = -1;
    public static final Text REALM_CLOSED_TEXT = Text.translatable((String)"mco.play.button.realm.closed");
    @SerializedName(value="id")
    public long id = -1L;
    @SerializedName(value="remoteSubscriptionId")
    public @Nullable String remoteSubscriptionId;
    @SerializedName(value="name")
    public @Nullable String name;
    @SerializedName(value="motd")
    public String description = "";
    @SerializedName(value="state")
    public State state = State.CLOSED;
    @SerializedName(value="owner")
    public @Nullable String owner;
    @SerializedName(value="ownerUUID")
    @JsonAdapter(value=UUIDTypeAdapter.class)
    public UUID ownerUUID = Util.NIL_UUID;
    @SerializedName(value="players")
    public List<PlayerInfo> players = Lists.newArrayList();
    @SerializedName(value="slots")
    private List<RealmsSlot> emptySlots = RealmsServer.getEmptySlots();
    @DontSerialize
    public Map<Integer, RealmsSlot> slots = new HashMap();
    @SerializedName(value="expired")
    public boolean expired;
    @SerializedName(value="expiredTrial")
    public boolean expiredTrial = false;
    @SerializedName(value="daysLeft")
    public int daysLeft;
    @SerializedName(value="worldType")
    public WorldType worldType = WorldType.NORMAL;
    @SerializedName(value="isHardcore")
    public boolean hardcore = false;
    @SerializedName(value="gameMode")
    public int gameMode = -1;
    @SerializedName(value="activeSlot")
    public int activeSlot = -1;
    @SerializedName(value="minigameName")
    public @Nullable String minigameName;
    @SerializedName(value="minigameId")
    public int minigameId = -1;
    @SerializedName(value="minigameImage")
    public @Nullable String minigameImage;
    @SerializedName(value="parentWorldId")
    public long parentWorldId = -1L;
    @SerializedName(value="parentWorldName")
    public @Nullable String parentWorldName;
    @SerializedName(value="activeVersion")
    public String activeVersion = "";
    @SerializedName(value="compatibility")
    public Compatibility compatibility = Compatibility.UNVERIFIABLE;
    @SerializedName(value="regionSelectionPreference")
    public @Nullable RealmsRegionSelectionPreference regionSelectionPreference;

    public String getDescription() {
        return this.description;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public @Nullable String getMinigameName() {
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
                LOGGER.error("Could not parse McoServer: {}", (Object)json);
                return new RealmsServer();
            }
            RealmsServer.replaceNullsWithDefaults((RealmsServer)realmsServer);
            return realmsServer;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse McoServer", (Throwable)exception);
            return new RealmsServer();
        }
    }

    public static void replaceNullsWithDefaults(RealmsServer server) {
        if (server.players == null) {
            server.players = Lists.newArrayList();
        }
        if (server.emptySlots == null) {
            server.emptySlots = RealmsServer.getEmptySlots();
        }
        if (server.slots == null) {
            server.slots = new HashMap();
        }
        if (server.worldType == null) {
            server.worldType = WorldType.NORMAL;
        }
        if (server.activeVersion == null) {
            server.activeVersion = "";
        }
        if (server.compatibility == null) {
            server.compatibility = Compatibility.UNVERIFIABLE;
        }
        if (server.regionSelectionPreference == null) {
            server.regionSelectionPreference = RealmsRegionSelectionPreference.DEFAULT;
        }
        RealmsServer.sortInvited((RealmsServer)server);
        RealmsServer.populateSlots((RealmsServer)server);
    }

    private static void sortInvited(RealmsServer server) {
        server.players.sort((a, b) -> ComparisonChain.start().compareFalseFirst(b.accepted, a.accepted).compare((Comparable)((Object)a.name.toLowerCase(Locale.ROOT)), (Comparable)((Object)b.name.toLowerCase(Locale.ROOT))).result());
    }

    private static void populateSlots(RealmsServer server) {
        server.emptySlots.forEach(slot -> realmsServer.slots.put(slot.slotId, slot));
        for (int i = 1; i <= 3; ++i) {
            if (server.slots.containsKey(i)) continue;
            server.slots.put(i, RealmsSlot.create((int)i));
        }
    }

    private static List<RealmsSlot> getEmptySlots() {
        ArrayList<RealmsSlot> list = new ArrayList<RealmsSlot>();
        list.add(RealmsSlot.create((int)1));
        list.add(RealmsSlot.create((int)2));
        list.add(RealmsSlot.create((int)3));
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
        boolean bl = !this.expired && this.state == State.OPEN;
        return bl && (this.isCompatible() || this.needsUpgrade() || this.isPlayerOwner());
    }

    private boolean isPlayerOwner() {
        return MinecraftClient.getInstance().uuidEquals(this.ownerUUID);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.description, this.state, this.owner, this.expired);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer realmsServer = (RealmsServer)o;
        return new EqualsBuilder().append(this.id, realmsServer.id).append((Object)this.name, (Object)realmsServer.name).append((Object)this.description, (Object)realmsServer.description).append((Object)this.state, (Object)realmsServer.state).append((Object)this.owner, (Object)realmsServer.owner).append(this.expired, realmsServer.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer copy() {
        RealmsServer realmsServer = new RealmsServer();
        realmsServer.id = this.id;
        realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
        realmsServer.name = this.name;
        realmsServer.description = this.description;
        realmsServer.state = this.state;
        realmsServer.owner = this.owner;
        realmsServer.players = this.players;
        realmsServer.emptySlots = this.emptySlots.stream().map(RealmsSlot::copy).toList();
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
        realmsServer.regionSelectionPreference = this.regionSelectionPreference != null ? this.regionSelectionPreference.copy() : null;
        return realmsServer;
    }

    public Map<Integer, RealmsSlot> cloneSlots(Map<Integer, RealmsSlot> slots) {
        HashMap map = Maps.newHashMap();
        for (Map.Entry<Integer, RealmsSlot> entry : slots.entrySet()) {
            map.put(entry.getKey(), new RealmsSlot(entry.getKey().intValue(), entry.getValue().options.copy(), entry.getValue().settings));
        }
        return map;
    }

    public boolean isPrerelease() {
        return this.parentWorldId != -1L;
    }

    public boolean isMinigame() {
        return this.worldType == WorldType.MINIGAME;
    }

    public String getWorldName(int slotId) {
        if (this.name == null) {
            return ((RealmsSlot)this.slots.get((Object)Integer.valueOf((int)slotId))).options.getSlotName(slotId);
        }
        return this.name + " (" + ((RealmsSlot)this.slots.get((Object)Integer.valueOf((int)slotId))).options.getSlotName(slotId) + ")";
    }

    public ServerInfo createServerInfo(String address) {
        return new ServerInfo(Objects.requireNonNullElse(this.name, "unknown server"), address, ServerInfo.ServerType.REALM);
    }
}

