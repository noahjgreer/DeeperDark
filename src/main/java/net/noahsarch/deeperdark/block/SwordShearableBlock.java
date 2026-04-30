package net.noahsarch.deeperdark.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SwordShearableBlock extends Block {

    public SwordShearableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        float hardness = state.getDestroySpeed(world, pos);
        if (hardness == -1.0F) return 0.0F;
        var held = player.getMainHandItem();
        if (held.is(ItemTags.SWORDS) || held.is(Items.SHEARS)) {
            return 15.0F / hardness / 30.0F;
        }
        return super.getDestroyProgress(state, player, world, pos);
    }
}
