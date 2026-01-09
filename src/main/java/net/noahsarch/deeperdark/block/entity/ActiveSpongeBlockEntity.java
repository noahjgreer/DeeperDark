package net.noahsarch.deeperdark.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.noahsarch.deeperdark.util.ActiveSpongeTracker;

public class ActiveSpongeBlockEntity extends BlockEntity {
    private boolean active = false;

    public ActiveSpongeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ACTIVE_SPONGE, pos, state);
    }

    public void setActive(boolean active) {
        this.active = active;
        this.markDirty();
    }


    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putBoolean("Active", this.active);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        // Default might need checking if key exists, but boolean defaults to false usually
        boolean oldActive = this.active;
        this.active = view.getBoolean("Active", false);
        if (this.hasWorld() && oldActive != this.active) {
            updateTracker(this.active);
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    private void updateTracker(boolean adding) {
        if (this.world instanceof ActiveSpongeTracker) {
             ActiveSpongeTracker tracker = (ActiveSpongeTracker) this.world;
             if (adding) {
                 tracker.addActiveSponge(this.pos);
             } else {
                 tracker.removeActiveSponge(this.pos);
             }
        }
    }

    @Override
    public void markRemoved() {
        if (this.active) {
            updateTracker(false);
        }
        super.markRemoved();
    }

    // We need to register when the BE is first added to the world or loaded
    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (this.active && world != null) {
            updateTracker(true);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, ActiveSpongeBlockEntity blockEntity) {
        if (state == null) return; // Suppress unused parameter warning
        if (!world.isClient && blockEntity.active) {
            int radius = 3;
            // Iterate using a new mutable BlockPos to avoid object creation spam if possible,
            // but iterate returns an iterable that reuses or creates pos. BlockPos.iterate typically works well.
            for (BlockPos currentPos : BlockPos.iterate(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius))) {
                if (currentPos.equals(pos)) continue;

                FluidState fluidState = world.getFluidState(currentPos);
                if (fluidState.isIn(FluidTags.WATER)) {
                     BlockState blockState = world.getBlockState(currentPos);
                     if (blockState.getBlock() instanceof FluidBlock) {
                         world.setBlockState(currentPos, Blocks.AIR.getDefaultState(), 3);
                     } else if (blockState.isOf(Blocks.KELP) || blockState.isOf(Blocks.KELP_PLANT) || blockState.isOf(Blocks.SEAGRASS) || blockState.isOf(Blocks.TALL_SEAGRASS)) {
                          world.breakBlock(currentPos, true);
                     } else if (blockState.getFluidState().isStill() || !blockState.getFluidState().isEmpty()) {
                          if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED)) {
                               world.setBlockState(currentPos, blockState.with(Properties.WATERLOGGED, false), 3);
                          }
                     }
                }
            }
        }
    }
}
