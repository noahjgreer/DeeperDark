package net.minecraft.client.gui.render.state.special;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BookModelGuiElementRenderState(BookModel bookModel, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SpecialGuiElementRenderState {
   public BookModelGuiElementRenderState(BookModel model, Identifier texture, float open, float flip, int x1, int y1, int x2, int y2, float scale, @Nullable ScreenRect scissorArea) {
      this(model, texture, open, flip, x1, y1, x2, y2, scale, scissorArea, SpecialGuiElementRenderState.createBounds(x1, y1, x2, y2, scissorArea));
   }

   public BookModelGuiElementRenderState(BookModel bookModel, Identifier identifier, float f, float g, int i, int j, int k, int l, float h, @Nullable ScreenRect screenRect, @Nullable ScreenRect screenRect2) {
      this.bookModel = bookModel;
      this.texture = identifier;
      this.open = f;
      this.flip = g;
      this.x1 = i;
      this.y1 = j;
      this.x2 = k;
      this.y2 = l;
      this.scale = h;
      this.scissorArea = screenRect;
      this.bounds = screenRect2;
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

   @Nullable
   public ScreenRect scissorArea() {
      return this.scissorArea;
   }

   @Nullable
   public ScreenRect bounds() {
      return this.bounds;
   }
}
