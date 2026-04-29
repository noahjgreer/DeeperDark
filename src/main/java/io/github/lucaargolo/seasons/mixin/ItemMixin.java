package io.github.lucaargolo.seasons.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import io.github.lucaargolo.seasons.utils.Season;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
@Mixin(Item.class)
public class ItemMixin {

    @Inject(at = @At("TAIL"), method = "appendHoverText")
    public void seasons$appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                        Consumer<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if (!FabricSeasons.CONFIG.isSeasonMessingCrops() && !FabricSeasons.CONFIG.isSeasonMessingBonemeal()) return;

        Block cropBlock = FabricSeasons.SEEDS_MAP.get(stack.getItem());
        if (cropBlock == null) return;

        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        KeyMapping keyShift = client.options.keyShift;
        boolean sneaking = InputConstants.isKeyDown(
                client.getWindow(),
                keyShift.key.getValue());

        if (!sneaking) {
            tooltip.accept(Component.translatable("tooltip.seasons.hold_shift",
                    keyShift.getTranslatedKeyMessage()).withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        Season season = FabricSeasons.getCurrentSeason(client.level);
        Identifier cropId = BuiltInRegistries.BLOCK.getKey(cropBlock);
        float multiplier = CropConfigs.getSeasonCropMultiplier(cropId, season);

        tooltip.accept(Component.translatable("tooltip.seasons.season",
                Component.translatable(season.getTranslationKey()).withStyle(season.getFormatting()))
                .withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.seasons.crop_multiplier",
                String.format("%.1f", multiplier)).withStyle(ChatFormatting.GRAY));
    }
}
