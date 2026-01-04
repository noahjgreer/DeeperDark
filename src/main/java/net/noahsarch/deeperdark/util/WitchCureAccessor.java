package net.noahsarch.deeperdark.util;

import java.util.UUID;

public interface WitchCureAccessor {
    void deeperdark$setConversionTimer(int time);
    int deeperdark$getConversionTimer();
    void deeperdark$setConverterUuid(UUID uuid);
    UUID deeperdark$getConverterUuid();
}

