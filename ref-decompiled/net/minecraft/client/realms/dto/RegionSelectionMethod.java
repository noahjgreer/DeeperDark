/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.RegionSelectionMethod
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class RegionSelectionMethod
extends Enum<RegionSelectionMethod> {
    public static final /* enum */ RegionSelectionMethod AUTOMATIC_PLAYER = new RegionSelectionMethod("AUTOMATIC_PLAYER", 0, 0, "realms.configuration.region_preference.automatic_player");
    public static final /* enum */ RegionSelectionMethod AUTOMATIC_OWNER = new RegionSelectionMethod("AUTOMATIC_OWNER", 1, 1, "realms.configuration.region_preference.automatic_owner");
    public static final /* enum */ RegionSelectionMethod MANUAL = new RegionSelectionMethod("MANUAL", 2, 2, "");
    public static final RegionSelectionMethod DEFAULT;
    public final int index;
    public final String translationKey;
    private static final /* synthetic */ RegionSelectionMethod[] field_60232;

    public static RegionSelectionMethod[] values() {
        return (RegionSelectionMethod[])field_60232.clone();
    }

    public static RegionSelectionMethod valueOf(String string) {
        return Enum.valueOf(RegionSelectionMethod.class, string);
    }

    private RegionSelectionMethod(int index, String translationKey) {
        this.index = index;
        this.translationKey = translationKey;
    }

    private static /* synthetic */ RegionSelectionMethod[] method_71190() {
        return new RegionSelectionMethod[]{AUTOMATIC_PLAYER, AUTOMATIC_OWNER, MANUAL};
    }

    static {
        field_60232 = RegionSelectionMethod.method_71190();
        DEFAULT = AUTOMATIC_PLAYER;
    }
}

