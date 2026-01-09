package net.minecraft.structure.pool.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.random.Random;

public record RandomStructurePoolAliasBinding(RegistryKey alias, Pool targets) implements StructurePoolAliasBinding {
   static MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL).fieldOf("alias").forGetter(RandomStructurePoolAliasBinding::alias), Pool.createNonEmptyCodec(RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL)).fieldOf("targets").forGetter(RandomStructurePoolAliasBinding::targets)).apply(instance, RandomStructurePoolAliasBinding::new);
   });

   public RandomStructurePoolAliasBinding(RegistryKey registryKey, Pool pool) {
      this.alias = registryKey;
      this.targets = pool;
   }

   public void forEach(Random random, BiConsumer aliasConsumer) {
      this.targets.getOrEmpty(random).ifPresent((target) -> {
         aliasConsumer.accept(this.alias, target);
      });
   }

   public Stream streamTargets() {
      return this.targets.getEntries().stream().map(Weighted::value);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryKey alias() {
      return this.alias;
   }

   public Pool targets() {
      return this.targets;
   }
}
