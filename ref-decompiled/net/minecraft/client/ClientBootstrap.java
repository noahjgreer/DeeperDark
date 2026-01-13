/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.ClientBootstrap
 *  net.minecraft.client.gui.screen.dialog.DialogBodyHandlers
 *  net.minecraft.client.gui.screen.dialog.DialogScreens
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers
 *  net.minecraft.client.render.item.model.ItemModelTypes
 *  net.minecraft.client.render.item.model.special.SpecialModelTypes
 *  net.minecraft.client.render.item.property.bool.BooleanProperties
 *  net.minecraft.client.render.item.property.numeric.NumericProperties
 *  net.minecraft.client.render.item.property.select.SelectProperties
 *  net.minecraft.client.render.item.tint.TintSourceTypes
 *  net.minecraft.client.texture.atlas.AtlasSourceManager
 */
package net.minecraft.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandlers;
import net.minecraft.client.gui.screen.dialog.DialogScreens;
import net.minecraft.client.gui.screen.dialog.InputControlHandlers;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.client.render.item.model.special.SpecialModelTypes;
import net.minecraft.client.render.item.property.bool.BooleanProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.texture.atlas.AtlasSourceManager;

@Environment(value=EnvType.CLIENT)
public class ClientBootstrap {
    private static volatile boolean initialized;

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        ItemModelTypes.bootstrap();
        SpecialModelTypes.bootstrap();
        TintSourceTypes.bootstrap();
        SelectProperties.bootstrap();
        BooleanProperties.bootstrap();
        NumericProperties.bootstrap();
        AtlasSourceManager.bootstrap();
        DialogScreens.bootstrap();
        InputControlHandlers.bootstrap();
        DialogBodyHandlers.bootstrap();
    }
}

