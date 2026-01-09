package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.slf4j.Logger;

public abstract class Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new Gson();
   private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
   public static final String DEFAULT_LANGUAGE = "en_us";
   private static volatile Language instance = create();

   private static Language create() {
      DeprecatedLanguageData deprecatedLanguageData = DeprecatedLanguageData.create();
      Map map = new HashMap();
      Objects.requireNonNull(map);
      BiConsumer biConsumer = map::put;
      load(biConsumer, "/assets/minecraft/lang/en_us.json");
      deprecatedLanguageData.apply(map);
      final Map map2 = Map.copyOf(map);
      return new Language() {
         public String get(String key, String fallback) {
            return (String)map2.getOrDefault(key, fallback);
         }

         public boolean hasTranslation(String key) {
            return map2.containsKey(key);
         }

         public boolean isRightToLeft() {
            return false;
         }

         public OrderedText reorder(StringVisitable text) {
            return (visitor) -> {
               return text.visit((style, string) -> {
                  return TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT;
               }, Style.EMPTY).isPresent();
            };
         }
      };
   }

   private static void load(BiConsumer entryConsumer, String path) {
      try {
         InputStream inputStream = Language.class.getResourceAsStream(path);

         try {
            load(inputStream, entryConsumer);
         } catch (Throwable var6) {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (inputStream != null) {
            inputStream.close();
         }
      } catch (JsonParseException | IOException var7) {
         LOGGER.error("Couldn't read strings from {}", path, var7);
      }

   }

   public static void load(InputStream inputStream, BiConsumer entryConsumer) {
      JsonObject jsonObject = (JsonObject)GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
      Iterator var3 = jsonObject.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry entry = (Map.Entry)var3.next();
         String string = TOKEN_PATTERN.matcher(JsonHelper.asString((JsonElement)entry.getValue(), (String)entry.getKey())).replaceAll("%$1s");
         entryConsumer.accept((String)entry.getKey(), string);
      }

   }

   public static Language getInstance() {
      return instance;
   }

   public static void setInstance(Language language) {
      instance = language;
   }

   public String get(String key) {
      return this.get(key, key);
   }

   public abstract String get(String key, String fallback);

   public abstract boolean hasTranslation(String key);

   public abstract boolean isRightToLeft();

   public abstract OrderedText reorder(StringVisitable text);

   public List reorder(List texts) {
      return (List)texts.stream().map(this::reorder).collect(ImmutableList.toImmutableList());
   }
}
