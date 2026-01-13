/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.JsonOps
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import org.slf4j.Logger;

record ServerPropertiesHandler.WorldGenProperties(JsonObject generatorSettings, String levelType) {
    private static final Map<String, RegistryKey<WorldPreset>> LEVEL_TYPE_TO_PRESET_KEY = Map.of("default", WorldPresets.DEFAULT, "largebiomes", WorldPresets.LARGE_BIOMES);

    public DimensionOptionsRegistryHolder createDimensionsRegistryHolder(RegistryWrapper.WrapperLookup registries) {
        RegistryEntryLookup registryWrapper = registries.getOrThrow(RegistryKeys.WORLD_PRESET);
        RegistryEntry.Reference<WorldPreset> reference = registryWrapper.getOptional(WorldPresets.DEFAULT).or(() -> ServerPropertiesHandler.WorldGenProperties.method_41241((RegistryWrapper)registryWrapper)).orElseThrow(() -> new IllegalStateException("Invalid datapack contents: can't find default preset"));
        RegistryEntry registryEntry = Optional.ofNullable(Identifier.tryParse(this.levelType)).map(levelTypeId -> RegistryKey.of(RegistryKeys.WORLD_PRESET, levelTypeId)).or(() -> Optional.ofNullable(LEVEL_TYPE_TO_PRESET_KEY.get(this.levelType))).flatMap(((RegistryWrapper)registryWrapper)::getOptional).orElseGet(() -> {
            LOGGER.warn("Failed to parse level-type {}, defaulting to {}", (Object)this.levelType, (Object)reference.registryKey().getValue());
            return reference;
        });
        DimensionOptionsRegistryHolder dimensionOptionsRegistryHolder = ((WorldPreset)registryEntry.value()).createDimensionsRegistryHolder();
        if (registryEntry.matchesKey(WorldPresets.FLAT)) {
            RegistryOps registryOps = registries.getOps(JsonOps.INSTANCE);
            Optional optional = FlatChunkGeneratorConfig.CODEC.parse(new Dynamic(registryOps, (Object)this.generatorSettings())).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0));
            if (optional.isPresent()) {
                return dimensionOptionsRegistryHolder.with(registries, new FlatChunkGenerator((FlatChunkGeneratorConfig)optional.get()));
            }
        }
        return dimensionOptionsRegistryHolder;
    }

    private static /* synthetic */ Optional method_41241(RegistryWrapper registryWrapper) {
        return registryWrapper.streamEntries().findAny();
    }
}
