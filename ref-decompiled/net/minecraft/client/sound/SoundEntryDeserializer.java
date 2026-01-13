/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.sound.Sound
 *  net.minecraft.client.sound.Sound$RegistrationType
 *  net.minecraft.client.sound.SoundEntry
 *  net.minecraft.client.sound.SoundEntryDeserializer
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  net.minecraft.util.math.floatprovider.ConstantFloatProvider
 *  net.minecraft.util.math.floatprovider.FloatProvider
 *  net.minecraft.util.math.floatprovider.FloatSupplier
 *  org.apache.commons.lang3.Validate
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import org.apache.commons.lang3.Validate;

@Environment(value=EnvType.CLIENT)
public class SoundEntryDeserializer
implements JsonDeserializer<SoundEntry> {
    private static final FloatProvider ONE = ConstantFloatProvider.create((float)1.0f);

    public SoundEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = JsonHelper.asObject((JsonElement)jsonElement, (String)"entry");
        boolean bl = JsonHelper.getBoolean((JsonObject)jsonObject, (String)"replace", (boolean)false);
        String string = JsonHelper.getString((JsonObject)jsonObject, (String)"subtitle", null);
        List list = this.deserializeSounds(jsonObject);
        return new SoundEntry(list, bl, string);
    }

    private List<Sound> deserializeSounds(JsonObject json) {
        ArrayList list = Lists.newArrayList();
        if (json.has("sounds")) {
            JsonArray jsonArray = JsonHelper.getArray((JsonObject)json, (String)"sounds");
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonElement jsonElement = jsonArray.get(i);
                if (JsonHelper.isString((JsonElement)jsonElement)) {
                    Identifier identifier = Identifier.of((String)JsonHelper.asString((JsonElement)jsonElement, (String)"sound"));
                    list.add(new Sound(identifier, (FloatSupplier)ONE, (FloatSupplier)ONE, 1, Sound.RegistrationType.FILE, false, false, 16));
                    continue;
                }
                list.add(this.deserializeSound(JsonHelper.asObject((JsonElement)jsonElement, (String)"sound")));
            }
        }
        return list;
    }

    private Sound deserializeSound(JsonObject json) {
        Identifier identifier = Identifier.of((String)JsonHelper.getString((JsonObject)json, (String)"name"));
        Sound.RegistrationType registrationType = this.deserializeType(json, Sound.RegistrationType.FILE);
        float f = JsonHelper.getFloat((JsonObject)json, (String)"volume", (float)1.0f);
        Validate.isTrue((f > 0.0f ? 1 : 0) != 0, (String)"Invalid volume", (Object[])new Object[0]);
        float g = JsonHelper.getFloat((JsonObject)json, (String)"pitch", (float)1.0f);
        Validate.isTrue((g > 0.0f ? 1 : 0) != 0, (String)"Invalid pitch", (Object[])new Object[0]);
        int i = JsonHelper.getInt((JsonObject)json, (String)"weight", (int)1);
        Validate.isTrue((i > 0 ? 1 : 0) != 0, (String)"Invalid weight", (Object[])new Object[0]);
        boolean bl = JsonHelper.getBoolean((JsonObject)json, (String)"preload", (boolean)false);
        boolean bl2 = JsonHelper.getBoolean((JsonObject)json, (String)"stream", (boolean)false);
        int j = JsonHelper.getInt((JsonObject)json, (String)"attenuation_distance", (int)16);
        return new Sound(identifier, (FloatSupplier)ConstantFloatProvider.create((float)f), (FloatSupplier)ConstantFloatProvider.create((float)g), i, registrationType, bl2, bl, j);
    }

    private Sound.RegistrationType deserializeType(JsonObject json, Sound.RegistrationType fallback) {
        Sound.RegistrationType registrationType = fallback;
        if (json.has("type")) {
            registrationType = Sound.RegistrationType.getByName((String)JsonHelper.getString((JsonObject)json, (String)"type"));
            Objects.requireNonNull(registrationType, "Invalid type");
        }
        return registrationType;
    }

    public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
        return this.deserialize(functionJson, unused, context);
    }
}

