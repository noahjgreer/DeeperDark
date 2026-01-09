package net.minecraft.client.gui.screen.ingame;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class SmithingScreen extends ForgingScreen {
   private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/smithing/error");
   private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE = Identifier.ofVanilla("container/slot/smithing_template_armor_trim");
   private static final Identifier EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE = Identifier.ofVanilla("container/slot/smithing_template_netherite_upgrade");
   private static final Text MISSING_TEMPLATE_TOOLTIP = Text.translatable("container.upgrade.missing_template_tooltip");
   private static final Text ERROR_TOOLTIP = Text.translatable("container.upgrade.error_tooltip");
   private static final List EMPTY_SLOT_TEXTURES;
   private static final int field_42057 = 44;
   private static final int field_42058 = 15;
   private static final int field_42059 = 28;
   private static final int field_42060 = 21;
   private static final int field_42061 = 65;
   private static final int field_42062 = 46;
   private static final int field_42063 = 115;
   private static final int field_42068 = 210;
   private static final int field_42047 = 25;
   private static final Vector3f ARMOR_STAND_TRANSLATION;
   private static final Quaternionf ARMOR_STAND_ROTATION;
   private static final int field_42049 = 25;
   private static final int field_59946 = 121;
   private static final int field_59947 = 20;
   private static final int field_59948 = 161;
   private static final int field_59949 = 80;
   private final CyclingSlotIcon templateSlotIcon = new CyclingSlotIcon(0);
   private final CyclingSlotIcon baseSlotIcon = new CyclingSlotIcon(1);
   private final CyclingSlotIcon additionsSlotIcon = new CyclingSlotIcon(2);
   @Nullable
   private ArmorStandEntity armorStand;

   public SmithingScreen(SmithingScreenHandler handler, PlayerInventory playerInventory, Text title) {
      super(handler, playerInventory, title, Identifier.ofVanilla("textures/gui/container/smithing.png"));
      this.titleX = 44;
      this.titleY = 15;
   }

   protected void setup() {
      this.armorStand = new ArmorStandEntity(this.client.world, 0.0, 0.0, 0.0);
      this.armorStand.setHideBasePlate(true);
      this.armorStand.setShowArms(true);
      this.armorStand.bodyYaw = 210.0F;
      this.armorStand.setPitch(25.0F);
      this.armorStand.headYaw = this.armorStand.getYaw();
      this.armorStand.lastHeadYaw = this.armorStand.getYaw();
      this.equipArmorStand(((SmithingScreenHandler)this.handler).getSlot(3).getStack());
   }

   public void handledScreenTick() {
      super.handledScreenTick();
      Optional optional = this.getSmithingTemplate();
      this.templateSlotIcon.updateTexture(EMPTY_SLOT_TEXTURES);
      this.baseSlotIcon.updateTexture((List)optional.map(SmithingTemplateItem::getEmptyBaseSlotTextures).orElse(List.of()));
      this.additionsSlotIcon.updateTexture((List)optional.map(SmithingTemplateItem::getEmptyAdditionsSlotTextures).orElse(List.of()));
   }

   private Optional getSmithingTemplate() {
      ItemStack itemStack = ((SmithingScreenHandler)this.handler).getSlot(0).getStack();
      if (!itemStack.isEmpty()) {
         Item var3 = itemStack.getItem();
         if (var3 instanceof SmithingTemplateItem) {
            SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)var3;
            return Optional.of(smithingTemplateItem);
         }
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
      InventoryScreen.drawEntity(context, i, j, k, l, 25.0F, ARMOR_STAND_TRANSLATION, ARMOR_STAND_ROTATION, (Quaternionf)null, this.armorStand);
   }

   public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
      if (slotId == 3) {
         this.equipArmorStand(stack);
      }

   }

   private void equipArmorStand(ItemStack stack) {
      if (this.armorStand != null) {
         Iterator var2 = EquipmentSlot.VALUES.iterator();

         EquipmentSlot equipmentSlot;
         while(var2.hasNext()) {
            equipmentSlot = (EquipmentSlot)var2.next();
            this.armorStand.equipStack(equipmentSlot, ItemStack.EMPTY);
         }

         if (!stack.isEmpty()) {
            EquippableComponent equippableComponent = (EquippableComponent)stack.get(DataComponentTypes.EQUIPPABLE);
            equipmentSlot = equippableComponent != null ? equippableComponent.slot() : EquipmentSlot.OFFHAND;
            this.armorStand.equipStack(equipmentSlot, stack.copy());
         }

      }
   }

   protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
      if (this.hasInvalidRecipe()) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, x + 65, y + 46, 28, 21);
      }

   }

   private void renderSlotTooltip(DrawContext context, int mouseX, int mouseY) {
      Optional optional = Optional.empty();
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
            Item var8 = itemStack.getItem();
            if (var8 instanceof SmithingTemplateItem) {
               SmithingTemplateItem smithingTemplateItem = (SmithingTemplateItem)var8;
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

      optional.ifPresent((text) -> {
         context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(text, 115), mouseX, mouseY);
      });
   }

   private boolean hasInvalidRecipe() {
      return ((SmithingScreenHandler)this.handler).hasInvalidRecipe();
   }

   static {
      EMPTY_SLOT_TEXTURES = List.of(EMPTY_SLOT_SMITHING_TEMPLATE_ARMOR_TRIM_TEXTURE, EMPTY_SLOT_SMITHING_TEMPLATE_NETHERITE_UPGRADE_TEXTURE);
      ARMOR_STAND_TRANSLATION = new Vector3f(0.0F, 1.0F, 0.0F);
      ARMOR_STAND_ROTATION = (new Quaternionf()).rotationXYZ(0.43633232F, 0.0F, 3.1415927F);
   }
}
