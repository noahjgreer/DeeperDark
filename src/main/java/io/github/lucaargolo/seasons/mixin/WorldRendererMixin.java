package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.utils.ColorsCache;
//? if >=26.3 {
/*import net.minecraft.client.renderer.extract.LevelExtractor;
*///?} else {
import net.minecraft.client.renderer.LevelRenderer;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >=26.3 {
/*@Mixin(LevelExtractor.class)
*///?} else {
@Mixin(LevelRenderer.class)
//?}
public class WorldRendererMixin {

    @Inject(at = @At("HEAD"), method = "allChanged()V")
    public void allChanged(CallbackInfo info) {
        ColorsCache.clearCache();
    }
}
