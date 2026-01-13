/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureTemplate;
import org.jspecify.annotations.Nullable;

public static final class StructureTemplate.PalettedBlockInfoList {
    private final List<StructureTemplate.StructureBlockInfo> infos;
    private final Map<Block, List<StructureTemplate.StructureBlockInfo>> blockToInfos = Maps.newHashMap();
    private @Nullable List<StructureTemplate.JigsawBlockInfo> jigsawBlockInfos;

    StructureTemplate.PalettedBlockInfoList(List<StructureTemplate.StructureBlockInfo> infos) {
        this.infos = infos;
    }

    public List<StructureTemplate.JigsawBlockInfo> getOrCreateJigsawBlockInfos() {
        if (this.jigsawBlockInfos == null) {
            this.jigsawBlockInfos = this.getAllOf(Blocks.JIGSAW).stream().map(StructureTemplate.JigsawBlockInfo::of).toList();
        }
        return this.jigsawBlockInfos;
    }

    public List<StructureTemplate.StructureBlockInfo> getAll() {
        return this.infos;
    }

    public List<StructureTemplate.StructureBlockInfo> getAllOf(Block block) {
        return this.blockToInfos.computeIfAbsent(block, block2 -> this.infos.stream().filter(info -> info.state.isOf((Block)block2)).collect(Collectors.toList()));
    }
}
