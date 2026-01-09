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
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, @Nullable UV uvs, AxisRotation rotation) {
   public static final int field_32789 = -1;

   public ModelElementFace(@Nullable Direction cullFace, int tintIndex, String textureId, @Nullable UV textureData, AxisRotation axisRotation) {
      this.cullFace = cullFace;
      this.tintIndex = tintIndex;
      this.textureId = textureId;
      this.uvs = textureData;
      this.rotation = axisRotation;
   }

   public static float getUValue(UV uV, AxisRotation axisRotation, int i) {
      return uV.getUVertices(axisRotation.rotate(i)) / 16.0F;
   }

   public static float getVValue(UV uV, AxisRotation axisRotation, int i) {
      return uV.getVVertices(axisRotation.rotate(i)) / 16.0F;
   }

   @Nullable
   public Direction cullFace() {
      return this.cullFace;
   }

   public int tintIndex() {
      return this.tintIndex;
   }

   public String textureId() {
      return this.textureId;
   }

   @Nullable
   public UV uvs() {
      return this.uvs;
   }

   public AxisRotation rotation() {
      return this.rotation;
   }

   @Environment(EnvType.CLIENT)
   public static record UV(float minU, float minV, float maxU, float maxV) {
      public UV(float f, float g, float h, float i) {
         this.minU = f;
         this.minV = g;
         this.maxU = h;
         this.maxV = i;
      }

      public float getUVertices(int i) {
         return i != 0 && i != 1 ? this.maxU : this.minU;
      }

      public float getVVertices(int i) {
         return i != 0 && i != 3 ? this.maxV : this.minV;
      }

      public float minU() {
         return this.minU;
      }

      public float minV() {
         return this.minV;
      }

      public float maxU() {
         return this.maxU;
      }

      public float maxV() {
         return this.maxV;
      }
   }

   @Environment(EnvType.CLIENT)
   protected static class Deserializer implements JsonDeserializer {
      private static final int DEFAULT_TINT_INDEX = -1;
      private static final int field_56927 = 0;

      public ModelElementFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Direction direction = deserializeCullFace(jsonObject);
         int i = deserializeTintIndex(jsonObject);
         String string = deserializeTexture(jsonObject);
         UV uV = getUV(jsonObject);
         AxisRotation axisRotation = getRotation(jsonObject);
         return new ModelElementFace(direction, i, string, uV, axisRotation);
      }

      private static int deserializeTintIndex(JsonObject jsonObject) {
         return JsonHelper.getInt(jsonObject, "tintindex", -1);
      }

      private static String deserializeTexture(JsonObject jsonObject) {
         return JsonHelper.getString(jsonObject, "texture");
      }

      @Nullable
      private static Direction deserializeCullFace(JsonObject jsonObject) {
         String string = JsonHelper.getString(jsonObject, "cullface", "");
         return Direction.byId(string);
      }

      private static AxisRotation getRotation(JsonObject jsonObject) {
         int i = JsonHelper.getInt(jsonObject, "rotation", 0);
         return AxisRotation.fromDegrees(i);
      }

      @Nullable
      private static UV getUV(JsonObject jsonObject) {
         if (!jsonObject.has("uv")) {
            return null;
         } else {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "uv");
            if (jsonArray.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + jsonArray.size());
            } else {
               float f = JsonHelper.asFloat(jsonArray.get(0), "minU");
               float g = JsonHelper.asFloat(jsonArray.get(1), "minV");
               float h = JsonHelper.asFloat(jsonArray.get(2), "maxU");
               float i = JsonHelper.asFloat(jsonArray.get(3), "maxV");
               return new UV(f, g, h, i);
            }
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
         return this.deserialize(functionJson, unused, context);
      }
   }
}
