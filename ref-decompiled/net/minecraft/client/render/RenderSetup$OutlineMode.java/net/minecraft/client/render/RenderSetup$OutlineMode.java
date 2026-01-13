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
public static final class RenderSetup.OutlineMode
extends Enum<RenderSetup.OutlineMode> {
    public static final /* enum */ RenderSetup.OutlineMode NONE = new RenderSetup.OutlineMode("none");
    public static final /* enum */ RenderSetup.OutlineMode IS_OUTLINE = new RenderSetup.OutlineMode("is_outline");
    public static final /* enum */ RenderSetup.OutlineMode AFFECTS_OUTLINE = new RenderSetup.OutlineMode("affects_outline");
    private final String name;
    private static final /* synthetic */ RenderSetup.OutlineMode[] field_21856;

    public static RenderSetup.OutlineMode[] values() {
        return (RenderSetup.OutlineMode[])field_21856.clone();
    }

    public static RenderSetup.OutlineMode valueOf(String string) {
        return Enum.valueOf(RenderSetup.OutlineMode.class, string);
    }

    private RenderSetup.OutlineMode(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    private static /* synthetic */ RenderSetup.OutlineMode[] method_36916() {
        return new RenderSetup.OutlineMode[]{NONE, IS_OUTLINE, AFFECTS_OUTLINE};
    }

    static {
        field_21856 = RenderSetup.OutlineMode.method_36916();
    }
}
