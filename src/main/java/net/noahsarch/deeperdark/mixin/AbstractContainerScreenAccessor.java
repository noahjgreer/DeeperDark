package net.noahsarch.deeperdark.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("imageWidth")
    int deeperdark$getImageWidth();

    @Accessor("imageWidth")
    void deeperdark$setImageWidth(int imageWidth);
}
