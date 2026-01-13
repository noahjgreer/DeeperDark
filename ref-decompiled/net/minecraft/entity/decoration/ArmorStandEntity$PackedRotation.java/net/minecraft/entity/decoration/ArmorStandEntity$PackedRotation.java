/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.entity.decoration;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.EulerAngle;

public record ArmorStandEntity.PackedRotation(EulerAngle head, EulerAngle body, EulerAngle leftArm, EulerAngle rightArm, EulerAngle leftLeg, EulerAngle rightLeg) {
    public static final ArmorStandEntity.PackedRotation DEFAULT = new ArmorStandEntity.PackedRotation(DEFAULT_HEAD_ROTATION, DEFAULT_BODY_ROTATION, DEFAULT_LEFT_ARM_ROTATION, DEFAULT_RIGHT_ARM_ROTATION, DEFAULT_LEFT_LEG_ROTATION, DEFAULT_RIGHT_LEG_ROTATION);
    public static final Codec<ArmorStandEntity.PackedRotation> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)EulerAngle.CODEC.optionalFieldOf("Head", (Object)DEFAULT_HEAD_ROTATION).forGetter(ArmorStandEntity.PackedRotation::head), (App)EulerAngle.CODEC.optionalFieldOf("Body", (Object)DEFAULT_BODY_ROTATION).forGetter(ArmorStandEntity.PackedRotation::body), (App)EulerAngle.CODEC.optionalFieldOf("LeftArm", (Object)DEFAULT_LEFT_ARM_ROTATION).forGetter(ArmorStandEntity.PackedRotation::leftArm), (App)EulerAngle.CODEC.optionalFieldOf("RightArm", (Object)DEFAULT_RIGHT_ARM_ROTATION).forGetter(ArmorStandEntity.PackedRotation::rightArm), (App)EulerAngle.CODEC.optionalFieldOf("LeftLeg", (Object)DEFAULT_LEFT_LEG_ROTATION).forGetter(ArmorStandEntity.PackedRotation::leftLeg), (App)EulerAngle.CODEC.optionalFieldOf("RightLeg", (Object)DEFAULT_RIGHT_LEG_ROTATION).forGetter(ArmorStandEntity.PackedRotation::rightLeg)).apply((Applicative)instance, ArmorStandEntity.PackedRotation::new));
}
