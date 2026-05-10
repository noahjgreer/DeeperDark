package net.noahsarch.deeperdark.menu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.Deeperdark;

public class ModMenus {
    public static final MenuType<CollarBenchMenu> COLLAR_BENCH = register(
        "collar_bench",
        ModMenus::createCollarBenchMenu
    );

    public static final MenuType<CollarMenu> COLLAR = register(
        "collar",
        ModMenus::createCollarMenu
    );

    public static final MenuType<BoxMenu> FLIMSY_BOX = register(
        "flimsy_box",
        ModMenus::createFlimsyMenu
    );

    public static final MenuType<BoxMenu> STURDY_BOX = register(
        "sturdy_box",
        ModMenus::createSturdyMenu
    );

    public static final MenuType<VaultMenu> SMALL_VAULT = register(
        "small_item_vault",
        ModMenus::createSmallVaultMenu
    );

    public static final MenuType<VaultMenu> MEDIUM_VAULT = register(
        "medium_item_vault",
        ModMenus::createMediumVaultMenu
    );

    public static final MenuType<VaultMenu> LARGE_VAULT = register(
        "large_item_vault",
        ModMenus::createLargeVaultMenu
    );

    private static CollarBenchMenu createCollarBenchMenu(int containerId, Inventory inventory) {
        return new CollarBenchMenu(COLLAR_BENCH, containerId, inventory, new SimpleContainer(2));
    }

    private static CollarMenu createCollarMenu(int containerId, Inventory inventory) {
        return new CollarMenu(COLLAR, containerId, inventory, ItemStack.EMPTY);
    }

    private static BoxMenu createFlimsyMenu(int containerId, Inventory inventory) {
        return new BoxMenu(FLIMSY_BOX, containerId, inventory, new SimpleContainer(3), 1);
    }

    private static BoxMenu createSturdyMenu(int containerId, Inventory inventory) {
        return new BoxMenu(STURDY_BOX, containerId, inventory, new SimpleContainer(6), 2);
    }

    private static VaultMenu createSmallVaultMenu(int containerId, Inventory inventory) {
        return new VaultMenu(SMALL_VAULT, containerId, inventory, 1);
    }

    private static VaultMenu createMediumVaultMenu(int containerId, Inventory inventory) {
        return new VaultMenu(MEDIUM_VAULT, containerId, inventory, 3);
    }

    private static VaultMenu createLargeVaultMenu(int containerId, Inventory inventory) {
        return new VaultMenu(LARGE_VAULT, containerId, inventory, 9);
    }

    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
        String name,
        MenuType.MenuSupplier<T> factory
    ) {
        return Registry.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, name),
            new MenuType<>(factory, FeatureFlags.VANILLA_SET)
        );
    }

    public static void initialize() {
        Deeperdark.LOGGER.info("Registering ModMenus for deeperdark");
    }
}
