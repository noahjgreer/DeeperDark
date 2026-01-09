package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PendingInvitesList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List pendingInvites = Lists.newArrayList();

   public static PendingInvitesList parse(String json) {
      PendingInvitesList pendingInvitesList = new PendingInvitesList();

      try {
         JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
         if (jsonObject.get("invites").isJsonArray()) {
            Iterator var3 = jsonObject.get("invites").getAsJsonArray().iterator();

            while(var3.hasNext()) {
               JsonElement jsonElement = (JsonElement)var3.next();
               pendingInvitesList.pendingInvites.add(PendingInvite.parse(jsonElement.getAsJsonObject()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse PendingInvitesList: {}", var5.getMessage());
      }

      return pendingInvitesList;
   }
}
