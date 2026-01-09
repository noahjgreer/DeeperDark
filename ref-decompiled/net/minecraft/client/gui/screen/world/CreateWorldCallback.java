package net.minecraft.client.gui.screen.world;

import java.nio.file.Path;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface CreateWorldCallback {
   boolean create(CreateWorldScreen screen, CombinedDynamicRegistries dynamicRegistries, LevelProperties levelProperties, @Nullable Path dataPackTempDir);
}
