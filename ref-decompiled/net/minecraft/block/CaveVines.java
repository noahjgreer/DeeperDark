/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CaveVines
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.loot.LootTables
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 */
package net.minecraft.block;

import java.util.function.ToIntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public interface CaveVines {
    public static final VoxelShape SHAPE = Block.createColumnShape((double)14.0, (double)0.0, (double)16.0);
    public static final BooleanProperty BERRIES = Properties.BERRIES;

    public static ActionResult pickBerries(Entity picker, BlockState state, World world, BlockPos pos) {
        if (((Boolean)state.get((Property)BERRIES)).booleanValue()) {
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)world;
                Block.generateBlockInteractLoot((ServerWorld)serverWorld, (RegistryKey)LootTables.CAVE_VINE_HARVEST, (BlockState)state, (BlockEntity)world.getBlockEntity(pos), null, (Entity)picker, (worldx, stack) -> Block.dropStack((World)worldx, (BlockPos)pos, (ItemStack)stack));
                float f = MathHelper.nextBetween((Random)serverWorld.random, (float)0.8f, (float)1.2f);
                serverWorld.playSound(null, pos, SoundEvents.BLOCK_CAVE_VINES_PICK_BERRIES, SoundCategory.BLOCKS, 1.0f, f);
                BlockState blockState = (BlockState)state.with((Property)BERRIES, (Comparable)Boolean.valueOf(false));
                serverWorld.setBlockState(pos, blockState, 2);
                serverWorld.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((Entity)picker, (BlockState)blockState));
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static boolean hasBerries(BlockState state) {
        return state.contains((Property)BERRIES) && (Boolean)state.get((Property)BERRIES) != false;
    }

    public static ToIntFunction<BlockState> getLuminanceSupplier(int luminance) {
        return state -> (Boolean)state.get((Property)Properties.BERRIES) != false ? luminance : 0;
    }
}

