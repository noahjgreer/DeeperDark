/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.mojang.logging.LogUtils
 *  com.mojang.util.UndashedUuid
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.realms.CheckedGson
 *  net.minecraft.client.realms.RealmsClient
 *  net.minecraft.client.realms.RealmsClient$CompatibleVersionResponse
 *  net.minecraft.client.realms.RealmsClient$Environment
 *  net.minecraft.client.realms.RealmsClientConfig
 *  net.minecraft.client.realms.RealmsError
 *  net.minecraft.client.realms.RealmsError$AuthenticationError
 *  net.minecraft.client.realms.RealmsError$SimpleHttpError
 *  net.minecraft.client.realms.RealmsSerializable
 *  net.minecraft.client.realms.Request
 *  net.minecraft.client.realms.dto.BackupList
 *  net.minecraft.client.realms.dto.Ops
 *  net.minecraft.client.realms.dto.PendingInvite
 *  net.minecraft.client.realms.dto.PendingInvitesList
 *  net.minecraft.client.realms.dto.PingResult
 *  net.minecraft.client.realms.dto.PlayerInfo
 *  net.minecraft.client.realms.dto.RealmsConfigurationDto
 *  net.minecraft.client.realms.dto.RealmsDescriptionDto
 *  net.minecraft.client.realms.dto.RealmsNews
 *  net.minecraft.client.realms.dto.RealmsNotification
 *  net.minecraft.client.realms.dto.RealmsOptionsDto
 *  net.minecraft.client.realms.dto.RealmsRegion
 *  net.minecraft.client.realms.dto.RealmsRegionDataList
 *  net.minecraft.client.realms.dto.RealmsRegionSelectionPreference
 *  net.minecraft.client.realms.dto.RealmsServer
 *  net.minecraft.client.realms.dto.RealmsServer$WorldType
 *  net.minecraft.client.realms.dto.RealmsServerAddress
 *  net.minecraft.client.realms.dto.RealmsServerList
 *  net.minecraft.client.realms.dto.RealmsServerPlayerList
 *  net.minecraft.client.realms.dto.RealmsSettingDto
 *  net.minecraft.client.realms.dto.RealmsWorldOptions
 *  net.minecraft.client.realms.dto.RealmsWorldResetDto
 *  net.minecraft.client.realms.dto.RegionData
 *  net.minecraft.client.realms.dto.RegionSelectionMethod
 *  net.minecraft.client.realms.dto.SentInvite
 *  net.minecraft.client.realms.dto.Subscription
 *  net.minecraft.client.realms.dto.UploadInfo
 *  net.minecraft.client.realms.dto.WorldDownload
 *  net.minecraft.client.realms.dto.WorldTemplatePaginatedList
 *  net.minecraft.client.realms.exception.RealmsHttpException
 *  net.minecraft.client.realms.exception.RealmsServiceException
 *  net.minecraft.client.realms.exception.RetryCallException
 *  net.minecraft.client.realms.gui.screen.RealmsMainScreen
 *  net.minecraft.client.realms.util.UploadTokenCache
 *  net.minecraft.util.LenientJsonParser
 *  net.minecraft.util.Util
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.realms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.CheckedGson;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsClientConfig;
import net.minecraft.client.realms.RealmsError;
import net.minecraft.client.realms.RealmsSerializable;
import net.minecraft.client.realms.Request;
import net.minecraft.client.realms.dto.BackupList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.dto.PendingInvitesList;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsConfigurationDto;
import net.minecraft.client.realms.dto.RealmsDescriptionDto;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsOptionsDto;
import net.minecraft.client.realms.dto.RealmsRegion;
import net.minecraft.client.realms.dto.RealmsRegionDataList;
import net.minecraft.client.realms.dto.RealmsRegionSelectionPreference;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerList;
import net.minecraft.client.realms.dto.RealmsSettingDto;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.RealmsWorldResetDto;
import net.minecraft.client.realms.dto.RegionData;
import net.minecraft.client.realms.dto.RegionSelectionMethod;
import net.minecraft.client.realms.dto.SentInvite;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.util.UploadTokenCache;
import net.minecraft.util.LenientJsonParser;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@net.fabricmc.api.Environment(value=EnvType.CLIENT)
public class RealmsClient {
    public static final Environment ENVIRONMENT = Optional.ofNullable(System.getenv("realms.environment")).or(() -> Optional.ofNullable(System.getProperty("realms.environment"))).flatMap(Environment::fromName).orElse(Environment.PRODUCTION);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile @Nullable RealmsClient instance = null;
    private final CompletableFuture<Set<String>> featureFlagsFuture;
    private final String sessionId;
    private final String username;
    private final MinecraftClient client;
    private static final String WORLDS_ENDPOINT = "worlds";
    private static final String INVITES_ENDPOINT = "invites";
    private static final String MCO_ENDPOINT = "mco";
    private static final String SUBSCRIPTIONS_ENDPOINT = "subscriptions";
    private static final String ACTIVITIES_ENDPOINT = "activities";
    private static final String OPS_ENDPOINT = "ops";
    private static final String PING_STAT_ENDPOINT = "regions/ping/stat";
    private static final String PREFERRED_REGIONS_ENDPOINT = "regions/preferredRegions";
    private static final String TRIAL_ENDPOINT = "trial";
    private static final String NOTIFICATIONS_ENDPOINT = "notifications";
    private static final String FEATURE_ENDPOINT = "feature/v1";
    private static final String LIST_USER_WORLDS_OF_TYPE_ANY_ENDPOINT = "/listUserWorldsOfType/any";
    private static final String CREATE_PRERELEASE_REALM_ENDPOINT = "/$PARENT_WORLD_ID/createPrereleaseRealm";
    private static final String LIST_PRERELEASE_ELIGIBLE_WORLDS_ENDPOINT = "/listPrereleaseEligibleWorlds";
    private static final String WORLD_INITIALIZE_ENDPOINT = "/$WORLD_ID/initialize";
    private static final String LIVEPLAYERLIST_ENDPOINT = "/liveplayerlist";
    private static final String WORLD_ENDPOINT_2 = "/$WORLD_ID";
    private static final String WORLD_PROFILE_ENDPOINT = "/$WORLD_ID/$PROFILE_UUID";
    private static final String MINIGAMES_ENDPOINT = "/minigames/$MINIGAME_ID/$WORLD_ID";
    private static final String AVAILABLE_ENDPOINT = "/available";
    private static final String TEMPLATES_ENDPOINT = "/templates/$WORLD_TYPE";
    private static final String JOIN_PC_ENDPOINT = "/v1/$ID/join/pc";
    private static final String ID_ENDPOINT = "/$ID";
    private static final String WORLD_ENDPOINT_3 = "/$WORLD_ID";
    private static final String INVITE_ENDPOINT = "/$WORLD_ID/invite/$UUID";
    private static final String PENDING_ENDPOINT = "/pending";
    private static final String ACCEPT_INVITATION_ENDPOINT = "/accept/$INVITATION_ID";
    private static final String REJECT_INVITATION_ENDPOINT = "/reject/$INVITATION_ID";
    private static final String WORLD_ENDPOINT_4 = "/$WORLD_ID";
    private static final String WORLD_CONFIGURATION_ENDPOINT = "/$WORLD_ID/configuration";
    private static final String WORLD_SLOT_ENDPOINT = "/$WORLD_ID/slot/$SLOT_ID";
    private static final String WORLD_OPEN_ENDPOINT = "/$WORLD_ID/open";
    private static final String WORLD_CLOSE_ENDPOINT = "/$WORLD_ID/close";
    private static final String WORLD_RESET_ENDPOINT = "/$WORLD_ID/reset";
    private static final String WORLD_ENDPOINT_6 = "/$WORLD_ID";
    private static final String WORLD_BACKUPS_ENDPOINT = "/$WORLD_ID/backups";
    private static final String WORLD_SLOT_DOWNLOAD_ENDPOINT = "/$WORLD_ID/slot/$SLOT_ID/download";
    private static final String WORLD_BACKUPS_UPLOAD_ENDPOINT = "/$WORLD_ID/backups/upload";
    private static final String CLIENT_COMPATIBLE_ENDPOINT = "/client/compatible";
    private static final String TOS_AGREED_ENDPOINT = "/tos/agreed";
    private static final String NEWS_ENDPOINT = "/v1/news";
    private static final String SEEN_ENDPOINT = "/seen";
    private static final String DISMISS_ENDPOINT = "/dismiss";
    private static final CheckedGson JSON = new CheckedGson();

