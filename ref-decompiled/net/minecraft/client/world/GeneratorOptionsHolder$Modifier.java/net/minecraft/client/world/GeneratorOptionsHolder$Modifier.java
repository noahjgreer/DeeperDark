/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.gen.GeneratorOptions;

@Environment(value=EnvType.CLIENT)
public static interface GeneratorOptionsHolder.Modifier
extends UnaryOperator<GeneratorOptions> {
}
