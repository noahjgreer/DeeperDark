/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class DiffuseLighting.Type
extends Enum<DiffuseLighting.Type> {
    public static final /* enum */ DiffuseLighting.Type LEVEL = new DiffuseLighting.Type();
    public static final /* enum */ DiffuseLighting.Type ITEMS_FLAT = new DiffuseLighting.Type();
    public static final /* enum */ DiffuseLighting.Type ITEMS_3D = new DiffuseLighting.Type();
    public static final /* enum */ DiffuseLighting.Type ENTITY_IN_UI = new DiffuseLighting.Type();
    public static final /* enum */ DiffuseLighting.Type PLAYER_SKIN = new DiffuseLighting.Type();
    private static final /* synthetic */ DiffuseLighting.Type[] field_60030;

    public static DiffuseLighting.Type[] values() {
        return (DiffuseLighting.Type[])field_60030.clone();
    }

    public static DiffuseLighting.Type valueOf(String string) {
        return Enum.valueOf(DiffuseLighting.Type.class, string);
    }

    private static /* synthetic */ DiffuseLighting.Type[] method_71037() {
        return new DiffuseLighting.Type[]{LEVEL, ITEMS_FLAT, ITEMS_3D, ENTITY_IN_UI, PLAYER_SKIN};
    }

    static {
        field_60030 = DiffuseLighting.Type.method_71037();
    }
}
