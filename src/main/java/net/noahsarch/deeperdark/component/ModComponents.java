package net.noahsarch.deeperdark.component;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.block.VaultBlockEntity;

import java.util.List;

public class ModComponents {

    public static final DataComponentType<CollarFuelData> COLLAR_FUEL = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "collar_fuel"),
        DataComponentType.<CollarFuelData>builder()
            .persistent(CollarFuelData.CODEC)
            .networkSynchronized(CollarFuelData.STREAM_CODEC)
            .build()
    );

    // Stores full vault entry list (item type + count) with no slot-count limit.
    // Network-synced so clients can show tooltips and inventory item display.
    public static final DataComponentType<List<VaultBlockEntity.VaultEntry>> VAULT_ENTRIES = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "vault_entries"),
        DataComponentType.<List<VaultBlockEntity.VaultEntry>>builder()
            .persistent(VaultBlockEntity.ENTRY_CODEC.listOf())
            .networkSynchronized(VaultBlockEntity.ENTRIES_STREAM_CODEC)
            .build()
    );

    public static void initialize() {
        Deeperdark.LOGGER.info("[Deeper Dark] Registering custom data components");
    }
}
