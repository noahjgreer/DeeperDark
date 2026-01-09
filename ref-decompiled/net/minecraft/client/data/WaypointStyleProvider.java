package net.minecraft.client.data;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.world.waypoint.WaypointStyles;

@Environment(EnvType.CLIENT)
public class WaypointStyleProvider implements DataProvider {
   private final DataOutput.PathResolver pathResolver;

   public WaypointStyleProvider(DataOutput output) {
      this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "waypoint_style");
   }

   private static void bootstrap(BiConsumer waypointStyleBiConsumer) {
      waypointStyleBiConsumer.accept(WaypointStyles.DEFAULT, new WaypointStyleAsset(128, 332, List.of(Identifier.ofVanilla("default_0"), Identifier.ofVanilla("default_1"), Identifier.ofVanilla("default_2"), Identifier.ofVanilla("default_3"))));
      waypointStyleBiConsumer.accept(WaypointStyles.BOWTIE, new WaypointStyleAsset(64, 332, List.of(Identifier.ofVanilla("bowtie"), Identifier.ofVanilla("default_0"), Identifier.ofVanilla("default_1"), Identifier.ofVanilla("default_2"), Identifier.ofVanilla("default_3"))));
   }

   public CompletableFuture run(DataWriter writer) {
      Map map = new HashMap();
      bootstrap((key, asset) -> {
         if (map.putIfAbsent(key, asset) != null) {
            throw new IllegalStateException("Tried to register waypoint style twice for id: " + String.valueOf(key));
         }
      });
      Codec var10001 = WaypointStyleAsset.CODEC;
      DataOutput.PathResolver var10002 = this.pathResolver;
      Objects.requireNonNull(var10002);
      return DataProvider.writeAllToPath(writer, (Codec)var10001, (Function)(var10002::resolveJson), map);
   }

   public String getName() {
      return "Waypoint Style Definitions";
   }
}
