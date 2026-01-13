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
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.render.model.json.WeightedVariant;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BlockStateModelGenerator.LogTexturePool {
    private final TextureMap textures;

    public BlockStateModelGenerator.LogTexturePool(TextureMap textures) {
        this.textures = textures;
    }

    public BlockStateModelGenerator.LogTexturePool wood(Block woodBlock) {
        TextureMap textureMap = this.textures.copyAndAdd(TextureKey.END, this.textures.getTexture(TextureKey.SIDE));
        Identifier identifier = Models.CUBE_COLUMN.upload(woodBlock, textureMap, BlockStateModelGenerator.this.modelCollector);
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(woodBlock, BlockStateModelGenerator.createWeightedVariant(identifier)));
        BlockStateModelGenerator.this.registerParentedItemModel(woodBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.LogTexturePool stem(Block stemBlock) {
        Identifier identifier = Models.CUBE_COLUMN.upload(stemBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(stemBlock, BlockStateModelGenerator.createWeightedVariant(identifier)));
        BlockStateModelGenerator.this.registerParentedItemModel(stemBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.LogTexturePool log(Block logBlock) {
        Identifier identifier = Models.CUBE_COLUMN.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector);
        WeightedVariant weightedVariant = BlockStateModelGenerator.createWeightedVariant(Models.CUBE_COLUMN_HORIZONTAL.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createAxisRotatedBlockState(logBlock, BlockStateModelGenerator.createWeightedVariant(identifier), weightedVariant));
        BlockStateModelGenerator.this.registerParentedItemModel(logBlock, identifier);
        return this;
    }

    public BlockStateModelGenerator.LogTexturePool uvLockedLog(Block logBlock) {
        BlockStateModelGenerator.this.blockStateCollector.accept(BlockStateModelGenerator.createUvLockedColumnBlockState(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        BlockStateModelGenerator.this.registerParentedItemModel(logBlock, Models.CUBE_COLUMN.upload(logBlock, this.textures, BlockStateModelGenerator.this.modelCollector));
        return this;
    }
}
