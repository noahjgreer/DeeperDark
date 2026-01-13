/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CopperGolemStatueBlock
 *  net.minecraft.block.Degradable
 *  net.minecraft.block.Oxidizable
 *  net.minecraft.block.Oxidizable$OxidationLevel
 *  net.minecraft.block.OxidizableCopperGolemStatueBlock
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.CopperGolemStatueBlockEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.CopperGolemEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CopperGolemStatueBlock;
import net.minecraft.block.Degradable;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CopperGolemStatueBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CopperGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/*
 * Exception performing whole class analysis ignored.
 */
public class OxidizableCopperGolemStatueBlock
extends CopperGolemStatueBlock
implements Oxidizable {
    public static final MapCodec<OxidizableCopperGolemStatueBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Oxidizable.OxidationLevel.CODEC.fieldOf("weathering_state").forGetter(Degradable::getDegradationLevel), (App)OxidizableCopperGolemStatueBlock.createSettingsCodec()).apply((Applicative)instance, OxidizableCopperGolemStatueBlock::new));

    public MapCodec<OxidizableCopperGolemStatueBlock> getCodec() {
        return CODEC;
    }

    public OxidizableCopperGolemStatueBlock(Oxidizable.OxidationLevel oxidationLevel, AbstractBlock.Settings settings) {
        super(oxidationLevel, settings);
    }

    protected boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock((Block)state.getBlock()).isPresent();
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    public Oxidizable.OxidationLevel getDegradationLevel() {
        return this.getOxidationLevel();
    }

    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CopperGolemStatueBlockEntity) {
            CopperGolemStatueBlockEntity copperGolemStatueBlockEntity = (CopperGolemStatueBlockEntity)blockEntity;
            if (stack.isIn(ItemTags.AXES)) {
                if (this.getDegradationLevel().equals((Object)Oxidizable.OxidationLevel.UNAFFECTED)) {
                    CopperGolemEntity copperGolemEntity = copperGolemStatueBlockEntity.createCopperGolem(state);
                    stack.damage(1, (LivingEntity)player, hand.getEquipmentSlot());
                    if (copperGolemEntity != null) {
                        world.spawnEntity((Entity)copperGolemEntity);
                        world.removeBlock(pos, false);
                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                if (stack.isOf(Items.HONEYCOMB)) {
                    return ActionResult.PASS;
                }
                this.changePose(world, state, pos, player);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public /* synthetic */ Enum getDegradationLevel() {
        return this.getDegradationLevel();
    }
}

