package net.noahsarch.deeperdark.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.noahsarch.deeperdark.Deeperdark;

public class ModBlockEntities {
    public static final BlockEntityType<CollarBenchBlockEntity> COLLAR_BENCH = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "collarbench"),
        FabricBlockEntityTypeBuilder.create(
            CollarBenchBlockEntity::new,
            ModBlocks.COLLAR_BENCH
        ).build()
    );

    public static final BlockEntityType<BoxBlockEntity> BOX = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "box"),
        FabricBlockEntityTypeBuilder.create(
            BoxBlockEntity::new,
            ModBlocks.FLIMSY_BOX,
            ModBlocks.STURDY_BOX,
            ModBlocks.REINFORCED_BOX
        ).build()
    );

    public static void initialize() {
        Deeperdark.LOGGER.info("Registering ModBlockEntities for deeperdark");
    }
}
