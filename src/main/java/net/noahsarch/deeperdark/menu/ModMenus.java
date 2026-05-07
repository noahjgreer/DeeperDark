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

    private static CollarMenu createCollarMenu(int containerId, Inventory inventory) {
        return new CollarMenu(COLLAR, containerId, inventory, ItemStack.EMPTY);
    }

    private static BoxMenu createFlimsyMenu(int containerId, Inventory inventory) {
        return new BoxMenu(FLIMSY_BOX, containerId, inventory, new SimpleContainer(3), 1);
    }

    private static BoxMenu createSturdyMenu(int containerId, Inventory inventory) {
        return new BoxMenu(STURDY_BOX, containerId, inventory, new SimpleContainer(6), 2);
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
