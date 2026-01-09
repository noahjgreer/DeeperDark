package net.minecraft.client.realms.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerPlayerList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public Map serverIdToPlayers = Map.of();

   public static RealmsServerPlayerList parse(String json) {
      RealmsServerPlayerList realmsServerPlayerList = new RealmsServerPlayerList();
      ImmutableMap.Builder builder = ImmutableMap.builder();

      try {
         JsonObject jsonObject = JsonHelper.deserialize(json);
         if (JsonHelper.hasArray(jsonObject, "lists")) {
            JsonArray jsonArray = jsonObject.getAsJsonArray("lists");

            Object list;
            JsonObject jsonObject2;
            for(Iterator var5 = jsonArray.iterator(); var5.hasNext(); builder.put(JsonUtils.getLongOr("serverId", jsonObject2, -1L), list)) {
               JsonElement jsonElement = (JsonElement)var5.next();
               jsonObject2 = jsonElement.getAsJsonObject();
               String string = JsonUtils.getNullableStringOr("playerList", jsonObject2, (String)null);
               if (string != null) {
                  JsonElement jsonElement2 = LenientJsonParser.parse(string);
                  if (jsonElement2.isJsonArray()) {
                     list = parsePlayers(jsonElement2.getAsJsonArray());
                  } else {
                     list = Lists.newArrayList();
                  }
               } else {
                  list = Lists.newArrayList();
               }
            }
         }
      } catch (Exception var11) {
         LOGGER.error("Could not parse RealmsServerPlayerLists: {}", var11.getMessage());
      }

      realmsServerPlayerList.serverIdToPlayers = builder.build();
      return realmsServerPlayerList;
   }

   private static List parsePlayers(JsonArray jsonArray) {
      List list = new ArrayList(jsonArray.size());
      MinecraftSessionService minecraftSessionService = MinecraftClient.getInstance().getSessionService();
      Iterator var3 = jsonArray.iterator();

      while(var3.hasNext()) {
         JsonElement jsonElement = (JsonElement)var3.next();
         if (jsonElement.isJsonObject()) {
            UUID uUID = JsonUtils.getUuidOr("playerId", jsonElement.getAsJsonObject(), (UUID)null);
            if (uUID != null && !MinecraftClient.getInstance().uuidEquals(uUID)) {
               try {
                  ProfileResult profileResult = minecraftSessionService.fetchProfile(uUID, false);
                  if (profileResult != null) {
                     list.add(profileResult);
                  }
               } catch (Exception var7) {
                  LOGGER.error("Could not get name for {}", uUID, var7);
               }
            }
         }
      }

      return list;
   }

   public List get(long serverId) {
      List list = (List)this.serverIdToPlayers.get(serverId);
      return list != null ? list : List.of();
   }
}
