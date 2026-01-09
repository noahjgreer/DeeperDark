package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class MultipartBlockStateModel implements BlockStateModel {
   private final MultipartBakedModel bakedModels;
   private final BlockState state;
   @Nullable
   private List models;

   MultipartBlockStateModel(MultipartBakedModel bakedModels, BlockState state) {
      this.bakedModels = bakedModels;
      this.state = state;
   }

   public Sprite particleSprite() {
      return this.bakedModels.particleSprite;
   }

   public void addParts(Random random, List parts) {
      if (this.models == null) {
         this.models = this.bakedModels.build(this.state);
      }

      long l = random.nextLong();
      Iterator var5 = this.models.iterator();

      while(var5.hasNext()) {
         BlockStateModel blockStateModel = (BlockStateModel)var5.next();
         random.setSeed(l);
         blockStateModel.addParts(random, parts);
      }

   }

   @Environment(EnvType.CLIENT)
   private static final class MultipartBakedModel {
      private final List selectors;
      final Sprite particleSprite;
      private final Map map = new ConcurrentHashMap();

      private static BlockStateModel getFirst(List selectors) {
         if (selectors.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
         } else {
            return (BlockStateModel)((Selector)selectors.getFirst()).model();
         }
      }

      public MultipartBakedModel(List selectors) {
         this.selectors = selectors;
         BlockStateModel blockStateModel = getFirst(selectors);
         this.particleSprite = blockStateModel.particleSprite();
      }

      public List build(BlockState state) {
         BitSet bitSet = new BitSet();

         for(int i = 0; i < this.selectors.size(); ++i) {
            if (((Selector)this.selectors.get(i)).condition.test(state)) {
               bitSet.set(i);
            }
         }

         return (List)this.map.computeIfAbsent(bitSet, (bitSetx) -> {
            ImmutableList.Builder builder = ImmutableList.builder();

            for(int i = 0; i < this.selectors.size(); ++i) {
               if (bitSetx.get(i)) {
                  builder.add((BlockStateModel)((Selector)this.selectors.get(i)).model);
               }
            }

            return builder.build();
         });
      }
   }

   @Environment(EnvType.CLIENT)
   public static class MultipartUnbaked implements BlockStateModel.UnbakedGrouped {
      final List selectors;
      private final Baker.ResolvableCacheKey bakerCache = new Baker.ResolvableCacheKey() {
         public MultipartBakedModel compute(Baker baker) {
            ImmutableList.Builder builder = ImmutableList.builderWithExpectedSize(MultipartUnbaked.this.selectors.size());
            Iterator var3 = MultipartUnbaked.this.selectors.iterator();

            while(var3.hasNext()) {
               Selector selector = (Selector)var3.next();
               builder.add(selector.build(((BlockStateModel.Unbaked)selector.model).bake(baker)));
            }

            return new MultipartBakedModel(builder.build());
         }

         // $FF: synthetic method
         public Object compute(final Baker baker) {
            return this.compute(baker);
         }
      };

      public MultipartUnbaked(List selectors) {
         this.selectors = selectors;
      }

      public Object getEqualityGroup(BlockState state) {
         IntList intList = new IntArrayList();

         for(int i = 0; i < this.selectors.size(); ++i) {
            if (((Selector)this.selectors.get(i)).condition.test(state)) {
               intList.add(i);
            }
         }

         @Environment(EnvType.CLIENT)
         record EqualityGroup(MultipartUnbaked model, IntList selectors) {
            EqualityGroup(MultipartUnbaked multipartUnbaked, IntList intList) {
               this.model = multipartUnbaked;
               this.selectors = intList;
            }

            public MultipartUnbaked model() {
               return this.model;
            }

            public IntList selectors() {
               return this.selectors;
            }
         }

         return new EqualityGroup(this, intList);
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.selectors.forEach((selector) -> {
            ((BlockStateModel.Unbaked)selector.model).resolve(resolver);
         });
      }

      public BlockStateModel bake(BlockState state, Baker baker) {
         MultipartBakedModel multipartBakedModel = (MultipartBakedModel)baker.compute(this.bakerCache);
         return new MultipartBlockStateModel(multipartBakedModel, state);
      }
   }

   @Environment(EnvType.CLIENT)
   public static record Selector(Predicate condition, Object model) {
      final Predicate condition;
      final Object model;

      public Selector(Predicate predicate, Object object) {
         this.condition = predicate;
         this.model = object;
      }

      public Selector build(Object object) {
         return new Selector(this.condition, object);
      }

      public Predicate condition() {
         return this.condition;
      }

      public Object model() {
         return this.model;
      }
   }
}
