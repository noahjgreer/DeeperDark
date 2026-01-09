package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class EndConfiguredFeatures {
   public static final RegistryKey END_PLATFORM = ConfiguredFeatures.of("end_platform");
   public static final RegistryKey END_SPIKE = ConfiguredFeatures.of("end_spike");
   public static final RegistryKey END_GATEWAY_RETURN = ConfiguredFeatures.of("end_gateway_return");
   public static final RegistryKey END_GATEWAY_DELAYED = ConfiguredFeatures.of("end_gateway_delayed");
   public static final RegistryKey CHORUS_PLANT = ConfiguredFeatures.of("chorus_plant");
   public static final RegistryKey END_ISLAND = ConfiguredFeatures.of("end_island");

   public static void bootstrap(Registerable featureRegisterable) {
      ConfiguredFeatures.register(featureRegisterable, END_PLATFORM, Feature.END_PLATFORM);
      ConfiguredFeatures.register(featureRegisterable, END_SPIKE, Feature.END_SPIKE, new EndSpikeFeatureConfig(false, ImmutableList.of(), (BlockPos)null));
      ConfiguredFeatures.register(featureRegisterable, END_GATEWAY_RETURN, Feature.END_GATEWAY, EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true));
      ConfiguredFeatures.register(featureRegisterable, END_GATEWAY_DELAYED, Feature.END_GATEWAY, EndGatewayFeatureConfig.createConfig());
      ConfiguredFeatures.register(featureRegisterable, CHORUS_PLANT, Feature.CHORUS_PLANT);
      ConfiguredFeatures.register(featureRegisterable, END_ISLAND, Feature.END_ISLAND);
   }
}
