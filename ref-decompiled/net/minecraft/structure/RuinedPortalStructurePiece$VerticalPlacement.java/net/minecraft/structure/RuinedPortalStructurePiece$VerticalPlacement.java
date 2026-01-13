/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public static final class RuinedPortalStructurePiece.VerticalPlacement
extends Enum<RuinedPortalStructurePiece.VerticalPlacement>
implements StringIdentifiable {
    public static final /* enum */ RuinedPortalStructurePiece.VerticalPlacement ON_LAND_SURFACE = new RuinedPortalStructurePiece.VerticalPlacement("on_land_surface");
    public static final /* enum */ RuinedPortalStructurePiece.VerticalPlacement PARTLY_BURIED = new RuinedPortalStructurePiece.VerticalPlacement("partly_buried");
    public static final /* enum */ RuinedPortalStructurePiece.VerticalPlacement ON_OCEAN_FLOOR = new RuinedPortalStructurePiece.VerticalPlacement("on_ocean_floor");
    public static final /* enum */ RuinedPortalStructurePiece.VerticalPlacement IN_MOUNTAIN = new RuinedPortalStructurePiece.VerticalPlacement("in_mountain");
    public static final /* enum */ RuinedPortalStructurePiece.VerticalPlacement UNDERGROUND = new RuinedPortalStructurePiece.VerticalPlacement("underground");
    public static final /* enum */ RuinedPortalStructurePiece.VerticalPlacement IN_NETHER = new RuinedPortalStructurePiece.VerticalPlacement("in_nether");
    public static final Codec<RuinedPortalStructurePiece.VerticalPlacement> CODEC;
    private final String id;
    private static final /* synthetic */ RuinedPortalStructurePiece.VerticalPlacement[] field_24037;

    public static RuinedPortalStructurePiece.VerticalPlacement[] values() {
        return (RuinedPortalStructurePiece.VerticalPlacement[])field_24037.clone();
    }

    public static RuinedPortalStructurePiece.VerticalPlacement valueOf(String string) {
        return Enum.valueOf(RuinedPortalStructurePiece.VerticalPlacement.class, string);
    }

    private RuinedPortalStructurePiece.VerticalPlacement(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ RuinedPortalStructurePiece.VerticalPlacement[] method_36761() {
        return new RuinedPortalStructurePiece.VerticalPlacement[]{ON_LAND_SURFACE, PARTLY_BURIED, ON_OCEAN_FLOOR, IN_MOUNTAIN, UNDERGROUND, IN_NETHER};
    }

    static {
        field_24037 = RuinedPortalStructurePiece.VerticalPlacement.method_36761();
        CODEC = StringIdentifiable.createCodec(RuinedPortalStructurePiece.VerticalPlacement::values);
    }
}
