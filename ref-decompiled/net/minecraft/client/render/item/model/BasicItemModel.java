package net.minecraft.client.render.item.model;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class BasicItemModel implements ItemModel {
   private final List tints;
   private final List quads;
   private final Supplier vector;
   private final ModelSettings settings;
   private final boolean animated;

   public BasicItemModel(List tints, List quads, ModelSettings settings) {
      this.tints = tints;
      this.quads = quads;
      this.settings = settings;
      this.vector = Suppliers.memoize(() -> {
         return bakeQuads(this.quads);
      });
      boolean bl = false;
      Iterator var5 = quads.iterator();

      while(var5.hasNext()) {
         BakedQuad bakedQuad = (BakedQuad)var5.next();
         if (bakedQuad.sprite().isAnimated()) {
            bl = true;
            break;
         }
      }

      this.animated = bl;
   }

   public static Vector3f[] bakeQuads(List quads) {
      Set set = new HashSet();
      Iterator var2 = quads.iterator();

      while(var2.hasNext()) {
         BakedQuad bakedQuad = (BakedQuad)var2.next();
         int[] var10000 = bakedQuad.vertexData();
         Objects.requireNonNull(set);
         BakedQuadFactory.calculatePosition(var10000, set::add);
      }

      return (Vector3f[])set.toArray((i) -> {
         return new Vector3f[i];
      });
   }

   public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed) {
      state.addModelKey(this);
      ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
      if (stack.hasGlint()) {
         ItemRenderState.Glint glint = shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD;
         layerRenderState.setGlint(glint);
         state.markAnimated();
         state.addModelKey(glint);
      }

      int i = this.tints.size();
      int[] is = layerRenderState.initTints(i);

      for(int j = 0; j < i; ++j) {
         int k = ((TintSource)this.tints.get(j)).getTint(stack, world, user);
         is[j] = k;
         state.addModelKey(k);
      }

      layerRenderState.setVertices(this.vector);
      layerRenderState.setRenderLayer(RenderLayers.getItemLayer(stack));
      this.settings.addSettings(layerRenderState, displayContext);
      layerRenderState.getQuads().addAll(this.quads);
      if (this.animated) {
         state.markAnimated();
      }

   }

   private static boolean shouldUseSpecialGlint(ItemStack stack) {
      return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
   }

   @Environment(EnvType.CLIENT)
   public static record Unbaked(Identifier model, List tints) implements ItemModel.Unbaked {
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model), TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply(instance, Unbaked::new);
      });

      public Unbaked(Identifier identifier, List list) {
         this.model = identifier;
         this.tints = list;
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         resolver.markDependency(this.model);
      }

      public ItemModel bake(ItemModel.BakeContext context) {
         Baker baker = context.blockModelBaker();
         BakedSimpleModel bakedSimpleModel = baker.getModel(this.model);
         ModelTextures modelTextures = bakedSimpleModel.getTextures();
         List list = bakedSimpleModel.bakeGeometry(modelTextures, baker, ModelRotation.X0_Y0).getAllQuads();
         ModelSettings modelSettings = ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
         return new BasicItemModel(this.tints, list, modelSettings);
      }

      public MapCodec getCodec() {
         return CODEC;
      }

      public Identifier model() {
         return this.model;
      }

      public List tints() {
         return this.tints;
      }
   }
}
