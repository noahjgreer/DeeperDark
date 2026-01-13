/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public static sealed class BipedEntityModel.ArmPose
extends Enum<BipedEntityModel.ArmPose> {
    public static final /* enum */ BipedEntityModel.ArmPose EMPTY = new BipedEntityModel.ArmPose(false, false);
    public static final /* enum */ BipedEntityModel.ArmPose ITEM = new BipedEntityModel.ArmPose(false, false);
    public static final /* enum */ BipedEntityModel.ArmPose BLOCK = new BipedEntityModel.ArmPose(false, false);
    public static final /* enum */ BipedEntityModel.ArmPose BOW_AND_ARROW = new BipedEntityModel.ArmPose(true, true);
    public static final /* enum */ BipedEntityModel.ArmPose THROW_TRIDENT = new BipedEntityModel.ArmPose(false, true);
    public static final /* enum */ BipedEntityModel.ArmPose CROSSBOW_CHARGE = new BipedEntityModel.ArmPose(true, true);
    public static final /* enum */ BipedEntityModel.ArmPose CROSSBOW_HOLD = new BipedEntityModel.ArmPose(true, true);
    public static final /* enum */ BipedEntityModel.ArmPose SPYGLASS = new BipedEntityModel.ArmPose(false, false);
    public static final /* enum */ BipedEntityModel.ArmPose TOOT_HORN = new BipedEntityModel.ArmPose(false, false);
    public static final /* enum */ BipedEntityModel.ArmPose BRUSH = new BipedEntityModel.ArmPose(false, false);
    public static final /* enum */ BipedEntityModel.ArmPose SPEAR = new BipedEntityModel.ArmPose(false, true){

        @Override
        public <S extends ArmedEntityRenderState> void method_75382(S armedEntityRenderState, MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
            Lancing.method_75392(armedEntityRenderState, matrixStack, f, arm, itemStack);
        }
    };
    private final boolean twoHanded;
    private final boolean field_64557;
    private static final /* synthetic */ BipedEntityModel.ArmPose[] field_3404;

    public static BipedEntityModel.ArmPose[] values() {
        return (BipedEntityModel.ArmPose[])field_3404.clone();
    }

    public static BipedEntityModel.ArmPose valueOf(String string) {
        return Enum.valueOf(BipedEntityModel.ArmPose.class, string);
    }

    BipedEntityModel.ArmPose(boolean twoHanded, boolean bl) {
        this.twoHanded = twoHanded;
        this.field_64557 = bl;
    }

    public boolean isTwoHanded() {
        return this.twoHanded;
    }

    public boolean method_76639() {
        return this.field_64557;
    }

    public <S extends ArmedEntityRenderState> void method_75382(S armedEntityRenderState, MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
    }

    private static /* synthetic */ BipedEntityModel.ArmPose[] method_36892() {
        return new BipedEntityModel.ArmPose[]{EMPTY, ITEM, BLOCK, BOW_AND_ARROW, THROW_TRIDENT, CROSSBOW_CHARGE, CROSSBOW_HOLD, SPYGLASS, TOOT_HORN, BRUSH, SPEAR};
    }

    static {
        field_3404 = BipedEntityModel.ArmPose.method_36892();
    }
}
