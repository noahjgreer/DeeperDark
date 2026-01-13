/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;

@Environment(value=EnvType.CLIENT)
public static final class HangingSignBlockEntityRenderer.Variant
extends Record {
    final WoodType woodType;
    final HangingSignBlockEntityRenderer.AttachmentType attachmentType;

    public HangingSignBlockEntityRenderer.Variant(WoodType woodType, HangingSignBlockEntityRenderer.AttachmentType attachmentType) {
        this.woodType = woodType;
        this.attachmentType = attachmentType;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{HangingSignBlockEntityRenderer.Variant.class, "woodType;attachmentType", "woodType", "attachmentType"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{HangingSignBlockEntityRenderer.Variant.class, "woodType;attachmentType", "woodType", "attachmentType"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{HangingSignBlockEntityRenderer.Variant.class, "woodType;attachmentType", "woodType", "attachmentType"}, this, object);
    }

    public WoodType woodType() {
        return this.woodType;
    }

    public HangingSignBlockEntityRenderer.AttachmentType attachmentType() {
        return this.attachmentType;
    }
}
