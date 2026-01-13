/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CarvedPumpkinBlock
 *  net.minecraft.block.PumpkinBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.loot.LootTables
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/*
 * Exception performing whole class analysis ignored.
 */
public class PumpkinBlock
extends Block {
    public static final MapCodec<PumpkinBlock> CODEC = PumpkinBlock.createCodec(PumpkinBlock::new);

    public MapCodec<PumpkinBlock> getCodec() {
        return CODEC;
    }

    public PumpkinBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected ActionResult onUseWithItem(ItemStack stack2, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!stack2.isOf(Items.SHEARS)) {
            return super.onUseWithItem(stack2, state, world, pos, player, hand, hit);
        }
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ServerWorld serverWorld = (ServerWorld)world;
        Direction direction = hit.getSide();
        Direction direction2 = direction.getAxis() == Direction.Axis.Y ? player.getHorizontalFacing().getOpposite() : direction;
        PumpkinBlock.generateBlockInteractLoot((ServerWorld)serverWorld, (RegistryKey)LootTables.PUMPKIN_CARVE, (BlockState)state, (BlockEntity)world.getBlockEntity(pos), (ItemStack)stack2, (Entity)player, (worldx, stack) -> {
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5 + (double)direction2.getOffsetX() * 0.65, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5 + (double)direction2.getOffsetZ() * 0.65, stack);
            itemEntity.setVelocity(0.05 * (double)direction2.getOffsetX() + world.random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction2.getOffsetZ() + world.random.nextDouble() * 0.02);
            world.spawnEntity((Entity)itemEntity);
        });
        world.playSound(null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.setBlockState(pos, (BlockState)Blocks.CARVED_PUMPKIN.getDefaultState().with((Property)CarvedPumpkinBlock.FACING, (Comparable)direction2), 11);
        stack2.damage(1, (LivingEntity)player, hand.getEquipmentSlot());
        world.emitGameEvent((Entity)player, (RegistryEntry)GameEvent.SHEAR, pos);
        player.incrementStat(Stats.USED.getOrCreateStat((Object)Items.SHEARS));
        return ActionResult.SUCCESS;
    }
}

