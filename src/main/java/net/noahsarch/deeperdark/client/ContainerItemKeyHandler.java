package net.noahsarch.deeperdark.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public class ContainerItemKeyHandler {
    // GLFW_KEY_LEFT_ALT = 342
    private static final int GLFW_KEY_LEFT_ALT = 342;

    /**
     * Keybind for opening a shulker box or box held in the player's inventory.
     * Defaults to Left Alt. Appears in Options → Controls under Inventory.
     * KeyMapping auto-registers to ALL when constructed.
     */
    public static final KeyMapping KEY = new KeyMapping(
        "key.deeperdark.open_container_item",
        InputConstants.Type.KEYSYM,
        GLFW_KEY_LEFT_ALT,
        KeyMapping.Category.INVENTORY
    );

    /**
     * Inventory slot index of the container currently open from inventory (-1 = none).
     * Set when the player presses the key; cleared when the player returns to the
     * plain inventory screen. Used by ContainerScreenOpenMixin to suppress the "+"
     * indicator on the slot that is already open (preventing duplication).
     */
    public static int openedFromInventorySlot = -1;

    public static void register() {
        // Constructing KEY above is sufficient — KeyMapping adds itself to the static ALL map.
    }
}
