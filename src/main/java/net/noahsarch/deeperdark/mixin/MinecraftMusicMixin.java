package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.Music;
import net.noahsarch.deeperdark.autoupdate.AutoUpdaterScreen;
import net.noahsarch.deeperdark.intro.DeeperDarkLogoScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMusicMixin {

    @Inject(at = @At("RETURN"), method = "getSituationalMusic()Lnet/minecraft/sounds/Music;", cancellable = true)
    private void suppressIntroScreenMusic(CallbackInfoReturnable<Music> cir) {
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.screen instanceof DeeperDarkLogoScreen || mc.screen instanceof AutoUpdaterScreen) {
            cir.setReturnValue(null);
        }
    }
}
