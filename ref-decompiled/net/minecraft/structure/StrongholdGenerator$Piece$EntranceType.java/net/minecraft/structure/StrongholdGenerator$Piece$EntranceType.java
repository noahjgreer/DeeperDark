/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.dynamic.Codecs;

protected static final class StrongholdGenerator.Piece.EntranceType
extends Enum<StrongholdGenerator.Piece.EntranceType> {
    public static final /* enum */ StrongholdGenerator.Piece.EntranceType OPENING = new StrongholdGenerator.Piece.EntranceType();
    public static final /* enum */ StrongholdGenerator.Piece.EntranceType WOOD_DOOR = new StrongholdGenerator.Piece.EntranceType();
    public static final /* enum */ StrongholdGenerator.Piece.EntranceType GRATES = new StrongholdGenerator.Piece.EntranceType();
    public static final /* enum */ StrongholdGenerator.Piece.EntranceType IRON_DOOR = new StrongholdGenerator.Piece.EntranceType();
    @Deprecated
    public static final Codec<StrongholdGenerator.Piece.EntranceType> CODEC;
    private static final /* synthetic */ StrongholdGenerator.Piece.EntranceType[] field_15292;

    public static StrongholdGenerator.Piece.EntranceType[] values() {
        return (StrongholdGenerator.Piece.EntranceType[])field_15292.clone();
    }

    public static StrongholdGenerator.Piece.EntranceType valueOf(String string) {
        return Enum.valueOf(StrongholdGenerator.Piece.EntranceType.class, string);
    }

    private static /* synthetic */ StrongholdGenerator.Piece.EntranceType[] method_36762() {
        return new StrongholdGenerator.Piece.EntranceType[]{OPENING, WOOD_DOOR, GRATES, IRON_DOOR};
    }

    static {
        field_15292 = StrongholdGenerator.Piece.EntranceType.method_36762();
        CODEC = Codecs.enumByName(StrongholdGenerator.Piece.EntranceType::valueOf);
    }
}
