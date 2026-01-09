package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsSerializable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerList extends ValueObject implements RealmsSerializable {
   private static final Logger LOGGER = LogUtils.getLogger();
   @SerializedName("servers")
   public List servers = new ArrayList();

   public static RealmsServerList parse(CheckedGson gson, String json) {
      try {
         RealmsServerList realmsServerList = (RealmsServerList)gson.fromJson(json, RealmsServerList.class);
         if (realmsServerList == null) {
            LOGGER.error("Could not parse McoServerList: {}", json);
            return new RealmsServerList();
         } else {
            realmsServerList.servers.forEach(RealmsServer::replaceNullsWithDefaults);
            return realmsServerList;
         }
      } catch (Exception var3) {
         LOGGER.error("Could not parse McoServerList: {}", var3.getMessage());
         return new RealmsServerList();
      }
   }
}
