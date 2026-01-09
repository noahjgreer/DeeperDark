package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.random.Random;

public record RandomGroupStructurePoolAliasBinding(Pool groups) implements StructurePoolAliasBinding {
   static MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Pool.createNonEmptyCodec(Codec.list(StructurePoolAliasBinding.CODEC)).fieldOf("groups").forGetter(RandomGroupStructurePoolAliasBinding::groups)).apply(instance, RandomGroupStructurePoolAliasBinding::new);
   });

   public RandomGroupStructurePoolAliasBinding(Pool pool) {
      this.groups = pool;
   }

   public void forEach(Random random, BiConsumer aliasConsumer) {
      this.groups.getOrEmpty(random).ifPresent((group) -> {
         group.forEach((binding) -> {
            binding.forEach(random, aliasConsumer);
         });
      });
   }

   public Stream streamTargets() {
      return this.groups.getEntries().stream().flatMap((present) -> {
         return ((List)present.value()).stream();
      }).flatMap(StructurePoolAliasBinding::streamTargets);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Pool groups() {
      return this.groups;
   }
}
