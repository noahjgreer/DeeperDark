/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.EyeblossomBlock
 *  net.minecraft.block.EyeblossomBlock$EyeblossomState
 *  net.minecraft.block.FlowerBlock
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityCollisionHandler
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.passive.BeeEntity
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.TriState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.World
 *  net.minecraft.world.attribute.EnvironmentAttributes
 *  net.minecraft.world.event.GameEvent
 *  net.minecraft.world.event.GameEvent$Emitter
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EyeblossomBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TriState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.event.GameEvent;

/*
 * Exception performing whole class analysis ignored.
 */
public class EyeblossomBlock
extends FlowerBlock {
    public static final MapCodec<EyeblossomBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.fieldOf("open").forGetter(block -> block.state.open), (App)EyeblossomBlock.createSettingsCodec()).apply((Applicative)instance, EyeblossomBlock::new));
    private static final int NOTIFY_RANGE_XZ = 3;
    private static final int NOTIFY_RANGE_Y = 2;
    private final EyeblossomState state;

    public MapCodec<? extends EyeblossomBlock> getCodec() {
        return CODEC;
    }

    public EyeblossomBlock(EyeblossomState state, AbstractBlock.Settings settings) {
        super(state.stewEffect, state.effectLengthInSeconds, settings);
        this.state = state;
    }

    public EyeblossomBlock(boolean open, AbstractBlock.Settings settings) {
        super(EyeblossomState.of((boolean)open).stewEffect, EyeblossomState.of((boolean)open).effectLengthInSeconds, settings);
        this.state = EyeblossomState.of((boolean)open);
    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockState blockState;
        if (this.state.isOpen() && random.nextInt(700) == 0 && (blockState = world.getBlockState(pos.down())).isOf(Blocks.PALE_MOSS_BLOCK)) {
            world.playSoundClient((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_EYEBLOSSOM_IDLE, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.updateStateAndNotifyOthers(state, world, pos, random)) {
            world.playSound(null, pos, this.state.getOpposite().longSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.randomTick(state, world, pos, random);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.updateStateAndNotifyOthers(state, world, pos, random)) {
            world.playSound(null, pos, this.state.getOpposite().sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.scheduledTick(state, world, pos, random);
    }

    private boolean updateStateAndNotifyOthers(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean bl = ((TriState)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.EYEBLOSSOM_OPEN_GAMEPLAY, pos)).asBoolean(this.state.open);
        if (bl == this.state.open) {
            return false;
        }
        EyeblossomState eyeblossomState = this.state.getOpposite();
        world.setBlockState(pos, eyeblossomState.getBlockState(), 3);
        world.emitGameEvent((RegistryEntry)GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of((BlockState)state));
        eyeblossomState.spawnTrailParticle(world, pos, random);
        BlockPos.iterate((BlockPos)pos.add(-3, -2, -3), (BlockPos)pos.add(3, 2, 3)).forEach(otherPos -> {
            BlockState blockState2 = world.getBlockState(otherPos);
            if (blockState2 == state) {
                double d = Math.sqrt(pos.getSquaredDistance((Vec3i)otherPos));
                int i = random.nextBetween((int)(d * 5.0), (int)(d * 10.0));
                world.scheduleBlockTick(otherPos, state.getBlock(), i);
            }
        });
        return true;
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl) {
        if (!world.isClient() && world.getDifficulty() != Difficulty.PEACEFUL && entity instanceof BeeEntity) {
            BeeEntity beeEntity = (BeeEntity)entity;
            if (BeeEntity.isAttractive((BlockState)state) && !beeEntity.hasStatusEffect(StatusEffects.POISON)) {
                beeEntity.addStatusEffect(this.getContactEffect());
            }
        }
    }

    public StatusEffectInstance getContactEffect() {
        return new StatusEffectInstance(StatusEffects.POISON, 25);
    }
}

