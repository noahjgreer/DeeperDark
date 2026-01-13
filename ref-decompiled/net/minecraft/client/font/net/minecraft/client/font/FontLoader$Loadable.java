/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Font;
import net.minecraft.resource.ResourceManager;

@Environment(value=EnvType.CLIENT)
public static interface FontLoader.Loadable {
    public Font load(ResourceManager var1) throws IOException;
}
