package net.noahsarch.deeperdark.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DispenserMenu;

/** Vanilla dispenser layout and screen, with box slot rules on the 3x3 grid. */
public class ReinforcedBoxMenu extends DispenserMenu {

    public ReinforcedBoxMenu(int containerId, Inventory inventory, Container container) {
        super(containerId, inventory, container);
    }

    @Override
    protected void add3x3GridSlots(Container container, int left, int top) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                this.addSlot(new BoxMenu.BoxSlot(container, x + y * 3, left + x * 18, top + y * 18));
            }
        }
    }
}
