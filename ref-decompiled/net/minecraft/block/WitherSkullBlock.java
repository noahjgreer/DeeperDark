/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.advancement.criterion.Criteria
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.CarvedPumpkinBlock
 *  net.minecraft.block.SkullBlock
 *  net.minecraft.block.SkullBlock$SkullType
 *  net.minecraft.block.SkullBlock$Type
 *  net.minecraft.block.WitherSkullBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.SkullBlockEntity
 *  net.minecraft.block.pattern.BlockPattern
 *  net.minecraft.block.pattern.BlockPattern$Result
 *  net.minecraft.block.pattern.BlockPatternBuilder
 *  net.minecraft.block.pattern.CachedBlockPosition
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.entity.boss.WitherEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.predicate.block.BlockStatePredicate
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldView
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.function.Predicate;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class WitherSkullBlock
extends SkullBlock {
    public static final MapCodec<WitherSkullBlock> CODEC = WitherSkullBlock.createCodec(WitherSkullBlock::new);
    private static @Nullable BlockPattern witherBossPattern;
    private static @Nullable BlockPattern witherDispenserPattern;

    public MapCodec<WitherSkullBlock> getCodec() {
        return CODEC;
    }

    public WitherSkullBlock(AbstractBlock.Settings settings) {
        super((SkullBlock.SkullType)SkullBlock.Type.WITHER_SKELETON, settings);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        WitherSkullBlock.onPlaced((World)world, (BlockPos)pos);
    }

    public static void onPlaced(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SkullBlockEntity) {
            SkullBlockEntity skullBlockEntity = (SkullBlockEntity)blockEntity;
            WitherSkullBlock.onPlaced((World)world, (BlockPos)pos, (SkullBlockEntity)skullBlockEntity);
        }
    }

    public static void onPlaced(World world, BlockPos pos, SkullBlockEntity blockEntity) {
        boolean bl;
        if (world.isClient()) {
            return;
        }
        BlockState blockState = blockEntity.getCachedState();
        boolean bl2 = bl = blockState.isOf(Blocks.WITHER_SKELETON_SKULL) || blockState.isOf(Blocks.WITHER_SKELETON_WALL_SKULL);
        if (!bl || pos.getY() < world.getBottomY() || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        BlockPattern.Result result = WitherSkullBlock.getWitherBossPattern().searchAround((WorldView)world, pos);
        if (result == null) {
            return;
        }
        WitherEntity witherEntity = (WitherEntity)EntityType.WITHER.create(world, SpawnReason.TRIGGERED);
        if (witherEntity != null) {
            CarvedPumpkinBlock.breakPatternBlocks((World)world, (BlockPattern.Result)result);
            BlockPos blockPos = result.translate(1, 2, 0).getBlockPos();
            witherEntity.refreshPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.55, (double)blockPos.getZ() + 0.5, result.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f, 0.0f);
            witherEntity.bodyYaw = result.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f;
            witherEntity.onSummoned();
            for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(ServerPlayerEntity.class, witherEntity.getBoundingBox().expand(50.0))) {
                Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, (Entity)witherEntity);
            }
            world.spawnEntity((Entity)witherEntity);
            CarvedPumpkinBlock.updatePatternBlocks((World)world, (BlockPattern.Result)result);
        }
    }

    public static boolean canDispense(World world, BlockPos pos, ItemStack stack) {
        if (stack.isOf(Items.WITHER_SKELETON_SKULL) && pos.getY() >= world.getBottomY() + 2 && world.getDifficulty() != Difficulty.PEACEFUL && !world.isClient()) {
            return WitherSkullBlock.getWitherDispenserPattern().searchAround((WorldView)world, pos) != null;
        }
        return false;
    }

    private static BlockPattern getWitherBossPattern() {
        if (witherBossPattern == null) {
            witherBossPattern = BlockPatternBuilder.start().aisle(new String[]{"^^^", "###", "~#~"}).where('#', pos -> pos.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).where('^', CachedBlockPosition.matchesBlockState((Predicate)BlockStatePredicate.forBlock((Block)Blocks.WITHER_SKELETON_SKULL).or((Predicate)BlockStatePredicate.forBlock((Block)Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', pos -> pos.getBlockState().isAir()).build();
        }
        return witherBossPattern;
    }

    private static BlockPattern getWitherDispenserPattern() {
        if (witherDispenserPattern == null) {
            witherDispenserPattern = BlockPatternBuilder.start().aisle(new String[]{"   ", "###", "~#~"}).where('#', pos -> pos.getBlockState().isIn(BlockTags.WITHER_SUMMON_BASE_BLOCKS)).where('~', pos -> pos.getBlockState().isAir()).build();
        }
        return witherDispenserPattern;
    }
}

