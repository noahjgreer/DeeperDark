package net.minecraft.client.gui.screen.narration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum NarrationPart {
   TITLE,
   POSITION,
   HINT,
   USAGE;

   // $FF: synthetic method
   private static NarrationPart[] method_37030() {
      return new NarrationPart[]{TITLE, POSITION, HINT, USAGE};
   }
}