    public static RealmsClient create() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        return RealmsClient.createRealmsClient((MinecraftClient)minecraftClient);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static RealmsClient createRealmsClient(MinecraftClient client) {
        String string = client.getSession().getUsername();
        String string2 = client.getSession().getSessionId();
        RealmsClient realmsClient = instance;
        if (realmsClient != null) {
            return realmsClient;
        }
        Class<RealmsClient> clazz = RealmsClient.class;
        synchronized (RealmsClient.class) {
            RealmsClient realmsClient2 = instance;
            if (realmsClient2 != null) {
                // ** MonitorExit[var4_4] (shouldn't be in output)
                return realmsClient2;
            }
            instance = realmsClient2 = new RealmsClient(string2, string, client);
            // ** MonitorExit[var4_4] (shouldn't be in output)
            return realmsClient2;
        }
    }

    private RealmsClient(String sessionId, String username, MinecraftClient client) {
        this.sessionId = sessionId;
        this.username = username;
        this.client = client;
        RealmsClientConfig.setProxy((Proxy)client.getNetworkProxy());
        this.featureFlagsFuture = CompletableFuture.supplyAsync(() -> this.fetchFeatureFlags(), (Executor)Util.getDownloadWorkerExecutor());
    }

    public Set<String> getFeatureFlags() {
        return (Set)this.featureFlagsFuture.join();
    }

