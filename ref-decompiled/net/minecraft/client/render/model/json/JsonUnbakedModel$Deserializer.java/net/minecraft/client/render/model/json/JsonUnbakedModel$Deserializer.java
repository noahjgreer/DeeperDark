/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.UnbakedGeometry;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class JsonUnbakedModel.Deserializer
implements JsonDeserializer<JsonUnbakedModel> {
    public JsonUnbakedModel deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Geometry geometry = this.elementsFromJson(jsonDeserializationContext, jsonObject);
        String string = this.parentFromJson(jsonObject);
        ModelTextures.Textures textures = this.texturesFromJson(jsonObject);
        Boolean boolean_ = this.ambientOcclusionFromJson(jsonObject);
        ModelTransformation modelTransformation = null;
        if (jsonObject.has("display")) {
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "display");
            modelTransformation = (ModelTransformation)jsonDeserializationContext.deserialize((JsonElement)jsonObject2, ModelTransformation.class);
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
            return ModelTextures.fromJson(jsonObject);
        }
        return ModelTextures.Textures.EMPTY;
    }

    private String parentFromJson(JsonObject json) {
        return JsonHelper.getString(json, "parent", "");
    }

    protected @Nullable Boolean ambientOcclusionFromJson(JsonObject json) {
        if (json.has("ambientocclusion")) {
            return JsonHelper.getBoolean(json, "ambientocclusion");
        }
        return null;
    }

    protected @Nullable Geometry elementsFromJson(JsonDeserializationContext context, JsonObject json) {
        if (json.has("elements")) {
            ArrayList<ModelElement> list = new ArrayList<ModelElement>();
            for (JsonElement jsonElement : JsonHelper.getArray(json, "elements")) {
                list.add((ModelElement)context.deserialize(jsonElement, ModelElement.class));
            }
            return new UnbakedGeometry(list);
        }
        return null;
    }

    public /* synthetic */ Object deserialize(JsonElement element, Type unused, JsonDeserializationContext ctx) throws JsonParseException {
        return this.deserialize(element, unused, ctx);
    }
}
