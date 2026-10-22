# Decompiled sources

`ref-decompiled/Minecraft/` holds deobfuscated Minecraft sources: `26.1.2` and `26.3`
(both built, see `MultiVersion.md`), plus `1.21.1`, `1.8` and `1.3.1` for porting old
behaviour. `ref-decompiled/Mods/` holds other mods' sources.

## How 26.x versions are built

From 26.1 on, Mojang ships the client jar unobfuscated and publishes no mappings, so no remapping
step is needed:

1. Download `client.jar` from the version's JSON in Mojang's
   `piston-meta.mojang.com/mc/game/version_manifest_v2.json` and check its sha1.
2. Decompile with Vineflower (`java -jar vineflower.jar -dgs=1 client.jar out/`; 1.12.0 for 26.3).
3. Lay out as `<version>/src/` (`com/`, `net/`, `META-INF/MANIFEST.MF`) and
   `<version>/assets/` (the jar's `assets/` and `data/`), which keeps vanilla JSON next to the code.

## How 1.8 was built

Mojang published no obfuscation maps before 1.14.4, so `1.8` was produced locally (see its
`README.md`):

1. Remapped with SpecialSource + `de.oceanlabs.mcp:mcp:1.8:srg`.
2. Decompiled with Vineflower.
3. Searge member names (`func_*`/`field_*`) replaced from `de.oceanlabs.mcp:mcp_stable:18-1.8`.

The result uses historical MCP names (`EntityPlayer`, `ItemSword`, `setItemInUse`), so 1.8-era
modding references apply directly. McDeob is NOT useful before 1.14.4: it only supplies Searge
names there, leaving methods as `func_70664_aZ`.
