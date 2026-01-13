/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.widget.CyclingButtonWidget;

@Environment(value=EnvType.CLIENT)
class CreateWorldScreen.WorldTab.2
implements CyclingButtonWidget.Values<WorldCreator.WorldType> {
    CreateWorldScreen.WorldTab.2() {
    }

    @Override
    public List<WorldCreator.WorldType> getCurrent() {
        return CyclingButtonWidget.HAS_ALT_DOWN.getAsBoolean() ? WorldTab.this.field_42182.worldCreator.getExtendedWorldTypes() : WorldTab.this.field_42182.worldCreator.getNormalWorldTypes();
    }

    @Override
    public List<WorldCreator.WorldType> getDefaults() {
        return WorldTab.this.field_42182.worldCreator.getNormalWorldTypes();
    }
}
