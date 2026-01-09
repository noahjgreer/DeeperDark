package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public interface BlockStateModel extends FabricBlockStateModel {
   void addParts(Random random, List parts);

   default List getParts(Random random) {
      List list = new ObjectArrayList();
      this.addParts(random, list);
      return list;
   }

   Sprite particleSprite();

   @Environment(EnvType.CLIENT)
   public static class CachedUnbaked implements UnbakedGrouped {
      final Unbaked delegate;
      private final Baker.ResolvableCacheKey cacheKey = new Baker.ResolvableCacheKey() {
         public BlockStateModel compute(Baker baker) {
            return CachedUnbaked.this.delegate.bake(baker);
         }

         // $FF: synthetic method
         public Object compute(final Baker baker) {
            return this.compute(baker);
         }
      };

      public CachedUnbaked(Unbaked delegate) {
         this.delegate = delegate;
      }

      public void resolve(ResolvableModel.Resolver resolver) {
         this.delegate.resolve(resolver);
      }

      public BlockStateModel bake(BlockState state, Baker baker) {
         return (BlockStateModel)baker.compute(this.cacheKey);
      }

      public Object getEqualityGroup(BlockState state) {
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   public interface Unbaked extends ResolvableModel {
      Codec WEIGHTED_VARIANT_CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(ModelVariant.MAP_CODEC.forGetter(Weighted::value), Codecs.POSITIVE_INT.optionalFieldOf("weight", 1).forGetter(Weighted::weight)).apply(instance, Weighted::new);
      });
      Codec WEIGHTED_CODEC = Codecs.nonEmptyList(WEIGHTED_VARIANT_CODEC.listOf()).flatComapMap((list) -> {
         return new WeightedBlockStateModel.Unbaked(Pool.of(Lists.transform(list, (weighted) -> {
            return weighted.transform(SimpleBlockStateModel.Unbaked::new);
         })));
      }, (unbaked) -> {
         List list = unbaked.entries().getEntries();
         List list2 = new ArrayList(list.size());
         Iterator var3 = list.iterator();

         while(var3.hasNext()) {
            Weighted weighted = (Weighted)var3.next();
            Object object = weighted.value();
            if (!(object instanceof SimpleBlockStateModel.Unbaked)) {
               return DataResult.error(() -> {
                  return "Only single variants are supported";
               });
            }

            SimpleBlockStateModel.Unbaked unbaked2 = (SimpleBlockStateModel.Unbaked)object;
            list2.add(new Weighted(unbaked2.variant(), weighted.weight()));
         }

         return DataResult.success(list2);
      });
      Codec CODEC = Codec.either(WEIGHTED_CODEC, SimpleBlockStateModel.Unbaked.CODEC).flatComapMap((either) -> {
         return (Unbaked)either.map((left) -> {
            return left;
         }, (right) -> {
            return right;
         });
      }, (variant) -> {
         Objects.requireNonNull(variant);
         int i = 0;
         DataResult var10000;
         switch (variant.typeSwitch<invokedynamic>(variant, i)) {
            case 0:
               SimpleBlockStateModel.Unbaked unbaked2 = (SimpleBlockStateModel.Unbaked)variant;
               var10000 = DataResult.success(Either.right(unbaked2));
               break;
            case 1:
               WeightedBlockStateModel.Unbaked unbaked3 = (WeightedBlockStateModel.Unbaked)variant;
               var10000 = DataResult.success(Either.left(unbaked3));
               break;
            default:
               var10000 = DataResult.error(() -> {
                  return "Only a single variant or a list of variants are supported";
               });
         }

         return var10000;
      });

      BlockStateModel bake(Baker baker);

      default UnbakedGrouped cached() {
         return new CachedUnbaked(this);
      }
   }

   @Environment(EnvType.CLIENT)
   public interface UnbakedGrouped extends ResolvableModel {
      BlockStateModel bake(BlockState state, Baker baker);

      Object getEqualityGroup(BlockState state);
   }
}
