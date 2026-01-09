package net.minecraft.client.render.model.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public record ModelElement(Vector3fc from, Vector3fc to, Map faces, @Nullable ModelRotation rotation, boolean shade, int lightEmission) {
   private static final boolean field_32785 = false;
   private static final float field_32786 = -16.0F;
   private static final float field_32787 = 32.0F;

   public ModelElement(Vector3fc vector3fc, Vector3fc vector3fc2, Map faces) {
      this(vector3fc, vector3fc2, faces, (ModelRotation)null, true, 0);
   }

   public ModelElement(Vector3fc vector3fc, Vector3fc vector3fc2, Map faces, @Nullable ModelRotation rotation, boolean shade, int lightEmission) {
      this.from = vector3fc;
      this.to = vector3fc2;
      this.faces = faces;
      this.rotation = rotation;
      this.shade = shade;
      this.lightEmission = lightEmission;
   }

   public Vector3fc from() {
      return this.from;
   }

   public Vector3fc to() {
      return this.to;
   }

   public Map faces() {
      return this.faces;
   }

   @Nullable
   public ModelRotation rotation() {
      return this.rotation;
   }

   public boolean shade() {
      return this.shade;
   }

   public int lightEmission() {
      return this.lightEmission;
   }

   @Environment(EnvType.CLIENT)
   protected static class Deserializer implements JsonDeserializer {
      private static final boolean DEFAULT_SHADE = true;
      private static final int field_53160 = 0;

      public ModelElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Vector3f vector3f = this.deserializeFrom(jsonObject);
         Vector3f vector3f2 = this.deserializeTo(jsonObject);
         ModelRotation modelRotation = this.deserializeRotation(jsonObject);
         Map map = this.deserializeFacesValidating(jsonDeserializationContext, jsonObject);
         if (jsonObject.has("shade") && !JsonHelper.hasBoolean(jsonObject, "shade")) {
            throw new JsonParseException("Expected shade to be a Boolean");
         } else {
            boolean bl = JsonHelper.getBoolean(jsonObject, "shade", true);
            int i = 0;
            if (jsonObject.has("light_emission")) {
               boolean bl2 = JsonHelper.hasNumber(jsonObject, "light_emission");
               if (bl2) {
                  i = JsonHelper.getInt(jsonObject, "light_emission");
               }

               if (!bl2 || i < 0 || i > 15) {
                  throw new JsonParseException("Expected light_emission to be an Integer between (inclusive) 0 and 15");
               }
            }

            return new ModelElement(vector3f, vector3f2, map, modelRotation, bl, i);
         }
      }

      @Nullable
      private ModelRotation deserializeRotation(JsonObject object) {
         ModelRotation modelRotation = null;
         if (object.has("rotation")) {
            JsonObject jsonObject = JsonHelper.getObject(object, "rotation");
            Vector3f vector3f = this.deserializeVec3f(jsonObject, "origin");
            vector3f.mul(0.0625F);
            Direction.Axis axis = this.deserializeAxis(jsonObject);
            float f = this.deserializeRotationAngle(jsonObject);
            boolean bl = JsonHelper.getBoolean(jsonObject, "rescale", false);
            modelRotation = new ModelRotation(vector3f, axis, f, bl);
         }

         return modelRotation;
      }

      private float deserializeRotationAngle(JsonObject object) {
         float f = JsonHelper.getFloat(object, "angle");
         if (MathHelper.abs(f) > 45.0F) {
            throw new JsonParseException("Invalid rotation " + f + " found, only values in [-45,45] range allowed");
         } else {
            return f;
         }
      }

      private Direction.Axis deserializeAxis(JsonObject object) {
         String string = JsonHelper.getString(object, "axis");
         Direction.Axis axis = Direction.Axis.fromId(string.toLowerCase(Locale.ROOT));
         if (axis == null) {
            throw new JsonParseException("Invalid rotation axis: " + string);
         } else {
            return axis;
         }
      }

      private Map deserializeFacesValidating(JsonDeserializationContext context, JsonObject object) {
         Map map = this.deserializeFaces(context, object);
         if (map.isEmpty()) {
            throw new JsonParseException("Expected between 1 and 6 unique faces, got 0");
         } else {
            return map;
         }
      }

      private Map deserializeFaces(JsonDeserializationContext context, JsonObject object) {
         Map map = Maps.newEnumMap(Direction.class);
         JsonObject jsonObject = JsonHelper.getObject(object, "faces");
         Iterator var5 = jsonObject.entrySet().iterator();

         while(var5.hasNext()) {
            Map.Entry entry = (Map.Entry)var5.next();
            Direction direction = this.getDirection((String)entry.getKey());
            map.put(direction, (ModelElementFace)context.deserialize((JsonElement)entry.getValue(), ModelElementFace.class));
         }

         return map;
      }

      private Direction getDirection(String name) {
         Direction direction = Direction.byId(name);
         if (direction == null) {
            throw new JsonParseException("Unknown facing: " + name);
         } else {
            return direction;
         }
      }

      private Vector3f deserializeTo(JsonObject object) {
         Vector3f vector3f = this.deserializeVec3f(object, "to");
         if (!(vector3f.x() < -16.0F) && !(vector3f.y() < -16.0F) && !(vector3f.z() < -16.0F) && !(vector3f.x() > 32.0F) && !(vector3f.y() > 32.0F) && !(vector3f.z() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'to' specifier exceeds the allowed boundaries: " + String.valueOf(vector3f));
         }
      }

      private Vector3f deserializeFrom(JsonObject object) {
         Vector3f vector3f = this.deserializeVec3f(object, "from");
         if (!(vector3f.x() < -16.0F) && !(vector3f.y() < -16.0F) && !(vector3f.z() < -16.0F) && !(vector3f.x() > 32.0F) && !(vector3f.y() > 32.0F) && !(vector3f.z() > 32.0F)) {
            return vector3f;
         } else {
            throw new JsonParseException("'from' specifier exceeds the allowed boundaries: " + String.valueOf(vector3f));
         }
      }

      private Vector3f deserializeVec3f(JsonObject object, String name) {
         JsonArray jsonArray = JsonHelper.getArray(object, name);
         if (jsonArray.size() != 3) {
            throw new JsonParseException("Expected 3 " + name + " values, found: " + jsonArray.size());
         } else {
            float[] fs = new float[3];

            for(int i = 0; i < fs.length; ++i) {
               fs[i] = JsonHelper.asFloat(jsonArray.get(i), name + "[" + i + "]");
            }

            return new Vector3f(fs[0], fs[1], fs[2]);
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement json, final Type type, final JsonDeserializationContext context) throws JsonParseException {
         return this.deserialize(json, type, context);
      }
   }
}
