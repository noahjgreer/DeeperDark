/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.filter;

@FunctionalInterface
public static interface AbstractTextFilterer.HashIgnorer {
    public static final AbstractTextFilterer.HashIgnorer NEVER_IGNORE = (hashes, hashesSize) -> false;
    public static final AbstractTextFilterer.HashIgnorer IGNORE_IF_MATCHES_ALL = (hashes, hashesSize) -> hashes.length() == hashesSize;

    public static AbstractTextFilterer.HashIgnorer internalDropHashes(int hashesToDrop) {
        return (hashes, hashesSize) -> hashesSize >= hashesToDrop;
    }

    public static AbstractTextFilterer.HashIgnorer dropHashes(int hashesToDrop) {
        return switch (hashesToDrop) {
            case -1 -> NEVER_IGNORE;
            case 0 -> IGNORE_IF_MATCHES_ALL;
            default -> AbstractTextFilterer.HashIgnorer.internalDropHashes(hashesToDrop);
        };
    }

    public boolean shouldIgnore(String var1, int var2);
}
