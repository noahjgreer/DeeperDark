package net.noahsarch.deeperdark;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.TitleScreen;
import net.noahsarch.deeperdark.autoupdate.AutoUpdaterScreen;

@Environment(EnvType.CLIENT)
public class DeeperDarkClient implements ClientModInitializer {

    private static boolean updateCheckShown = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!updateCheckShown && client.screen instanceof TitleScreen) {
                DeeperDarkConfig.AutoUpdaterConfig cfg = DeeperDarkConfig.get().autoUpdater;
                if (cfg != null && cfg.repoURL != null && !cfg.repoURL.isBlank()) {
                    updateCheckShown = true;
                    client.setScreen(new AutoUpdaterScreen());
                }
            }
        });
    }
}
