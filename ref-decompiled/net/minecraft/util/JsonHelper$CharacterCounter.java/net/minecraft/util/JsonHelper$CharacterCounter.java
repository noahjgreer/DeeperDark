/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

static class JsonHelper.CharacterCounter
implements Appendable {
    private int length;
    private final int maxLength;

    public JsonHelper.CharacterCounter(int maxLength) {
        this.maxLength = maxLength;
    }

    private Appendable addCharacters(int count) {
        this.length += count;
        if (this.length > this.maxLength) {
            throw new IllegalStateException("Character count over limit: " + this.length + " > " + this.maxLength);
        }
        return this;
    }

    @Override
    public Appendable append(CharSequence charSequence) {
        return this.addCharacters(charSequence.length());
    }

    @Override
    public Appendable append(CharSequence charSequence, int from, int to) {
        return this.addCharacters(to - from);
    }

    @Override
    public Appendable append(char c) {
        return this.addCharacters(1);
    }
}
