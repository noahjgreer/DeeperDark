package net.minecraft.util;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public record DeprecatedLanguageData(List removed, Map renamed) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final DeprecatedLanguageData NONE = new DeprecatedLanguageData(List.of(), Map.of());
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codec.STRING.listOf().fieldOf("removed").forGetter(DeprecatedLanguageData::removed), Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("renamed").forGetter(DeprecatedLanguageData::renamed)).apply(instance, DeprecatedLanguageData::new);
   });

   public DeprecatedLanguageData(List list, Map map) {
      this.removed = list;
      this.renamed = map;
   }

   public static DeprecatedLanguageData fromInputStream(InputStream stream) {
      JsonElement jsonElement = StrictJsonParser.parse((Reader)(new InputStreamReader(stream, StandardCharsets.UTF_8)));
      return (DeprecatedLanguageData)CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow((error) -> {
         return new IllegalStateException("Failed to parse deprecated language data: " + error);
      });
   }

   public static DeprecatedLanguageData fromPath(String path) {
      try {
         InputStream inputStream = Language.class.getResourceAsStream(path);

         DeprecatedLanguageData var2;
         label50: {
            try {
               if (inputStream != null) {
                  var2 = fromInputStream(inputStream);
                  break label50;
               }
            } catch (Throwable var5) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return NONE;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var2;
      } catch (Exception var6) {
         LOGGER.error("Failed to read {}", path, var6);
         return NONE;
      }
   }

   public static DeprecatedLanguageData create() {
      return fromPath("/assets/minecraft/lang/deprecated.json");
   }

   public void apply(Map map) {
      Iterator var2 = this.removed.iterator();

      while(var2.hasNext()) {
         String string = (String)var2.next();
         map.remove(string);
      }

      this.renamed.forEach((oldKey, newKey) -> {
         String string = (String)map.remove(oldKey);
         if (string == null) {
            LOGGER.warn("Missing translation key for rename: {}", oldKey);
            map.remove(newKey);
         } else {
            map.put(newKey, string);
         }

      });
   }

   public List removed() {
      return this.removed;
   }

   public Map renamed() {
      return this.renamed;
   }
}
