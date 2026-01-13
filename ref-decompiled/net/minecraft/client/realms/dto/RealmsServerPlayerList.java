/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.dto.RealmsServerPlayerList
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.component.type.ProfileComponent
 *  net.minecraft.util.JsonHelper
 *  net.minecraft.util.LenientJsonParser
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record RealmsServerPlayerList(Map<Long, List<ProfileComponent>> serverIdToPlayers) {
    private final Map<Long, List<ProfileComponent>> serverIdToPlayers;
    private static final Logger LOGGER = LogUtils.getLogger();

    public RealmsServerPlayerList(Map<Long, List<ProfileComponent>> serverIdToPlayers) {
        this.serverIdToPlayers = serverIdToPlayers;
    }

    public static RealmsServerPlayerList parse(String json) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        try {
            JsonObject jsonObject = JsonHelper.deserialize((String)json);
            if (JsonHelper.hasArray((JsonObject)jsonObject, (String)"lists")) {
                JsonArray jsonArray = jsonObject.getAsJsonArray("lists");
                for (JsonElement jsonElement : jsonArray) {
                    JsonElement jsonElement2;
                    JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                    String string = JsonUtils.getNullableStringOr((String)"playerList", (JsonObject)jsonObject2, null);
                    List list = string != null ? ((jsonElement2 = LenientJsonParser.parse((String)string)).isJsonArray() ? RealmsServerPlayerList.parsePlayers((JsonArray)jsonElement2.getAsJsonArray()) : Lists.newArrayList()) : Lists.newArrayList();
                    builder.put((Object)JsonUtils.getLongOr((String)"serverId", (JsonObject)jsonObject2, (long)-1L), (Object)list);
                }
            }
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse RealmsServerPlayerLists", (Throwable)exception);
        }
        return new RealmsServerPlayerList((Map)builder.build());
    }

    private static List<ProfileComponent> parsePlayers(JsonArray jsonArray) {
        ArrayList<ProfileComponent> list = new ArrayList<ProfileComponent>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            UUID uUID;
            if (!jsonElement.isJsonObject() || (uUID = JsonUtils.getUuidOr((String)"playerId", (JsonObject)jsonElement.getAsJsonObject(), null)) == null || MinecraftClient.getInstance().uuidEquals(uUID)) continue;
            list.add(ProfileComponent.ofDynamic((UUID)uUID));
        }
        return list;
    }

    public List<ProfileComponent> get(long serverId) {
        List list = (List)this.serverIdToPlayers.get(serverId);
        if (list != null) {
            return list;
        }
        return List.of();
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsServerPlayerList.class, "servers", "serverIdToPlayers"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsServerPlayerList.class, "servers", "serverIdToPlayers"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsServerPlayerList.class, "servers", "serverIdToPlayers"}, this, object);
    }

    public Map<Long, List<ProfileComponent>> serverIdToPlayers() {
        return this.serverIdToPlayers;
    }
}

