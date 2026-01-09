package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChangeGameModeC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

@Environment(EnvType.CLIENT)
public class GameModeSwitcherScreen extends Screen {
   static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("gamemode_switcher/slot");
   static final Identifier SELECTION_TEXTURE = Identifier.ofVanilla("gamemode_switcher/selection");
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/gamemode_switcher.png");
   private static final int TEXTURE_WIDTH = 128;
   private static final int TEXTURE_HEIGHT = 128;
   private static final int BUTTON_SIZE = 26;
   private static final int ICON_OFFSET = 5;
   private static final int field_32314 = 31;
   private static final int field_32315 = 5;
   private static final int UI_WIDTH = GameModeSwitcherScreen.GameModeSelection.values().length * 31 - 5;
   private static final Text SELECT_NEXT_TEXT;
   private final GameModeSelection currentGameMode = GameModeSwitcherScreen.GameModeSelection.of(this.getPreviousGameMode());
   private GameModeSelection gameMode;
   private int lastMouseX;
   private int lastMouseY;
   private boolean mouseUsedForSelection;
   private final List gameModeButtons = Lists.newArrayList();

   public GameModeSwitcherScreen() {
      super(NarratorManager.EMPTY);
      this.gameMode = this.currentGameMode;
   }

   private GameMode getPreviousGameMode() {
      ClientPlayerInteractionManager clientPlayerInteractionManager = MinecraftClient.getInstance().interactionManager;
      GameMode gameMode = clientPlayerInteractionManager.getPreviousGameMode();
      if (gameMode != null) {
         return gameMode;
      } else {
         return clientPlayerInteractionManager.getCurrentGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE;
      }
   }

   protected void init() {
      super.init();
      this.gameMode = this.currentGameMode;

      for(int i = 0; i < GameModeSwitcherScreen.GameModeSelection.VALUES.length; ++i) {
         GameModeSelection gameModeSelection = GameModeSwitcherScreen.GameModeSelection.VALUES[i];
         this.gameModeButtons.add(new ButtonWidget(gameModeSelection, this.width / 2 - UI_WIDTH / 2 + i * 31, this.height / 2 - 31));
      }

   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (!this.checkForClose()) {
         context.drawCenteredTextWithShadow(this.textRenderer, (Text)this.gameMode.text, this.width / 2, this.height / 2 - 31 - 20, -1);
         context.drawCenteredTextWithShadow(this.textRenderer, (Text)SELECT_NEXT_TEXT, this.width / 2, this.height / 2 + 5, -1);
         if (!this.mouseUsedForSelection) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseUsedForSelection = true;
         }

         boolean bl = this.lastMouseX == mouseX && this.lastMouseY == mouseY;
         Iterator var6 = this.gameModeButtons.iterator();

         while(var6.hasNext()) {
            ButtonWidget buttonWidget = (ButtonWidget)var6.next();
            buttonWidget.render(context, mouseX, mouseY, deltaTicks);
            buttonWidget.setSelected(this.gameMode == buttonWidget.gameMode);
            if (!bl && buttonWidget.isSelected()) {
               this.gameMode = buttonWidget.gameMode;
            }
         }

      }
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      int i = this.width / 2 - 62;
      int j = this.height / 2 - 31 - 27;
      context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, 125, 75, 128, 128);
   }

   private void apply() {
      apply(this.client, this.gameMode);
   }

   private static void apply(MinecraftClient client, GameModeSelection gameModeSelection) {
      if (client.interactionManager != null && client.player != null) {
         GameModeSelection gameModeSelection2 = GameModeSwitcherScreen.GameModeSelection.of(client.interactionManager.getCurrentGameMode());
         if (client.player.hasPermissionLevel(2) && gameModeSelection != gameModeSelection2) {
            client.player.networkHandler.sendPacket(new ChangeGameModeC2SPacket(gameModeSelection.gameMode));
         }

      }
   }

   private boolean checkForClose() {
      if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), 292)) {
         this.apply();
         this.client.setScreen((Screen)null);
         return true;
      } else {
         return false;
      }
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 293) {
         this.mouseUsedForSelection = false;
         this.gameMode = this.gameMode.next();
         return true;
      } else {
         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean shouldPause() {
      return false;
   }

   static {
      SELECT_NEXT_TEXT = Text.translatable("debug.gamemodes.select_next", Text.translatable("debug.gamemodes.press_f4").formatted(Formatting.AQUA));
   }

   @Environment(EnvType.CLIENT)
   private static enum GameModeSelection {
      CREATIVE(Text.translatable("gameMode.creative"), GameMode.CREATIVE, new ItemStack(Blocks.GRASS_BLOCK)),
      SURVIVAL(Text.translatable("gameMode.survival"), GameMode.SURVIVAL, new ItemStack(Items.IRON_SWORD)),
      ADVENTURE(Text.translatable("gameMode.adventure"), GameMode.ADVENTURE, new ItemStack(Items.MAP)),
      SPECTATOR(Text.translatable("gameMode.spectator"), GameMode.SPECTATOR, new ItemStack(Items.ENDER_EYE));

      static final GameModeSelection[] VALUES = values();
      private static final int field_32317 = 16;
      private static final int field_32316 = 5;
      final Text text;
      final GameMode gameMode;
      private final ItemStack icon;

      private GameModeSelection(final Text text, final GameMode gameMode, final ItemStack icon) {
         this.text = text;
         this.gameMode = gameMode;
         this.icon = icon;
      }

      void renderIcon(DrawContext context, int x, int y) {
         context.drawItem(this.icon, x, y);
      }

      GameModeSelection next() {
         GameModeSelection var10000;
         switch (this.ordinal()) {
            case 0:
               var10000 = SURVIVAL;
               break;
            case 1:
               var10000 = ADVENTURE;
               break;
            case 2:
               var10000 = SPECTATOR;
               break;
            case 3:
               var10000 = CREATIVE;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      static GameModeSelection of(GameMode gameMode) {
         GameModeSelection var10000;
         switch (gameMode) {
            case SPECTATOR:
               var10000 = SPECTATOR;
               break;
            case SURVIVAL:
               var10000 = SURVIVAL;
               break;
            case CREATIVE:
               var10000 = CREATIVE;
               break;
            case ADVENTURE:
               var10000 = ADVENTURE;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static GameModeSelection[] method_36886() {
         return new GameModeSelection[]{CREATIVE, SURVIVAL, ADVENTURE, SPECTATOR};
      }
   }

   @Environment(EnvType.CLIENT)
   public static class ButtonWidget extends ClickableWidget {
      final GameModeSelection gameMode;
      private boolean selected;

      public ButtonWidget(GameModeSelection gameMode, int x, int y) {
         super(x, y, 26, 26, gameMode.text);
         this.gameMode = gameMode;
      }

      public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         this.drawBackground(context);
         if (this.selected) {
            this.drawSelectionBox(context);
         }

         this.gameMode.renderIcon(context, this.getX() + 5, this.getY() + 5);
      }

      public void appendClickableNarrations(NarrationMessageBuilder builder) {
         this.appendDefaultNarrations(builder);
      }

      public boolean isSelected() {
         return super.isSelected() || this.selected;
      }

      public void setSelected(boolean selected) {
         this.selected = selected;
      }

      private void drawBackground(DrawContext context) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, GameModeSwitcherScreen.SLOT_TEXTURE, this.getX(), this.getY(), 26, 26);
      }

      private void drawSelectionBox(DrawContext context) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, GameModeSwitcherScreen.SELECTION_TEXTURE, this.getX(), this.getY(), 26, 26);
      }
   }
}
