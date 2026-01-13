/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.TexturedModel;
import net.minecraft.util.Identifier;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface TexturedModel.Factory {
    public TexturedModel get(Block var1);

    default public Identifier upload(Block block, BiConsumer<Identifier, ModelSupplier> writer) {
        return this.get(block).upload(block, writer);
    }

    default public Identifier upload(Block block, String suffix, BiConsumer<Identifier, ModelSupplier> writer) {
        return this.get(block).upload(block, suffix, writer);
    }

    default public TexturedModel.Factory andThen(Consumer<TextureMap> consumer) {
        return block -> this.get(block).textures(consumer);
    }
}
