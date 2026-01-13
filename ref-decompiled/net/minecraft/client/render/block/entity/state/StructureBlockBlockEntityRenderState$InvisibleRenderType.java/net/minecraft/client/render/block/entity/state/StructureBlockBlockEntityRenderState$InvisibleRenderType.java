/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class StructureBlockBlockEntityRenderState.InvisibleRenderType
extends Enum<StructureBlockBlockEntityRenderState.InvisibleRenderType> {
    public static final /* enum */ StructureBlockBlockEntityRenderState.InvisibleRenderType AIR = new StructureBlockBlockEntityRenderState.InvisibleRenderType();
    public static final /* enum */ StructureBlockBlockEntityRenderState.InvisibleRenderType BARRIER = new StructureBlockBlockEntityRenderState.InvisibleRenderType();
    public static final /* enum */ StructureBlockBlockEntityRenderState.InvisibleRenderType LIGHT = new StructureBlockBlockEntityRenderState.InvisibleRenderType();
    public static final /* enum */ StructureBlockBlockEntityRenderState.InvisibleRenderType STRUCTURE_VOID = new StructureBlockBlockEntityRenderState.InvisibleRenderType();
    private static final /* synthetic */ StructureBlockBlockEntityRenderState.InvisibleRenderType[] field_62687;

    public static StructureBlockBlockEntityRenderState.InvisibleRenderType[] values() {
        return (StructureBlockBlockEntityRenderState.InvisibleRenderType[])field_62687.clone();
    }

    public static StructureBlockBlockEntityRenderState.InvisibleRenderType valueOf(String string) {
        return Enum.valueOf(StructureBlockBlockEntityRenderState.InvisibleRenderType.class, string);
    }

    private static /* synthetic */ StructureBlockBlockEntityRenderState.InvisibleRenderType[] method_74401() {
        return new StructureBlockBlockEntityRenderState.InvisibleRenderType[]{AIR, BARRIER, LIGHT, STRUCTURE_VOID};
    }

    static {
        field_62687 = StructureBlockBlockEntityRenderState.InvisibleRenderType.method_74401();
    }
}
