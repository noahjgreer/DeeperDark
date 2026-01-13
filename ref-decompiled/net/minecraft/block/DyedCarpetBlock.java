/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.CarpetBlock
 *  net.minecraft.block.DyedCarpetBlock
 *  net.minecraft.util.DyeColor
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.util.DyeColor;

/*
 * Exception performing whole class analysis ignored.
 */
public class DyedCarpetBlock
extends CarpetBlock {
    public static final MapCodec<DyedCarpetBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.fieldOf("color").forGetter(DyedCarpetBlock::getDyeColor), (App)DyedCarpetBlock.createSettingsCodec()).apply((Applicative)instance, DyedCarpetBlock::new));
    private final DyeColor dyeColor;

    public MapCodec<DyedCarpetBlock> getCodec() {
        return CODEC;
    }

    public DyedCarpetBlock(DyeColor dyeColor, AbstractBlock.Settings settings) {
        super(settings);
        this.dyeColor = dyeColor;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }
}

