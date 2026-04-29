package io.github.lucaargolo.seasons.mixed;

public interface BiomeMixed {
    float seasons_getOriginalTemperature();
    float seasons_getOriginalDownfall();
    boolean seasons_getOriginalHasPrecipitation();
    void seasons_storeOriginal(float temperature, float downfall, boolean hasPrecipitation);
    boolean seasons_hasOriginalStored();
    /** Always reads from the immutable ClimateSettings record — never affected by season override. */
    float seasons_getBaseDownfall();
    void seasons_applySeasonWeather(float temperature, boolean hasPrecipitation);
    void seasons_clearSeasonWeather();
    boolean seasons_hasSeasonWeather();
    float seasons_getSeasonTemperature();
    boolean seasons_getSeasonHasPrecipitation();
}
