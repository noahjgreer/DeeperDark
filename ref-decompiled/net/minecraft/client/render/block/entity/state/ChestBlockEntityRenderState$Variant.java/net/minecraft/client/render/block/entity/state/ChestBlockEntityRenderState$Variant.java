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
public static final class ChestBlockEntityRenderState.Variant
extends Enum<ChestBlockEntityRenderState.Variant> {
    public static final /* enum */ ChestBlockEntityRenderState.Variant ENDER_CHEST = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant CHRISTMAS = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant TRAPPED = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant COPPER_UNAFFECTED = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant COPPER_EXPOSED = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant COPPER_WEATHERED = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant COPPER_OXIDIZED = new ChestBlockEntityRenderState.Variant();
    public static final /* enum */ ChestBlockEntityRenderState.Variant REGULAR = new ChestBlockEntityRenderState.Variant();
    private static final /* synthetic */ ChestBlockEntityRenderState.Variant[] field_62705;

    public static ChestBlockEntityRenderState.Variant[] values() {
        return (ChestBlockEntityRenderState.Variant[])field_62705.clone();
    }

    public static ChestBlockEntityRenderState.Variant valueOf(String string) {
        return Enum.valueOf(ChestBlockEntityRenderState.Variant.class, string);
    }

    private static /* synthetic */ ChestBlockEntityRenderState.Variant[] method_74402() {
        return new ChestBlockEntityRenderState.Variant[]{ENDER_CHEST, CHRISTMAS, TRAPPED, COPPER_UNAFFECTED, COPPER_EXPOSED, COPPER_WEATHERED, COPPER_OXIDIZED, REGULAR};
    }

    static {
        field_62705 = ChestBlockEntityRenderState.Variant.method_74402();
    }
}
