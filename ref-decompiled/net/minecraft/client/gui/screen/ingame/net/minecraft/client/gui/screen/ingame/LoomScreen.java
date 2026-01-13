/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.model.BannerFlagBlockModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class LoomScreen
extends HandledScreen<LoomScreenHandler> {
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
    private static final float field_59943 = 64.0f;
    private static final float field_59944 = 21.0f;
    private static final float field_59945 = 40.0f;
    private BannerFlagBlockModel bannerField;
    private @Nullable BannerPatternsComponent bannerPatterns;
    private ItemStack banner = ItemStack.EMPTY;
    private ItemStack dye = ItemStack.EMPTY;
    private ItemStack pattern = ItemStack.EMPTY;
    private boolean canApplyDyePattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int visibleTopRow;

    public LoomScreen(LoomScreenHandler screenHandler, PlayerInventory inventory, Text title) {
        super(screenHandler, inventory, title);
        screenHandler.setInventoryChangeListener(this::onInventoryChanged);
        this.titleY -= 2;
    }

    @Override
    protected void init() {
        super.init();
        ModelPart modelPart = this.client.getLoadedEntityModels().getModelPart(EntityModelLayers.STANDING_BANNER_FLAG);
        this.bannerField = new BannerFlagBlockModel(modelPart);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private int getRows() {
        return MathHelper.ceilDiv(((LoomScreenHandler)this.handler).getBannerPatterns().size(), 4);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int n;
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
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
        int k = (int)(41.0f * this.scrollPosition);
        Identifier identifier = this.canApplyDyePattern ? SCROLLER_TEXTURE : SCROLLER_DISABLED_TEXTURE;
        int l = i + 119;
        int m = j + 13 + k;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, l, m, 12, 15);
        if (mouseX >= l && mouseX < l + 12 && mouseY >= m && mouseY < m + 15) {
            context.setCursor(this.scrollbarClicked ? StandardCursors.RESIZE_NS : StandardCursors.POINTING_HAND);
        }
        if (this.bannerPatterns != null && !this.hasTooManyPatterns) {
            DyeColor dyeColor = ((BannerItem)slot4.getStack().getItem()).getColor();
            n = i + 141;
            int o = j + 8;
            context.addBannerResult(this.bannerField, dyeColor, this.bannerPatterns, n, o, n + 20, o + 40);
        } else if (this.hasTooManyPatterns) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, i + slot4.x - 5, j + slot4.y - 5, 26, 26);
        }
        if (this.canApplyDyePattern) {
            int p = i + 60;
            n = j + 13;
            List<RegistryEntry<BannerPattern>> list = ((LoomScreenHandler)this.handler).getBannerPatterns();
            block0: for (int q = 0; q < 4; ++q) {
                for (int r = 0; r < 4; ++r) {
                    Identifier identifier2;
                    boolean bl;
                    int s = q + this.visibleTopRow;
                    int t = s * 4 + r;
                    if (t >= list.size()) break block0;
                    int u = p + r * 14;
                    int v = n + q * 14;
                    RegistryEntry<BannerPattern> registryEntry = list.get(t);
                    boolean bl2 = bl = mouseX >= u && mouseY >= v && mouseX < u + 14 && mouseY < v + 14;
                    if (t == ((LoomScreenHandler)this.handler).getSelectedPattern()) {
                        identifier2 = PATTERN_SELECTED_TEXTURE;
                    } else if (bl) {
                        identifier2 = PATTERN_HIGHLIGHTED_TEXTURE;
                        DyeColor dyeColor2 = ((DyeItem)this.dye.getItem()).getColor();
                        context.drawTooltip(Text.translatable(registryEntry.value().translationKey() + "." + dyeColor2.getId()), mouseX, mouseY);
                        context.setCursor(StandardCursors.POINTING_HAND);
                    } else {
                        identifier2 = PATTERN_TEXTURE;
                    }
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier2, u, v, 14, 14);
                    Sprite sprite = context.getSprite(TexturedRenderLayers.getBannerPatternTextureId(registryEntry));
                    this.drawBanner(context, u, v, sprite);
                }
            }
        }
        MinecraftClient.getInstance().gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
    }

    private void drawBanner(DrawContext context, int x, int y, Sprite sprite) {
        context.getMatrices().pushMatrix();
        context.getMatrices().translate((float)(x + 4), (float)(y + 2));
        float f = sprite.getMinU();
        float g = f + (sprite.getMaxU() - sprite.getMinU()) * 21.0f / 64.0f;
        float h = sprite.getMaxV() - sprite.getMinV();
        float i = sprite.getMinV() + h / 64.0f;
        float j = i + h * 40.0f / 64.0f;
        int k = 5;
        int l = 10;
        context.fill(0, 0, 5, 10, DyeColor.GRAY.getEntityColor());
        context.drawTexturedQuad(sprite.getAtlasId(), 0, 0, 5, 10, f, g, i, j);
        context.getMatrices().popMatrix();
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.canApplyDyePattern) {
            int i = this.x + 60;
            int j = this.y + 13;
            for (int k = 0; k < 4; ++k) {
                for (int l = 0; l < 4; ++l) {
                    double d = click.x() - (double)(i + l * 14);
                    double e = click.y() - (double)(j + k * 14);
                    int m = k + this.visibleTopRow;
                    int n = m * 4 + l;
                    if (!(d >= 0.0) || !(e >= 0.0) || !(d < 14.0) || !(e < 14.0) || !((LoomScreenHandler)this.handler).onButtonClick(this.client.player, n)) continue;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.ui(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                    this.client.interactionManager.clickButton(((LoomScreenHandler)this.handler).syncId, n);
                    return true;
                }
            }
            i = this.x + 119;
            j = this.y + 9;
            if (click.x() >= (double)i && click.x() < (double)(i + 12) && click.y() >= (double)j && click.y() < (double)(j + 56)) {
                this.scrollbarClicked = true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        int i = this.getRows() - 4;
        if (this.scrollbarClicked && this.canApplyDyePattern && i > 0) {
            int j = this.y + 13;
            int k = j + 56;
            this.scrollPosition = ((float)click.y() - (float)j - 7.5f) / ((float)(k - j) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.visibleTopRow = Math.max((int)((double)(this.scrollPosition * (float)i) + 0.5), 0);
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        this.scrollbarClicked = false;
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }
        int i = this.getRows() - 4;
        if (this.canApplyDyePattern && i > 0) {
            float f = (float)verticalAmount / (float)i;
            this.scrollPosition = MathHelper.clamp(this.scrollPosition - f, 0.0f, 1.0f);
            this.visibleTopRow = Math.max((int)(this.scrollPosition * (float)i + 0.5f), 0);
        }
        return true;
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top) {
        return mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
    }

    private void onInventoryChanged() {
        ItemStack itemStack = ((LoomScreenHandler)this.handler).getOutputSlot().getStack();
        this.bannerPatterns = itemStack.isEmpty() ? null : itemStack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        ItemStack itemStack2 = ((LoomScreenHandler)this.handler).getBannerSlot().getStack();
        ItemStack itemStack3 = ((LoomScreenHandler)this.handler).getDyeSlot().getStack();
        ItemStack itemStack4 = ((LoomScreenHandler)this.handler).getPatternSlot().getStack();
        BannerPatternsComponent bannerPatternsComponent = itemStack2.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT);
        boolean bl = this.hasTooManyPatterns = bannerPatternsComponent.layers().size() >= 6;
        if (this.hasTooManyPatterns) {
            this.bannerPatterns = null;
        }
        if (!(ItemStack.areEqual(itemStack2, this.banner) && ItemStack.areEqual(itemStack3, this.dye) && ItemStack.areEqual(itemStack4, this.pattern))) {
            boolean bl2 = this.canApplyDyePattern = !itemStack2.isEmpty() && !itemStack3.isEmpty() && !this.hasTooManyPatterns && !((LoomScreenHandler)this.handler).getBannerPatterns().isEmpty();
        }
        if (this.visibleTopRow >= this.getRows()) {
            this.visibleTopRow = 0;
            this.scrollPosition = 0.0f;
        }
        this.banner = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }
}
