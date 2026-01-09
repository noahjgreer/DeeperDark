package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.LenientJsonParser;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldTemplatePaginatedList extends ValueObject {
   private static final Logger LOGGER = LogUtils.getLogger();
   public List templates;
   public int page;
   public int size;
   public int total;

   public WorldTemplatePaginatedList() {
   }

   public WorldTemplatePaginatedList(int size) {
      this.templates = Collections.emptyList();
      this.page = 0;
      this.size = size;
      this.total = -1;
   }

   public boolean isLastPage() {
      return this.page * this.size >= this.total && this.page > 0 && this.total > 0 && this.size > 0;
   }

   public static WorldTemplatePaginatedList parse(String json) {
      WorldTemplatePaginatedList worldTemplatePaginatedList = new WorldTemplatePaginatedList();
      worldTemplatePaginatedList.templates = Lists.newArrayList();

      try {
         JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
         if (jsonObject.get("templates").isJsonArray()) {
            Iterator var3 = jsonObject.get("templates").getAsJsonArray().iterator();

            while(var3.hasNext()) {
               JsonElement jsonElement = (JsonElement)var3.next();
               worldTemplatePaginatedList.templates.add(WorldTemplate.parse(jsonElement.getAsJsonObject()));
            }
         }

         worldTemplatePaginatedList.page = JsonUtils.getIntOr("page", jsonObject, 0);
         worldTemplatePaginatedList.size = JsonUtils.getIntOr("size", jsonObject, 0);
         worldTemplatePaginatedList.total = JsonUtils.getIntOr("total", jsonObject, 0);
      } catch (Exception var5) {
         LOGGER.error("Could not parse WorldTemplatePaginatedList: {}", var5.getMessage());
      }

      return worldTemplatePaginatedList;
   }
}
