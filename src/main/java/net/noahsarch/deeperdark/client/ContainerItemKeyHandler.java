package net.noahsarch.deeperdark.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public class ContainerItemKeyHandler {
    /**
     * Keybind for opening a shulker box or box held in the player's inventory.
     * Unbound by default; configure in vanilla Options → Controls.
     * KeyMapping auto-registers to ALL when constructed, so no helper needed.
     */
    public static final KeyMapping KEY = new KeyMapping(
        "key.deeperdark.open_container_item",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        KeyMapping.Category.INVENTORY
    );

    public static void register() {
        // Constructing KEY above is sufficient — KeyMapping adds itself to the static ALL map.
    }
}
