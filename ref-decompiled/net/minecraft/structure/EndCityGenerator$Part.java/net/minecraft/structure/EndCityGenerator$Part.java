/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

import java.util.List;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

static interface EndCityGenerator.Part {
    public void init();

    public boolean create(StructureTemplateManager var1, int var2, EndCityGenerator.Piece var3, BlockPos var4, List<StructurePiece> var5, Random var6);
}
