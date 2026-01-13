/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ingame.CyclingSlotIcon
 *  net.minecraft.client.gui.screen.ingame.ForgingScreen
 *  net.minecraft.client.gui.screen.ingame.SmithingScreen
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.state.ArmorStandEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.SmithingTemplateItem
 *  net.minecraft.screen.ForgingScreenHandler
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.SmithingScreenHandler
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screen.ingame;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CyclingSlotIcon;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class SmithingScreen
extends ForgingScreen<SmithingScreenHandler> {
    private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla((String)"container/smithing/error");
    private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE = Identifier.ofVanilla((String)"container/slot/smithing_template_armor_trim");
    private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE = Identifier.ofVanilla((String)"container/slot/smithing_template_netherite_upgrade");
    private static final Text MISSING_TEMPLATE_TOOLTIP = Text.translatable((String)"container.upgrade.missing_template_tooltip");
    private static final Text ERROR_TOOLTIP = Text.translatable((String)"container.upgrade.error_tooltip");
    private static final List<Identifier> EMPTY_SLOT_TEXTURES = List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE);
    private static final int field_42057 = 44;
    private static final int field_42058 = 15;
    private static final int field_42059 = 28;
    private static final int field_42060 = 21;
    private static final int field_42061 = 65;
    private static final int field_42062 = 46;
    private static final int field_42063 = 115;
    private static final int field_42068 = 210;
    private static final int field_42047 = 25;
    private static final Vector3f ARMOR_STAND_TRANSLATION = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Quaternionf ARMOR_STAND_ROTATION = new Quaternionf().rotationXYZ(0.43633232f, 0.0f, (float)Math.PI);
    private static final int field_42049 = 25;
    private static final int field_59946 = 121;
    private static final int field_59947 = 20;
    private static final int field_59948 = 161;
    private static final int field_59949 = 80;
    private final CyclingSlotIcon templateSlotIcon = new CyclingSlotIcon(0);
    private final CyclingSlotIcon baseSlotIcon = new CyclingSlotIcon(1);
    private final CyclingSlotIcon additionsSlotIcon = new CyclingSlotIcon(2);
    private final ArmorStandEntityRenderState armorStand = new ArmorStandEntityRenderState();

    public SmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super((ForgingScreenHandler)handler, playerInventory, title, Identifier.ofVanilla((String)"textures/gui/container/smithing.png"));
        this.titleX = 44;
        this.titleY = 15;
        this.armorStand.entityType = EntityType.ARMOR_STAND;
        this.armorStand.showBasePlate = false;
        this.armorStand.showArms = true;
        this.armorStand.pitch = 25.0f;
        this.armorStand.bodyYaw = 210.0f;
    }

    protected void setup() {
        this.equipArmorStand(((SmithingScreenHandler)this.handler).getSlot(3).getStack());
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        Optional optional = this.getSmithingTemplate();
        this.templateSlotIcon.updateTexture(EMPTY_SLOT_TEXTURES);
        this.baseSlotIcon.updateTexture(optional.map(SmithingTemplateItem::getEmptyBaseSlotTextures).orElse(List.of()));
        this.additionsSlotIcon.updateTexture(optional.map(SmithingTemplateItem::getEmptyAdditionsSlotTextures).orElse(List.of()));
    }

    private Optional<SmithingTemplateItem> getSmithingTemplate() {
        Item item;
        ItemStack itemStack = ((SmithingScreenHandler)this.handler).getSlot(0).getStack();
        if (!itemStack.isEmpty() && (item = itemStack.getItem()) instanceof SmithingTemplateItem) {
            SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)item;
            return Optional.of(smithingTemplateItem);
        }
        return Optional.empty();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.renderSlotTooltip(context, mouseX, mouseY);
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        super.drawBackground(context, deltaTicks, mouseX, mouseY);
        this.templateSlotIcon.render(this.handler, context, deltaTicks, this.x, this.y);
        this.baseSlotIcon.render(this.handler, context, deltaTicks, this.x, this.y);
        this.additionsSlotIcon.render(this.handler, context, deltaTicks, this.x, this.y);
        int i = this.x + 121;
        int j = this.y + 20;
        int k = this.x + 161;
        int l = this.y + 80;
        context.addEntity((EntityRenderState)this.armorStand, 25.0f, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ROTATION, null, i, j, k, l);
    }

    public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
        if (slotId == 3) {
            this.equipArmorStand(stack);
        }
    }

    private void equipArmorStand(ItemStack stack) {
        this.armorStand.leftHandItem = ItemStack.EMPTY;
        this.armorStand.leftHandItemState.clear();
        this.armorStand.equippedHeadStack = ItemStack.EMPTY;
        this.armorStand.headItemRenderState.clear();
        this.armorStand.equippedChestStack = ItemStack.EMPTY;
        this.armorStand.equippedLegsStack = ItemStack.EMPTY;
        this.armorStand.equippedFeetStack = ItemStack.EMPTY;
        if (!stack.isEmpty()) {
            EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
            EquipmentSlot equipmentSlot = equippableComponent != null ? equippableComponent.slot() : null;
            ItemModelManager itemModelManager = this.client.getItemModelManager();
            EquipmentSlot equipmentSlot2 = equipmentSlot;
            int n = 0;
            switch (SwitchBootstraps.enumSwitch("enumSwitch", new Object[]{"HEAD", "CHEST", "LEGS", "FEET"}, (EquipmentSlot)equipmentSlot2, n)) {
                case 0: {
                    if (ArmorFeatureRenderer.hasModel((ItemStack)stack, (EquipmentSlot)EquipmentSlot.HEAD)) {
                        this.armorStand.equippedHeadStack = stack.copy();
                        break;
                    }
                    itemModelManager.clearAndUpdate(this.armorStand.headItemRenderState, stack, ItemDisplayContext.HEAD, null, null, 0);
                    break;
                }
                case 1: {
                    this.armorStand.equippedChestStack = stack.copy();
                    break;
                }
                case 2: {
                    this.armorStand.equippedLegsStack = stack.copy();
                    break;
                }
                case 3: {
                    this.armorStand.equippedFeetStack = stack.copy();
                    break;
                }
                default: {
                    this.armorStand.leftHandItem = stack.copy();
                    itemModelManager.clearAndUpdate(this.armorStand.leftHandItemState, stack, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, 0);
                }
            }
        }
    }

    protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
        if (this.hasInvalidRecipe()) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, x + 65, y + 46, 28, 21);
        }
    }

    private void renderSlotTooltip(DrawContext context, int mouseX, int mouseY) {
        Optional<Text> optional = Optional.empty();
        if (this.hasInvalidRecipe() && this.isPointWithinBounds(65, 46, 28, 21, (double)mouseX, (double)mouseY)) {
            optional = Optional.of(ERROR_TOOLTIP);
        }
        if (this.focusedSlot != null) {
            ItemStack itemStack = ((SmithingScreenHandler)this.handler).getSlot(0).getStack();
            ItemStack itemStack2 = this.focusedSlot.getStack();
            if (itemStack.isEmpty()) {
                if (this.focusedSlot.id == 0) {
                    optional = Optional.of(MISSING_TEMPLATE_TOOLTIP);
                }
            } else {
                Item item = itemStack.getItem();
                if (item instanceof SmithingTemplateItem) {
                    SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)item;
                    if (itemStack2.isEmpty()) {
                        if (this.focusedSlot.id == 1) {
                            optional = Optional.of(smithingTemplateItem.getBaseSlotDescription());
                        } else if (this.focusedSlot.id == 2) {
                            optional = Optional.of(smithingTemplateItem.getAdditionsSlotDescription());
                        }
                    }
                }
            }
        }
        optional.ifPresent(text -> context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines((StringVisitable)text, 115), mouseX, mouseY));
    }

    private boolean hasInvalidRecipe() {
        return ((SmithingScreenHandler)this.handler).hasInvalidRecipe();
    }
}

