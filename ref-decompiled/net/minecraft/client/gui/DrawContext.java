package net.minecraft.client.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.TextGuiElementRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.BannerResultGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.BookModelGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.EntityGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.PlayerSkinGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState;
import net.minecraft.client.gui.render.state.special.SignGuiElementRenderState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.KeyedItemRenderState;
import net.minecraft.client.texture.GuiAtlasManager;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector2ic;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class DrawContext {
   private static final int BACKGROUND_MARGIN = 2;
   private final MinecraftClient client;
   private final Matrix3x2fStack matrices;
   public final ScissorStack scissorStack;
   private final GuiAtlasManager guiAtlasManager;
   public final GuiRenderState state;
   @Nullable
   private Runnable tooltipDrawer;

   private DrawContext(MinecraftClient client, Matrix3x2fStack matrices, GuiRenderState state) {
      this.scissorStack = new ScissorStack();
      this.client = client;
      this.matrices = matrices;
      this.guiAtlasManager = client.getGuiAtlasManager();
      this.state = state;
   }

   public DrawContext(MinecraftClient client, GuiRenderState state) {
      this(client, new Matrix3x2fStack(16), state);
   }

   public int getScaledWindowWidth() {
      return this.client.getWindow().getScaledWidth();
   }

   public int getScaledWindowHeight() {
      return this.client.getWindow().getScaledHeight();
   }

   public void createNewRootLayer() {
      this.state.createNewRootLayer();
   }

   public void applyBlur() {
      this.state.applyBlur();
   }

   public Matrix3x2fStack getMatrices() {
      return this.matrices;
   }

   public void drawHorizontalLine(int x1, int x2, int y, int color) {
      if (x2 < x1) {
         int i = x1;
         x1 = x2;
         x2 = i;
      }

      this.fill(x1, y, x2 + 1, y + 1, color);
   }

   public void drawVerticalLine(int x, int y1, int y2, int color) {
      if (y2 < y1) {
         int i = y1;
         y1 = y2;
         y2 = i;
      }

      this.fill(x, y1 + 1, x + 1, y2, color);
   }

   public void enableScissor(int x1, int y1, int x2, int y2) {
      ScreenRect screenRect = (new ScreenRect(x1, y1, x2 - x1, y2 - y1)).transform(this.matrices);
      this.scissorStack.push(screenRect);
   }

   public void disableScissor() {
      this.scissorStack.pop();
   }

   public boolean scissorContains(int x, int y) {
      return this.scissorStack.contains(x, y);
   }

   public void fill(int x1, int y1, int x2, int y2, int color) {
      this.fill(RenderPipelines.GUI, x1, y1, x2, y2, color);
   }

   public void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
      int i;
      if (x1 < x2) {
         i = x1;
         x1 = x2;
         x2 = i;
      }

      if (y1 < y2) {
         i = y1;
         y1 = y2;
         y2 = i;
      }

      this.fill(pipeline, TextureSetup.empty(), x1, y1, x2, y2, color, (Integer)null);
   }

   public void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
      this.fill(RenderPipelines.GUI, TextureSetup.empty(), startX, startY, endX, endY, colorStart, colorEnd);
   }

   public void fill(RenderPipeline pipeline, TextureSetup textureSetup, int x1, int y1, int x2, int y2) {
      this.fill(pipeline, textureSetup, x1, y1, x2, y2, -1, (Integer)null);
   }

   private void fill(RenderPipeline pipeline, TextureSetup textureSetup, int x1, int y1, int x2, int y2, int color, @Nullable Integer color2) {
      this.state.addSimpleElement(new ColoredQuadGuiElementRenderState(pipeline, textureSetup, new Matrix3x2f(this.matrices), x1, y1, x2, y2, color, color2 != null ? color2 : color, this.scissorStack.peekLast()));
   }

   public void drawSelection(int x1, int y1, int x2, int y2) {
      this.fill(RenderPipelines.GUI_INVERT, x1, y1, x2, y2, -1);
      this.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x1, y1, x2, y2, -16776961);
   }

   public void drawCenteredTextWithShadow(TextRenderer textRenderer, String text, int centerX, int y, int color) {
      this.drawTextWithShadow(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color);
   }

   public void drawCenteredTextWithShadow(TextRenderer textRenderer, Text text, int centerX, int y, int color) {
      OrderedText orderedText = text.asOrderedText();
      this.drawTextWithShadow(textRenderer, orderedText, centerX - textRenderer.getWidth(orderedText) / 2, y, color);
   }

   public void drawCenteredTextWithShadow(TextRenderer textRenderer, OrderedText text, int centerX, int y, int color) {
      this.drawTextWithShadow(textRenderer, text, centerX - textRenderer.getWidth(text) / 2, y, color);
   }

   public void drawTextWithShadow(TextRenderer textRenderer, @Nullable String text, int x, int y, int color) {
      this.drawText(textRenderer, text, x, y, color, true);
   }

   public void drawText(TextRenderer textRenderer, @Nullable String text, int x, int y, int color, boolean shadow) {
      if (text != null) {
         this.drawText(textRenderer, Language.getInstance().reorder(StringVisitable.plain(text)), x, y, color, shadow);
      }
   }

   public void drawTextWithShadow(TextRenderer textRenderer, OrderedText text, int x, int y, int color) {
      this.drawText(textRenderer, text, x, y, color, true);
   }

   public void drawText(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow) {
      if (ColorHelper.getAlpha(color) != 0) {
         this.state.addText(new TextGuiElementRenderState(textRenderer, text, new Matrix3x2f(this.matrices), x, y, color, 0, shadow, this.scissorStack.peekLast()));
      }
   }

   public void drawTextWithShadow(TextRenderer textRenderer, Text text, int x, int y, int color) {
      this.drawText(textRenderer, text, x, y, color, true);
   }

   public void drawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
      this.drawText(textRenderer, text.asOrderedText(), x, y, color, shadow);
   }

   public void drawWrappedTextWithShadow(TextRenderer textRenderer, StringVisitable text, int x, int y, int width, int color) {
      this.drawWrappedText(textRenderer, text, x, y, width, color, true);
   }

   public void drawWrappedText(TextRenderer textRenderer, StringVisitable text, int x, int y, int width, int color, boolean shadow) {
      for(Iterator var8 = textRenderer.wrapLines(text, width).iterator(); var8.hasNext(); y += 9) {
         OrderedText orderedText = (OrderedText)var8.next();
         this.drawText(textRenderer, orderedText, x, y, color, shadow);
         Objects.requireNonNull(textRenderer);
      }

   }

   public void drawTextWithBackground(TextRenderer textRenderer, Text text, int x, int y, int width, int color) {
      int i = this.client.options.getTextBackgroundColor(0.0F);
      if (i != 0) {
         int j = true;
         int var10001 = x - 2;
         int var10002 = y - 2;
         int var10003 = x + width + 2;
         Objects.requireNonNull(textRenderer);
         this.fill(var10001, var10002, var10003, y + 9 + 2, ColorHelper.mix(i, color));
      }

      this.drawText(textRenderer, text, x, y, color, true);
   }

   public void drawBorder(int x, int y, int width, int height, int color) {
      this.fill(x, y, x + width, y + 1, color);
      this.fill(x, y + height - 1, x + width, y + height, color);
      this.fill(x, y + 1, x + 1, y + height - 1, color);
      this.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
   }

   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
      this.drawGuiTexture(pipeline, sprite, x, y, width, height, -1);
   }

   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, float alpha) {
      this.drawGuiTexture(pipeline, sprite, x, y, width, height, ColorHelper.withAlpha(alpha, -1));
   }

   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color) {
      Sprite sprite2 = this.guiAtlasManager.getSprite(sprite);
      Scaling scaling = this.guiAtlasManager.getScaling(sprite2);
      if (scaling instanceof Scaling.Stretch) {
         this.drawSpriteStretched(pipeline, sprite2, x, y, width, height, color);
      } else if (scaling instanceof Scaling.Tile) {
         Scaling.Tile tile = (Scaling.Tile)scaling;
         this.drawSpriteTiled(pipeline, sprite2, x, y, width, height, 0, 0, tile.width(), tile.height(), tile.width(), tile.height(), color);
      } else if (scaling instanceof Scaling.NineSlice) {
         Scaling.NineSlice nineSlice = (Scaling.NineSlice)scaling;
         this.drawSpriteNineSliced(pipeline, sprite2, nineSlice, x, y, width, height, color);
      }

   }

   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height) {
      this.drawGuiTexture(pipeline, sprite, textureWidth, textureHeight, u, v, x, y, width, height, -1);
   }

   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, int color) {
      Sprite sprite2 = this.guiAtlasManager.getSprite(sprite);
      Scaling scaling = this.guiAtlasManager.getScaling(sprite2);
      if (scaling instanceof Scaling.Stretch) {
         this.drawSpriteRegion(pipeline, sprite2, textureWidth, textureHeight, u, v, x, y, width, height, color);
      } else {
         this.enableScissor(x, y, x + width, y + height);
         this.drawGuiTexture(pipeline, sprite, x - u, y - v, textureWidth, textureHeight, color);
         this.disableScissor();
      }

   }

   public void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height) {
      this.drawSpriteStretched(pipeline, sprite, x, y, width, height, -1);
   }

   public void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height, int color) {
      if (width != 0 && height != 0) {
         this.drawTexturedQuad(pipeline, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), color);
      }
   }

   private void drawSpriteRegion(RenderPipeline pipeline, Sprite sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height, int color) {
      if (width != 0 && height != 0) {
         this.drawTexturedQuad(pipeline, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getFrameU((float)u / (float)textureWidth), sprite.getFrameU((float)(u + width) / (float)textureWidth), sprite.getFrameV((float)v / (float)textureHeight), sprite.getFrameV((float)(v + height) / (float)textureHeight), color);
      }
   }

   private void drawSpriteNineSliced(RenderPipeline pipeline, Sprite sprite, Scaling.NineSlice nineSlice, int x, int y, int width, int height, int color) {
      Scaling.NineSlice.Border border = nineSlice.border();
      int i = Math.min(border.left(), width / 2);
      int j = Math.min(border.right(), width / 2);
      int k = Math.min(border.top(), height / 2);
      int l = Math.min(border.bottom(), height / 2);
      if (width == nineSlice.width() && height == nineSlice.height()) {
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, height, color);
      } else if (height == nineSlice.height()) {
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, i, height, color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x + i, y, width - j - i, height, i, 0, nineSlice.width() - j - i, nineSlice.height(), nineSlice.width(), nineSlice.height(), color);
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, 0, x + width - j, y, j, height, color);
      } else if (width == nineSlice.width()) {
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, width, k, color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x, y + k, width, height - l - k, 0, k, nineSlice.width(), nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - l, x, y + height - l, width, l, color);
      } else {
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, 0, x, y, i, k, color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x + i, y, width - j - i, k, i, 0, nineSlice.width() - j - i, k, nineSlice.width(), nineSlice.height(), color);
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, 0, x + width - j, y, j, k, color);
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), 0, nineSlice.height() - l, x, y + height - l, i, l, color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x + i, y + height - l, width - j - i, l, i, nineSlice.height() - l, nineSlice.width() - j - i, l, nineSlice.width(), nineSlice.height(), color);
         this.drawSpriteRegion(pipeline, sprite, nineSlice.width(), nineSlice.height(), nineSlice.width() - j, nineSlice.height() - l, x + width - j, y + height - l, j, l, color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x, y + k, i, height - l - k, 0, k, i, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x + i, y + k, width - j - i, height - l - k, i, k, nineSlice.width() - j - i, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
         this.drawInnerSprite(pipeline, nineSlice, sprite, x + width - j, y + k, j, height - l - k, nineSlice.width() - j, k, j, nineSlice.height() - l - k, nineSlice.width(), nineSlice.height(), color);
      }
   }

   private void drawInnerSprite(RenderPipeline pipeline, Scaling.NineSlice nineSlice, Sprite sprite, int x, int y, int width, int height, int u, int v, int tileWidth, int tileHeight, int textureWidth, int textureHeight, int color) {
      if (width > 0 && height > 0) {
         if (nineSlice.stretchInner()) {
            this.drawTexturedQuad(pipeline, sprite.getAtlasId(), x, x + width, y, y + height, sprite.getFrameU((float)u / (float)textureWidth), sprite.getFrameU((float)(u + tileWidth) / (float)textureWidth), sprite.getFrameV((float)v / (float)textureHeight), sprite.getFrameV((float)(v + tileHeight) / (float)textureHeight), color);
         } else {
            this.drawSpriteTiled(pipeline, sprite, x, y, width, height, u, v, tileWidth, tileHeight, textureWidth, textureHeight, color);
         }

      }
   }

   private void drawSpriteTiled(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height, int u, int v, int tileWidth, int tileHeight, int textureWidth, int textureHeight, int color) {
      if (width > 0 && height > 0) {
         if (tileWidth > 0 && tileHeight > 0) {
            for(int i = 0; i < width; i += tileWidth) {
               int j = Math.min(tileWidth, width - i);

               for(int k = 0; k < height; k += tileHeight) {
                  int l = Math.min(tileHeight, height - k);
                  this.drawSpriteRegion(pipeline, sprite, textureWidth, textureHeight, u, v, x + i, y + k, j, l, color);
               }
            }

         } else {
            throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + tileWidth + "x" + tileHeight);
         }
      }
   }

   public void drawTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color) {
      this.drawTexture(pipeline, sprite, x, y, u, v, width, height, width, height, textureWidth, textureHeight, color);
   }

   public void drawTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
      this.drawTexture(pipeline, sprite, x, y, u, v, width, height, width, height, textureWidth, textureHeight);
   }

   public void drawTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
      this.drawTexture(pipeline, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, -1);
   }

   public void drawTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
      this.drawTexturedQuad(pipeline, sprite, x, x + width, y, y + height, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight, color);
   }

   public void drawTexturedQuad(Identifier sprite, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2) {
      this.drawTexturedQuad(RenderPipelines.GUI_TEXTURED, (Identifier)sprite, x1, x2, y1, y2, u1, u2, v1, v2, -1);
   }

   private void drawTexturedQuad(RenderPipeline pipeline, Identifier sprite, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color) {
      GpuTextureView gpuTextureView = this.client.getTextureManager().getTexture(sprite).getGlTextureView();
      this.drawTexturedQuad(pipeline, gpuTextureView, x1, y1, x2, y2, u1, u2, v1, v2, color);
   }

   private void drawTexturedQuad(RenderPipeline pipeline, GpuTextureView texture, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color) {
      this.state.addSimpleElement(new TexturedQuadGuiElementRenderState(pipeline, TextureSetup.withoutGlTexture(texture), new Matrix3x2f(this.matrices), x1, y1, x2, y2, u1, u2, v1, v2, color, this.scissorStack.peekLast()));
   }

   public void drawItem(ItemStack item, int x, int y) {
      this.drawItem(this.client.player, this.client.world, item, x, y, 0);
   }

   public void drawItem(ItemStack stack, int x, int y, int seed) {
      this.drawItem(this.client.player, this.client.world, stack, x, y, seed);
   }

   public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
      this.drawItemWithoutEntity(stack, x, y, 0);
   }

   public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
      this.drawItem((LivingEntity)null, this.client.world, stack, x, y, seed);
   }

   public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      this.drawItem(entity, entity.getWorld(), stack, x, y, seed);
   }

   private void drawItem(@Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed) {
      if (!stack.isEmpty()) {
         KeyedItemRenderState keyedItemRenderState = new KeyedItemRenderState();
         this.client.getItemModelManager().clearAndUpdate(keyedItemRenderState, stack, ItemDisplayContext.GUI, world, entity, seed);

         try {
            this.state.addItem(new ItemGuiElementRenderState(stack.getItem().getName().toString(), new Matrix3x2f(this.matrices), keyedItemRenderState, x, y, this.scissorStack.peekLast()));
         } catch (Throwable var11) {
            CrashReport crashReport = CrashReport.create(var11, "Rendering item");
            CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
            crashReportSection.add("Item Type", () -> {
               return String.valueOf(stack.getItem());
            });
            crashReportSection.add("Item Components", () -> {
               return String.valueOf(stack.getComponents());
            });
            crashReportSection.add("Item Foil", () -> {
               return String.valueOf(stack.hasGlint());
            });
            throw new CrashException(crashReport);
         }
      }
   }

   public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y) {
      this.drawStackOverlay(textRenderer, stack, x, y, (String)null);
   }

   public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String stackCountText) {
      if (!stack.isEmpty()) {
         this.matrices.pushMatrix();
         this.drawItemBar(stack, x, y);
         this.drawCooldownProgress(stack, x, y);
         this.drawStackCount(textRenderer, stack, x, y, stackCountText);
         this.matrices.popMatrix();
      }
   }

   public void drawTooltip(Text text, int x, int y) {
      this.drawTooltip(List.of(text.asOrderedText()), x, y);
   }

   public void drawTooltip(List text, int x, int y) {
      this.drawTooltip(this.client.textRenderer, text, HoveredTooltipPositioner.INSTANCE, x, y, false);
   }

   public void drawItemTooltip(TextRenderer textRenderer, ItemStack stack, int x, int y) {
      this.drawTooltip(textRenderer, Screen.getTooltipFromItem(this.client, stack), stack.getTooltipData(), x, y, (Identifier)stack.get(DataComponentTypes.TOOLTIP_STYLE));
   }

   public void drawTooltip(TextRenderer textRenderer, List text, Optional data, int x, int y) {
      this.drawTooltip(textRenderer, text, data, x, y, (Identifier)null);
   }

   public void drawTooltip(TextRenderer textRenderer, List text, Optional data, int x, int y, @Nullable Identifier texture) {
      List list = (List)text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Util.toArrayList());
      data.ifPresent((datax) -> {
         list.add(list.isEmpty() ? 0 : 1, TooltipComponent.of(datax));
      });
      this.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, texture, false);
   }

   public void drawTooltip(TextRenderer textRenderer, Text text, int x, int y) {
      this.drawTooltip(textRenderer, (Text)text, x, y, (Identifier)null);
   }

   public void drawTooltip(TextRenderer textRenderer, Text text, int x, int y, @Nullable Identifier texture) {
      this.drawOrderedTooltip(textRenderer, List.of(text.asOrderedText()), x, y, texture);
   }

   public void drawTooltip(TextRenderer textRenderer, List text, int x, int y) {
      this.drawTooltip(textRenderer, (List)text, x, y, (Identifier)null);
   }

   public void drawTooltip(TextRenderer textRenderer, List text, int x, int y, @Nullable Identifier texture) {
      this.drawTooltip(textRenderer, text.stream().map(Text::asOrderedText).map(TooltipComponent::of).toList(), x, y, HoveredTooltipPositioner.INSTANCE, texture, false);
   }

   public void drawOrderedTooltip(TextRenderer textRenderer, List text, int x, int y) {
      this.drawOrderedTooltip(textRenderer, text, x, y, (Identifier)null);
   }

   public void drawOrderedTooltip(TextRenderer textRenderer, List text, int x, int y, @Nullable Identifier texture) {
      this.drawTooltip(textRenderer, (List)text.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, HoveredTooltipPositioner.INSTANCE, texture, false);
   }

   public void drawTooltip(TextRenderer textRenderer, List text, TooltipPositioner positioner, int x, int y, boolean focused) {
      this.drawTooltip(textRenderer, (List)text.stream().map(TooltipComponent::of).collect(Collectors.toList()), x, y, positioner, (Identifier)null, focused);
   }

   private void drawTooltip(TextRenderer textRenderer, List components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture, boolean focused) {
      if (!components.isEmpty()) {
         if (this.tooltipDrawer == null || focused) {
            this.tooltipDrawer = () -> {
               this.drawTooltipImmediately(textRenderer, components, x, y, positioner, texture);
            };
         }

      }
   }

   public void drawTooltipImmediately(TextRenderer textRenderer, List components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture) {
      int i = 0;
      int j = components.size() == 1 ? -2 : 0;

      TooltipComponent tooltipComponent;
      for(Iterator var9 = components.iterator(); var9.hasNext(); j += tooltipComponent.getHeight(textRenderer)) {
         tooltipComponent = (TooltipComponent)var9.next();
         int k = tooltipComponent.getWidth(textRenderer);
         if (k > i) {
            i = k;
         }
      }

      int l = i;
      int m = j;
      Vector2ic vector2ic = positioner.getPosition(this.getScaledWindowWidth(), this.getScaledWindowHeight(), x, y, i, j);
      int n = vector2ic.x();
      int o = vector2ic.y();
      this.matrices.pushMatrix();
      TooltipBackgroundRenderer.render(this, n, o, i, j, texture);
      int p = o;

      int q;
      TooltipComponent tooltipComponent2;
      for(q = 0; q < components.size(); ++q) {
         tooltipComponent2 = (TooltipComponent)components.get(q);
         tooltipComponent2.drawText(this, textRenderer, n, p);
         p += tooltipComponent2.getHeight(textRenderer) + (q == 0 ? 2 : 0);
      }

      p = o;

      for(q = 0; q < components.size(); ++q) {
         tooltipComponent2 = (TooltipComponent)components.get(q);
         tooltipComponent2.drawItems(textRenderer, n, p, l, m, this);
         p += tooltipComponent2.getHeight(textRenderer) + (q == 0 ? 2 : 0);
      }

      this.matrices.popMatrix();
   }

   public void renderTooltip() {
      if (this.tooltipDrawer != null) {
         this.createNewRootLayer();
         this.tooltipDrawer.run();
         this.tooltipDrawer = null;
      }

   }

   private void drawItemBar(ItemStack stack, int x, int y) {
      if (stack.isItemBarVisible()) {
         int i = x + 2;
         int j = y + 13;
         this.fill(RenderPipelines.GUI, i, j, i + 13, j + 2, -16777216);
         this.fill(RenderPipelines.GUI, i, j, i + stack.getItemBarStep(), j + 1, ColorHelper.fullAlpha(stack.getItemBarColor()));
      }

   }

   private void drawStackCount(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String stackCountText) {
      if (stack.getCount() != 1 || stackCountText != null) {
         String string = stackCountText == null ? String.valueOf(stack.getCount()) : stackCountText;
         this.drawText(textRenderer, (String)string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, -1, true);
      }

   }

   private void drawCooldownProgress(ItemStack stack, int x, int y) {
      ClientPlayerEntity clientPlayerEntity = this.client.player;
      float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack, this.client.getRenderTickCounter().getTickProgress(true));
      if (f > 0.0F) {
         int i = y + MathHelper.floor(16.0F * (1.0F - f));
         int j = i + MathHelper.ceil(16.0F * f);
         this.fill(RenderPipelines.GUI, x, i, x + 16, j, Integer.MAX_VALUE);
      }

   }

   public void drawHoverEvent(TextRenderer textRenderer, @Nullable Style style, int x, int y) {
      if (style != null && style.getHoverEvent() != null) {
         HoverEvent var10000 = style.getHoverEvent();
         Objects.requireNonNull(var10000);
         HoverEvent var5 = var10000;
         byte var6 = 0;
         boolean var10001;
         Throwable var21;
         switch (var5.typeSwitch<invokedynamic>(var5, var6)) {
            case 0:
               HoverEvent.ShowItem var7 = (HoverEvent.ShowItem)var5;
               HoverEvent.ShowItem var25 = var7;

               ItemStack var26;
               try {
                  var26 = var25.item();
               } catch (Throwable var16) {
                  var21 = var16;
                  var10001 = false;
                  break;
               }

               ItemStack var18 = var26;
               this.drawItemTooltip(textRenderer, var18, x, y);
               return;
            case 1:
               HoverEvent.ShowEntity var9 = (HoverEvent.ShowEntity)var5;
               HoverEvent.ShowEntity var23 = var9;

               HoverEvent.EntityContent var24;
               try {
                  var24 = var23.entity();
               } catch (Throwable var15) {
                  var21 = var15;
                  var10001 = false;
                  break;
               }

               HoverEvent.EntityContent var19 = var24;
               if (this.client.options.advancedItemTooltips) {
                  this.drawTooltip(textRenderer, var19.asTooltip(), x, y);
               }

               return;
            case 2:
               HoverEvent.ShowText var11 = (HoverEvent.ShowText)var5;
               HoverEvent.ShowText var20 = var11;

               Text var22;
               try {
                  var22 = var20.value();
               } catch (Throwable var14) {
                  var21 = var14;
                  var10001 = false;
                  break;
               }

               Text var13 = var22;
               this.drawOrderedTooltip(textRenderer, textRenderer.wrapLines(var13, Math.max(this.getScaledWindowWidth() / 2, 200)), x, y);
               return;
            default:
               return;
         }

         Throwable var17 = var21;
         throw new MatchException(var17.toString(), var17);
      }
   }

   public void drawMap(MapRenderState mapState) {
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      TextureManager textureManager = minecraftClient.getTextureManager();
      GpuTextureView gpuTextureView = textureManager.getTexture(mapState.texture).getGlTextureView();
      this.drawTexturedQuad(RenderPipelines.GUI_TEXTURED, (GpuTextureView)gpuTextureView, 0, 0, 128, 128, 0.0F, 1.0F, 0.0F, 1.0F, -1);
      Iterator var5 = mapState.decorations.iterator();

      while(var5.hasNext()) {
         MapRenderState.Decoration decoration = (MapRenderState.Decoration)var5.next();
         if (decoration.alwaysRendered) {
            this.matrices.pushMatrix();
            this.matrices.translate((float)decoration.x / 2.0F + 64.0F, (float)decoration.z / 2.0F + 64.0F);
            this.matrices.rotate(0.017453292F * (float)decoration.rotation * 360.0F / 16.0F);
            this.matrices.scale(4.0F, 4.0F);
            this.matrices.translate(-0.125F, 0.125F);
            Sprite sprite = decoration.sprite;
            if (sprite != null) {
               GpuTextureView gpuTextureView2 = textureManager.getTexture(sprite.getAtlasId()).getGlTextureView();
               this.drawTexturedQuad(RenderPipelines.GUI_TEXTURED, (GpuTextureView)gpuTextureView2, -1, -1, 1, 1, sprite.getMinU(), sprite.getMaxU(), sprite.getMaxV(), sprite.getMinV(), -1);
            }

            this.matrices.popMatrix();
            if (decoration.name != null) {
               TextRenderer textRenderer = minecraftClient.textRenderer;
               float f = (float)textRenderer.getWidth((StringVisitable)decoration.name);
               float var10000 = 25.0F / f;
               Objects.requireNonNull(textRenderer);
               float g = MathHelper.clamp(var10000, 0.0F, 6.0F / 9.0F);
               this.matrices.pushMatrix();
               this.matrices.translate((float)decoration.x / 2.0F + 64.0F - f * g / 2.0F, (float)decoration.z / 2.0F + 64.0F + 4.0F);
               this.matrices.scale(g, g);
               this.state.addText(new TextGuiElementRenderState(textRenderer, decoration.name.asOrderedText(), new Matrix3x2f(this.matrices), 0, 0, -1, Integer.MIN_VALUE, false, this.scissorStack.peekLast()));
               this.matrices.popMatrix();
            }
         }
      }

   }

   public void addEntity(EntityRenderState entityState, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle, int x1, int y1, int x2, int y2) {
      this.state.addSpecialElement(new EntityGuiElementRenderState(entityState, translation, rotation, overrideCameraAngle, x1, y1, x2, y2, scale, this.scissorStack.peekLast()));
   }

   public void addPlayerSkin(PlayerEntityModel playerModel, Identifier texture, float scale, float xRotation, float yRotation, float yPivot, int x1, int y1, int x2, int y2) {
      this.state.addSpecialElement(new PlayerSkinGuiElementRenderState(playerModel, texture, xRotation, yRotation, yPivot, x1, y1, x2, y2, scale, this.scissorStack.peekLast()));
   }

   public void addBookModel(BookModel bookModel, Identifier texture, float scale, float open, float flip, int x1, int y1, int x2, int y2) {
      this.state.addSpecialElement(new BookModelGuiElementRenderState(bookModel, texture, open, flip, x1, y1, x2, y2, scale, this.scissorStack.peekLast()));
   }

   public void addBannerResult(ModelPart flag, DyeColor baseColor, BannerPatternsComponent resultBannerPatterns, int x1, int y1, int x2, int y2) {
      this.state.addSpecialElement(new BannerResultGuiElementRenderState(flag, baseColor, resultBannerPatterns, x1, y1, x2, y2, this.scissorStack.peekLast()));
   }

   public void addSign(Model signModel, float scale, WoodType woodType, int x1, int y1, int x2, int y2) {
      this.state.addSpecialElement(new SignGuiElementRenderState(signModel, woodType, x1, y1, x2, y2, scale, this.scissorStack.peekLast()));
   }

   public void addProfilerChart(List chartData, int x1, int y1, int x2, int y2) {
      this.state.addSpecialElement(new ProfilerChartGuiElementRenderState(chartData, x1, y1, x2, y2, this.scissorStack.peekLast()));
   }

   @Environment(EnvType.CLIENT)
   public static class ScissorStack {
      private final Deque stack = new ArrayDeque();

      ScissorStack() {
      }

      public ScreenRect push(ScreenRect rect) {
         ScreenRect screenRect = (ScreenRect)this.stack.peekLast();
         if (screenRect != null) {
            ScreenRect screenRect2 = (ScreenRect)Objects.requireNonNullElse(rect.intersection(screenRect), ScreenRect.empty());
            this.stack.addLast(screenRect2);
            return screenRect2;
         } else {
            this.stack.addLast(rect);
            return rect;
         }
      }

      @Nullable
      public ScreenRect pop() {
         if (this.stack.isEmpty()) {
            throw new IllegalStateException("Scissor stack underflow");
         } else {
            this.stack.removeLast();
            return (ScreenRect)this.stack.peekLast();
         }
      }

      @Nullable
      public ScreenRect peekLast() {
         return (ScreenRect)this.stack.peekLast();
      }

      public boolean contains(int x, int y) {
         return this.stack.isEmpty() ? true : ((ScreenRect)this.stack.peek()).contains(x, y);
      }
   }
}
