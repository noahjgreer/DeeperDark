/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.data.WaypointStyleProvider
 *  net.minecraft.client.resource.waypoint.WaypointStyleAsset
 *  net.minecraft.data.DataOutput
 *  net.minecraft.data.DataOutput$OutputType
 *  net.minecraft.data.DataOutput$PathResolver
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.DataWriter
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.util.Identifier
 *  net.minecraft.world.waypoint.WaypointStyle
 *  net.minecraft.world.waypoint.WaypointStyles
 */
package net.minecraft.client.data;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.waypoint.WaypointStyleAsset;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.waypoint.WaypointStyle;
import net.minecraft.world.waypoint.WaypointStyles;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class WaypointStyleProvider
implements DataProvider {
    private final DataOutput.PathResolver pathResolver;

    public WaypointStyleProvider(DataOutput output) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "waypoint_style");
    }

    private static void bootstrap(BiConsumer<RegistryKey<WaypointStyle>, WaypointStyleAsset> waypointStyleBiConsumer) {
        waypointStyleBiConsumer.accept((RegistryKey<WaypointStyle>)WaypointStyles.DEFAULT, new WaypointStyleAsset(128, 332, List.of(Identifier.ofVanilla((String)"default_0"), Identifier.ofVanilla((String)"default_1"), Identifier.ofVanilla((String)"default_2"), Identifier.ofVanilla((String)"default_3"))));
        waypointStyleBiConsumer.accept((RegistryKey<WaypointStyle>)WaypointStyles.BOWTIE, new WaypointStyleAsset(64, 332, List.of(Identifier.ofVanilla((String)"bowtie"), Identifier.ofVanilla((String)"default_0"), Identifier.ofVanilla((String)"default_1"), Identifier.ofVanilla((String)"default_2"), Identifier.ofVanilla((String)"default_3"))));
    }

    public CompletableFuture<?> run(DataWriter writer) {
        HashMap map = new HashMap();
        WaypointStyleProvider.bootstrap((T key, U asset) -> {
            if (map.putIfAbsent(key, asset) != null) {
                throw new IllegalStateException("Tried to register waypoint style twice for id: " + String.valueOf(key));
            }
        });
        return DataProvider.writeAllToPath((DataWriter)writer, (Codec)WaypointStyleAsset.CODEC, arg_0 -> ((DataOutput.PathResolver)this.pathResolver).resolveJson(arg_0), map);
    }

    public String getName() {
        return "Waypoint Style Definitions";
    }
}

