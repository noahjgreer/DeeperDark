package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.UnbakedGeometry;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record JsonUnbakedModel(@Nullable Geometry geometry, @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ModelTransformation transformations, ModelTextures.Textures textures, @Nullable Identifier parent) implements UnbakedModel {
   @VisibleForTesting
   static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(JsonUnbakedModel.class, new Deserializer()).registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer()).registerTypeAdapter(Transformation.class, new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer()).create();

   public JsonUnbakedModel(@Nullable Geometry geometry, @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean boolean_, @Nullable ModelTransformation modelTransformation, ModelTextures.Textures textures, @Nullable Identifier identifier) {
      this.geometry = geometry;
      this.guiLight = guiLight;
      this.ambientOcclusion = boolean_;
      this.transformations = modelTransformation;
      this.textures = textures;
      this.parent = identifier;
   }

   public static JsonUnbakedModel deserialize(Reader input) {
      return (JsonUnbakedModel)JsonHelper.deserialize(GSON, input, JsonUnbakedModel.class);
   }

   @Nullable
   public Geometry geometry() {
      return this.geometry;
   }

   @Nullable
   public UnbakedModel.GuiLight guiLight() {
      return this.guiLight;
   }

   @Nullable
   public Boolean ambientOcclusion() {
      return this.ambientOcclusion;
   }

   @Nullable
   public ModelTransformation transformations() {
      return this.transformations;
   }

   public ModelTextures.Textures textures() {
      return this.textures;
   }

   @Nullable
   public Identifier parent() {
      return this.parent;
   }

   @Environment(EnvType.CLIENT)
   public static class Deserializer implements JsonDeserializer {
      public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Geometry geometry = this.elementsFromJson(jsonDeserializationContext, jsonObject);
         String string = this.parentFromJson(jsonObject);
         ModelTextures.Textures textures = this.texturesFromJson(jsonObject);
         Boolean boolean_ = this.ambientOcclusionFromJson(jsonObject);
         ModelTransformation modelTransformation = null;
         if (jsonObject.has("display")) {
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
            modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize(jsonObject2, ModelTransformation.class);
         }

         UnbakedModel.GuiLight guiLight = null;
         if (jsonObject.has("gui_light")) {
            guiLight = UnbakedModel.GuiLight.byName(JsonHelper.getString(jsonObject, "gui_light"));
         }

         Identifier identifier = string.isEmpty() ? null : Identifier.of(string);
         return new JsonUnbakedModel(geometry, guiLight, boolean_, modelTransformation, textures, identifier);
      }

      private ModelTextures.Textures texturesFromJson(JsonObject object) {
         if (object.has("textures")) {
            JsonObject jsonObject = JsonHelper.getObject(object, "textures");
            return ModelTextures.fromJson(jsonObject, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
         } else {
            return ModelTextures.Textures.EMPTY;
         }
      }

      private String parentFromJson(JsonObject json) {
         return JsonHelper.getString(json, "parent", "");
      }

      @Nullable
      protected Boolean ambientOcclusionFromJson(JsonObject json) {
         return json.has("ambientocclusion") ? JsonHelper.getBoolean(json, "ambientocclusion") : null;
      }

      @Nullable
      protected Geometry elementsFromJson(JsonDeserializationContext context, JsonObject json) {
         if (!json.has("elements")) {
            return null;
         } else {
            List list = new ArrayList();
            Iterator var4 = JsonHelper.getArray(json, "elements").iterator();

            while(var4.hasNext()) {
               JsonElement jsonElement = (JsonElement)var4.next();
               list.add((ModelElement)context.deserialize(jsonElement, ModelElement.class));
            }

            return new UnbakedGeometry(list);
         }
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement element, final Type unused, final JsonDeserializationContext ctx) throws JsonParseException {
         return this.deserialize(element, unused, ctx);
      }
   }
}
