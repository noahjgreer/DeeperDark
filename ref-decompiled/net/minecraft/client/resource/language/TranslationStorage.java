/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.language.ReorderingUtil
 *  net.minecraft.client.resource.language.TranslationStorage
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.util.DeprecatedLanguageData
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Language
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource.language;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.ReorderingUtil;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.DeprecatedLanguageData;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TranslationStorage
extends Language {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, String> translations;
    private final boolean rightToLeft;

    private TranslationStorage(Map<String, String> translations, boolean rightToLeft) {
        this.translations = translations;
        this.rightToLeft = rightToLeft;
    }

    public static TranslationStorage load(ResourceManager resourceManager, List<String> definitions, boolean rightToLeft) {
        HashMap map = new HashMap();
        for (String string : definitions) {
            String string2 = String.format(Locale.ROOT, "lang/%s.json", string);
            for (String string3 : resourceManager.getAllNamespaces()) {
                try {
                    Identifier identifier = Identifier.of((String)string3, (String)string2);
                    TranslationStorage.load((String)string, (List)resourceManager.getAllResources(identifier), map);
                }
                catch (Exception exception) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{string3, string2, exception.toString()});
                }
            }
        }
        DeprecatedLanguageData.create().apply(map);
        return new TranslationStorage(Map.copyOf(map), rightToLeft);
    }

    private static void load(String langCode, List<Resource> resourceRefs, Map<String, String> translations) {
        for (Resource resource : resourceRefs) {
            try {
                InputStream inputStream = resource.getInputStream();
                try {
                    Language.load((InputStream)inputStream, translations::put);
                }
                finally {
                    if (inputStream == null) continue;
                    inputStream.close();
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to load translations for {} from pack {}", new Object[]{langCode, resource.getPackId(), iOException});
            }
        }
    }

    public String get(String key, String fallback) {
        return this.translations.getOrDefault(key, fallback);
    }

    public boolean hasTranslation(String key) {
        return this.translations.containsKey(key);
    }

    public boolean isRightToLeft() {
        return this.rightToLeft;
    }

    public OrderedText reorder(StringVisitable text) {
        return ReorderingUtil.reorder((StringVisitable)text, (boolean)this.rightToLeft);
    }
}

