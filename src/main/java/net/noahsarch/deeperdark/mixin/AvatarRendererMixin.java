package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.noahsarch.deeperdark.client.renderer.SaddlePlayerLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Shadow
    protected abstract boolean addLayer(@SuppressWarnings("rawtypes") RenderLayer layer);

    @SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("TAIL"))
    private void deeperdark$addSaddleLayer(EntityRendererProvider.Context context, boolean slimSteve, CallbackInfo ci) {
        LivingEntityRenderer<?, AvatarRenderState, PlayerModel> self =
                (LivingEntityRenderer<?, AvatarRenderState, PlayerModel>)(Object)this;
        this.addLayer(new SaddlePlayerLayer<>(self, context.getEquipmentRenderer()));
    }
}
