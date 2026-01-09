package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class LanguageManager implements SynchronousResourceReloader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final LanguageDefinition ENGLISH_US = new LanguageDefinition("US", "English", false);
   private Map languageDefs;
   private String currentLanguageCode;
   private final Consumer reloadCallback;

   public LanguageManager(String languageCode, Consumer reloadCallback) {
      this.languageDefs = ImmutableMap.of("en_us", ENGLISH_US);
      this.currentLanguageCode = languageCode;
      this.reloadCallback = reloadCallback;
   }

   private static Map loadAvailableLanguages(Stream packs) {
      Map map = Maps.newHashMap();
      packs.forEach((pack) -> {
         try {
            LanguageResourceMetadata languageResourceMetadata = (LanguageResourceMetadata)pack.parseMetadata(LanguageResourceMetadata.SERIALIZER);
            if (languageResourceMetadata != null) {
               Map var10000 = languageResourceMetadata.definitions();
               Objects.requireNonNull(map);
               var10000.forEach(map::putIfAbsent);
            }
         } catch (IOException | RuntimeException var3) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", pack.getId(), var3);
         }

      });
      return ImmutableMap.copyOf(map);
   }

   public void reload(ResourceManager manager) {
      this.languageDefs = loadAvailableLanguages(manager.streamResourcePacks());
      List list = new ArrayList(2);
      boolean bl = ENGLISH_US.rightToLeft();
      list.add("en_us");
      if (!this.currentLanguageCode.equals("en_us")) {
         LanguageDefinition languageDefinition = (LanguageDefinition)this.languageDefs.get(this.currentLanguageCode);
         if (languageDefinition != null) {
            list.add(this.currentLanguageCode);
            bl = languageDefinition.rightToLeft();
         }
      }

      TranslationStorage translationStorage = TranslationStorage.load(manager, list, bl);
      I18n.setLanguage(translationStorage);
      Language.setInstance(translationStorage);
      this.reloadCallback.accept(translationStorage);
   }

   public void setLanguage(String languageCode) {
      this.currentLanguageCode = languageCode;
   }

   public String getLanguage() {
      return this.currentLanguageCode;
   }

   public SortedMap getAllLanguages() {
      return new TreeMap(this.languageDefs);
   }

   @Nullable
   public LanguageDefinition getLanguage(String code) {
      return (LanguageDefinition)this.languageDefs.get(code);
   }
}
