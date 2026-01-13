/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data;

import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;

public class DataGenerator.Pack {
    private final boolean shouldRun;
    private final String packName;
    private final DataOutput output;

    DataGenerator.Pack(boolean shouldRun, String name, DataOutput output) {
        this.shouldRun = shouldRun;
        this.packName = name;
        this.output = output;
    }

    public <T extends DataProvider> T addProvider(DataProvider.Factory<T> factory) {
        T dataProvider = factory.create(this.output);
        String string = this.packName + "/" + dataProvider.getName();
        if (!DataGenerator.this.providerNames.add(string)) {
            throw new IllegalStateException("Duplicate provider: " + string);
        }
        if (this.shouldRun) {
            DataGenerator.this.runningProviders.put(string, (DataProvider)dataProvider);
        }
        return dataProvider;
    }
}
