package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SpecialItemModel implements ItemModel {
   private final SpecialModelRenderer specialModelType;
   private final ModelSettings settings;

   public SpecialItemModel(SpecialModelRenderer specialModelType, ModelSettings settings) {
      this.specialModelType = specialModelType;
      this.settings = settings;
   }

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
      ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
      if (stack.hasGlint()) {
         ItemRenderState.Glint glint = ItemRenderState.Glint.STANDARD;
         layerRenderState.setGlint(glint);
         state.markAnimated();
         state.addModelKey(glint);
      }

      Object object = this.specialModelType.getData(stack);
      layerRenderState.setVertices(() -> {
         Set set = new HashSet();
         this.specialModelType.collectVertices(set);
         return (Vector3f[])set.toArray(new Vector3f[0]);
      });
      layerRenderState.setSpecialModel(this.specialModelType, object);
      if (object != null) {
         state.addModelKey(object);
      }

      this.settings.addSettings(layerRenderState, displayContext);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(Identifier base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("base").forGetter(Unbaked::base), SpecialModelTypes.CODEC.fieldOf("model").forGetter(Unbaked::specialModel)).apply(instance, Unbaked::new);
      });

      public Unbaked(Identifier identifier, SpecialModelRenderer.Unbaked unbaked) {
         this.base = identifier;
         this.specialModel = unbaked;
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         resolver.markDependency(this.base);
      }

      public ItemModel bake(ItemModel.BakeContext context) {
         SpecialModelRenderer specialModelRenderer = this.specialModel.bake(context.entityModelSet());
         if (specialModelRenderer == null) {
            return context.missingItemModel();
         } else {
            ModelSettings modelSettings = this.getSettings(context);
            return new SpecialItemModel(specialModelRenderer, modelSettings);
         }
      }

      private ModelSettings getSettings(ItemModel.BakeContext context) {
         Baker baker = context.blockModelBaker();
         BakedSimpleModel bakedSimpleModel = baker.getModel(this.base);
         ModelTextures modelTextures = bakedSimpleModel.getTextures();
         return ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public Identifier base() {
         return this.base;
      }

      public SpecialModelRenderer.Unbaked specialModel() {
         return this.specialModel;
      }
   }
}
