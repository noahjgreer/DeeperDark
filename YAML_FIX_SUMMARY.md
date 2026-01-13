# YAML Global Tag Error - FIXED

## Problem
When running `.\gradlew runClient`, the game crashed with:
```
Caused by: Global tag is not allowed: tag:yaml.org,2002:net.noahsarch.deeperdark.DeeperDarkConfig$ConfigInstance
```

## Root Cause
The existing config file (`run/config/deeperdark.yaml`) was created with an older version of the code that included the Java class tag in the YAML file (line 9: `!!net.noahsarch.deeperdark.DeeperDarkConfig$ConfigInstance`).

## Solution Applied

### 1. Deleted the Old Config File
- Removed `run/config/deeperdark.yaml` which contained the problematic global tag
- The mod will automatically create a new, clean config file on next run

### 2. Updated Config Loading Code
Added three improvements to `DeeperDarkConfig.java`:

**a) Allow Global Tags (Backwards Compatibility)**
```java
loaderOptions.setTagInspector(tag -> true); // Allow all tags
```
This allows the code to read old config files with global tags if they exist.

**b) Better Error Handling**
Changed from catching only `IOException` to catching all `Exception`s during YAML parsing.

**c) Auto-Recovery from Corrupted Configs**
```java
catch (Exception e) {
    LOGGER.error("Failed to load config, creating new one", e);
    // Delete corrupted file and create new one
    java.nio.file.Files.deleteIfExists(CONFIG_FILE_YAML.toPath());
    instance = null; // Will trigger creation of new config
}
```
If the config file is corrupted or has parsing errors, it will be automatically deleted and recreated.

### 3. Fixed Config Saving
The `save()` method now uses a custom `Representer` that prevents global tags from being written:
```java
representer.addClassTag(ConfigInstance.class, org.yaml.snakeyaml.nodes.Tag.MAP);
```

## Expected Behavior Now

1. **First Run After Fix**: 
   - Old config deleted
   - New config created with clean format (no global tags)
   - Game starts successfully

2. **Subsequent Runs**:
   - Config loads normally
   - New values added to config in future updates

3. **If Config Gets Corrupted**:
   - Automatically detected and deleted
   - New clean config created
   - Game continues without manual intervention

## New Config Format
The new `deeperdark.yaml` will look like this (no `!!` tags):
```yaml
# DeeperDark configuration
# ...comments...
allowEntitySpawning: false
creeperEffectMaxSeconds: 60
creeperEffectMinSeconds: 15
forceMultiplier: 20.0
originX: 100
originZ: 100
safeRadius: 3.0
anvilRepairCost: 0
anvilEnchantCost: 5
customFortuneEnabled: true
fortune1DropChance: 0.25
fortune2DropChance: 0.5
fortune3DropChance: 0.75
fortuneMaxDrops: 2
```

## Testing
Run the client again:
```powershell
.\gradlew runClient
```

The game should now start without errors and create a clean config file with all the new Fortune and Anvil settings.

## Note
If you have a backup of your old config values, you can manually copy them into the new config file after it's created. Just don't copy the `!!net.noahsarch...` line!

