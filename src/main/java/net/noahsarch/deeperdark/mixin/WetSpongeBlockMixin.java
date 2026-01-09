package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.WetSpongeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.block.entity.ActiveSpongeBlockEntity;
import net.noahsarch.deeperdark.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WetSpongeBlock.class)
public abstract class WetSpongeBlockMixin implements BlockEntityProvider {

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ActiveSpongeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Return ticker only server side
        if (world.isClient) return null;
        return type == ModBlockEntities.ACTIVE_SPONGE ? (BlockEntityTicker<T>) (BlockEntityTicker<ActiveSpongeBlockEntity>) ActiveSpongeBlockEntity::tick : null;
    }
}

