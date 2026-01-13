/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

public static final class CopperGolemStatueBlock.Pose
extends Enum<CopperGolemStatueBlock.Pose>
implements StringIdentifiable {
    public static final /* enum */ CopperGolemStatueBlock.Pose STANDING = new CopperGolemStatueBlock.Pose("standing");
    public static final /* enum */ CopperGolemStatueBlock.Pose SITTING = new CopperGolemStatueBlock.Pose("sitting");
    public static final /* enum */ CopperGolemStatueBlock.Pose RUNNING = new CopperGolemStatueBlock.Pose("running");
    public static final /* enum */ CopperGolemStatueBlock.Pose STAR = new CopperGolemStatueBlock.Pose("star");
    public static final IntFunction<CopperGolemStatueBlock.Pose> INDEX_MAPPER;
    public static final Codec<CopperGolemStatueBlock.Pose> CODEC;
    private final String id;
    private static final /* synthetic */ CopperGolemStatueBlock.Pose[] field_61421;

    public static CopperGolemStatueBlock.Pose[] values() {
        return (CopperGolemStatueBlock.Pose[])field_61421.clone();
    }

    public static CopperGolemStatueBlock.Pose valueOf(String string) {
        return Enum.valueOf(CopperGolemStatueBlock.Pose.class, string);
    }

    private CopperGolemStatueBlock.Pose(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public CopperGolemStatueBlock.Pose getNext() {
        return INDEX_MAPPER.apply(this.ordinal() + 1);
    }

    private static /* synthetic */ CopperGolemStatueBlock.Pose[] method_72608() {
        return new CopperGolemStatueBlock.Pose[]{STANDING, SITTING, RUNNING, STAR};
    }

    static {
        field_61421 = CopperGolemStatueBlock.Pose.method_72608();
        INDEX_MAPPER = ValueLists.createIndexToValueFunction(Enum::ordinal, CopperGolemStatueBlock.Pose.values(), ValueLists.OutOfBoundsHandling.ZERO);
        CODEC = StringIdentifiable.createCodec(CopperGolemStatueBlock.Pose::values);
    }
}
