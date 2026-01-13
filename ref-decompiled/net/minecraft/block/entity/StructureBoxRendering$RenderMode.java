/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

public static final class StructureBoxRendering.RenderMode
extends Enum<StructureBoxRendering.RenderMode> {
    public static final /* enum */ StructureBoxRendering.RenderMode NONE = new StructureBoxRendering.RenderMode();
    public static final /* enum */ StructureBoxRendering.RenderMode BOX = new StructureBoxRendering.RenderMode();
    public static final /* enum */ StructureBoxRendering.RenderMode BOX_AND_INVISIBLE_BLOCKS = new StructureBoxRendering.RenderMode();
    private static final /* synthetic */ StructureBoxRendering.RenderMode[] field_55997;

    public static StructureBoxRendering.RenderMode[] values() {
        return (StructureBoxRendering.RenderMode[])field_55997.clone();
    }

    public static StructureBoxRendering.RenderMode valueOf(String string) {
        return Enum.valueOf(StructureBoxRendering.RenderMode.class, string);
    }

    private static /* synthetic */ StructureBoxRendering.RenderMode[] method_66715() {
        return new StructureBoxRendering.RenderMode[]{NONE, BOX, BOX_AND_INVISIBLE_BLOCKS};
    }

    static {
        field_55997 = StructureBoxRendering.RenderMode.method_66715();
    }
}
