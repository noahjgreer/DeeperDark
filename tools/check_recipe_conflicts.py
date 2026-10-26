"""Report crafting/cooking recipes that the same input could match, per version.
Usage: python tools/check_recipe_conflicts.py <ver>=<fabric-convention-tags-v2 jar> ...
Each version's jar is in ~/.gradle/caches; its build is listed in that version's server log."""
import json, os, sys, zipfile, itertools

REPO = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JARS = dict(a.split("=", 1) for a in sys.argv[1:])
VERS = ["26.1.2", "26.3"]

def load_dir(base, ns_root, out, sub):
    """Load data/<ns>/<sub>/**.json under base into out[ns:path]."""
    if not os.path.isdir(base):
        return
    for ns in os.listdir(base):
        d = f"{base}/{ns}/{sub}"
        for root, _, files in os.walk(d):
            for f in files:
                if f.endswith(".json"):
                    rel = os.path.relpath(f"{root}/{f}", d).replace("\\", "/")[:-5]
                    out.setdefault(f"{ns}:{rel}", []).append(json.load(open(f"{root}/{f}")))

def check(v):
    ref = f"{REPO}/ref-decompiled/Minecraft/{v}/assets/data"
    mod = [f"{REPO}/src/main/resources/data", f"{REPO}/src/main/resources-{v}/data"]
    recipes, mod_ids = {}, set()
    vr = {}; load_dir(ref, None, vr, "recipe")
    for k, ds in vr.items(): recipes[k] = ds[-1]
    for m in mod:
        mr = {}; load_dir(m, None, mr, "recipe")
        for k, ds in mr.items():
            recipes[k] = ds[-1]; mod_ids.add(k)

    tags = {}
    load_dir(ref, None, tags, "tags/item")
    with zipfile.ZipFile(JARS[v]) as z:
        for n in z.namelist():
            if n.startswith("data/c/tags/item/") and n.endswith(".json"):
                tags.setdefault("c:" + n[len("data/c/tags/item/"):-5], []).append(json.loads(z.read(n)))
    for m in mod:
        load_dir(m, None, tags, "tags/item")
    items = {"minecraft:" + f[:-5] for f in os.listdir(f"{REPO}/ref-decompiled/Minecraft/{v}/assets/assets/minecraft/items")}
    for d in (f"{REPO}/src/main/resources/assets/deeperdark/items", f"{REPO}/src/main/resources-{v}/assets/deeperdark/items"):
        if os.path.isdir(d):
            items |= {"deeperdark:" + f[:-5] for f in os.listdir(d)}

    cache = {}
    def tag(t):
        if t in cache: return cache[t]
        cache[t] = set()
        if t not in tags:
            raise SystemExit(f"{v}: unknown tag #{t}")
        s = set()
        for d in tags[t]:  # load order: vanilla, Fabric, mod
            if d.get("replace"):
                s = set()
            for e in d["values"]:
                req = True
                if isinstance(e, dict): e, req = e["id"], e.get("required", True)
                if e.startswith("#"):
                    if e[1:] in tags: s |= tag(e[1:])
                    elif req: raise SystemExit(f"{v}: #{t} needs missing #{e}")
                elif e in items: s.add(e)
                elif req: raise SystemExit(f"{v}: #{t} needs missing {e}")
        cache[t] = s
        return s

    def ing(x, rid):
        if isinstance(x, list):
            return set().union(*(ing(i, rid) for i in x))
        if isinstance(x, dict):
            return {f"<custom {rid}>"}
        if x.startswith("#"):
            return tag(x[1:])
        x = x if ":" in x else "minecraft:" + x
        if x not in items:
            raise SystemExit(f"{v}: {rid} uses missing item {x}")
        return {x}

    shaped, shapeless, cooking = [], [], []
    for rid, d in recipes.items():
        t = d["type"]
        try:
            if t == "minecraft:crafting_shaped":
                rows = d["pattern"]
                while rows and not rows[0].strip(): rows = rows[1:]
                while rows and not rows[-1].strip(): rows = rows[:-1]
                lo = min(len(r) - len(r.lstrip()) for r in rows); hi = max(len(r.rstrip()) for r in rows)
                rows = [r.ljust(hi)[lo:hi] for r in rows]
                keys = {c: ing(val, rid) for c, val in d["key"].items()}
                grid = [[None if c == " " else keys[c] for c in r] for r in rows]
                shaped.append((rid, grid))
            elif t == "minecraft:crafting_shapeless":
                shapeless.append((rid, [ing(i, rid) for i in d["ingredients"]]))
            elif t == "minecraft:crafting_transmute":
                shapeless.append((rid, [ing(d["input"], rid), ing(d["material"], rid)]))
            elif t in ("minecraft:smelting", "minecraft:blasting", "minecraft:smoking", "minecraft:campfire_cooking"):
                cooking.append((rid, t, ing(d["ingredient"], rid)))
        except KeyError as e:
            raise SystemExit(f"{v}: {rid} bad recipe {e}")
        if rid in mod_ids and "result" in d:
            r = d["result"]; r = r if isinstance(r, str) else r["id"]
            ing(r, rid)

    def matching(a, b):
        if len(a) != len(b): return False
        match = {}
        def aug(i, seen):
            for j in range(len(b)):
                if j not in seen and a[i] & b[j]:
                    seen.add(j)
                    if j not in match or aug(match[j], seen):
                        match[j] = i; return True
            return False
        return all(aug(i, set()) for i in range(len(a)))

    def cells(g): return [c for r in g for c in r if c is not None]
    def same_shape(a, b):
        if len(a) != len(b) or len(a[0]) != len(b[0]): return False
        for m in (False, True):
            if all(((x is None) == (y is None)) and (x is None or x & y)
                   for ra, rb in zip(a, b) for x, y in zip(ra[::-1] if m else ra, rb)):
                return True
        return False

    found = []
    for (i, a), (j, b) in itertools.combinations(shaped, 2):
        if (i in mod_ids or j in mod_ids) and same_shape(a, b): found.append((i, j))
    for (i, a), (j, b) in itertools.product(shaped, shapeless):
        if (i in mod_ids or j in mod_ids) and matching(cells(a), b): found.append((i, j))
    for (i, a), (j, b) in itertools.combinations(shapeless, 2):
        if (i in mod_ids or j in mod_ids) and matching(a, b): found.append((i, j))
    for (i, t, a), (j, u, b) in itertools.combinations(cooking, 2):
        if t == u and (i in mod_ids or j in mod_ids) and a & b: found.append((i, j))
    print(f"== {v}: {len(recipes)} recipes, {len(mod_ids)} from mod, {len(found)} conflicts")
    for i, j in sorted(found):
        print("  ", i, "<->", j)

for v in VERS:
    check(v)
