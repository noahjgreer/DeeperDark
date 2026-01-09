package net.minecraft.client.resource.server;

import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface DownloadQueuer {
   void enqueue(Map entries, Consumer callback);
}
