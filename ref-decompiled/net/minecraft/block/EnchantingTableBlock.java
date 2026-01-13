/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.EnchantingTableBlock
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.EnchantingTableBlockEntity
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.screen.EnchantmentScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public class EnchantingTableBlock
extends BlockWithEntity {
    public static final MapCodec<EnchantingTableBlock> CODEC = EnchantingTableBlock.createCodec(EnchantingTableBlock::new);
    public static final List<BlockPos> POWER_PROVIDER_OFFSETS = BlockPos.stream((int)-2, (int)0, (int)-2, (int)2, (int)1, (int)2).filter(pos -> Math.abs(pos.getX()) == 2 || Math.abs(pos.getZ()) == 2).map(BlockPos::toImmutable).toList();
    private static final VoxelShape SHAPE = Block.createColumnShape((double)16.0, (double)0.0, (double)12.0);

    public MapCodec<EnchantingTableBlock> getCodec() {
        return CODEC;
    }

    public EnchantingTableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public static boolean canAccessPowerProvider(World world, BlockPos tablePos, BlockPos providerOffset) {
        return world.getBlockState(tablePos.add((Vec3i)providerOffset)).isIn(BlockTags.ENCHANTMENT_POWER_PROVIDER) && world.getBlockState(tablePos.add(providerOffset.getX() / 2, providerOffset.getY(), providerOffset.getZ() / 2)).isIn(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);
        for (BlockPos blockPos : POWER_PROVIDER_OFFSETS) {
            if (random.nextInt(16) != 0 || !EnchantingTableBlock.canAccessPowerProvider((World)world, (BlockPos)pos, (BlockPos)blockPos)) continue;
            world.addParticleClient((ParticleEffect)ParticleTypes.ENCHANT, (double)pos.getX() + 0.5, (double)pos.getY() + 2.0, (double)pos.getZ() + 0.5, (double)((float)blockPos.getX() + random.nextFloat()) - 0.5, (double)((float)blockPos.getY() - random.nextFloat() - 1.0f), (double)((float)blockPos.getZ() + random.nextFloat()) - 0.5);
        }
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantingTableBlockEntity(pos, state);
    }

    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? EnchantingTableBlock.validateTicker(type, (BlockEntityType)BlockEntityType.ENCHANTING_TABLE, EnchantingTableBlockEntity::tick) : null;
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        }
        return ActionResult.SUCCESS;
    }

    protected @Nullable NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EnchantingTableBlockEntity) {
            EnchantingTableBlockEntity enchantingTableBlockEntity = (EnchantingTableBlockEntity)blockEntity;
            Text text = enchantingTableBlockEntity.getDisplayName();
            return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new EnchantmentScreenHandler(syncId, inventory, ScreenHandlerContext.create((World)world, (BlockPos)pos)), text);
        }
        return null;
    }

    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

