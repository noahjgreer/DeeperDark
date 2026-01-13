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
import net.minecraft.client.model.Model;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record OrderedRenderCommandQueueImpl.ModelCommand<S>(MatrixStack.Entry matricesEntry, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable Sprite sprite, int outlineColor,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{OrderedRenderCommandQueueImpl.ModelCommand.class, "pose;model;state;lightCoords;overlayCoords;tintedColor;sprite;outlineColor;crumblingOverlay", "matricesEntry", "model", "state", "lightCoords", "overlayCoords", "tintedColor", "sprite", "outlineColor", "crumblingOverlay"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{OrderedRenderCommandQueueImpl.ModelCommand.class, "pose;model;state;lightCoords;overlayCoords;tintedColor;sprite;outlineColor;crumblingOverlay", "matricesEntry", "model", "state", "lightCoords", "overlayCoords", "tintedColor", "sprite", "outlineColor", "crumblingOverlay"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{OrderedRenderCommandQueueImpl.ModelCommand.class, "pose;model;state;lightCoords;overlayCoords;tintedColor;sprite;outlineColor;crumblingOverlay", "matricesEntry", "model", "state", "lightCoords", "overlayCoords", "tintedColor", "sprite", "outlineColor", "crumblingOverlay"}, this, object);
    }
}
