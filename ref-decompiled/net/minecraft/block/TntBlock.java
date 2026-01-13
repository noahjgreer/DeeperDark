/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.TntBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.TntEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Properties
 *  net.minecraft.state.property.Property
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.block.WireOrientation
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.rule.GameRules
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.rule.GameRules;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class TntBlock
extends Block {
    public static final MapCodec<TntBlock> CODEC = TntBlock.createCodec(TntBlock::new);
    public static final BooleanProperty UNSTABLE = Properties.UNSTABLE;

    public MapCodec<TntBlock> getCodec() {
        return CODEC;
    }

    public TntBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with((Property)UNSTABLE, (Comparable)Boolean.valueOf(false)));
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock())) {
            return;
        }
        if (world.isReceivingRedstonePower(pos) && TntBlock.primeTnt((World)world, (BlockPos)pos)) {
            world.removeBlock(pos, false);
        }
    }

    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isReceivingRedstonePower(pos) && TntBlock.primeTnt((World)world, (BlockPos)pos)) {
            world.removeBlock(pos, false);
        }
    }

    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.getAbilities().creativeMode && ((Boolean)state.get((Property)UNSTABLE)).booleanValue()) {
            TntBlock.primeTnt((World)world, (BlockPos)pos);
        }
        return super.onBreak(world, pos, state, player);
    }

    public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
        if (!((Boolean)world.getGameRules().getValue(GameRules.TNT_EXPLODES)).booleanValue()) {
            return;
        }
        TntEntity tntEntity = new TntEntity((World)world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, explosion.getCausingEntity());
        int i = tntEntity.getFuse();
        tntEntity.setFuse((int)((short)(world.random.nextInt(i / 4) + i / 8)));
        world.spawnEntity((Entity)tntEntity);
    }

    public static boolean primeTnt(World world, BlockPos pos) {
        return TntBlock.primeTnt((World)world, (BlockPos)pos, null);
    }

    private static boolean primeTnt(World world, BlockPos pos, @Nullable LivingEntity igniter) {
        ServerWorld serverWorld;
        if (!(world instanceof ServerWorld) || !((Boolean)(serverWorld = (ServerWorld)world).getGameRules().getValue(GameRules.TNT_EXPLODES)).booleanValue()) {
            return false;
        }
        TntEntity tntEntity = new TntEntity(world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, igniter);
        world.spawnEntity((Entity)tntEntity);
        world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        world.emitGameEvent((Entity)igniter, (RegistryEntry)GameEvent.PRIME_FUSE, pos);
        return true;
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ServerWorld serverWorld;
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }
        if (TntBlock.primeTnt((World)world, (BlockPos)pos, (LivingEntity)player)) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
            Item item = stack.getItem();
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
                stack.damage(1, (LivingEntity)player, hand.getEquipmentSlot());
            } else {
                stack.decrementUnlessCreative(1, (LivingEntity)player);
            }
            player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
        } else if (world instanceof ServerWorld && !((Boolean)(serverWorld = (ServerWorld)world).getGameRules().getValue(GameRules.TNT_EXPLODES)).booleanValue()) {
            player.sendMessage((Text)Text.translatable((String)"block.minecraft.tnt.disabled"), true);
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            BlockPos blockPos = hit.getBlockPos();
            Entity entity = projectile.getOwner();
            if (projectile.isOnFire() && projectile.canModifyAt(serverWorld, blockPos) && TntBlock.primeTnt((World)world, (BlockPos)blockPos, (LivingEntity)(entity instanceof LivingEntity ? (LivingEntity)entity : null))) {
                world.removeBlock(blockPos, false);
            }
        }
    }

    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{UNSTABLE});
    }
}

