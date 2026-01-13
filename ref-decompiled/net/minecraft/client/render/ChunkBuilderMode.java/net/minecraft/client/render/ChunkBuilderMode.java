/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.function.ValueLists;

@Environment(value=EnvType.CLIENT)
public final class ChunkBuilderMode
extends Enum<ChunkBuilderMode> {
    public static final /* enum */ ChunkBuilderMode NONE = new ChunkBuilderMode(0, "options.prioritizeChunkUpdates.none");
    public static final /* enum */ ChunkBuilderMode PLAYER_AFFECTED = new ChunkBuilderMode(1, "options.prioritizeChunkUpdates.byPlayer");
    public static final /* enum */ ChunkBuilderMode NEARBY = new ChunkBuilderMode(2, "options.prioritizeChunkUpdates.nearby");
    private static final IntFunction<ChunkBuilderMode> BY_ID;
    public static final Codec<ChunkBuilderMode> CODEC;
    private final int id;
    private final Text text;
    private static final /* synthetic */ ChunkBuilderMode[] field_34794;

    public static ChunkBuilderMode[] values() {
        return (ChunkBuilderMode[])field_34794.clone();
    }

    public static ChunkBuilderMode valueOf(String string) {
        return Enum.valueOf(ChunkBuilderMode.class, string);
    }

    private ChunkBuilderMode(int id, String name) {
        this.id = id;
        this.text = Text.translatable(name);
    }

    public Text getText() {
        return this.text;
    }

    private static /* synthetic */ ChunkBuilderMode[] method_38526() {
        return new ChunkBuilderMode[]{NONE, PLAYER_AFFECTED, NEARBY};
    }

    static {
        field_34794 = ChunkBuilderMode.method_38526();
        BY_ID = ValueLists.createIndexToValueFunction(chunkBuilderMode -> chunkBuilderMode.id, ChunkBuilderMode.values(), ValueLists.OutOfBoundsHandling.WRAP);
        CODEC = Codec.INT.xmap(BY_ID::apply, mode -> mode.id);
    }
}
