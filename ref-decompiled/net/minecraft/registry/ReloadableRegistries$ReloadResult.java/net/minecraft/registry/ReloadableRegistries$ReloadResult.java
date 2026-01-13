/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry;

import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ServerDynamicRegistryType;

public record ReloadableRegistries.ReloadResult(CombinedDynamicRegistries<ServerDynamicRegistryType> layers, RegistryWrapper.WrapperLookup lookupWithUpdatedTags) {
}
