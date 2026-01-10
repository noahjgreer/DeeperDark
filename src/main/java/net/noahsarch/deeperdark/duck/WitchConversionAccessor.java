package net.noahsarch.deeperdark.duck;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface WitchConversionAccessor {
    boolean deeperdark$isConverting();
    void deeperdark$setConverting(@Nullable UUID uuid, int delay);
    int deeperdark$getConversionTimer();
    void deeperdark$setConversionTimer(int time);
    @Nullable UUID deeperdark$getConverter();
    void deeperdark$setConverter(@Nullable UUID uuid);
}

