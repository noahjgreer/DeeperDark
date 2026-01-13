/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item;

import com.google.common.annotations.VisibleForTesting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;

@Environment(value=EnvType.CLIENT)
@VisibleForTesting
static final class HeldItemRenderer.HandRenderType
extends Enum<HeldItemRenderer.HandRenderType> {
    public static final /* enum */ HeldItemRenderer.HandRenderType RENDER_BOTH_HANDS = new HeldItemRenderer.HandRenderType(true, true);
    public static final /* enum */ HeldItemRenderer.HandRenderType RENDER_MAIN_HAND_ONLY = new HeldItemRenderer.HandRenderType(true, false);
    public static final /* enum */ HeldItemRenderer.HandRenderType RENDER_OFF_HAND_ONLY = new HeldItemRenderer.HandRenderType(false, true);
    final boolean renderMainHand;
    final boolean renderOffHand;
    private static final /* synthetic */ HeldItemRenderer.HandRenderType[] field_28389;

    public static HeldItemRenderer.HandRenderType[] values() {
        return (HeldItemRenderer.HandRenderType[])field_28389.clone();
    }

    public static HeldItemRenderer.HandRenderType valueOf(String string) {
        return Enum.valueOf(HeldItemRenderer.HandRenderType.class, string);
    }

    private HeldItemRenderer.HandRenderType(boolean renderMainHand, boolean renderOffHand) {
        this.renderMainHand = renderMainHand;
        this.renderOffHand = renderOffHand;
    }

    public static HeldItemRenderer.HandRenderType shouldOnlyRender(Hand hand) {
        return hand == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
    }

    private static /* synthetic */ HeldItemRenderer.HandRenderType[] method_36915() {
        return new HeldItemRenderer.HandRenderType[]{RENDER_BOTH_HANDS, RENDER_MAIN_HAND_ONLY, RENDER_OFF_HAND_ONLY};
    }

    static {
        field_28389 = HeldItemRenderer.HandRenderType.method_36915();
    }
}
