/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class HangingSignBlockEntityRenderer.AttachmentType
extends Enum<HangingSignBlockEntityRenderer.AttachmentType>
implements StringIdentifiable {
    public static final /* enum */ HangingSignBlockEntityRenderer.AttachmentType WALL = new HangingSignBlockEntityRenderer.AttachmentType("wall");
    public static final /* enum */ HangingSignBlockEntityRenderer.AttachmentType CEILING = new HangingSignBlockEntityRenderer.AttachmentType("ceiling");
    public static final /* enum */ HangingSignBlockEntityRenderer.AttachmentType CEILING_MIDDLE = new HangingSignBlockEntityRenderer.AttachmentType("ceiling_middle");
    private final String id;
    private static final /* synthetic */ HangingSignBlockEntityRenderer.AttachmentType[] field_55162;

    public static HangingSignBlockEntityRenderer.AttachmentType[] values() {
        return (HangingSignBlockEntityRenderer.AttachmentType[])field_55162.clone();
    }

    public static HangingSignBlockEntityRenderer.AttachmentType valueOf(String string) {
        return Enum.valueOf(HangingSignBlockEntityRenderer.AttachmentType.class, string);
    }

    private HangingSignBlockEntityRenderer.AttachmentType(String id) {
        this.id = id;
    }

    public static HangingSignBlockEntityRenderer.AttachmentType from(BlockState state) {
        if (state.getBlock() instanceof HangingSignBlock) {
            return state.get(Properties.ATTACHED) != false ? CEILING_MIDDLE : CEILING;
        }
        return WALL;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ HangingSignBlockEntityRenderer.AttachmentType[] method_65243() {
        return new HangingSignBlockEntityRenderer.AttachmentType[]{WALL, CEILING, CEILING_MIDDLE};
    }

    static {
        field_55162 = HangingSignBlockEntityRenderer.AttachmentType.method_65243();
    }
}
