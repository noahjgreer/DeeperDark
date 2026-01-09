package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ReferencedModelsCollector {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Object2ObjectMap modelCache = new Object2ObjectOpenHashMap();
   private final Holder missingModel;
   private final Object2ObjectFunction holder;
   private final ResolvableModel.Resolver resolver;
   private final Queue queue = new ArrayDeque();

   public ReferencedModelsCollector(Map unbakedModels, UnbakedModel missingModel) {
      this.missingModel = new Holder(MissingModel.ID, missingModel, true);
      this.modelCache.put(MissingModel.ID, this.missingModel);
      this.holder = (id) -> {
         Identifier identifier = (Identifier)id;
         UnbakedModel unbakedModel = (UnbakedModel)unbakedModels.get(identifier);
         if (unbakedModel == null) {
            LOGGER.warn("Missing block model: {}", identifier);
            return this.missingModel;
         } else {
            return this.schedule(identifier, unbakedModel);
         }
      };
      this.resolver = this::resolve;
   }

   private static boolean isRootModel(UnbakedModel model) {
      return model.parent() == null;
   }

   private Holder resolve(Identifier id) {
      return (Holder)this.modelCache.computeIfAbsent(id, this.holder);
   }

   private Holder schedule(Identifier id, UnbakedModel model) {
      boolean bl = isRootModel(model);
      Holder holder = new Holder(id, model, bl);
      if (!bl) {
         this.queue.add(holder);
      }

      return holder;
   }

   public void resolve(ResolvableModel model) {
      model.resolve(this.resolver);
   }

   public void addSpecialModel(Identifier id, UnbakedModel model) {
      if (!isRootModel(model)) {
         LOGGER.warn("Trying to add non-root special model {}, ignoring", id);
      } else {
         Holder holder = (Holder)this.modelCache.put(id, this.schedule(id, model));
         if (holder != null) {
            LOGGER.warn("Duplicate special model {}", id);
         }

      }
   }

   public BakedSimpleModel getMissingModel() {
      return this.missingModel;
   }

   public Map collectModels() {
      List list = new ArrayList();
      this.resolveAll(list);
      checkIfValid(list);
      ImmutableMap.Builder builder = ImmutableMap.builder();
      this.modelCache.forEach((id, model) -> {
         if (model.valid) {
            builder.put(id, model);
         } else {
            LOGGER.warn("Model {} ignored due to cyclic dependency", id);
         }

      });
      return builder.build();
   }

   private void resolveAll(List models) {
      Holder holder;
      while((holder = (Holder)this.queue.poll()) != null) {
         Identifier identifier = (Identifier)Objects.requireNonNull(holder.model.parent());
         Holder holder2 = this.resolve(identifier);
         holder.parent = holder2;
         if (holder2.valid) {
            holder.valid = true;
         } else {
            models.add(holder);
         }
      }

   }

   private static void checkIfValid(List models) {
      boolean bl = true;

      while(bl) {
         bl = false;
         Iterator iterator = models.iterator();

         while(iterator.hasNext()) {
            Holder holder = (Holder)iterator.next();
            if (((Holder)Objects.requireNonNull(holder.parent)).valid) {
               holder.valid = true;
               iterator.remove();
               bl = true;
            }
         }
      }

   }

   @Environment(EnvType.CLIENT)
   private static class Holder implements BakedSimpleModel {
      private static final Property AMBIENT_OCCLUSION_PROPERTY = createProperty(0);
      private static final Property GUI_LIGHT_PROPERTY = createProperty(1);
      private static final Property GEOMETRY_PROPERTY = createProperty(2);
      private static final Property TRANSFORMATIONS_PROPERTY = createProperty(3);
      private static final Property TEXTURE_PROPERTY = createProperty(4);
      private static final Property PARTICLE_TEXTURE_PROPERTY = createProperty(5);
      private static final Property BAKED_GEOMETRY_PROPERTY = createProperty(6);
      private static final int PROPERTY_COUNT = 7;
      private final Identifier id;
      boolean valid;
      @Nullable
      Holder parent;
      final UnbakedModel model;
      private final AtomicReferenceArray properties = new AtomicReferenceArray(7);
      private final Map bakeCache = new ConcurrentHashMap();

      private static Property createProperty(int i) {
         Objects.checkIndex(i, 7);
         return new Property(i);
      }

      Holder(Identifier id, UnbakedModel model, boolean valid) {
         this.id = id;
         this.model = model;
         this.valid = valid;
      }

      public UnbakedModel getModel() {
         return this.model;
      }

      @Nullable
      public BakedSimpleModel getParent() {
         return this.parent;
      }

      public String name() {
         return this.id.toString();
      }

      @Nullable
      private Object getProperty(Property property) {
         return this.properties.get(property.index);
      }

      private Object setProperty(Property property, Object value) {
         Object object = this.properties.compareAndExchange(property.index, (Object)null, value);
         return object == null ? value : object;
      }

      private Object getProperty(Property property, Function fallback) {
         Object object = this.getProperty(property);
         return object != null ? object : this.setProperty(property, fallback.apply(this));
      }

      public boolean getAmbientOcclusion() {
         return (Boolean)this.getProperty(AMBIENT_OCCLUSION_PROPERTY, BakedSimpleModel::getAmbientOcclusion);
      }

      public UnbakedModel.GuiLight getGuiLight() {
         return (UnbakedModel.GuiLight)this.getProperty(GUI_LIGHT_PROPERTY, BakedSimpleModel::getGuiLight);
      }

      public ModelTransformation getTransformations() {
         return (ModelTransformation)this.getProperty(TRANSFORMATIONS_PROPERTY, BakedSimpleModel::copyTransformations);
      }

      public Geometry getGeometry() {
         return (Geometry)this.getProperty(GEOMETRY_PROPERTY, BakedSimpleModel::getGeometry);
      }

      public ModelTextures getTextures() {
         return (ModelTextures)this.getProperty(TEXTURE_PROPERTY, BakedSimpleModel::getTextures);
      }

      public Sprite getParticleTexture(ModelTextures textures, Baker baker) {
         Sprite sprite = (Sprite)this.getProperty(PARTICLE_TEXTURE_PROPERTY);
         return sprite != null ? sprite : (Sprite)this.setProperty(PARTICLE_TEXTURE_PROPERTY, BakedSimpleModel.getParticleTexture(textures, baker, this));
      }

      private BakedGeometry getBakedGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
         BakedGeometry bakedGeometry = (BakedGeometry)this.getProperty(BAKED_GEOMETRY_PROPERTY);
         return bakedGeometry != null ? bakedGeometry : (BakedGeometry)this.setProperty(BAKED_GEOMETRY_PROPERTY, this.getGeometry().bake(textures, baker, settings, this));
      }

      public BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
         return settings == ModelRotation.X0_Y0 ? this.getBakedGeometry(textures, baker, settings) : (BakedGeometry)this.bakeCache.computeIfAbsent(settings, (settings1) -> {
            Geometry geometry = this.getGeometry();
            return geometry.bake(textures, baker, settings1, this);
         });
      }
   }

   @Environment(EnvType.CLIENT)
   private static record Property(int index) {
      final int index;

      Property(int i) {
         this.index = i;
      }

      public int index() {
         return this.index;
      }
   }
}
