/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.gui.screen.tab;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static final class RealmsSettingsTab.Region
extends Record {
    final RegionSelectionMethod preference;
    private final @Nullable RealmsRegion region;

    public RealmsSettingsTab.Region(RegionSelectionMethod preference, @Nullable RealmsRegion region) {
        this.preference = preference;
        this.region = region;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RealmsSettingsTab.Region.class, "preference;region", "preference", "region"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RealmsSettingsTab.Region.class, "preference;region", "preference", "region"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RealmsSettingsTab.Region.class, "preference;region", "preference", "region"}, this, object);
    }

    public RegionSelectionMethod preference() {
        return this.preference;
    }

    public @Nullable RealmsRegion region() {
        return this.region;
    }
}
