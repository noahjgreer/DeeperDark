/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.session.report.AbuseReportReason
 *  net.minecraft.client.session.report.AbuseReportReason$1
 *  net.minecraft.client.session.report.AbuseReportType
 *  net.minecraft.text.Text
 */
package net.minecraft.client.session.report;

import java.util.List;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public final class AbuseReportReason
extends Enum<AbuseReportReason> {
    public static final /* enum */ AbuseReportReason I_WANT_TO_REPORT_THEM = new AbuseReportReason("I_WANT_TO_REPORT_THEM", 0, "i_want_to_report_them");
    public static final /* enum */ AbuseReportReason HATE_SPEECH = new AbuseReportReason("HATE_SPEECH", 1, "hate_speech");
    public static final /* enum */ AbuseReportReason HARASSMENT_OR_BULLYING = new AbuseReportReason("HARASSMENT_OR_BULLYING", 2, "harassment_or_bullying");
    public static final /* enum */ AbuseReportReason SELF_HARM_OR_SUICIDE = new AbuseReportReason("SELF_HARM_OR_SUICIDE", 3, "self_harm_or_suicide");
    public static final /* enum */ AbuseReportReason IMMINENT_HARM = new AbuseReportReason("IMMINENT_HARM", 4, "imminent_harm");
    public static final /* enum */ AbuseReportReason DEFAMATION_IMPERSONATION_FALSE_INFORMATION = new AbuseReportReason("DEFAMATION_IMPERSONATION_FALSE_INFORMATION", 5, "defamation_impersonation_false_information");
    public static final /* enum */ AbuseReportReason ALCOHOL_TOBACCO_DRUGS = new AbuseReportReason("ALCOHOL_TOBACCO_DRUGS", 6, "alcohol_tobacco_drugs");
    public static final /* enum */ AbuseReportReason CHILD_SEXUAL_EXPLOITATION_OR_ABUSE = new AbuseReportReason("CHILD_SEXUAL_EXPLOITATION_OR_ABUSE", 7, "child_sexual_exploitation_or_abuse");
    public static final /* enum */ AbuseReportReason TERRORISM_OR_VIOLENT_EXTREMISM = new AbuseReportReason("TERRORISM_OR_VIOLENT_EXTREMISM", 8, "terrorism_or_violent_extremism");
    public static final /* enum */ AbuseReportReason NON_CONSENSUAL_INTIMATE_IMAGERY = new AbuseReportReason("NON_CONSENSUAL_INTIMATE_IMAGERY", 9, "non_consensual_intimate_imagery");
    public static final /* enum */ AbuseReportReason SEXUALLY_INAPPROPRIATE = new AbuseReportReason("SEXUALLY_INAPPROPRIATE", 10, "sexually_inappropriate");
    private final String id;
    private final Text text;
    private final Text description;
    private static final /* synthetic */ AbuseReportReason[] field_39674;

    public static AbuseReportReason[] values() {
        return (AbuseReportReason[])field_39674.clone();
    }

    public static AbuseReportReason valueOf(String string) {
        return Enum.valueOf(AbuseReportReason.class, string);
    }

    private AbuseReportReason(String id) {
        this.id = id.toUpperCase(Locale.ROOT);
        String string2 = "gui.abuseReport.reason." + id;
        this.text = Text.translatable((String)string2);
        this.description = Text.translatable((String)(string2 + ".description"));
    }

    public String getId() {
        return this.id;
    }

    public Text getText() {
        return this.text;
    }

    public Text getDescription() {
        return this.description;
    }

    public static List<AbuseReportReason> getExcludedReasonsForType(AbuseReportType reportType) {
        return switch (1.field_53037[reportType.ordinal()]) {
            case 1 -> List.of(SEXUALLY_INAPPROPRIATE);
            case 2 -> List.of(IMMINENT_HARM, DEFAMATION_IMPERSONATION_FALSE_INFORMATION);
            default -> List.of();
        };
    }

    private static /* synthetic */ AbuseReportReason[] method_44597() {
        return new AbuseReportReason[]{I_WANT_TO_REPORT_THEM, HATE_SPEECH, HARASSMENT_OR_BULLYING, SELF_HARM_OR_SUICIDE, IMMINENT_HARM, DEFAMATION_IMPERSONATION_FALSE_INFORMATION, ALCOHOL_TOBACCO_DRUGS, CHILD_SEXUAL_EXPLOITATION_OR_ABUSE, TERRORISM_OR_VIOLENT_EXTREMISM, NON_CONSENSUAL_INTIMATE_IMAGERY, SEXUALLY_INAPPROPRIATE};
    }

    static {
        field_39674 = AbuseReportReason.method_44597();
    }
}

