package net.noahsarch.deeperdark.client.mixin;

import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.VillagerProfession;
import net.noahsarch.deeperdark.villager.ModVillagers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(VillagerClothingFeatureRenderer.class)
public class VillagerClothingFeatureRendererMixin {
    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/LivingEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;matchesKey(Lnet/minecraft/registry/RegistryKey;)Z", ordinal = 0))
    private boolean deeperdark$disableProfessionLayer(RegistryEntry<VillagerProfession> entry, net.minecraft.registry.RegistryKey<VillagerProfession> key) {
        if (entry.matchesKey(ModVillagers.POTION_MASTER_KEY)) {
            return true; // Treat Potion Master as "NONE" (matchesKey(NONE) is the check being redirected)
        }
        return entry.matchesKey(key);
    }
}

