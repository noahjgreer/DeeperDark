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

@Environment(value=EnvType.CLIENT)
public static final class ParrotEntityModel.Pose
extends Enum<ParrotEntityModel.Pose> {
    public static final /* enum */ ParrotEntityModel.Pose FLYING = new ParrotEntityModel.Pose();
    public static final /* enum */ ParrotEntityModel.Pose STANDING = new ParrotEntityModel.Pose();
    public static final /* enum */ ParrotEntityModel.Pose SITTING = new ParrotEntityModel.Pose();
    public static final /* enum */ ParrotEntityModel.Pose PARTY = new ParrotEntityModel.Pose();
    public static final /* enum */ ParrotEntityModel.Pose ON_SHOULDER = new ParrotEntityModel.Pose();
    private static final /* synthetic */ ParrotEntityModel.Pose[] field_3467;

    public static ParrotEntityModel.Pose[] values() {
        return (ParrotEntityModel.Pose[])field_3467.clone();
    }

    public static ParrotEntityModel.Pose valueOf(String string) {
        return Enum.valueOf(ParrotEntityModel.Pose.class, string);
    }

    private static /* synthetic */ ParrotEntityModel.Pose[] method_36893() {
        return new ParrotEntityModel.Pose[]{FLYING, STANDING, SITTING, PARTY, ON_SHOULDER};
    }

    static {
        field_3467 = ParrotEntityModel.Pose.method_36893();
    }
}
