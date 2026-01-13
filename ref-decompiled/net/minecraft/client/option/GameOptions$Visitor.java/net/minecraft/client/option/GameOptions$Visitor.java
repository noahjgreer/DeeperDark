/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.option;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameOptions;

@Environment(value=EnvType.CLIENT)
static interface GameOptions.Visitor
extends GameOptions.OptionVisitor {
    public int visitInt(String var1, int var2);

    public boolean visitBoolean(String var1, boolean var2);

    public String visitString(String var1, String var2);

    public float visitFloat(String var1, float var2);

    public <T> T visitObject(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
}
