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
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, @Nullable UV uvs, AxisRotation rotation) {
    public static final int field_32789 = -1;

    public static float getUValue(UV uv, AxisRotation rotation, int index) {
        return uv.getUVertices(rotation.rotate(index)) / 16.0f;
    }

    public static float getVValue(UV uv, AxisRotation rotation, int index) {
        return uv.getVVertices(rotation.rotate(index)) / 16.0f;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelElementFace.class, "cullForDirection;tintIndex;texture;uvs;rotation", "cullFace", "tintIndex", "textureId", "uvs", "rotation"}, this, object);
    }

    @Environment(value=EnvType.CLIENT)
    public record UV(float minU, float minV, float maxU, float maxV) {
        public float getUVertices(int i) {
            return i == 0 || i == 1 ? this.minU : this.maxU;
        }

        public float getVVertices(int i) {
            return i == 0 || i == 3 ? this.minV : this.maxV;
        }
    }

    @Environment(value=EnvType.CLIENT)
    protected static class Deserializer
    implements JsonDeserializer<ModelElementFace> {
        private static final int DEFAULT_TINT_INDEX = -1;
        private static final int field_56927 = 0;

        protected Deserializer() {
        }

        public ModelElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Direction direction = Deserializer.deserializeCullFace(jsonObject);
            int i = Deserializer.deserializeTintIndex(jsonObject);
            String string = Deserializer.deserializeTexture(jsonObject);
            UV uV = Deserializer.getUV(jsonObject);
            AxisRotation axisRotation = Deserializer.getRotation(jsonObject);
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

        private static @Nullable UV getUV(JsonObject json) {
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
            return new UV(f, g, h, i);
        }

        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}
