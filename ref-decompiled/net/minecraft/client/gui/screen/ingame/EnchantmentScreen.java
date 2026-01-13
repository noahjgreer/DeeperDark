/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.cursor.StandardCursors
 *  net.minecraft.client.gui.screen.ingame.EnchantingPhrases
 *  net.minecraft.client.gui.screen.ingame.EnchantmentScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.render.entity.model.BookModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.screen.EnchantmentScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.gui.screen.ingame;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class EnchantmentScreen
extends HandledScreen<EnchantmentScreenHandler> {
    private static final Identifier[] LEVEL_TEXTURES = new Identifier[]{Identifier.ofVanilla((String)"container/enchanting_table/level_1"), Identifier.ofVanilla((String)"container/enchanting_table/level_2"), Identifier.ofVanilla((String)"container/enchanting_table/level_3")};
    private static final Identifier[] LEVEL_DISABLED_TEXTURES = new Identifier[]{Identifier.ofVanilla((String)"container/enchanting_table/level_1_disabled"), Identifier.ofVanilla((String)"container/enchanting_table/level_2_disabled"), Identifier.ofVanilla((String)"container/enchanting_table/level_3_disabled")};
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_TEXTURE = Identifier.ofVanilla((String)"container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla((String)"container/enchanting_table/enchantment_slot_highlighted");
    private static final Identifier ENCHANTMENT_SLOT_TEXTURE = Identifier.ofVanilla((String)"container/enchanting_table/enchantment_slot");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/enchanting_table.png");
    private static final Identifier BOOK_TEXTURE = Identifier.ofVanilla((String)"textures/entity/enchanting_table_book.png");
    private final Random random = Random.create();
    private BookModel BOOK_MODEL;
    public float nextPageAngle;
    public float pageAngle;
    public float approximatePageAngle;
    public float pageRotationSpeed;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack stack = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentScreenHandler handler, PlayerInventory inventory, Text title) {
        super((ScreenHandler)handler, inventory, title);
    }

    protected void init() {
        super.init();
        this.BOOK_MODEL = new BookModel(this.client.getLoadedEntityModels().getModelPart(EntityModelLayers.BOOK));
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        this.client.player.experienceBarDisplayStartTime = this.client.player.age;
        this.doTick();
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        for (int k = 0; k < 3; ++k) {
            double d = click.x() - (double)(i + 60);
            double e = click.y() - (double)(j + 14 + 19 * k);
            if (!(d >= 0.0) || !(e >= 0.0) || !(d < 108.0) || !(e < 19.0) || !((EnchantmentScreenHandler)this.handler).onButtonClick((PlayerEntity)this.client.player, k)) continue;
            this.client.interactionManager.clickButton(((EnchantmentScreenHandler)this.handler).syncId, k);
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        this.drawBook(context, i, j);
        EnchantingPhrases.getInstance().setSeed((long)((EnchantmentScreenHandler)this.handler).getSeed());
        int k = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int l = 0; l < 3; ++l) {
            int m = i + 60;
            int n = m + 20;
            int o = ((EnchantmentScreenHandler)this.handler).enchantmentPower[l];
            if (o == 0) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                continue;
            }
            String string = "" + o;
            int p = 86 - this.textRenderer.getWidth(string);
            StringVisitable stringVisitable = EnchantingPhrases.getInstance().generatePhrase(this.textRenderer, p);
            int q = -9937334;
            if (!(k >= l + 1 && this.client.player.experienceLevel >= o || this.client.player.isInCreativeMode())) {
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LEVEL_DISABLED_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                context.drawWrappedText(this.textRenderer, stringVisitable, n, j + 16 + 19 * l, p, ColorHelper.fullAlpha((int)((q & 0xFEFEFE) >> 1)), false);
                q = -12550384;
            } else {
                int r = mouseX - (i + 60);
                int s = mouseY - (j + 14 + 19 * l);
                if (r >= 0 && s >= 0 && r < 108 && s < 19) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                    context.setCursor(StandardCursors.POINTING_HAND);
                    q = -128;
                } else {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_TEXTURE, m, j + 14 + 19 * l, 108, 19);
                }
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LEVEL_TEXTURES[l], m + 1, j + 15 + 19 * l, 16, 16);
                context.drawWrappedText(this.textRenderer, stringVisitable, n, j + 16 + 19 * l, p, q, false);
                q = -8323296;
            }
            context.drawTextWithShadow(this.textRenderer, string, n + 86 - this.textRenderer.getWidth(string), j + 16 + 19 * l + 7, q);
        }
    }

    private void drawBook(DrawContext context, int x, int y) {
        float f = this.client.getRenderTickCounter().getTickProgress(false);
        float g = MathHelper.lerp((float)f, (float)this.pageTurningSpeed, (float)this.nextPageTurningSpeed);
        float h = MathHelper.lerp((float)f, (float)this.pageAngle, (float)this.nextPageAngle);
        int i = x + 14;
        int j = y + 14;
        int k = i + 38;
        int l = j + 31;
        context.addBookModel(this.BOOK_MODEL, BOOK_TEXTURE, 40.0f, g, h, i, j, k, l);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        float f = this.client.getRenderTickCounter().getTickProgress(false);
        super.render(context, mouseX, mouseY, f);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        boolean bl = this.client.player.isInCreativeMode();
        int i = ((EnchantmentScreenHandler)this.handler).getLapisCount();
        for (int j = 0; j < 3; ++j) {
            int k = ((EnchantmentScreenHandler)this.handler).enchantmentPower[j];
            Optional optional = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(((EnchantmentScreenHandler)this.handler).enchantmentId[j]);
            if (optional.isEmpty()) continue;
            int l = ((EnchantmentScreenHandler)this.handler).enchantmentLevel[j];
            int m = j + 1;
            if (!this.isPointWithinBounds(60, 14 + 19 * j, 108, 17, (double)mouseX, (double)mouseY) || k <= 0 || l < 0) continue;
            ArrayList list = Lists.newArrayList();
            list.add(Text.translatable((String)"container.enchant.clue", (Object[])new Object[]{Enchantment.getName((RegistryEntry)((RegistryEntry)optional.get()), (int)l)}).formatted(Formatting.WHITE));
            if (!bl) {
                list.add(ScreenTexts.EMPTY);
                if (this.client.player.experienceLevel < k) {
                    list.add(Text.translatable((String)"container.enchant.level.requirement", (Object[])new Object[]{((EnchantmentScreenHandler)this.handler).enchantmentPower[j]}).formatted(Formatting.RED));
                } else {
                    MutableText mutableText = m == 1 ? Text.translatable((String)"container.enchant.lapis.one") : Text.translatable((String)"container.enchant.lapis.many", (Object[])new Object[]{m});
                    list.add(mutableText.formatted(i >= m ? Formatting.GRAY : Formatting.RED));
                    MutableText mutableText2 = m == 1 ? Text.translatable((String)"container.enchant.level.one") : Text.translatable((String)"container.enchant.level.many", (Object[])new Object[]{m});
                    list.add(mutableText2.formatted(Formatting.GRAY));
                }
            }
            context.drawTooltip(this.textRenderer, (List)list, mouseX, mouseY);
            break;
        }
    }

    public void doTick() {
        ItemStack itemStack = ((EnchantmentScreenHandler)this.handler).getSlot(0).getStack();
        if (!ItemStack.areEqual((ItemStack)itemStack, (ItemStack)this.stack)) {
            this.stack = itemStack;
            do {
                this.approximatePageAngle += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.nextPageAngle <= this.approximatePageAngle + 1.0f && this.nextPageAngle >= this.approximatePageAngle - 1.0f);
        }
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean bl = false;
        for (int i = 0; i < 3; ++i) {
            if (((EnchantmentScreenHandler)this.handler).enchantmentPower[i] == 0) continue;
            bl = true;
            break;
        }
        this.nextPageTurningSpeed = bl ? (this.nextPageTurningSpeed += 0.2f) : (this.nextPageTurningSpeed -= 0.2f);
        this.nextPageTurningSpeed = MathHelper.clamp((float)this.nextPageTurningSpeed, (float)0.0f, (float)1.0f);
        float f = (this.approximatePageAngle - this.nextPageAngle) * 0.4f;
        float g = 0.2f;
        f = MathHelper.clamp((float)f, (float)-0.2f, (float)0.2f);
        this.pageRotationSpeed += (f - this.pageRotationSpeed) * 0.9f;
        this.nextPageAngle += this.pageRotationSpeed;
    }
}

