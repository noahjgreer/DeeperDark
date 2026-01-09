package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class LoomScreen extends HandledScreen {
   private static final Identifier BANNER_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/banner");
   private static final Identifier DYE_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/dye");
   private static final Identifier PATTERN_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/banner_pattern");
   private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("container/loom/scroller");
   private static final Identifier SCROLLER_DISABLED_TEXTURE = Identifier.ofVanilla("container/loom/scroller_disabled");
   private static final Identifier PATTERN_SELECTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_selected");
   private static final Identifier PATTERN_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("container/loom/pattern_highlighted");
   private static final Identifier PATTERN_TEXTURE = Identifier.ofVanilla("container/loom/pattern");
   private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/loom/error");
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/loom.png");
   private static final int PATTERN_LIST_COLUMNS = 4;
   private static final int PATTERN_LIST_ROWS = 4;
   private static final int SCROLLBAR_WIDTH = 12;
   private static final int SCROLLBAR_HEIGHT = 15;
   private static final int PATTERN_ENTRY_SIZE = 14;
   private static final int SCROLLBAR_AREA_HEIGHT = 56;
   private static final int PATTERN_LIST_OFFSET_X = 60;
   private static final int PATTERN_LIST_OFFSET_Y = 13;
   private static final float field_59943 = 64.0F;
   private static final float field_59944 = 21.0F;
   private static final float field_59945 = 40.0F;
   private ModelPart bannerField;
   @Nullable
   private BannerPatternsComponent bannerPatterns;
   private ItemStack banner;
   private ItemStack dye;
   private ItemStack pattern;
   private boolean canApplyDyePattern;
   private boolean hasTooManyPatterns;
   private float scrollPosition;
   private boolean scrollbarClicked;
   private int visibleTopRow;

   public LoomScreen(LoomScreenHandler screenHandler, PlayerInventory inventory, Text title) {
      super(screenHandler, inventory, title);
      this.banner = ItemStack.EMPTY;
      this.dye = ItemStack.EMPTY;
      this.pattern = ItemStack.EMPTY;
      screenHandler.setInventoryChangeListener(this::onInventoryChanged);
      this.titleY -= 2;
   }

   protected void init() {
      super.init();
      this.bannerField = this.client.getLoadedEntityModels().getModelPart(EntityModelLayers.STANDING_BANNER_FLAG).getChild("flag");
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      this.drawMouseoverTooltip(context, mouseX, mouseY);
   }

   private int getRows() {
      return MathHelper.ceilDiv(((LoomScreenHandler)this.handler).getBannerPatterns().size(), 4);
   }

   protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
      int i = this.x;
      int j = this.y;
      context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      Slot slot = ((LoomScreenHandler)this.handler).getBannerSlot();
      Slot slot2 = ((LoomScreenHandler)this.handler).getDyeSlot();
      Slot slot3 = ((LoomScreenHandler)this.handler).getPatternSlot();
      Slot slot4 = ((LoomScreenHandler)this.handler).getOutputSlot();
      if (!slot.hasStack()) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BANNER_SLOT_TEXTURE, i + slot.x, j + slot.y, 16, 16);
      }

      if (!slot2.hasStack()) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, DYE_SLOT_TEXTURE, i + slot2.x, j + slot2.y, 16, 16);
      }

      if (!slot3.hasStack()) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PATTERN_SLOT_TEXTURE, i + slot3.x, j + slot3.y, 16, 16);
      }

      int k = (int)(41.0F * this.scrollPosition);
      Identifier identifier = this.canApplyDyePattern ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i + 119, j + 13 + k, 12, 15);
      int l;
      if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
         DyeColor dyeColor = ((BannerItem)slot4.getStack().getItem()).getColor();
         l = i + 141;
         int m = j + 8;
         context.addBannerResult(this.bannerField, dyeColor, this.bannerPatterns, l, m, l + 20, m + 40);
      } else if (this.hasTooManyPatterns) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + slot4.x - 5, j + slot4.y - 5, 26, 26);
      }

      if (this.canApplyDyePattern) {
         int n = i + 60;
         l = j + 13;
         List list = ((LoomScreenHandler)this.handler).getBannerPatterns();

         label63:
         for(int o = 0; o < 4; ++o) {
            for(int p = 0; p < 4; ++p) {
               int q = o + this.visibleTopRow;
               int r = q * 4 + p;
               if (r >= list.size()) {
                  break label63;
               }

               int s = n + p * 14;
               int t = l + o * 14;
               boolean bl = mouseX >= s && mouseY >= t && mouseX < s + 14 && mouseY < t + 14;
               Identifier identifier2;
               if (r == ((LoomScreenHandler)this.handler).getSelectedPattern()) {
                  identifier2 = PATTERN_SELECTED_TEXTURE;
               } else if (bl) {
                  identifier2 = PATTERN_HIGHLIGHTED_TEXTURE;
               } else {
                  identifier2 = PATTERN_TEXTURE;
               }

               context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, s, t, 14, 14);
               Sprite sprite = TexturedRenderLayers.getBannerPatternTextureId((RegistryEntry)list.get(r)).getSprite();
               this.drawBanner(context, s, t, sprite);
            }
         }
      }

      MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
   }

   private void drawBanner(DrawContext context, int x, int y, Sprite sprite) {
      context.getMatrices().pushMatrix();
      context.getMatrices().translate((float)(x + 4), (float)(y + 2));
      float f = sprite.getMinU();
      float g = f + (sprite.getMaxU() - sprite.getMinU()) * 21.0F / 64.0F;
      float h = sprite.getMaxV() - sprite.getMinV();
      float i = sprite.getMinV() + h / 64.0F;
      float j = i + h * 40.0F / 64.0F;
      int k = true;
      int l = true;
      context.fill(0, 0, 5, 10, DyeColor.GRAY.getEntityColor());
      context.drawTexturedQuad(sprite.getAtlasId(), 0, 0, 5, 10, f, g, i, j);
      context.getMatrices().popMatrix();
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.scrollbarClicked = false;
      if (this.canApplyDyePattern) {
         int i = this.x + 60;
         int j = this.y + 13;

         for(int k = 0; k < 4; ++k) {
            for(int l = 0; l < 4; ++l) {
               double d = mouseX - (double)(i + l * 14);
               double e = mouseY - (double)(j + k * 14);
               int m = k + this.visibleTopRow;
               int n = m * 4 + l;
               if (d >= 0.0 && e >= 0.0 && d < 14.0 && e < 14.0 && ((LoomScreenHandler)this.handler).onButtonClick(this.client.player, n)) {
                  MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
                  this.client.interactionManager.clickButton(((LoomScreenHandler)this.handler).syncId, n);
                  return true;
               }
            }
         }

         i = this.x + 119;
         j = this.y + 9;
         if (mouseX >= (double)i && mouseX < (double)(i + 12) && mouseY >= (double)j && mouseY < (double)(j + 56)) {
            this.scrollbarClicked = true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      int i = this.getRows() - 4;
      if (this.scrollbarClicked && this.canApplyDyePattern && i > 0) {
         int j = this.y + 13;
         int k = j + 56;
         this.scrollPosition = ((float)mouseY - (float)j - 7.5F) / ((float)(k - j) - 15.0F);
         this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0F, 1.0F);
         this.visibleTopRow = Math.max((int)((double)(this.scrollPosition * (float)i) + 0.5), 0);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
         return true;
      } else {
         int i = this.getRows() - 4;
         if (this.canApplyDyePattern && i > 0) {
            float f = (float)verticalAmount / (float)i;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0F, 1.0F);
            this.visibleTopRow = Math.max((int)(this.scrollPosition * (float)i + 0.5F), 0);
         }

         return true;
      }
   }

   protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
      return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
   }

   private void onInventoryChanged() {
      ItemStack itemStack = ((LoomScreenHandler)this.handler).getOutputSlot().getStack();
      if (itemStack.isEmpty()) {
         this.bannerPatterns = null;
      } else {
         this.bannerPatterns = (BannerPatternsComponent)itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
      }

      ItemStack itemStack2 = ((LoomScreenHandler)this.handler).getBannerSlot().getStack();
      ItemStack itemStack3 = ((LoomScreenHandler)this.handler).getDyeSlot().getStack();
      ItemStack itemStack4 = ((LoomScreenHandler)this.handler).getPatternSlot().getStack();
      BannerPatternsComponent bannerPatternsComponent = (BannerPatternsComponent)itemStack2.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
      this.hasTooManyPatterns = bannerPatternsComponent.layers().size() >= 6;
      if (this.hasTooManyPatterns) {
         this.bannerPatterns = null;
      }

      if (!ItemStack.areEqual(itemStack2, this.banner) || !ItemStack.areEqual(itemStack3, this.dye) || !ItemStack.areEqual(itemStack4, this.pattern)) {
         this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && !this.hasTooManyPatterns && !((LoomScreenHandler)this.handler).getBannerPatterns().isEmpty();
      }

      if (this.visibleTopRow >= this.getRows()) {
         this.visibleTopRow = 0;
         this.scrollPosition = 0.0F;
      }

      this.banner = itemStack2.copy();
      this.dye = itemStack3.copy();
      this.pattern = itemStack4.copy();
   }
}
