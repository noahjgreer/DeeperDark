package net.noahsarch.deeperdark;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.noahsarch.deeperdark.autoupdate.AutoUpdater;
import net.noahsarch.deeperdark.autoupdate.AutoUpdaterScreen;
import net.noahsarch.deeperdark.block.ModBlockEntities;
import net.noahsarch.deeperdark.client.renderer.CreatureEntityRenderer;
import net.noahsarch.deeperdark.client.renderer.SaddlePlayerLayer;
import net.noahsarch.deeperdark.client.renderer.VaultBlockEntityRenderer;
import net.noahsarch.deeperdark.client.screen.BoxScreen;
import net.noahsarch.deeperdark.client.screen.VaultScreen;
import net.noahsarch.deeperdark.entity.ModEntities;
import net.noahsarch.deeperdark.intro.DeeperDarkLogoScreen;
import net.noahsarch.deeperdark.menu.ModMenus;
import net.noahsarch.deeperdark.event.VoidParticleHandler;
import net.noahsarch.deeperdark.payload.PlayerLeashPacket;
import net.noahsarch.deeperdark.payload.VoidFogSyncPacket;

@Environment(EnvType.CLIENT)
public class DeeperDarkClient implements ClientModInitializer {

    private static boolean updateCheckShown = false;

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.COLLAR, net.noahsarch.deeperdark.client.screen.CollarScreen::new);
        MenuScreens.register(ModMenus.COLLAR_BENCH, net.noahsarch.deeperdark.client.screen.CollarBenchScreen::new);
        MenuScreens.register(ModMenus.FLIMSY_BOX, BoxScreen::new);
        MenuScreens.register(ModMenus.STURDY_BOX, BoxScreen::new);
        MenuScreens.register(ModMenus.SMALL_VAULT, VaultScreen::new);
        MenuScreens.register(ModMenus.MEDIUM_VAULT, VaultScreen::new);
        MenuScreens.register(ModMenus.LARGE_VAULT, VaultScreen::new);

        BlockEntityRenderers.register(ModBlockEntities.VAULT, VaultBlockEntityRenderer::new);
        EntityRenderers.register(ModEntities.CREATURE, CreatureEntityRenderer::new);
        LivingEntityRenderLayerRegistrationCallback.EVENT
                .register((entityType, entityRenderer, registrationHelper, context) -> {
                    if (entityRenderer instanceof AvatarRenderer<?> avatarRenderer) {
                        registrationHelper
                                .register(new SaddlePlayerLayer<>(avatarRenderer, context.getEquipmentRenderer()));
                    }
                });

        ClientPlayNetworking.registerGlobalReceiver(PlayerLeashPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().level == null)
                    return;
                // Find the leashed entity — check local player first since level.getEntity()
                // may not find the local player in all code paths.
                Entity leashed = context.client().level.getEntity(payload.leashedEntityId());
                if (leashed == null && context.client().player != null
                        && context.client().player.getId() == payload.leashedEntityId()) {
                    leashed = context.client().player;
                }
                if (!(leashed instanceof Leashable leashedLeashable))
                    return;

                if (payload.holderEntityId() == -1) {
                    // Before clearing, grab holder reference for reverse-visual cleanup
                    Entity currentHolder = leashedLeashable.getLeashHolder();
                    leashedLeashable.removeLeash();
                    // If the local player was leashed, clear the reverse visual on the holder
                    if (leashed == context.client().player && currentHolder instanceof Leashable holderLeashable) {
                        holderLeashable.removeLeash();
                    }
                } else {
                    Entity holder = context.client().level.getEntity(payload.holderEntityId());
                    if (holder != null) {
                        leashedLeashable.setLeashedTo(holder, false);
                        // The leash rope is rendered on the LEASHED entity's model, which the local
                        // player can't see in first-person. Fix: also set the holder to visually point
                        // back to the local player — the holder's model then renders a rope coming
                        // toward the camera, which IS visible in first-person.
                        if (leashed == context.client().player && holder instanceof Leashable holderLeashable) {
                            holderLeashable.setLeashedTo(leashed, false);
                        }
                    }
                }
            });
        });

        VoidParticleHandler.register();

        ClientPlayNetworking.registerGlobalReceiver(VoidFogSyncPacket.ID, (payload, context) ->
                DeeperDarkConfig.get().voidFogEnabled = payload.enabled());

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!updateCheckShown && client.screen instanceof TitleScreen) {
                DeeperDarkConfig.AutoUpdaterConfig cfg = DeeperDarkConfig.get().autoUpdater;
                if (cfg != null && cfg.repoURL != null && !cfg.repoURL.isBlank()) {
                    updateCheckShown = true;
                    AutoUpdater.processPendingDeletes();
                    client.setScreen(new AutoUpdaterScreen(new DeeperDarkLogoScreen(new TitleScreen(), 1.0)));
                }
            }
        });
    }
}
