package net.minecraft.client.render.model.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemDisplayContext;

@Environment(EnvType.CLIENT)
public record ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed) {
   public static final ModelTransformation NONE;

   public ModelTransformation(Transformation thirdPersonLeftHand, Transformation thirdPersonRightHand, Transformation firstPersonLeftHand, Transformation firstPersonRightHand, Transformation head, Transformation gui, Transformation ground, Transformation fixed) {
      this.thirdPersonLeftHand = thirdPersonLeftHand;
      this.thirdPersonRightHand = thirdPersonRightHand;
      this.firstPersonLeftHand = firstPersonLeftHand;
      this.firstPersonRightHand = firstPersonRightHand;
      this.head = head;
      this.gui = gui;
      this.ground = ground;
      this.fixed = fixed;
   }

   public Transformation getTransformation(ItemDisplayContext renderMode) {
      Transformation var10000;
      switch (renderMode) {
         case THIRD_PERSON_LEFT_HAND:
            var10000 = this.thirdPersonLeftHand;
            break;
         case THIRD_PERSON_RIGHT_HAND:
            var10000 = this.thirdPersonRightHand;
            break;
         case FIRST_PERSON_LEFT_HAND:
            var10000 = this.firstPersonLeftHand;
            break;
         case FIRST_PERSON_RIGHT_HAND:
            var10000 = this.firstPersonRightHand;
            break;
         case HEAD:
            var10000 = this.head;
            break;
         case GUI:
            var10000 = this.gui;
            break;
         case GROUND:
            var10000 = this.ground;
            break;
         case FIXED:
            var10000 = this.fixed;
            break;
         default:
            var10000 = Transformation.IDENTITY;
      }

      return var10000;
   }

   public Transformation thirdPersonLeftHand() {
      return this.thirdPersonLeftHand;
   }

   public Transformation thirdPersonRightHand() {
      return this.thirdPersonRightHand;
   }

   public Transformation firstPersonLeftHand() {
      return this.firstPersonLeftHand;
   }

   public Transformation firstPersonRightHand() {
      return this.firstPersonRightHand;
   }

   public Transformation head() {
      return this.head;
   }

   public Transformation gui() {
      return this.gui;
   }

   public Transformation ground() {
      return this.ground;
   }

   public Transformation fixed() {
      return this.fixed;
   }

   static {
      NONE = new ModelTransformation(Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY);
   }

   @Environment(EnvType.CLIENT)
   protected static class Deserializer implements JsonDeserializer {
      public ModelTransformation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         Transformation transformation = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
         Transformation transformation2 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
         if (transformation2 == Transformation.IDENTITY) {
            transformation2 = transformation;
         }

         Transformation transformation3 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
         Transformation transformation4 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
         if (transformation4 == Transformation.IDENTITY) {
            transformation4 = transformation3;
         }

         Transformation transformation5 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.HEAD);
         Transformation transformation6 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.GUI);
         Transformation transformation7 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.GROUND);
         Transformation transformation8 = this.parseModelTransformation(jsonDeserializationContext, jsonObject, ItemDisplayContext.FIXED);
         return new ModelTransformation(transformation2, transformation, transformation4, transformation3, transformation5, transformation6, transformation7, transformation8);
      }

      private Transformation parseModelTransformation(JsonDeserializationContext ctx, JsonObject json, ItemDisplayContext displayContext) {
         String string = displayContext.asString();
         return json.has(string) ? (Transformation)ctx.deserialize(json.get(string), Transformation.class) : Transformation.IDENTITY;
      }

      // $FF: synthetic method
      public Object deserialize(final JsonElement functionJson, final Type unused, final JsonDeserializationContext context) throws JsonParseException {
         return this.deserialize(functionJson, unused, context);
      }
   }
}
