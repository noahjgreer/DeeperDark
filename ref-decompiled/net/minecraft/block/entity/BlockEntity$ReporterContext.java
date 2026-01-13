/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ErrorReporter;

record BlockEntity.ReporterContext(BlockEntity blockEntity) implements ErrorReporter.Context
{
    @Override
    public String getName() {
        return this.blockEntity.getNameForReport() + "@" + String.valueOf(this.blockEntity.getPos());
    }
}