    private Set<String> fetchFeatureFlags() {
        if (MinecraftClient.getInstance().isOfflineDeveloperMode()) {
            return Set.of();
        }
        String string = RealmsClient.url((String)"feature/v1", null, (boolean)false);
        try {
            String string2 = this.execute(Request.get((String)string, (int)5000, (int)10000));
            JsonArray jsonArray = LenientJsonParser.parse((String)string2).getAsJsonArray();
            Set<String> set = jsonArray.asList().stream().map(JsonElement::getAsString).collect(Collectors.toSet());
            LOGGER.debug("Fetched Realms feature flags: {}", set);
            return set;
        }
        catch (RealmsServiceException realmsServiceException) {
            LOGGER.error("Failed to fetch Realms feature flags", (Throwable)realmsServiceException);
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse Realms feature flags", (Throwable)exception);
        }
        return Set.of();
    }

    public RealmsServerList listWorlds() throws RealmsServiceException {
        Object string = this.url("worlds");
        if (RealmsMainScreen.isSnapshotRealmsEligible()) {
            string = (String)string + "/listUserWorldsOfType/any";
        }
        String string2 = this.execute(Request.get((String)string));
        return RealmsServerList.parse((CheckedGson)JSON, (String)string2);
    }

    public List<RealmsServer> getPrereleaseEligibleServers() throws RealmsServiceException {
        String string = this.url("worlds/listPrereleaseEligibleWorlds");
        String string2 = this.execute(Request.get((String)string));
        return RealmsServerList.parse((CheckedGson)JSON, (String)string2).servers();
    }

    public RealmsServer createPrereleaseServer(Long parentWorldId) throws RealmsServiceException {
        String string = String.valueOf(parentWorldId);
        String string2 = this.url("worlds" + "/$PARENT_WORLD_ID/createPrereleaseRealm".replace("$PARENT_WORLD_ID", string));
        return RealmsServer.parse((CheckedGson)JSON, (String)this.execute(Request.post((String)string2, (String)string)));
    }

    public List<RealmsNotification> listNotifications() throws RealmsServiceException {
        String string = this.url("notifications");
        String string2 = this.execute(Request.get((String)string));
        return RealmsNotification.parse((String)string2);
    }

    private static JsonArray toJsonArray(List<UUID> uuids) {
        JsonArray jsonArray = new JsonArray();
        for (UUID uUID : uuids) {
            if (uUID == null) continue;
            jsonArray.add(uUID.toString());
        }
        return jsonArray;
    }

