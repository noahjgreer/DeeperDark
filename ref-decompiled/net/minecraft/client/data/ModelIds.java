/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.Block
 *  net.minecraft.client.data.ModelIds
 *  net.minecraft.item.Item
 *  net.minecraft.registry.Registries
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ModelIds {
    @Deprecated
    public static Identifier getMinecraftNamespacedBlock(String name) {
        return Identifier.ofVanilla((String)("block/" + name));
    }

    public static Identifier getMinecraftNamespacedItem(String name) {
        return Identifier.ofVanilla((String)("item/" + name));
    }

    public static Identifier getBlockSubModelId(Block block, String suffix) {
        Identifier identifier = Registries.BLOCK.getId((Object)block);
        return identifier.withPath(path -> "block/" + path + suffix);
    }

    public static Identifier getBlockModelId(Block block) {
        Identifier identifier = Registries.BLOCK.getId((Object)block);
        return identifier.withPrefixedPath("block/");
    }

    public static Identifier getItemModelId(Item item) {
        Identifier identifier = Registries.ITEM.getId((Object)item);
        return identifier.withPrefixedPath("item/");
    }

    public static Identifier getItemSubModelId(Item item, String suffix) {
        Identifier identifier = Registries.ITEM.getId((Object)item);
        return identifier.withPath(path -> "item/" + path + suffix);
    }
}

