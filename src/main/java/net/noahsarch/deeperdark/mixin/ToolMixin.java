package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tool.class)
public class ToolMixin {
    private static final TagKey<Block> NOT_MINEABLE_WITH_PICKAXE =
            TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "not_mineable_with_pickaxe"));

    // Vanilla's mineable/pickaxe holds all of #walls, and tags can't exclude entries, so wooden walls skip that rule here.
    @Inject(method = "getMiningSpeed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$skipPickaxeRule(BlockState state, CallbackInfoReturnable<Float> cir) {
        if (!state.is(NOT_MINEABLE_WITH_PICKAXE)) return;
        Tool tool = (Tool) (Object) this;
        for (Tool.Rule rule : tool.rules()) {
            boolean pickaxeRule = rule.blocks().unwrapKey().filter(BlockTags.MINEABLE_WITH_PICKAXE::equals).isPresent();
            if (!pickaxeRule && rule.speed().isPresent() && state.is(rule.blocks())) {
                cir.setReturnValue(rule.speed().get());
                return;
            }
        }
        cir.setReturnValue(tool.defaultMiningSpeed());
    }
}
