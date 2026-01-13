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
import net.minecraft.client.render.gizmo.GizmoDrawerImpl;

@Environment(value=EnvType.CLIENT)
record WorldRenderer.Gizmos(GizmoDrawerImpl standardPrimitives, GizmoDrawerImpl alwaysOnTopPrimitives) {
}
