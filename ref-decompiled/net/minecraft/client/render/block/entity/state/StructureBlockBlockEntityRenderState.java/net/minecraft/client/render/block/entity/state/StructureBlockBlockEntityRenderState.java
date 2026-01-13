/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBoxRendering;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderState
extends BlockEntityRenderState {
    public boolean visible;
    public StructureBoxRendering.RenderMode renderMode;
    public StructureBoxRendering.StructureBox structureBox;
    public @Nullable InvisibleRenderType @Nullable [] invisibleBlocks;
    public boolean @Nullable [] field_62682;

    @Environment(value=EnvType.CLIENT)
    public static final class InvisibleRenderType
    extends Enum<InvisibleRenderType> {
        public static final /* enum */ InvisibleRenderType AIR = new InvisibleRenderType();
        public static final /* enum */ InvisibleRenderType BARRIER = new InvisibleRenderType();
        public static final /* enum */ InvisibleRenderType LIGHT = new InvisibleRenderType();
        public static final /* enum */ InvisibleRenderType STRUCTURE_VOID = new InvisibleRenderType();
        private static final /* synthetic */ InvisibleRenderType[] field_62687;

        public static InvisibleRenderType[] values() {
            return (InvisibleRenderType[])field_62687.clone();
        }

        public static InvisibleRenderType valueOf(String string) {
            return Enum.valueOf(InvisibleRenderType.class, string);
        }

        private static /* synthetic */ InvisibleRenderType[] method_74401() {
            return new InvisibleRenderType[]{AIR, BARRIER, LIGHT, STRUCTURE_VOID};
        }

        static {
            field_62687 = InvisibleRenderType.method_74401();
        }
    }
}
