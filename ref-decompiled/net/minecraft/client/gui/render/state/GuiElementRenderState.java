package net.minecraft.client.gui.render.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface GuiElementRenderState {
   @Nullable
   ScreenRect bounds();
}
