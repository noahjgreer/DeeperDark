/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementRotation;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
protected static class ModelElement.Deserializer
implements JsonDeserializer<ModelElement> {
    private static final boolean DEFAULT_SHADE = true;
    private static final int field_53160 = 0;
    private static final String SHADE_KEY = "shade";
    private static final String LIGHT_EMISSION_KEY = "light_emission";
    private static final String ROTATION_KEY = "rotation";
    private static final String ORIGIN_KEY = "origin";
    private static final String ANGLE_KEY = "angle";
    private static final String X_KEY = "x";
    private static final String Y_KEY = "y";
    private static final String Z_KEY = "z";
    private static final String AXIS_KEY = "axis";
    private static final String RESCALE_KEY = "rescale";
    private static final String FACES_KEY = "faces";
    private static final String TO_KEY = "to";
    private static final String FROM_KEY = "from";

    protected ModelElement.Deserializer() {
    }

    public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Vector3f vector3f = ModelElement.Deserializer.deserializeFromTo(jsonObject, FROM_KEY);
        Vector3f vector3f2 = ModelElement.Deserializer.deserializeFromTo(jsonObject, TO_KEY);
        ModelElementRotation modelElementRotation = this.deserializeRotation(jsonObject);
        Map<Direction, ModelElementFace> map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
        if (jsonObject.has(SHADE_KEY) && !JsonHelper.hasBoolean(jsonObject, SHADE_KEY)) {
            throw new JsonParseException("Expected 'shade' to be a Boolean");
        }
        boolean bl = JsonHelper.getBoolean(jsonObject, SHADE_KEY, true);
        int i = 0;
        if (jsonObject.has(LIGHT_EMISSION_KEY)) {
            boolean bl2 = JsonHelper.hasNumber(jsonObject, LIGHT_EMISSION_KEY);
            if (bl2) {
                i = JsonHelper.getInt(jsonObject, LIGHT_EMISSION_KEY);
            }
            if (!bl2 || i < 0 || i > 15) {
                throw new JsonParseException("Expected 'light_emission' to be an Integer between (inclusive) 0 and 15");
            }
        }
        return new ModelElement((Vector3fc)vector3f, (Vector3fc)vector3f2, map, modelElementRotation, bl, i);
    }

    private @Nullable ModelElementRotation deserializeRotation(JsonObject object) {
        if (object.has(ROTATION_KEY)) {
            Record rotationValue;
            JsonObject jsonObject = JsonHelper.getObject(object, ROTATION_KEY);
            Vector3f vector3f = ModelElement.Deserializer.deserializeVec3f(jsonObject, ORIGIN_KEY);
            vector3f.mul(0.0625f);
            if (jsonObject.has(AXIS_KEY) || jsonObject.has(ANGLE_KEY)) {
                Direction.Axis axis = this.deserializeAxis(jsonObject);
                float f = JsonHelper.getFloat(jsonObject, ANGLE_KEY);
                rotationValue = new ModelElementRotation.OfAxisAngle(axis, f);
            } else if (jsonObject.has(X_KEY) || jsonObject.has(Y_KEY) || jsonObject.has(Z_KEY)) {
                float g = JsonHelper.getFloat(jsonObject, X_KEY, 0.0f);
                float f = JsonHelper.getFloat(jsonObject, Y_KEY, 0.0f);
                float h = JsonHelper.getFloat(jsonObject, Z_KEY, 0.0f);
                rotationValue = new ModelElementRotation.OfEuler(g, f, h);
            } else {
                throw new JsonParseException("Missing rotation value, expected either 'axis' and 'angle' or 'x', 'y' and 'z'");
            }
            boolean bl = JsonHelper.getBoolean(jsonObject, RESCALE_KEY, false);
            return new ModelElementRotation((Vector3fc)vector3f, (ModelElementRotation.RotationValue)((Object)rotationValue), bl);
        }
        return null;
    }

    private Direction.Axis deserializeAxis(JsonObject object) {
        String string = JsonHelper.getString(object, AXIS_KEY);
        Direction.Axis axis = Direction.Axis.fromId(string.toLowerCase(Locale.ROOT));
        if (axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + string);
        }
        return axis;
    }

    private Map<Direction, ModelElementFace> deserializeFacesValidating(JsonDeserializationContext context, JsonObject object) {
        Map<Direction, ModelElementFace> map = this.deserializeFaces(context, object);
        if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
        }
        return map;
    }

    private Map<Direction, ModelElementFace> deserializeFaces(JsonDeserializationContext context, JsonObject object) {
        EnumMap map = Maps.newEnumMap(Direction.class);
        JsonObject jsonObject = JsonHelper.getObject(object, FACES_KEY);
        for (Map.Entry entry : jsonObject.entrySet()) {
            Direction direction = this.getDirection((String)entry.getKey());
            map.put(direction, (ModelElementFace)context.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
        }
        return map;
    }

    private Direction getDirection(String name) {
        Direction direction = Direction.byId(name);
        if (direction == null) {
            throw new JsonParseException("Unknown facing: " + name);
        }
        return direction;
    }

    private static Vector3f deserializeFromTo(JsonObject json, String key) {
        Vector3f vector3f = ModelElement.Deserializer.deserializeVec3f(json, key);
        if (vector3f.x() < -16.0f || vector3f.y() < -16.0f || vector3f.z() < -16.0f || vector3f.x() > 32.0f || vector3f.y() > 32.0f || vector3f.z() > 32.0f) {
            throw new JsonParseException("'" + key + "' specifier exceeds the allowed boundaries: " + String.valueOf(vector3f));
        }
        return vector3f;
    }

    private static Vector3f deserializeVec3f(JsonObject json, String key) {
        JsonArray jsonArray = JsonHelper.getArray(json, key);
        if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + key + " values, found: " + jsonArray.size());
        }
        float[] fs = new float[3];
        for (int i = 0; i < fs.length; ++i) {
            fs[i] = JsonHelper.asFloat(jsonArray.get(i), key + "[" + i + "]");
        }
        return new Vector3f(fs[0], fs[1], fs[2]);
    }

    public /* synthetic */ Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return this.deserialize(json, type, context);
    }
}
