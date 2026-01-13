/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.AnvilScreen
 *  net.minecraft.client.gui.screen.ingame.BeaconScreen
 *  net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen
 *  net.minecraft.client.gui.screen.ingame.BrewingStandScreen
 *  net.minecraft.client.gui.screen.ingame.CartographyTableScreen
 *  net.minecraft.client.gui.screen.ingame.CrafterScreen
 *  net.minecraft.client.gui.screen.ingame.CraftingScreen
 *  net.minecraft.client.gui.screen.ingame.EnchantmentScreen
 *  net.minecraft.client.gui.screen.ingame.FurnaceScreen
 *  net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen
 *  net.minecraft.client.gui.screen.ingame.GenericContainerScreen
 *  net.minecraft.client.gui.screen.ingame.GrindstoneScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreens
 *  net.minecraft.client.gui.screen.ingame.HandledScreens$Provider
 *  net.minecraft.client.gui.screen.ingame.HopperScreen
 *  net.minecraft.client.gui.screen.ingame.LecternScreen
 *  net.minecraft.client.gui.screen.ingame.LoomScreen
 *  net.minecraft.client.gui.screen.ingame.MerchantScreen
 *  net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen
 *  net.minecraft.client.gui.screen.ingame.SmithingScreen
 *  net.minecraft.client.gui.screen.ingame.SmokerScreen
 *  net.minecraft.client.gui.screen.ingame.StonecutterScreen
 *  net.minecraft.registry.Registries
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenHandlerType
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.BrewingStandScreen;
import net.minecraft.client.gui.screen.ingame.CartographyTableScreen;
import net.minecraft.client.gui.screen.ingame.CrafterScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.LecternScreen;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.SmokerScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class HandledScreens {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ScreenHandlerType<?>, Provider<?, ?>> PROVIDERS = Maps.newHashMap();

    public static <T extends ScreenHandler> void open(ScreenHandlerType<T> type, MinecraftClient client, int id, Text title) {
        Provider provider = HandledScreens.getProvider(type);
        if (provider == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", (Object)Registries.SCREEN_HANDLER.getId(type));
            return;
        }
        provider.open(title, type, client, id);
    }

    private static <T extends ScreenHandler> // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable HandledScreens.Provider<T, ?> getProvider(ScreenHandlerType<T> type) {
        return (Provider)PROVIDERS.get(type);
    }

    public static <M extends ScreenHandler, U extends Screen> void register(ScreenHandlerType<? extends M> type, Provider<M, U> provider) {
        Provider<M, U> provider2 = PROVIDERS.put(type, provider);
        if (provider2 != null) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf(Registries.SCREEN_HANDLER.getId(type)));
        }
    }

    public static boolean isMissingScreens() {
        boolean bl = false;
        for (ScreenHandlerType screenHandlerType : Registries.SCREEN_HANDLER) {
            if (PROVIDERS.containsKey(screenHandlerType)) continue;
            LOGGER.debug("Menu {} has no matching screen", (Object)Registries.SCREEN_HANDLER.getId((Object)screenHandlerType));
            bl = true;
        }
        return bl;
    }

    static {
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_9X1, GenericContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_9X2, GenericContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_9X3, GenericContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_9X4, GenericContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_9X5, GenericContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_9X6, GenericContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GENERIC_3X3, Generic3x3ContainerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.CRAFTER_3X3, CrafterScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.ANVIL, AnvilScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.BEACON, BeaconScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.BREWING_STAND, BrewingStandScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.CRAFTING, CraftingScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.ENCHANTMENT, EnchantmentScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.FURNACE, FurnaceScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.GRINDSTONE, GrindstoneScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.HOPPER, HopperScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.LECTERN, LecternScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.LOOM, LoomScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.MERCHANT, MerchantScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.SHULKER_BOX, ShulkerBoxScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.SMITHING, SmithingScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.SMOKER, SmokerScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
        HandledScreens.register((ScreenHandlerType)ScreenHandlerType.STONECUTTER, StonecutterScreen::new);
    }
}

