/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.command;

import net.minecraft.server.command.FillCommand;

static final class FillCommand.Mode
extends Enum<FillCommand.Mode> {
    public static final /* enum */ FillCommand.Mode REPLACE = new FillCommand.Mode(FillCommand.PostProcessor.EMPTY, FillCommand.Filter.IDENTITY);
    public static final /* enum */ FillCommand.Mode OUTLINE = new FillCommand.Mode(FillCommand.PostProcessor.EMPTY, (range, pos, block, world) -> {
        if (pos.getX() == range.getMinX() || pos.getX() == range.getMaxX() || pos.getY() == range.getMinY() || pos.getY() == range.getMaxY() || pos.getZ() == range.getMinZ() || pos.getZ() == range.getMaxZ()) {
            return block;
        }
        return null;
    });
    public static final /* enum */ FillCommand.Mode HOLLOW = new FillCommand.Mode(FillCommand.PostProcessor.EMPTY, (range, pos, block, world) -> {
        if (pos.getX() == range.getMinX() || pos.getX() == range.getMaxX() || pos.getY() == range.getMinY() || pos.getY() == range.getMaxY() || pos.getZ() == range.getMinZ() || pos.getZ() == range.getMaxZ()) {
            return block;
        }
        return AIR_BLOCK_ARGUMENT;
    });
    public static final /* enum */ FillCommand.Mode DESTROY = new FillCommand.Mode((world, pos) -> world.breakBlock(pos, true), FillCommand.Filter.IDENTITY);
    public final FillCommand.Filter filter;
    public final FillCommand.PostProcessor postProcessor;
    private static final /* synthetic */ FillCommand.Mode[] field_13653;

    public static FillCommand.Mode[] values() {
        return (FillCommand.Mode[])field_13653.clone();
    }

    public static FillCommand.Mode valueOf(String string) {
        return Enum.valueOf(FillCommand.Mode.class, string);
    }

    private FillCommand.Mode(FillCommand.PostProcessor postProcessor, FillCommand.Filter filter) {
        this.postProcessor = postProcessor;
        this.filter = filter;
    }

    private static /* synthetic */ FillCommand.Mode[] method_36968() {
        return new FillCommand.Mode[]{REPLACE, OUTLINE, HOLLOW, DESTROY};
    }

    static {
        field_13653 = FillCommand.Mode.method_36968();
    }
}
