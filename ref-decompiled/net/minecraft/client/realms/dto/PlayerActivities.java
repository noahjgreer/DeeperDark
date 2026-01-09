package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;

@Environment(EnvType.CLIENT)
public class PlayerActivities extends ValueObject {
   public long periodInMillis;
   public List playerActivityDto = Lists.newArrayList();

   public static PlayerActivities parse(String json) {
      PlayerActivities playerActivities = new PlayerActivities();

      try {
         JsonElement jsonElement = LenientJsonParser.parse(json);
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         playerActivities.periodInMillis = JsonUtils.getLongOr("periodInMillis", jsonObject, -1L);
         JsonElement jsonElement2 = jsonObject.get("playerActivityDto");
         if (jsonElement2 != null && jsonElement2.isJsonArray()) {
            JsonArray jsonArray = jsonElement2.getAsJsonArray();
            Iterator var6 = jsonArray.iterator();

            while(var6.hasNext()) {
               JsonElement jsonElement3 = (JsonElement)var6.next();
               PlayerActivity playerActivity = PlayerActivity.parse(jsonElement3.getAsJsonObject());
               playerActivities.playerActivityDto.add(playerActivity);
            }
         }
      } catch (Exception var9) {
      }

      return playerActivities;
   }
}
