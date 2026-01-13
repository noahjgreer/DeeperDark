/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static abstract class WorldListWidget.Entry
extends AlwaysSelectedEntryListWidget.Entry<WorldListWidget.Entry>
implements AutoCloseable {
    @Override
    public void close() {
    }

    public @Nullable LevelSummary getLevel() {
        return null;
    }
}
