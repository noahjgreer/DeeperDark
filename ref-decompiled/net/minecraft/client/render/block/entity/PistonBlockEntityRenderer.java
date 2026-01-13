/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.PistonBlock
 *  net.minecraft.block.PistonHeadBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.PistonBlockEntity
 *  net.minecraft.block.enums.PistonType
 *  net.minecraft.client.render.block.MovingBlockRenderState
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.PistonBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.PistonBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.PistonBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PistonBlockEntityRenderer
implements BlockEntityRenderer<PistonBlockEntity, PistonBlockEntityRenderState> {
    public PistonBlockEntityRenderState createRenderState() {
        return new PistonBlockEntityRenderState();
    }

    public void updateRenderState(PistonBlockEntity pistonBlockEntity, PistonBlockEntityRenderState pistonBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)pistonBlockEntity, (BlockEntityRenderState)pistonBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        pistonBlockEntityRenderState.offsetX = pistonBlockEntity.getRenderOffsetX(f);
        pistonBlockEntityRenderState.offsetY = pistonBlockEntity.getRenderOffsetY(f);
        pistonBlockEntityRenderState.offsetZ = pistonBlockEntity.getRenderOffsetZ(f);
        pistonBlockEntityRenderState.pushedState = null;
        pistonBlockEntityRenderState.extendedPistonState = null;
        BlockState blockState = pistonBlockEntity.getPushedBlock();
        World world = pistonBlockEntity.getWorld();
        if (world != null && !blockState.isAir()) {
            BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.getMovementDirection().getOpposite());
            RegistryEntry registryEntry = world.getBiome(blockPos);
            if (blockState.isOf(Blocks.PISTON_HEAD) && pistonBlockEntity.getProgress(f) <= 4.0f) {
                blockState = (BlockState)blockState.with((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(pistonBlockEntity.getProgress(f) <= 0.5f));
                pistonBlockEntityRenderState.pushedState = PistonBlockEntityRenderer.renderModel((BlockPos)blockPos, (BlockState)blockState, (RegistryEntry)registryEntry, (World)world);
            } else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
                PistonType pistonType = blockState.isOf(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT;
                BlockState blockState2 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with((Property)PistonHeadBlock.TYPE, (Comparable)pistonType)).with((Property)PistonHeadBlock.FACING, (Comparable)((Direction)blockState.get((Property)PistonBlock.FACING)));
                blockState2 = (BlockState)blockState2.with((Property)PistonHeadBlock.SHORT, (Comparable)Boolean.valueOf(pistonBlockEntity.getProgress(f) >= 0.5f));
                pistonBlockEntityRenderState.pushedState = PistonBlockEntityRenderer.renderModel((BlockPos)blockPos, (BlockState)blockState2, (RegistryEntry)registryEntry, (World)world);
                BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.getMovementDirection());
                blockState = (BlockState)blockState.with((Property)PistonBlock.EXTENDED, (Comparable)Boolean.valueOf(true));
                pistonBlockEntityRenderState.extendedPistonState = PistonBlockEntityRenderer.renderModel((BlockPos)blockPos2, (BlockState)blockState, (RegistryEntry)registryEntry, (World)world);
            } else {
                pistonBlockEntityRenderState.pushedState = PistonBlockEntityRenderer.renderModel((BlockPos)blockPos, (BlockState)blockState, (RegistryEntry)registryEntry, (World)world);
            }
        }
    }

    public void render(PistonBlockEntityRenderState pistonBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        if (pistonBlockEntityRenderState.pushedState == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(pistonBlockEntityRenderState.offsetX, pistonBlockEntityRenderState.offsetY, pistonBlockEntityRenderState.offsetZ);
        orderedRenderCommandQueue.submitMovingBlock(matrixStack, pistonBlockEntityRenderState.pushedState);
        matrixStack.pop();
        if (pistonBlockEntityRenderState.extendedPistonState != null) {
            orderedRenderCommandQueue.submitMovingBlock(matrixStack, pistonBlockEntityRenderState.extendedPistonState);
        }
    }

    private static MovingBlockRenderState renderModel(BlockPos pos, BlockState state, RegistryEntry<Biome> biome, World world) {
        MovingBlockRenderState movingBlockRenderState = new MovingBlockRenderState();
        movingBlockRenderState.fallingBlockPos = pos;
        movingBlockRenderState.entityBlockPos = pos;
        movingBlockRenderState.blockState = state;
        movingBlockRenderState.biome = biome;
        movingBlockRenderState.world = world;
        return movingBlockRenderState;
    }

    public int getRenderDistance() {
        return 68;
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

