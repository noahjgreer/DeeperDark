/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.RealmsText
 *  net.minecraft.client.realms.util.JsonUtils
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class RealmsText {
    private static final String TRANSLATION_KEY_KEY = "translationKey";
    private static final String ARGS_KEY = "args";
    private final String translationKey;
    private final String @Nullable [] args;

    private RealmsText(String translationKey, String @Nullable [] args) {
        this.translationKey = translationKey;
        this.args = args;
    }

    public Text toText(Text fallback) {
        return Objects.requireNonNullElse(this.toText(), fallback);
    }

    public @Nullable Text toText() {
        if (!I18n.hasTranslation((String)this.translationKey)) {
            return null;
        }
        if (this.args == null) {
            return Text.translatable((String)this.translationKey);
        }
        return Text.translatable((String)this.translationKey, (Object[])this.args);
    }

    public static RealmsText fromJson(JsonObject json) {
        String[] strings;
        String string = JsonUtils.getString((String)TRANSLATION_KEY_KEY, (JsonObject)json);
        JsonElement jsonElement = json.get(ARGS_KEY);
        if (jsonElement == null || jsonElement.isJsonNull()) {
            strings = null;
        } else {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            strings = new String[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); ++i) {
                strings[i] = jsonArray.get(i).getAsString();
            }
        }
        return new RealmsText(string, strings);
    }

    public String toString() {
        return this.translationKey;
    }
}

