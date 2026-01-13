/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.session;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class BanReason
extends Enum<BanReason> {
    public static final /* enum */ BanReason GENERIC_VIOLATION = new BanReason("generic_violation");
    public static final /* enum */ BanReason FALSE_REPORTING = new BanReason("false_reporting");
    public static final /* enum */ BanReason HATE_SPEECH = new BanReason("hate_speech");
    public static final /* enum */ BanReason HATE_TERRORISM_NOTORIOUS_FIGURE = new BanReason("hate_terrorism_notorious_figure");
    public static final /* enum */ BanReason HARASSMENT_OR_BULLYING = new BanReason("harassment_or_bullying");
    public static final /* enum */ BanReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new BanReason("defamation_impersonation_false_information");
    public static final /* enum */ BanReason DRUGS = new BanReason("drugs");
    public static final /* enum */ BanReason FRAUD = new BanReason("fraud");
    public static final /* enum */ BanReason SPAM_OR_ADVERTISING = new BanReason("spam_or_advertising");
    public static final /* enum */ BanReason NUDITY_OR_PORNOGRAPHY = new BanReason("nudity_or_pornography");
    public static final /* enum */ BanReason SEXUALLY_INAPPROPRIATE = new BanReason("sexually_inappropriate");
    public static final /* enum */ BanReason EXTREME_VIOLENCE_OR_GORE = new BanReason("extreme_violence_or_gore");
    public static final /* enum */ BanReason IMMINENT_HARM_TO_PERSON_OR_PROPERTY = new BanReason("imminent_harm_to_person_or_property");
    private final Text description;
    private static final /* synthetic */ BanReason[] field_42905;

    public static BanReason[] values() {
        return (BanReason[])field_42905.clone();
    }

    public static BanReason valueOf(String string) {
        return Enum.valueOf(BanReason.class, string);
    }

    private BanReason(String id) {
        this.description = Text.translatable("gui.banned.reason." + id);
    }

    public Text getDescription() {
        return this.description;
    }

    public static @Nullable BanReason byId(int id) {
        return switch (id) {
            case 17, 19, 23, 31 -> GENERIC_VIOLATION;
            case 2 -> FALSE_REPORTING;
            case 5 -> HATE_SPEECH;
            case 16, 25 -> HATE_TERRORISM_NOTORIOUS_FIGURE;
            case 21 -> HARASSMENT_OR_BULLYING;
            case 27 -> DEFAMATION_IMPERSONATION_FALSE_INFORMATION;
            case 28 -> DRUGS;
            case 29 -> FRAUD;
            case 30 -> SPAM_OR_ADVERTISING;
            case 32 -> NUDITY_OR_PORNOGRAPHY;
            case 33, 35, 36 -> SEXUALLY_INAPPROPRIATE;
            case 34 -> EXTREME_VIOLENCE_OR_GORE;
            case 53 -> IMMINENT_HARM_TO_PERSON_OR_PROPERTY;
            default -> null;
        };
    }

    private static /* synthetic */ BanReason[] method_49314() {
        return new BanReason[]{GENERIC_VIOLATION, FALSE_REPORTING, HATE_SPEECH, HATE_TERRORISM_NOTORIOUS_FIGURE, HARASSMENT_OR_BULLYING, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, DRUGS, FRAUD, SPAM_OR_ADVERTISING, NUDITY_OR_PORNOGRAPHY, SEXUALLY_INAPPROPRIATE, EXTREME_VIOLENCE_OR_GORE, IMMINENT_HARM_TO_PERSON_OR_PROPERTY};
    }

    static {
        field_42905 = BanReason.method_49314();
    }
}
