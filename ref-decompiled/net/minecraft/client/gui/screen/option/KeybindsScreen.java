package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class KeybindsScreen extends GameOptionsScreen {
   private static final Text TITLE_TEXT = Text.translatable("controls.keybinds.title");
   @Nullable
   public KeyBinding selectedKeyBinding;
   public long lastKeyCodeUpdateTime;
   private ControlsListWidget controlsList;
   private ButtonWidget resetAllButton;

   public KeybindsScreen(Screen parent, GameOptions gameOptions) {
      super(parent, gameOptions, TITLE_TEXT);
   }

   protected void initBody() {
      this.controlsList = (ControlsListWidget)this.layout.addBody(new ControlsListWidget(this, this.client));
   }

   protected void addOptions() {
   }

   protected void initFooter() {
      this.resetAllButton = ButtonWidget.builder(Text.translatable("controls.resetAll"), (button) -> {
         KeyBinding[] var2 = this.gameOptions.allKeys;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            KeyBinding keyBinding = var2[var4];
            keyBinding.setBoundKey(keyBinding.getDefaultKey());
         }

         this.controlsList.update();
      }).build();
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
      directionalLayoutWidget.add(this.resetAllButton);
      directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.close();
      }).build());
   }

   protected void refreshWidgetPositions() {
      this.layout.refreshPositions();
      this.controlsList.position(this.width, this.layout);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.selectedKeyBinding != null) {
         this.selectedKeyBinding.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
         this.selectedKeyBinding = null;
         this.controlsList.update();
         return true;
      } else {
         return super.mouseClicked(mouseX, mouseY, button);
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.selectedKeyBinding != null) {
         if (keyCode == 256) {
            this.selectedKeyBinding.setBoundKey(InputUtil.UNKNOWN_KEY);
         } else {
            this.selectedKeyBinding.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
         }

         this.selectedKeyBinding = null;
         this.lastKeyCodeUpdateTime = Util.getMeasuringTimeMs();
         this.controlsList.update();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      boolean bl = false;
      KeyBinding[] var6 = this.gameOptions.allKeys;
      int var7 = var6.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         KeyBinding keyBinding = var6[var8];
         if (!keyBinding.isDefault()) {
            bl = true;
            break;
         }
      }

      this.resetAllButton.active = bl;
   }
}
