/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.client.data.Model
 *  net.minecraft.client.data.ModelSupplier
 *  net.minecraft.client.data.Models
 *  net.minecraft.client.data.TextureMap
 *  net.minecraft.client.data.TexturedModel
 *  net.minecraft.client.data.TexturedModel$Factory
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.Model;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class TexturedModel {
    public static final Factory CUBE_ALL = TexturedModel.makeFactory(TextureMap::all, (Model)Models.CUBE_ALL);
    public static final Factory CUBE_ALL_INNER_FACES = TexturedModel.makeFactory(TextureMap::all, (Model)Models.CUBE_ALL_INNER_FACES);
    public static final Factory CUBE_MIRRORED_ALL = TexturedModel.makeFactory(TextureMap::all, (Model)Models.CUBE_MIRRORED_ALL);
    public static final Factory CUBE_COLUMN = TexturedModel.makeFactory(TextureMap::sideEnd, (Model)Models.CUBE_COLUMN);
    public static final Factory CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(TextureMap::sideEnd, (Model)Models.CUBE_COLUMN_HORIZONTAL);
    public static final Factory CUBE_BOTTOM_TOP = TexturedModel.makeFactory(TextureMap::sideTopBottom, (Model)Models.CUBE_BOTTOM_TOP);
    public static final Factory CUBE_TOP = TexturedModel.makeFactory(TextureMap::sideAndTop, (Model)Models.CUBE_TOP);
    public static final Factory ORIENTABLE = TexturedModel.makeFactory(TextureMap::sideFrontTop, (Model)Models.ORIENTABLE);
    public static final Factory ORIENTABLE_WITH_BOTTOM = TexturedModel.makeFactory(TextureMap::sideFrontTopBottom, (Model)Models.ORIENTABLE_WITH_BOTTOM);
    public static final Factory CARPET = TexturedModel.makeFactory(TextureMap::wool, (Model)Models.CARPET);
    public static final Factory MOSSY_CARPET_SIDE = TexturedModel.makeFactory(TextureMap::side, (Model)Models.MOSSY_CARPET_SIDE);
    public static final Factory FLOWERBED_1 = TexturedModel.makeFactory(TextureMap::flowerbed, (Model)Models.FLOWERBED_1);
    public static final Factory FLOWERBED_2 = TexturedModel.makeFactory(TextureMap::flowerbed, (Model)Models.FLOWERBED_2);
    public static final Factory FLOWERBED_3 = TexturedModel.makeFactory(TextureMap::flowerbed, (Model)Models.FLOWERBED_3);
    public static final Factory FLOWERBED_4 = TexturedModel.makeFactory(TextureMap::flowerbed, (Model)Models.FLOWERBED_4);
    public static final Factory TEMPLATE_LEAF_LITTER_1 = TexturedModel.makeFactory(TextureMap::texture, (Model)Models.TEMPLATE_LEAF_LITTER_1);
    public static final Factory TEMPLATE_LEAF_LITTER_2 = TexturedModel.makeFactory(TextureMap::texture, (Model)Models.TEMPLATE_LEAF_LITTER_2);
    public static final Factory TEMPLATE_LEAF_LITTER_3 = TexturedModel.makeFactory(TextureMap::texture, (Model)Models.TEMPLATE_LEAF_LITTER_3);
    public static final Factory TEMPLATE_LEAF_LITTER_4 = TexturedModel.makeFactory(TextureMap::texture, (Model)Models.TEMPLATE_LEAF_LITTER_4);
    public static final Factory TEMPLATE_GLAZED_TERRACOTTA = TexturedModel.makeFactory(TextureMap::pattern, (Model)Models.TEMPLATE_GLAZED_TERRACOTTA);
    public static final Factory CORAL_FAN = TexturedModel.makeFactory(TextureMap::fan, (Model)Models.CORAL_FAN);
    public static final Factory TEMPLATE_ANVIL = TexturedModel.makeFactory(TextureMap::top, (Model)Models.TEMPLATE_ANVIL);
    public static final Factory LEAVES = TexturedModel.makeFactory(TextureMap::all, (Model)Models.LEAVES);
    public static final Factory TEMPLATE_LANTERN = TexturedModel.makeFactory(TextureMap::lantern, (Model)Models.TEMPLATE_LANTERN);
    public static final Factory TEMPLATE_HANGING_LANTERN = TexturedModel.makeFactory(TextureMap::lantern, (Model)Models.TEMPLATE_HANGING_LANTERN);
    public static final Factory TEMPLATE_CHAIN = TexturedModel.makeFactory(TextureMap::texture, (Model)Models.TEMPLATE_CHAIN);
    public static final Factory TEMPLATE_SEAGRASS = TexturedModel.makeFactory(TextureMap::texture, (Model)Models.TEMPLATE_SEAGRASS);
    public static final Factory END_FOR_TOP_CUBE_COLUMN = TexturedModel.makeFactory(TextureMap::sideAndEndForTop, (Model)Models.CUBE_COLUMN);
    public static final Factory END_FOR_TOP_CUBE_COLUMN_HORIZONTAL = TexturedModel.makeFactory(TextureMap::sideAndEndForTop, (Model)Models.CUBE_COLUMN_HORIZONTAL);
    public static final Factory SIDE_TOP_BOTTOM_WALL = TexturedModel.makeFactory(TextureMap::wallSideTopBottom, (Model)Models.CUBE_BOTTOM_TOP);
    public static final Factory SIDE_END_WALL = TexturedModel.makeFactory(TextureMap::wallSideEnd, (Model)Models.CUBE_COLUMN);
    private final TextureMap textures;
    private final Model model;

    private TexturedModel(TextureMap textures, Model model) {
        this.textures = textures;
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public TextureMap getTextures() {
        return this.textures;
    }

    public TexturedModel textures(Consumer<TextureMap> texturesConsumer) {
        texturesConsumer.accept(this.textures);
        return this;
    }

    public Identifier upload(Block block, BiConsumer<Identifier, ModelSupplier> writer) {
        return this.model.upload(block, this.textures, writer);
    }

    public Identifier upload(Block block, String suffix, BiConsumer<Identifier, ModelSupplier> writer) {
        return this.model.upload(block, suffix, this.textures, writer);
    }

    public static Factory makeFactory(Function<Block, TextureMap> texturesGetter, Model model) {
        return block -> new TexturedModel((TextureMap)texturesGetter.apply(block), model);
    }

    public static TexturedModel getCubeAll(Identifier id) {
        return new TexturedModel(TextureMap.all((Identifier)id), Models.CUBE_ALL);
    }
}

