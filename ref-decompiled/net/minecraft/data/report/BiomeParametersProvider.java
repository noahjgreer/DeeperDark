package net.minecraft.data.report;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.slf4j.Logger;

public class BiomeParametersProvider implements DataProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Path path;
   private final CompletableFuture registriesFuture;
   private static final MapCodec BIOME_KEY_CODEC;
   private static final Codec BIOME_ENTRY_CODEC;

   public BiomeParametersProvider(DataOutput output, CompletableFuture registriesFuture) {
      this.path = output.resolvePath(DataOutput.OutputType.REPORTS).resolve("biome_parameters");
      this.registriesFuture = registriesFuture;
   }

   public CompletableFuture run(DataWriter writer) {
      return this.registriesFuture.thenCompose((registries) -> {
         DynamicOps dynamicOps = registries.getOps(JsonOps.INSTANCE);
         List list = new ArrayList();
         MultiNoiseBiomeSourceParameterList.getPresetToEntriesMap().forEach((preset, entries) -> {
            list.add(write(this.resolvePath(preset.id()), writer, dynamicOps, BIOME_ENTRY_CODEC, entries));
         });
         return CompletableFuture.allOf((CompletableFuture[])list.toArray((i) -> {
            return new CompletableFuture[i];
         }));
      });
   }

   private static CompletableFuture write(Path path, DataWriter writer, DynamicOps ops, Encoder codec, Object biomeSource) {
      Optional optional = codec.encodeStart(ops, biomeSource).resultOrPartial((error) -> {
         LOGGER.error("Couldn't serialize element {}: {}", path, error);
      });
      return optional.isPresent() ? DataProvider.writeToPath(writer, (JsonElement)optional.get(), path) : CompletableFuture.completedFuture((Object)null);
   }

   private Path resolvePath(Identifier id) {
      return this.path.resolve(id.getNamespace()).resolve(id.getPath() + ".json");
   }

   public String getName() {
      return "Biome Parameters";
   }

   static {
      BIOME_KEY_CODEC = RegistryKey.createCodec(RegistryKeys.BIOME).fieldOf("biome");
      BIOME_ENTRY_CODEC = MultiNoiseUtil.Entries.createCodec(BIOME_KEY_CODEC).fieldOf("biomes").codec();
   }
}
