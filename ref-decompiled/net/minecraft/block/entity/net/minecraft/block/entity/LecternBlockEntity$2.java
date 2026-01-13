/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.screen.PropertyDelegate;

class LecternBlockEntity.2
implements PropertyDelegate {
    LecternBlockEntity.2() {
    }

    @Override
    public int get(int index) {
        return index == 0 ? LecternBlockEntity.this.currentPage : 0;
    }

    @Override
    public void set(int index, int value) {
        if (index == 0) {
            LecternBlockEntity.this.setCurrentPage(value);
        }
    }

    @Override
    public int size() {
        return 1;
    }
}
