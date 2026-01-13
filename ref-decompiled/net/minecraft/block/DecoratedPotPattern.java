/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.DecoratedPotPattern
 *  net.minecraft.util.Identifier
 */
package net.minecraft.block;

import net.minecraft.util.Identifier;

public record DecoratedPotPattern(Identifier assetId) {
    private final Identifier assetId;

    public DecoratedPotPattern(Identifier assetId) {
        this.assetId = assetId;
    }

    public Identifier assetId() {
        return this.assetId;
    }
}

