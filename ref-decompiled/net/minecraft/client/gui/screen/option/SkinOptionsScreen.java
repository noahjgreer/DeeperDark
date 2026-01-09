package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SkinOptionsScreen extends GameOptionsScreen {
   private static final Text TITLE_TEXT = Text.translatable("options.skinCustomisation.title");

   public SkinOptionsScreen(Screen parent, GameOptions gameOptions) {
      super(parent, gameOptions, TITLE_TEXT);
   }

   protected void addOptions() {
      List list = new ArrayList();
      PlayerModelPart[] var2 = PlayerModelPart.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PlayerModelPart playerModelPart = var2[var4];
         list.add(CyclingButtonWidget.onOffBuilder(this.gameOptions.isPlayerModelPartEnabled(playerModelPart)).build(playerModelPart.getOptionName(), (button, enabled) -> {
            this.gameOptions.setPlayerModelPart(playerModelPart, enabled);
         }));
      }

      list.add(this.gameOptions.getMainArm().createWidget(this.gameOptions));
      this.body.addAll((List)list);
   }
}
