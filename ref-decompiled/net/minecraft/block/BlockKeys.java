/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockKeys
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.util.Identifier
 */
package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BlockKeys {
    public static final RegistryKey<Block> PUMPKIN = BlockKeys.of((String)"pumpkin");
    public static final RegistryKey<Block> PUMPKIN_STEM = BlockKeys.of((String)"pumpkin_stem");
    public static final RegistryKey<Block> ATTACHED_PUMPKIN_STEM = BlockKeys.of((String)"attached_pumpkin_stem");
    public static final RegistryKey<Block> MELON = BlockKeys.of((String)"melon");
    public static final RegistryKey<Block> MELON_STEM = BlockKeys.of((String)"melon_stem");
    public static final RegistryKey<Block> ATTACHED_MELON_STEM = BlockKeys.of((String)"attached_melon_stem");

    private static RegistryKey<Block> of(String id) {
        return RegistryKey.of((RegistryKey)RegistryKeys.BLOCK, (Identifier)Identifier.ofVanilla((String)id));
    }
}

