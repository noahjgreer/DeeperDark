package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.random.Random;

public interface StructurePoolAliasBinding {
   Codec CODEC = Registries.POOL_ALIAS_BINDING.getCodec().dispatch(StructurePoolAliasBinding::getCodec, Function.identity());

   void forEach(Random random, BiConsumer aliasConsumer);

   Stream streamTargets();

   static DirectStructurePoolAliasBinding direct(String alias, String target) {
      return direct(StructurePools.ofVanilla(alias), StructurePools.ofVanilla(target));
   }

   static DirectStructurePoolAliasBinding direct(RegistryKey alias, RegistryKey target) {
      return new DirectStructurePoolAliasBinding(alias, target);
   }

   static RandomStructurePoolAliasBinding random(String alias, Pool targets) {
      Pool.Builder builder = Pool.builder();
      targets.getEntries().forEach((target) -> {
         builder.add(StructurePools.ofVanilla((String)target.value()), target.weight());
      });
      return random(StructurePools.ofVanilla(alias), builder.build());
   }

   static RandomStructurePoolAliasBinding random(RegistryKey alias, Pool targets) {
      return new RandomStructurePoolAliasBinding(alias, targets);
   }

   static RandomGroupStructurePoolAliasBinding randomGroup(Pool groups) {
      return new RandomGroupStructurePoolAliasBinding(groups);
   }

   MapCodec getCodec();
}
