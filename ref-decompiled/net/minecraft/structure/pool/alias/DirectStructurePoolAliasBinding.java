package net.minecraft.structure.pool.alias;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.random.Random;

public record DirectStructurePoolAliasBinding(RegistryKey alias, RegistryKey target) implements StructurePoolAliasBinding {
   static MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL).fieldOf("alias").forGetter(DirectStructurePoolAliasBinding::alias), RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL).fieldOf("target").forGetter(DirectStructurePoolAliasBinding::target)).apply(instance, DirectStructurePoolAliasBinding::new);
   });

   public DirectStructurePoolAliasBinding(RegistryKey registryKey, RegistryKey registryKey2) {
      this.alias = registryKey;
      this.target = registryKey2;
   }

   public void forEach(Random random, BiConsumer aliasConsumer) {
      aliasConsumer.accept(this.alias, this.target);
   }

   public Stream streamTargets() {
      return Stream.of(this.target);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public RegistryKey alias() {
      return this.alias;
   }

   public RegistryKey target() {
      return this.target;
   }
}
