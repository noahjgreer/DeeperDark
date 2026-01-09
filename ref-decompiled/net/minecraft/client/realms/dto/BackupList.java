package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class BackupList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List backups;

   public static BackupList parse(String json) {
      BackupList backupList = new BackupList();
      backupList.backups = Lists.newArrayList();

      try {
         JsonElement jsonElement = LenientJsonParser.parse(json).getAsJsonObject().get("backups");
         if (jsonElement.isJsonArray()) {
            Iterator var3 = jsonElement.getAsJsonArray().iterator();

            while(var3.hasNext()) {
               JsonElement jsonElement2 = (JsonElement)var3.next();
               backupList.backups.add(Backup.parse(jsonElement2));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse BackupList: {}", var5.getMessage());
      }

      return backupList;
   }
}
