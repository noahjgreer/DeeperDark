package net.noahsarch.deeperdark.compat;

import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.client.screen.VaultScreen;
import net.noahsarch.deeperdark.menu.VaultMenu;
import red.jackf.chesttracker.api.ChestTrackerPlugin;
import red.jackf.chesttracker.api.EventPhases;
import red.jackf.chesttracker.api.memory.Memory;
import red.jackf.chesttracker.api.providers.InteractionTracker;
import red.jackf.chesttracker.api.providers.MemoryBuilder;
import red.jackf.chesttracker.api.providers.MemoryLocation;
import red.jackf.chesttracker.api.providers.defaults.DefaultProviderScreenClose;
import red.jackf.chesttracker.api.providers.defaults.DefaultProviderScreenOpen;
import red.jackf.jackfredlib.api.base.ResultHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * ChestTracker plugin that teaches Chest Tracker about Item Vault screens.
 *
 * Without this:
 *   - Memory location is unknown (no block position associated with the GUI).
 *   - Stored counts show as 1 because VaultDisplaySlot always returns copyWithCount(1).
 *
 * With this:
 *   - Screen-open handler resolves the vault's world position via InteractionTracker.
 *   - Screen-close handler reads the real counts from VaultMenu and stores them.
 */
public class DeeperDarkChestTrackerPlugin implements ChestTrackerPlugin {

    @Override
    public void load() {
        // Resolve the block position when the vault GUI opens so ChestTracker can
        // link the memory to the correct location (required for renaming, manual mode, etc.)
        DefaultProviderScreenOpen.EVENT.register((provider, context) -> {
            if (!(context.getScreen() instanceof VaultScreen)) {
                return false;
            }
            InteractionTracker.INSTANCE.getLastBlockSource().ifPresent(cbs ->
                context.setMemoryLocation(MemoryLocation.inWorld(
                    cbs.level().dimension().identifier(),
                    cbs.pos()
                ))
            );
            return true;
        });

        // Store the real item counts when the vault GUI closes.  The default handler
        // would read VaultDisplaySlot.getItem() which always has count=1; we intercept
        // at PRIORITY_PHASE and provide stacks with the actual stored quantities.
        DefaultProviderScreenClose.EVENT.register(EventPhases.PRIORITY_PHASE, (provider, context) -> {
            if (!(context.getScreen() instanceof VaultScreen)) {
                return ResultHolder.pass();
            }

            VaultMenu menu = ((VaultScreen) context.getScreen()).getMenu();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < menu.getMaxTypes(); i++) {
                ItemStack display = menu.getVaultDisplayItem(i);
                if (!display.isEmpty()) {
                    items.add(display.copyWithCount(menu.getStoredCount(i)));
                }
            }

            return InteractionTracker.INSTANCE.getLastBlockSource()
                .map(cbs -> {
                    Memory memory = MemoryBuilder.create(items)
                        .inContainer(cbs.blockState().getBlock())
                        .build();
                    return ResultHolder.value(new DefaultProviderScreenClose.Result(
                        cbs.level().dimension().identifier(),
                        cbs.pos(),
                        memory
                    ));
                })
                .orElse(ResultHolder.empty());
        });
    }
}
