/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.CraftingTableBlock
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.CraftingScreenHandler
 *  net.minecraft.screen.NamedScreenHandlerFactory
 *  net.minecraft.screen.ScreenHandlerContext
 *  net.minecraft.screen.SimpleNamedScreenHandlerFactory
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CraftingTableBlock
extends Block {
    public static final MapCodec<CraftingTableBlock> CODEC = CraftingTableBlock.createCodec(CraftingTableBlock::new);
    private static final Text TITLE = Text.translatable((String)"container.crafting");

    public MapCodec<? extends CraftingTableBlock> getCodec() {
        return CODEC;
    }

    public CraftingTableBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
        return ActionResult.SUCCESS;
    }

    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> new CraftingScreenHandler(syncId, inventory, ScreenHandlerContext.create((World)world, (BlockPos)pos)), TITLE);
    }
}

