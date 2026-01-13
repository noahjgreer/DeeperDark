/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ChestBlockEntityRenderState
extends BlockEntityRenderState {
    public ChestType chestType = ChestType.SINGLE;
    public float lidAnimationProgress;
    public float yaw;
    public Variant variant = Variant.REGULAR;

    @Environment(value=EnvType.CLIENT)
    public static final class Variant
    extends Enum<Variant> {
        public static final /* enum */ Variant ENDER_CHEST = new Variant();
        public static final /* enum */ Variant CHRISTMAS = new Variant();
        public static final /* enum */ Variant TRAPPED = new Variant();
        public static final /* enum */ Variant COPPER_UNAFFECTED = new Variant();
        public static final /* enum */ Variant COPPER_EXPOSED = new Variant();
        public static final /* enum */ Variant COPPER_WEATHERED = new Variant();
        public static final /* enum */ Variant COPPER_OXIDIZED = new Variant();
        public static final /* enum */ Variant REGULAR = new Variant();
        private static final /* synthetic */ Variant[] field_62705;

        public static Variant[] values() {
            return (Variant[])field_62705.clone();
        }

        public static Variant valueOf(String string) {
            return Enum.valueOf(Variant.class, string);
        }

        private static /* synthetic */ Variant[] method_74402() {
            return new Variant[]{ENDER_CHEST, CHRISTMAS, TRAPPED, COPPER_UNAFFECTED, COPPER_EXPOSED, COPPER_WEATHERED, COPPER_OXIDIZED, REGULAR};
        }

        static {
            field_62705 = Variant.method_74402();
        }
    }
}
