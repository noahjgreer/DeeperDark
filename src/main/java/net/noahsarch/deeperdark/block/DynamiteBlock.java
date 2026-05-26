package net.noahsarch.deeperdark.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.noahsarch.deeperdark.entity.PrimedDynamite;
import org.jspecify.annotations.Nullable;

public class DynamiteBlock extends Block {

    public static final MapCodec<DynamiteBlock> CODEC = simpleCodec(DynamiteBlock::new);
    public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

    @Override
    public MapCodec<DynamiteBlock> codec() {
        return CODEC;
    }

    public DynamiteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, false));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            if (level.hasNeighborSignal(pos) && prime(level, pos)) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.hasNeighborSignal(pos) && prime(level, pos)) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && !player.getAbilities().instabuild && state.getValue(UNSTABLE)) {
            prime(level, pos);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void onExplosionHit(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> onHit) {
        if (!state.isAir() && explosion.getBlockInteraction() != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            if (level.getGameRules().get(GameRules.TNT_EXPLODES)) {
                PrimedDynamite dynamite = new PrimedDynamite(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, explosion.getIndirectSourceEntity());
                int fuse = dynamite.getFuse();
                dynamite.setFuse((short) (level.getRandom().nextInt(fuse / 4) + fuse / 8));
                level.addFreshEntity(dynamite);
            }
        }
    }

    @Override
    public boolean dropFromExplosion(Explosion explosion) {
        return false;
    }

    public static boolean prime(Level level, BlockPos pos) {
        return prime(level, pos, null);
    }

    private static boolean prime(Level level, BlockPos pos, @Nullable LivingEntity source) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getGameRules().get(GameRules.TNT_EXPLODES)) {
            PrimedDynamite dynamite = new PrimedDynamite(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, source);
            level.addFreshEntity(dynamite);
            // Pitch 0.0F for the primed sound, as requested
            level.playSound(null, dynamite.getX(), dynamite.getY(), dynamite.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 0.0F);
            level.gameEvent(source, GameEvent.PRIME_FUSE, pos);
            return true;
        }
        return false;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!itemStack.is(Items.FLINT_AND_STEEL) && !itemStack.is(Items.FIRE_CHARGE)) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        } else {
            if (prime(level, pos, player)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
                Item item = itemStack.getItem();
                if (itemStack.is(Items.FLINT_AND_STEEL)) {
                    itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
                } else {
                    itemStack.consume(1, player);
                }
                player.awardStat(Stats.ITEM_USED.get(item));
            } else if (level instanceof ServerLevel serverLevel && !serverLevel.getGameRules().get(GameRules.TNT_EXPLODES)) {
                player.sendOverlayMessage(Component.translatable("block.minecraft.tnt.disabled"));
                return InteractionResult.PASS;
            }
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult blockHit, Projectile projectile) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = blockHit.getBlockPos();
            Entity owner = projectile.getOwner();
            if (projectile.isOnFire() && projectile.mayInteract(serverLevel, pos) && prime(level, pos, owner instanceof LivingEntity livingOwner ? livingOwner : null)) {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UNSTABLE);
    }
}
