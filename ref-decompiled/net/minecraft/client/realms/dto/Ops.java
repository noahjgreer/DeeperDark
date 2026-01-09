package net.minecraft.client.realms.dto;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.LenientJsonParser;

@Environment(EnvType.CLIENT)
public class Ops extends ValueObject {
   public Set ops = Sets.newHashSet();

   public static Ops parse(String json) {
      Ops ops = new Ops();

      try {
         JsonObject jsonObject = LenientJsonParser.parse(json).getAsJsonObject();
         JsonElement jsonElement = jsonObject.get("ops");
         if (jsonElement.isJsonArray()) {
            Iterator var4 = jsonElement.getAsJsonArray().iterator();

            while(var4.hasNext()) {
               JsonElement jsonElement2 = (JsonElement)var4.next();
               ops.ops.add(jsonElement2.getAsString());
            }
         }
      } catch (Exception var6) {
      }

      return ops;
   }
}