    public void markNotificationsAsSeen(List<UUID> notifications) throws RealmsServiceException {
        String string = this.url("notifications/seen");
        this.execute(Request.post((String)string, (String)JSON.toJson((JsonElement)RealmsClient.toJsonArray(notifications))));
    }

    public void dismissNotifications(List<UUID> notifications) throws RealmsServiceException {
        String string = this.url("notifications/dismiss");
        this.execute(Request.post((String)string, (String)JSON.toJson((JsonElement)RealmsClient.toJsonArray(notifications))));
    }

    public RealmsServer getOwnWorld(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get((String)string));
        return RealmsServer.parse((CheckedGson)JSON, (String)string2);
    }

    public RealmsRegionDataList getRegionDataList() throws RealmsServiceException {
        String string = this.url("regions/preferredRegions");
        String string2 = this.execute(Request.get((String)string));
        try {
            RealmsRegionDataList realmsRegionDataList = (RealmsRegionDataList)JSON.fromJson(string2, RealmsRegionDataList.class);
            if (realmsRegionDataList == null) {
                return RealmsRegionDataList.empty();
            }
            Set set = realmsRegionDataList.regionData().stream().map(RegionData::region).collect(Collectors.toSet());
            for (RealmsRegion realmsRegion : RealmsRegion.values()) {
                if (realmsRegion == RealmsRegion.INVALID_REGION || set.contains(realmsRegion)) continue;
                LOGGER.debug("No realms region matching {} in server response", (Object)realmsRegion);
            }
            return realmsRegionDataList;
        }
        catch (Exception exception) {
            LOGGER.error("Could not parse PreferredRegionSelections", (Throwable)exception);
            return RealmsRegionDataList.empty();
        }
    }

    public RealmsServerPlayerList getLiveStats() throws RealmsServiceException {
        String string = this.url("activities/liveplayerlist");
        String string2 = this.execute(Request.get((String)string));
        return RealmsServerPlayerList.parse((String)string2);
    }

    public RealmsServerAddress join(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + worldId));
        String string2 = this.execute(Request.get((String)string, (int)5000, (int)30000));
        return RealmsServerAddress.parse((CheckedGson)JSON, (String)string2);
    }

    public void initializeWorld(long worldId, String name, String motd) throws RealmsServiceException {
        RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
        String string = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = JSON.toJson((RealmsSerializable)realmsDescriptionDto);
        this.execute(Request.post((String)string, (String)string2, (int)5000, (int)10000));
    }

    public boolean mcoEnabled() throws RealmsServiceException {
        String string = this.url("mco/available");
        String string2 = this.execute(Request.get((String)string));
        return Boolean.parseBoolean(string2);
    }

    public CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
        CompatibleVersionResponse compatibleVersionResponse;
        String string = this.url("mco/client/compatible");
        String string2 = this.execute(Request.get((String)string));
        try {
            compatibleVersionResponse = CompatibleVersionResponse.valueOf((String)string2);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new RealmsServiceException((RealmsError)RealmsError.SimpleHttpError.unknownCompatibility((String)string2));
        }
        return compatibleVersionResponse;
    }

    public void uninvite(long worldId, UUID profileUuid) throws RealmsServiceException {
        String string = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$UUID", UndashedUuid.toString((UUID)profileUuid)));
        this.execute(Request.delete((String)string));
    }

    public void uninviteMyselfFrom(long worldId) throws RealmsServiceException {
        String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.delete((String)string));
    }

    public List<PlayerInfo> invite(long worldId, String profileName) throws RealmsServiceException {
        SentInvite sentInvite = new SentInvite();
        sentInvite.profileName = profileName;
        String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.post((String)string, (String)JSON.toJson((RealmsSerializable)sentInvite)));
        return RealmsServer.parse((CheckedGson)RealmsClient.JSON, (String)string2).players;
    }

    public BackupList backupsFor(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get((String)string));
        return BackupList.parse((String)string2);
    }

    public void configure(long worldId, String name, String description, @Nullable RealmsRegionSelectionPreference regionSelectionPreference, int slotId, RealmsWorldOptions options, List<RealmsSettingDto> settings) throws RealmsServiceException {
        RealmsRegionSelectionPreference realmsRegionSelectionPreference = regionSelectionPreference != null ? regionSelectionPreference : new RealmsRegionSelectionPreference(RegionSelectionMethod.DEFAULT, null);
        RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, description);
        RealmsOptionsDto realmsOptionsDto = new RealmsOptionsDto(slotId, options, RealmsSettingDto.isHardcore(settings));
        RealmsConfigurationDto realmsConfigurationDto = new RealmsConfigurationDto(realmsOptionsDto, settings, realmsRegionSelectionPreference, realmsDescriptionDto);
        String string = this.url("worlds" + "/$WORLD_ID/configuration".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.post((String)string, (String)JSON.toJson((RealmsSerializable)realmsConfigurationDto)));
    }

    public void updateSlot(long worldId, int slot, RealmsWorldOptions options, List<RealmsSettingDto> settings) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
        String string2 = JSON.toJson((RealmsSerializable)new RealmsOptionsDto(slot, options, RealmsSettingDto.isHardcore(settings)));
        this.execute(Request.post((String)string, (String)string2));
    }

    public boolean switchSlot(long worldId, int slot) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
        String string2 = this.execute(Request.put((String)string, (String)""));
        return Boolean.valueOf(string2);
    }

    public void restoreWorld(long worldId, String backupId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)), "backupId=" + backupId);
        this.execute(Request.put((String)string, (String)"", (int)40000, (int)600000));
    }

    public WorldTemplatePaginatedList fetchWorldTemplates(int page, int pageSize, RealmsServer.WorldType type) throws RealmsServiceException {
        String string = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", type.toString()), String.format(Locale.ROOT, "page=%d&pageSize=%d", page, pageSize));
        String string2 = this.execute(Request.get((String)string));
        return WorldTemplatePaginatedList.parse((String)string2);
    }

    public Boolean putIntoMinigameMode(long worldId, String minigameId) throws RealmsServiceException {
        String string = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", minigameId).replace("$WORLD_ID", String.valueOf(worldId));
        String string2 = this.url("worlds" + string);
        return Boolean.valueOf(this.execute(Request.put((String)string2, (String)"")));
    }

    public Ops op(long worldId, UUID profileUuid) throws RealmsServiceException {
        String string = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", UndashedUuid.toString((UUID)profileUuid));
        String string2 = this.url("ops" + string);
        return Ops.parse((String)this.execute(Request.post((String)string2, (String)"")));
    }

    public Ops deop(long worldId, UUID profileUuid) throws RealmsServiceException {
        String string = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", UndashedUuid.toString((UUID)profileUuid));
        String string2 = this.url("ops" + string);
        return Ops.parse((String)this.execute(Request.delete((String)string2)));
    }

    public Boolean open(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.put((String)string, (String)""));
        return Boolean.valueOf(string2);
    }

    public Boolean close(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.put((String)string, (String)""));
        return Boolean.valueOf(string2);
    }

    public Boolean resetWorldWithTemplate(long worldId, String worldTemplateId) throws RealmsServiceException {
        RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(null, Long.valueOf(worldTemplateId).longValue(), -1, false, Set.of());
        String string = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.post((String)string, (String)JSON.toJson((RealmsSerializable)realmsWorldResetDto), (int)30000, (int)80000));
        return Boolean.valueOf(string2);
    }

    public Subscription subscriptionFor(long worldId) throws RealmsServiceException {
        String string = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        String string2 = this.execute(Request.get((String)string));
        return Subscription.parse((String)string2);
    }

    public int pendingInvitesCount() throws RealmsServiceException {
        return this.pendingInvites().pendingInvites().size();
    }

    public PendingInvitesList pendingInvites() throws RealmsServiceException {
        String string = this.url("invites/pending");
        String string2 = this.execute(Request.get((String)string));
        PendingInvitesList pendingInvitesList = PendingInvitesList.parse((String)string2);
        pendingInvitesList.pendingInvites().removeIf(arg_0 -> this.isOwnerBlocked(arg_0));
        return pendingInvitesList;
    }

    private boolean isOwnerBlocked(PendingInvite invite) {
        return this.client.getSocialInteractionsManager().isPlayerBlocked(invite.worldOwnerUuid());
    }

    public void acceptInvitation(String invitationId) throws RealmsServiceException {
        String string = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
        this.execute(Request.put((String)string, (String)""));
    }

    public WorldDownload download(long worldId, int slotId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slotId)));
        String string2 = this.execute(Request.get((String)string));
        return WorldDownload.parse((String)string2);
    }

    public @Nullable UploadInfo upload(long worldId) throws RealmsServiceException {
        String string2;
        String string = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(worldId)));
        UploadInfo uploadInfo = UploadInfo.parse((String)this.execute(Request.put((String)string, (String)UploadInfo.createRequestContent((String)(string2 = UploadTokenCache.get((long)worldId))))));
        if (uploadInfo != null) {
            UploadTokenCache.put((long)worldId, (String)uploadInfo.token());
        }
        return uploadInfo;
    }

    public void rejectInvitation(String invitationId) throws RealmsServiceException {
        String string = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
        this.execute(Request.put((String)string, (String)""));
    }

    public void agreeToTos() throws RealmsServiceException {
        String string = this.url("mco/tos/agreed");
        this.execute(Request.post((String)string, (String)""));
    }

    public RealmsNews getNews() throws RealmsServiceException {
        String string = this.url("mco/v1/news");
        String string2 = this.execute(Request.get((String)string, (int)5000, (int)10000));
        return RealmsNews.parse((String)string2);
    }

    public void sendPingResults(PingResult pingResult) throws RealmsServiceException {
        String string = this.url("regions/ping/stat");
        this.execute(Request.post((String)string, (String)JSON.toJson((RealmsSerializable)pingResult)));
    }

    public Boolean trialAvailable() throws RealmsServiceException {
        String string = this.url("trial");
        String string2 = this.execute(Request.get((String)string));
        return Boolean.valueOf(string2);
    }

    public void deleteWorld(long worldId) throws RealmsServiceException {
        String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
        this.execute(Request.delete((String)string));
    }

    private String url(String path) throws RealmsServiceException {
        return this.url(path, null);
    }

    private String url(String path, @Nullable String queryString) {
        return RealmsClient.url((String)path, (String)queryString, (boolean)this.getFeatureFlags().contains("realms_in_aks"));
    }

    private static String url(String path, @Nullable String queryString, boolean aks) {
        try {
            return new URI(RealmsClient.ENVIRONMENT.protocol, aks ? RealmsClient.ENVIRONMENT.aksUrl : RealmsClient.ENVIRONMENT.baseUrl, "/" + path, queryString, null).toASCIIString();
        }
        catch (URISyntaxException uRISyntaxException) {
            throw new IllegalArgumentException(path, uRISyntaxException);
        }
    }

    private String execute(Request<?> r) throws RealmsServiceException {
        r.cookie("sid", this.sessionId);
        r.cookie("user", this.username);
        r.cookie("version", SharedConstants.getGameVersion().name());
        r.prerelease(RealmsMainScreen.isSnapshotRealmsEligible());
        try {
            int i = r.responseCode();
            if (i == 503 || i == 277) {
                int j = r.getRetryAfterHeader();
                throw new RetryCallException(j, i);
            }
            String string = r.text();
            if (i < 200 || i >= 300) {
                if (i == 401) {
                    String string2 = r.getHeader("WWW-Authenticate");
                    LOGGER.info("Could not authorize you against Realms server: {}", (Object)string2);
                    throw new RealmsServiceException((RealmsError)new RealmsError.AuthenticationError(string2));
                }
                String string2 = r.connection.getContentType();
                if (string2 != null && string2.startsWith("text/html")) {
                    throw new RealmsServiceException((RealmsError)RealmsError.SimpleHttpError.unreadableHtmlBody((int)i, (String)string));
                }
                RealmsError realmsError = RealmsError.ofHttp((int)i, (String)string);
                throw new RealmsServiceException(realmsError);
            }
            return string;
        }
        catch (RealmsHttpException realmsHttpException) {
            throw new RealmsServiceException((RealmsError)RealmsError.SimpleHttpError.connectivity((RealmsHttpException)realmsHttpException));
        }
    }
}

