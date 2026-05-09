package net.noahsarch.deeperdark.component;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.noahsarch.deeperdark.Deeperdark;

public class ModComponents {

    public static final DataComponentType<CollarFuelData> COLLAR_FUEL = Registry.register(
        BuiltInRegistries.DATA_COMPONENT_TYPE,
        Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "collar_fuel"),
        DataComponentType.<CollarFuelData>builder()
            .persistent(CollarFuelData.CODEC)
            .networkSynchronized(CollarFuelData.STREAM_CODEC)
            .build()
    );

    public static void initialize() {
        Deeperdark.LOGGER.info("[Deeper Dark] Registering custom data components");
    }
}
