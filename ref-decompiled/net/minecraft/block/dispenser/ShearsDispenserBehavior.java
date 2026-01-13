/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BeehiveBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.block.dispenser.FallibleItemDispenserBehavior
 *  net.minecraft.block.dispenser.ShearsDispenserBehavior
 *  net.minecraft.block.entity.BeehiveBlockEntity$BeeState
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Shearable
 *  net.minecraft.item.ItemStack
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 */
package net.minecraft.block.dispenser;

import java.util.List;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Shearable;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/*
 * Exception performing whole class analysis ignored.
 */
public class ShearsDispenserBehavior
extends FallibleItemDispenserBehavior {
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        ServerWorld serverWorld = pointer.world();
        if (!serverWorld.isClient()) {
            BlockPos blockPos = pointer.pos().offset((Direction)pointer.state().get((Property)DispenserBlock.FACING));
            this.setSuccess(ShearsDispenserBehavior.tryShearBlock((ServerWorld)serverWorld, (ItemStack)stack, (BlockPos)blockPos) || ShearsDispenserBehavior.tryShearEntity((ServerWorld)serverWorld, (BlockPos)blockPos, (ItemStack)stack));
            if (this.isSuccess()) {
                stack.damage(1, serverWorld, null, item -> {});
            }
        }
        return stack;
    }

    private static boolean tryShearBlock(ServerWorld world, ItemStack tool, BlockPos pos) {
        int i;
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(BlockTags.BEEHIVES, state -> state.contains((Property)BeehiveBlock.HONEY_LEVEL) && state.getBlock() instanceof BeehiveBlock) && (i = ((Integer)blockState.get((Property)BeehiveBlock.HONEY_LEVEL)).intValue()) >= 5) {
            world.playSound(null, pos, SoundEvents.BLOCK_BEEHIVE_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            BeehiveBlock.dropHoneycomb((ServerWorld)world, (ItemStack)tool, (BlockState)blockState, (BlockEntity)world.getBlockEntity(pos), null, (BlockPos)pos);
            ((BeehiveBlock)blockState.getBlock()).takeHoney((World)world, blockState, pos, null, BeehiveBlockEntity.BeeState.BEE_RELEASED);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.SHEAR, pos);
            return true;
        }
        return false;
    }

    private static boolean tryShearEntity(ServerWorld world, BlockPos pos, ItemStack shears) {
        List list = world.getEntitiesByClass(Entity.class, new Box(pos), EntityPredicates.EXCEPT_SPECTATOR);
        for (Entity entity : list) {
            Shearable shearable;
            if (entity.snipAllHeldLeashes(null)) {
                return true;
            }
            if (!(entity instanceof Shearable) || !(shearable = (Shearable)entity).isShearable()) continue;
            shearable.sheared(world, SoundCategory.BLOCKS, shears);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.SHEAR, pos);
            return true;
        }
        return false;
    }
}

