/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
protected static class ModelElementFace.Deserializer
implements JsonDeserializer<ModelElementFace> {
    private static final int DEFAULT_TINT_INDEX = -1;
    private static final int field_56927 = 0;

    protected ModelElementFace.Deserializer() {
    }

    public ModelElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Direction direction = ModelElementFace.Deserializer.deserializeCullFace(jsonObject);
        int i = ModelElementFace.Deserializer.deserializeTintIndex(jsonObject);
        String string = ModelElementFace.Deserializer.deserializeTexture(jsonObject);
        ModelElementFace.UV uV = ModelElementFace.Deserializer.getUV(jsonObject);
        AxisRotation axisRotation = ModelElementFace.Deserializer.getRotation(jsonObject);
        return new ModelElementFace(direction, i, string, uV, axisRotation);
    }

    private static int deserializeTintIndex(JsonObject json) {
        return JsonHelper.getInt(json, "tintindex", -1);
    }

    private static String deserializeTexture(JsonObject json) {
        return JsonHelper.getString(json, "texture");
    }

    private static @Nullable Direction deserializeCullFace(JsonObject json) {
        String string = JsonHelper.getString(json, "cullface", "");
        return Direction.byId(string);
    }

    private static AxisRotation getRotation(JsonObject json) {
        int i = JsonHelper.getInt(json, "rotation", 0);
        return AxisRotation.fromDegrees(i);
    }

    private static @Nullable ModelElementFace.UV getUV(JsonObject json) {
        if (!json.has("uv")) {
            return null;
        }
        JsonArray jsonArray = JsonHelper.getArray(json, "uv");
        if (jsonArray.size() != 4) {
            throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
        }
        float f = JsonHelper.asFloat(jsonArray.get(0), "minU");
        float g = JsonHelper.asFloat(jsonArray.get(1), "minV");
        float h = JsonHelper.asFloat(jsonArray.get(2), "maxU");
        float i = JsonHelper.asFloat(jsonArray.get(3), "maxV");
        return new ModelElementFace.UV(f, g, h, i);
    }

    public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
        return this.deserialize(functionJson, unused, context);
    }
}
