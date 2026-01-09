package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class RandomizedIntBlockStateProvider extends BlockStateProvider {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("source").forGetter((randomizedIntBlockStateProvider) -> {
         return randomizedIntBlockStateProvider.source;
      }), Codec.STRING.fieldOf("property").forGetter((randomizedIntBlockStateProvider) -> {
         return randomizedIntBlockStateProvider.propertyName;
      }), IntProvider.VALUE_CODEC.fieldOf("values").forGetter((randomizedIntBlockStateProvider) -> {
         return randomizedIntBlockStateProvider.values;
      })).apply(instance, RandomizedIntBlockStateProvider::new);
   });
   private final BlockStateProvider source;
   private final String propertyName;
   @Nullable
   private IntProperty property;
   private final IntProvider values;

   public RandomizedIntBlockStateProvider(BlockStateProvider source, IntProperty property, IntProvider values) {
      this.source = source;
      this.property = property;
      this.propertyName = property.getName();
      this.values = values;
      Collection collection = property.getValues();

      for(int i = values.getMin(); i <= values.getMax(); ++i) {
         if (!collection.contains(i)) {
            String var10002 = property.getName();
            throw new IllegalArgumentException("Property value out of range: " + var10002 + ": " + i);
         }
      }

   }

   public RandomizedIntBlockStateProvider(BlockStateProvider source, String propertyName, IntProvider values) {
      this.source = source;
      this.propertyName = propertyName;
      this.values = values;
   }

   protected BlockStateProviderType getType() {
      return BlockStateProviderType.RANDOMIZED_INT_STATE_PROVIDER;
   }

   public BlockState get(Random random, BlockPos pos) {
      BlockState blockState = this.source.get(random, pos);
      if (this.property == null || !blockState.contains(this.property)) {
         IntProperty intProperty = getIntPropertyByName(blockState, this.propertyName);
         if (intProperty == null) {
            return blockState;
         }

         this.property = intProperty;
      }

      return (BlockState)blockState.with(this.property, this.values.get(random));
   }

   @Nullable
   private static IntProperty getIntPropertyByName(BlockState state, String propertyName) {
      Collection collection = state.getProperties();
      Optional optional = collection.stream().filter((property) -> {
         return property.getName().equals(propertyName);
      }).filter((property) -> {
         return property instanceof IntProperty;
      }).map((property) -> {
         return (IntProperty)property;
      }).findAny();
      return (IntProperty)optional.orElse((Object)null);
   }
}
