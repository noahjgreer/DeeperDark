/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CraftingTableBlock
 *  net.minecraft.block.SmithingTableBlock
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
 *  net.minecraft.screen.SmithingScreenHandler
 *  net.minecraft.stat.Stats
 *  net.minecraft.text.Text
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingTableBlock
extends CraftingTableBlock {
    public static final MapCodec<SmithingTableBlock> CODEC = SmithingTableBlock.createCodec(SmithingTableBlock::new);
    private static final Text SCREEN_TITLE = Text.translatable((String)"container.upgrade");

    public MapCodec<SmithingTableBlock> getCodec() {
        return CODEC;
    }

    public SmithingTableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new SmithingScreenHandler(syncId, inventory, ScreenHandlerContext.create((World)world, (BlockPos)pos)), SCREEN_TITLE);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);
        }
        return ActionResult.SUCCESS;
    }
}

