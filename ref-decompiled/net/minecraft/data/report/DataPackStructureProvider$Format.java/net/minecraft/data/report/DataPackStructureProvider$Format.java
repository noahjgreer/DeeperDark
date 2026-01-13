/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.data.report;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

static final class DataPackStructureProvider.Format
extends Enum<DataPackStructureProvider.Format>
implements StringIdentifiable {
    public static final /* enum */ DataPackStructureProvider.Format STRUCTURE = new DataPackStructureProvider.Format("structure");
    public static final /* enum */ DataPackStructureProvider.Format MCFUNCTION = new DataPackStructureProvider.Format("mcfunction");
    public static final Codec<DataPackStructureProvider.Format> CODEC;
    private final String id;
    private static final /* synthetic */ DataPackStructureProvider.Format[] field_53712;

    public static DataPackStructureProvider.Format[] values() {
        return (DataPackStructureProvider.Format[])field_53712.clone();
    }

    public static DataPackStructureProvider.Format valueOf(String string) {
        return Enum.valueOf(DataPackStructureProvider.Format.class, string);
    }

    private DataPackStructureProvider.Format(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ DataPackStructureProvider.Format[] method_62725() {
        return new DataPackStructureProvider.Format[]{STRUCTURE, MCFUNCTION};
    }

    static {
        field_53712 = DataPackStructureProvider.Format.method_62725();
        CODEC = StringIdentifiable.createCodec(DataPackStructureProvider.Format::values);
    }
}
