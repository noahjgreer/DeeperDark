/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class Ping.Region
extends Enum<Ping.Region> {
    public static final /* enum */ Ping.Region US_EAST_1 = new Ping.Region("us-east-1", "ec2.us-east-1.amazonaws.com");
    public static final /* enum */ Ping.Region US_WEST_2 = new Ping.Region("us-west-2", "ec2.us-west-2.amazonaws.com");
    public static final /* enum */ Ping.Region US_WEST_1 = new Ping.Region("us-west-1", "ec2.us-west-1.amazonaws.com");
    public static final /* enum */ Ping.Region EU_WEST_1 = new Ping.Region("eu-west-1", "ec2.eu-west-1.amazonaws.com");
    public static final /* enum */ Ping.Region AP_SOUTHEAST_1 = new Ping.Region("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com");
    public static final /* enum */ Ping.Region AP_SOUTHEAST_2 = new Ping.Region("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com");
    public static final /* enum */ Ping.Region AP_NORTHEAST_1 = new Ping.Region("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com");
    public static final /* enum */ Ping.Region SA_EAST_1 = new Ping.Region("sa-east-1", "ec2.sa-east-1.amazonaws.com");
    final String name;
    final String endpoint;
    private static final /* synthetic */ Ping.Region[] field_19575;

    public static Ping.Region[] values() {
        return (Ping.Region[])field_19575.clone();
    }

    public static Ping.Region valueOf(String name) {
        return Enum.valueOf(Ping.Region.class, name);
    }

    private Ping.Region(String name, String endpoint) {
        this.name = name;
        this.endpoint = endpoint;
    }

    private static /* synthetic */ Ping.Region[] method_36845() {
        return new Ping.Region[]{US_EAST_1, US_WEST_2, US_WEST_1, EU_WEST_1, AP_SOUTHEAST_1, AP_SOUTHEAST_2, AP_NORTHEAST_1, SA_EAST_1};
    }

    static {
        field_19575 = Ping.Region.method_36845();
    }
}
