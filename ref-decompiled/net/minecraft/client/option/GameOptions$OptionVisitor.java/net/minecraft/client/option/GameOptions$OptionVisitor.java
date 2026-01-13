/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;

@Environment(value=EnvType.CLIENT)
static interface GameOptions.OptionVisitor {
    public <T> void accept(String var1, SimpleOption<T> var2);
}
