/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.structure;

static class WoodlandMansionGenerator.FlagMatrix {
    private final int[][] array;
    final int n;
    final int m;
    private final int fallback;

    public WoodlandMansionGenerator.FlagMatrix(int n, int m, int fallback) {
        this.n = n;
        this.m = m;
        this.fallback = fallback;
        this.array = new int[n][m];
    }

    public void set(int i, int j, int value) {
        if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
            this.array[i][j] = value;
        }
    }

    public void fill(int i0, int j0, int i1, int j1, int value) {
        for (int i = j0; i <= j1; ++i) {
            for (int j = i0; j <= i1; ++j) {
                this.set(j, i, value);
            }
        }
    }

    public int get(int i, int j) {
        if (i >= 0 && i < this.n && j >= 0 && j < this.m) {
            return this.array[i][j];
        }
        return this.fallback;
    }

    public void update(int i, int j, int expected, int newValue) {
        if (this.get(i, j) == expected) {
            this.set(i, j, newValue);
        }
    }

    public boolean anyMatchAround(int i, int j, int value) {
        return this.get(i - 1, j) == value || this.get(i + 1, j) == value || this.get(i, j + 1) == value || this.get(i, j - 1) == value;
    }
}
