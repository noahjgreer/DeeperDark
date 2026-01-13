/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ErrorReporter;

record Entity.ErrorReporterContext(Entity entity) implements ErrorReporter.Context
{
    @Override
    public String getName() {
        return this.entity.toString();
    }
}
