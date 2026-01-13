/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.dto;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class RealmsRegion
extends Enum<RealmsRegion> {
    public static final /* enum */ RealmsRegion AUSTRALIA_EAST = new RealmsRegion("AUSTRALIA_EAST", 0, "AustraliaEast", "realms.configuration.region.australia_east");
    public static final /* enum */ RealmsRegion AUSTRALIA_SOUTHEAST = new RealmsRegion("AUSTRALIA_SOUTHEAST", 1, "AustraliaSoutheast", "realms.configuration.region.australia_southeast");
    public static final /* enum */ RealmsRegion BRAZIL_SOUTH = new RealmsRegion("BRAZIL_SOUTH", 2, "BrazilSouth", "realms.configuration.region.brazil_south");
    public static final /* enum */ RealmsRegion CENTRAL_INDIA = new RealmsRegion("CENTRAL_INDIA", 3, "CentralIndia", "realms.configuration.region.central_india");
    public static final /* enum */ RealmsRegion CENTRAL_US = new RealmsRegion("CENTRAL_US", 4, "CentralUs", "realms.configuration.region.central_us");
    public static final /* enum */ RealmsRegion EAST_ASIA = new RealmsRegion("EAST_ASIA", 5, "EastAsia", "realms.configuration.region.east_asia");
    public static final /* enum */ RealmsRegion EAST_US = new RealmsRegion("EAST_US", 6, "EastUs", "realms.configuration.region.east_us");
    public static final /* enum */ RealmsRegion EAST_US_2 = new RealmsRegion("EAST_US_2", 7, "EastUs2", "realms.configuration.region.east_us_2");
    public static final /* enum */ RealmsRegion FRANCE_CENTRAL = new RealmsRegion("FRANCE_CENTRAL", 8, "FranceCentral", "realms.configuration.region.france_central");
    public static final /* enum */ RealmsRegion JAPAN_EAST = new RealmsRegion("JAPAN_EAST", 9, "JapanEast", "realms.configuration.region.japan_east");
    public static final /* enum */ RealmsRegion JAPAN_WEST = new RealmsRegion("JAPAN_WEST", 10, "JapanWest", "realms.configuration.region.japan_west");
    public static final /* enum */ RealmsRegion KOREA_CENTRAL = new RealmsRegion("KOREA_CENTRAL", 11, "KoreaCentral", "realms.configuration.region.korea_central");
    public static final /* enum */ RealmsRegion NORTH_CENTRAL_US = new RealmsRegion("NORTH_CENTRAL_US", 12, "NorthCentralUs", "realms.configuration.region.north_central_us");
    public static final /* enum */ RealmsRegion NORTH_EUROPE = new RealmsRegion("NORTH_EUROPE", 13, "NorthEurope", "realms.configuration.region.north_europe");
    public static final /* enum */ RealmsRegion SOUTH_CENTRAL_US = new RealmsRegion("SOUTH_CENTRAL_US", 14, "SouthCentralUs", "realms.configuration.region.south_central_us");
    public static final /* enum */ RealmsRegion SOUTHEAST_ASIA = new RealmsRegion("SOUTHEAST_ASIA", 15, "SoutheastAsia", "realms.configuration.region.southeast_asia");
    public static final /* enum */ RealmsRegion SWEDEN_CENTRAL = new RealmsRegion("SWEDEN_CENTRAL", 16, "SwedenCentral", "realms.configuration.region.sweden_central");
    public static final /* enum */ RealmsRegion UAE_NORTH = new RealmsRegion("UAE_NORTH", 17, "UAENorth", "realms.configuration.region.uae_north");
    public static final /* enum */ RealmsRegion UK_SOUTH = new RealmsRegion("UK_SOUTH", 18, "UKSouth", "realms.configuration.region.uk_south");
    public static final /* enum */ RealmsRegion WEST_CENTRAL_US = new RealmsRegion("WEST_CENTRAL_US", 19, "WestCentralUs", "realms.configuration.region.west_central_us");
    public static final /* enum */ RealmsRegion WEST_EUROPE = new RealmsRegion("WEST_EUROPE", 20, "WestEurope", "realms.configuration.region.west_europe");
    public static final /* enum */ RealmsRegion WEST_US = new RealmsRegion("WEST_US", 21, "WestUs", "realms.configuration.region.west_us");
    public static final /* enum */ RealmsRegion WEST_US_2 = new RealmsRegion("WEST_US_2", 22, "WestUs2", "realms.configuration.region.west_us_2");
    public static final /* enum */ RealmsRegion INVALID_REGION = new RealmsRegion("INVALID_REGION", 23, "invalid", "");
    public final String name;
    public final String translationKey;
    private static final /* synthetic */ RealmsRegion[] field_60175;

    public static RealmsRegion[] values() {
        return (RealmsRegion[])field_60175.clone();
    }

    public static RealmsRegion valueOf(String string) {
        return Enum.valueOf(RealmsRegion.class, string);
    }

    private RealmsRegion(String name, String translationKey) {
        this.name = name;
        this.translationKey = translationKey;
    }

    public static @Nullable RealmsRegion fromName(String name) {
        for (RealmsRegion realmsRegion : RealmsRegion.values()) {
            if (!realmsRegion.name.equals(name)) continue;
            return realmsRegion;
        }
        return null;
    }

    private static /* synthetic */ RealmsRegion[] method_71172() {
        return new RealmsRegion[]{AUSTRALIA_EAST, AUSTRALIA_SOUTHEAST, BRAZIL_SOUTH, CENTRAL_INDIA, CENTRAL_US, EAST_ASIA, EAST_US, EAST_US_2, FRANCE_CENTRAL, JAPAN_EAST, JAPAN_WEST, KOREA_CENTRAL, NORTH_CENTRAL_US, NORTH_EUROPE, SOUTH_CENTRAL_US, SOUTHEAST_ASIA, SWEDEN_CENTRAL, UAE_NORTH, UK_SOUTH, WEST_CENTRAL_US, WEST_EUROPE, WEST_US, WEST_US_2, INVALID_REGION};
    }

    static {
        field_60175 = RealmsRegion.method_71172();
    }
}

