/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.util.function.BiFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface GeneratorOptionsHolder.RegistryAwareModifier
extends BiFunction<DynamicRegistryManager.Immutable, DimensionOptionsRegistryHolder, DimensionOptionsRegistryHolder> {
}
