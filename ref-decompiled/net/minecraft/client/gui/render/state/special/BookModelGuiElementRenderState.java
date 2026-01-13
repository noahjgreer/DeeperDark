/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.render.state.special.BookModelGuiElementRenderState
 *  net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState
 *  net.minecraft.client.render.entity.model.BookModel
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.render.state.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record BookModelGuiElementRenderState(BookModel bookModel, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState
{
    private final BookModel bookModel;
    private final Identifier texture;
    private final float open;
    private final float flip;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final float scale;
    private final @Nullable ScreenRect scissorArea;
    private final @Nullable ScreenRect bounds;

    public BookModelGuiElementRenderState(BookModel model, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
        this(model, texture, open, flip, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds((int)x1, (int)y1, (int)x2, (int)y2, (ScreenRect)scissorArea));
    }

    public BookModelGuiElementRenderState(BookModel bookModel, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) {
        this.bookModel = bookModel;
        this.texture = texture;
        this.open = open;
        this.flip = flip;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.scale = scale;
        this.scissorArea = scissorArea;
        this.bounds = bounds;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BookModelGuiElementRenderState.class, "bookModel;texture;open;flip;x0;y0;x1;y1;scale;scissorArea;bounds", "bookModel", "texture", "open", "flip", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BookModelGuiElementRenderState.class, "bookModel;texture;open;flip;x0;y0;x1;y1;scale;scissorArea;bounds", "bookModel", "texture", "open", "flip", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BookModelGuiElementRenderState.class, "bookModel;texture;open;flip;x0;y0;x1;y1;scale;scissorArea;bounds", "bookModel", "texture", "open", "flip", "x1", "y1", "x2", "y2", "scale", "scissorArea", "bounds"}, this, object);
    }

    public BookModel bookModel() {
        return this.bookModel;
    }

    public Identifier texture() {
        return this.texture;
    }

    public float open() {
        return this.open;
    }

    public float flip() {
        return this.flip;
    }

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public int x2() {
        return this.x2;
    }

    public int y2() {
        return this.y2;
    }

    public float scale() {
        return this.scale;
    }

    public @Nullable ScreenRect scissorArea() {
        return this.scissorArea;
    }

    public @Nullable ScreenRect bounds() {
        return this.bounds;
    }
}

