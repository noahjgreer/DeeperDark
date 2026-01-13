/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.datafixer.fix;

static class ChunkPalettedStorageFix.ChunkNibbleArray {
    private static final int CONTENTS_LENGTH = 2048;
    private static final int field_29880 = 4;
    private final byte[] contents;

    public ChunkPalettedStorageFix.ChunkNibbleArray() {
        this.contents = new byte[2048];
    }

    public ChunkPalettedStorageFix.ChunkNibbleArray(byte[] contents) {
        this.contents = contents;
        if (contents.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + contents.length);
        }
    }

    public int get(int x, int y, int z) {
        int i = this.getRawIndex(y << 8 | z << 4 | x);
        if (this.usesLowNibble(y << 8 | z << 4 | x)) {
            return this.contents[i] & 0xF;
        }
        return this.contents[i] >> 4 & 0xF;
    }

    private boolean usesLowNibble(int index) {
        return (index & 1) == 0;
    }

    private int getRawIndex(int index) {
        return index >> 1;
    }
}
