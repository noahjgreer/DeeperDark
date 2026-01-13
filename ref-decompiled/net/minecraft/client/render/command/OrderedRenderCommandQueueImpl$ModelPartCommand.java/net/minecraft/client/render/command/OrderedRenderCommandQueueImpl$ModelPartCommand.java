/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.command;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.ModelPartCommand(MatrixStack.Entry matricesEntry, ModelPart modelPart, int lightCoords, int overlayCoords, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int outlineColor) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.ModelPartCommand.class, "pose;modelPart;lightCoords;overlayCoords;sprite;sheeted;hasFoil;tintedColor;crumblingOverlay;outlineColor", "matricesEntry", "modelPart", "lightCoords", "overlayCoords", "sprite", "sheeted", "hasGlint", "tintedColor", "crumblingOverlay", "outlineColor"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.ModelPartCommand.class, "pose;modelPart;lightCoords;overlayCoords;sprite;sheeted;hasFoil;tintedColor;crumblingOverlay;outlineColor", "matricesEntry", "modelPart", "lightCoords", "overlayCoords", "sprite", "sheeted", "hasGlint", "tintedColor", "crumblingOverlay", "outlineColor"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.ModelPartCommand.class, "pose;modelPart;lightCoords;overlayCoords;sprite;sheeted;hasFoil;tintedColor;crumblingOverlay;outlineColor", "matricesEntry", "modelPart", "lightCoords", "overlayCoords", "sprite", "sheeted", "hasGlint", "tintedColor", "crumblingOverlay", "outlineColor"}, this, object);
    }
}
