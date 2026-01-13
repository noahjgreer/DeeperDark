/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import net.minecraft.util.CsvWriter;

public static class CsvWriter.Header {
    private final List<String> columns = Lists.newArrayList();

    public CsvWriter.Header addColumn(String name) {
        this.columns.add(name);
        return this;
    }

    public CsvWriter startBody(Writer writer) throws IOException {
        return new CsvWriter(writer, this.columns);
    }
}
