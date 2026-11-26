package net.noahsarch.deeperdark.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/** A crate's saved state, both in the block entity and on the crate item. Entries are never mutated. */
public record CrateContents(List<VaultBlockEntity.VaultEntry> entries, boolean autoMerge) {

    public static final CrateContents EMPTY = new CrateContents(List.of(), false);

    public static final Codec<CrateContents> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        VaultBlockEntity.ENTRY_CODEC.listOf().fieldOf("entries").forGetter(CrateContents::entries),
        Codec.BOOL.optionalFieldOf("auto_merge", false).forGetter(CrateContents::autoMerge)
    ).apply(inst, CrateContents::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrateContents> STREAM_CODEC = StreamCodec.composite(
        VaultBlockEntity.ENTRIES_STREAM_CODEC, CrateContents::entries,
        ByteBufCodecs.BOOL, CrateContents::autoMerge,
        CrateContents::new
    );

    public long totalCount() {
        long total = 0;
        for (VaultBlockEntity.VaultEntry e : entries) total += e.count;
        return total;
    }
}
