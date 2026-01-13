/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class BlockStateModelGenerator.CrossType
extends Enum<BlockStateModelGenerator.CrossType> {
    public static final /* enum */ BlockStateModelGenerator.CrossType TINTED = new BlockStateModelGenerator.CrossType(Models.TINTED_CROSS, Models.TINTED_FLOWER_POT_CROSS, false);
    public static final /* enum */ BlockStateModelGenerator.CrossType NOT_TINTED = new BlockStateModelGenerator.CrossType(Models.CROSS, Models.FLOWER_POT_CROSS, false);
    public static final /* enum */ BlockStateModelGenerator.CrossType EMISSIVE_NOT_TINTED = new BlockStateModelGenerator.CrossType(Models.CROSS_EMISSIVE, Models.FLOWER_POT_CROSS_EMISSIVE, true);
    private final Model model;
    private final Model flowerPotModel;
    private final boolean emissive;
    private static final /* synthetic */ BlockStateModelGenerator.CrossType[] field_22841;

    public static BlockStateModelGenerator.CrossType[] values() {
        return (BlockStateModelGenerator.CrossType[])field_22841.clone();
    }

    public static BlockStateModelGenerator.CrossType valueOf(String string) {
        return Enum.valueOf(BlockStateModelGenerator.CrossType.class, string);
    }

    private BlockStateModelGenerator.CrossType(Model model, Model flowerPotModel, boolean emissive) {
        this.model = model;
        this.flowerPotModel = flowerPotModel;
        this.emissive = emissive;
    }

    public Model getCrossModel() {
        return this.model;
    }

    public Model getFlowerPotCrossModel() {
        return this.flowerPotModel;
    }

    public Identifier registerItemModel(BlockStateModelGenerator modelGenerator, Block block) {
        Item item = block.asItem();
        if (this.emissive) {
            return modelGenerator.uploadTwoLayerBlockItemModel(item, block, "_emissive");
        }
        return modelGenerator.uploadBlockItemModel(item, block);
    }

    public TextureMap getTextureMap(Block block) {
        return this.emissive ? TextureMap.crossAndCrossEmissive(block) : TextureMap.cross(block);
    }

    public TextureMap getFlowerPotTextureMap(Block block) {
        return this.emissive ? TextureMap.plantAndCrossEmissive(block) : TextureMap.plant(block);
    }

    private static /* synthetic */ BlockStateModelGenerator.CrossType[] method_36939() {
        return new BlockStateModelGenerator.CrossType[]{TINTED, NOT_TINTED, EMISSIVE_NOT_TINTED};
    }

    static {
        field_22841 = BlockStateModelGenerator.CrossType.method_36939();
    }
}
