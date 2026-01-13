/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class ItemRenderState.Glint
extends Enum<ItemRenderState.Glint> {
    public static final /* enum */ ItemRenderState.Glint NONE = new ItemRenderState.Glint();
    public static final /* enum */ ItemRenderState.Glint STANDARD = new ItemRenderState.Glint();
    public static final /* enum */ ItemRenderState.Glint SPECIAL = new ItemRenderState.Glint();
    private static final /* synthetic */ ItemRenderState.Glint[] field_55344;

    public static ItemRenderState.Glint[] values() {
        return (ItemRenderState.Glint[])field_55344.clone();
    }

    public static ItemRenderState.Glint valueOf(String string) {
        return Enum.valueOf(ItemRenderState.Glint.class, string);
    }

    private static /* synthetic */ ItemRenderState.Glint[] method_65611() {
        return new ItemRenderState.Glint[]{NONE, STANDARD, SPECIAL};
    }

    static {
        field_55344 = ItemRenderState.Glint.method_65611();
    }
}
