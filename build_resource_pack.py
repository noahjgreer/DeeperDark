#!/usr/bin/env python3
"""
Resource Pack Builder for DeeperDark Mod
=========================================

This script:
1. Collects assets, pack.mcmeta, and pack.png from the resources folder
2. Excludes non-game files (.wav, .aup, etc.)
3. Creates a zip file named dd_rp-[version].zip
4. Moves the zip to F:\MuffinMode\
5. Computes SHA-1 hash of the zip
6. Updates server.properties with new resource pack URL, UUID, and SHA-1

Usage:
    python build_resource_pack.py [version]

    If version is not provided, it will be read from fabric.mod.json
"""

import os
import sys
import json
import zipfile
import hashlib
import uuid
import shutil
import re
from pathlib import Path

# Configuration
RESOURCES_DIR = Path(__file__).parent / "src" / "main" / "resources"
OUTPUT_DIR = Path("F:/MuffinMode")
SERVER_PROPERTIES_PATH = Path("F:/MuffinMode-LiamServer-Fabric/server.properties")
RESOURCE_PACK_URL_BASE = "https://muffinmode.net/"

# File extensions to exclude from the resource pack
EXCLUDED_EXTENSIONS = {
    # Audio source files
    '.wav', '.aup', '.aup3', '.aup3-shm', '.aup3-wal', '.mp3', '.flac', '.au', '.aiff', '.wma', '.m4a',
    # Image source files
    '.psd', '.xcf', '.ai', '.svg', '.raw', '.ase', '.aseprite',
    # 3D model source files
    '.bbmodel', '.blend', '.blend1', '.fbx', '.obj', '.mtl',
    # Temporary files
    '.bak', '.tmp', '.temp', '.swp', '.swo',
    # Script files
    '.py', '.ps1', '.sh', '.bat', '.cmd',
    # Documentation (except specific ones we need)
    '.md', '.txt', '.log',
    # Git files
    '.gitignore', '.gitattributes',
    # IDE files
    '.idea', '.vscode',
}

# Files/folders to explicitly include from resources root
ROOT_INCLUDES = {'assets', 'pack.mcmeta', 'pack.png'}

# Files to always exclude by name
EXCLUDED_FILES = {
    'Thumbs.db', '.DS_Store', 'desktop.ini',
}


def get_mod_version() -> str:
    """Read version from gradle.properties"""
    gradle_properties = Path(__file__).parent / "gradle.properties"
    if gradle_properties.exists():
        with open(gradle_properties, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if line.startswith('mod_version='):
                    return line.split('=', 1)[1].strip()

    # Fallback to fabric.mod.json if gradle.properties doesn't have version
    fabric_mod_json = RESOURCES_DIR / "fabric.mod.json"
    if fabric_mod_json.exists():
        with open(fabric_mod_json, 'r', encoding='utf-8') as f:
            data = json.load(f)
            version = data.get('version', '1.0.0')
            # Skip if it's a placeholder
            if not version.startswith('${'):
                return version

    return '1.0.0'


def should_include_file(file_path: Path) -> bool:
    """Check if a file should be included in the resource pack"""
    # Check file name exclusions
    if file_path.name in EXCLUDED_FILES:
        return False

    # Check extension exclusions (including compound extensions like .aup3-shm)
    file_lower = file_path.name.lower()
    for ext in EXCLUDED_EXTENSIONS:
        if file_lower.endswith(ext):
            return False

    # Also check the standard suffix
    if file_path.suffix.lower() in EXCLUDED_EXTENSIONS:
        return False

    # Include everything else
    return True


def collect_files(source_dir: Path) -> list:
    """
    Collect all files that should be included in the resource pack.
    Returns list of (source_path, archive_path) tuples.
    """
    files = []

    # Handle root level files (pack.mcmeta, pack.png)
    for item in ROOT_INCLUDES:
        item_path = source_dir / item
        if item_path.is_file():
            if should_include_file(item_path):
                files.append((item_path, item))
        elif item_path.is_dir():
            # Walk the directory
            for root, dirs, filenames in os.walk(item_path):
                root_path = Path(root)
                for filename in filenames:
                    file_path = root_path / filename
                    if should_include_file(file_path):
                        # Calculate relative path for archive
                        archive_path = file_path.relative_to(source_dir)
                        files.append((file_path, str(archive_path)))

    return files


def create_zip(files: list, output_path: Path) -> None:
    """Create a zip file from the collected files"""
    print(f"Creating zip file: {output_path}")

    with zipfile.ZipFile(output_path, 'w', zipfile.ZIP_DEFLATED) as zf:
        for source_path, archive_path in files:
            print(f"  Adding: {archive_path}")
            zf.write(source_path, archive_path)

    print(f"Created zip with {len(files)} files")


def compute_sha1(file_path: Path) -> str:
    """Compute SHA-1 hash of a file"""
    sha1 = hashlib.sha1()
    with open(file_path, 'rb') as f:
        while chunk := f.read(8192):
            sha1.update(chunk)
    return sha1.hexdigest().upper()


def generate_uuid() -> str:
    """Generate a new UUID for the resource pack"""
    return str(uuid.uuid4())


def update_server_properties(properties_path: Path, zip_name: str, sha1_hash: str, pack_uuid: str) -> None:
    """Update server.properties with new resource pack info"""
    if not properties_path.exists():
        print(f"Warning: server.properties not found at {properties_path}")
        return

    # Read the file
    with open(properties_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # The URL needs to have colons escaped
    escaped_url = f"{RESOURCE_PACK_URL_BASE}{zip_name}".replace(":", "\\:")

    # Update resource-pack line
    content = re.sub(
        r'^resource-pack=.*$',
        f'resource-pack={escaped_url}',
        content,
        flags=re.MULTILINE
    )

    # Update resource-pack-id line
    content = re.sub(
        r'^resource-pack-id=.*$',
        f'resource-pack-id={pack_uuid}',
        content,
        flags=re.MULTILINE
    )

    # Update resource-pack-sha1 line
    content = re.sub(
        r'^resource-pack-sha1=.*$',
        f'resource-pack-sha1={sha1_hash}',
        content,
        flags=re.MULTILINE
    )

    # Write the file
    with open(properties_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Updated server.properties:")
    print(f"  resource-pack: {RESOURCE_PACK_URL_BASE}{zip_name}")
    print(f"  resource-pack-id: {pack_uuid}")
    print(f"  resource-pack-sha1: {sha1_hash}")


def main():
    # Handle help argument
    if len(sys.argv) > 1 and sys.argv[1] in ('-h', '--help', '/?'):
        print(__doc__)
        sys.exit(0)

    # Get version
    if len(sys.argv) > 1:
        version = sys.argv[1]
    else:
        version = get_mod_version()

    print(f"Building resource pack for version: {version}")
    print(f"Source directory: {RESOURCES_DIR}")

    # Check source directory exists
    if not RESOURCES_DIR.exists():
        print(f"Error: Resources directory not found: {RESOURCES_DIR}")
        sys.exit(1)

    # Collect files
    print("\nCollecting files...")
    files = collect_files(RESOURCES_DIR)

    if not files:
        print("Error: No files found to include in resource pack")
        sys.exit(1)

    print(f"Found {len(files)} files to include")

    # Create output directory if needed
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)

    # Create zip filename
    zip_name = f"dd_rp-{version}.zip"
    temp_zip_path = Path(__file__).parent / zip_name
    final_zip_path = OUTPUT_DIR / zip_name

    # Create the zip file
    create_zip(files, temp_zip_path)

    # Move to output directory
    print(f"\nMoving zip to: {final_zip_path}")
    if final_zip_path.exists():
        final_zip_path.unlink()  # Remove existing file
    shutil.move(str(temp_zip_path), str(final_zip_path))

    # Compute SHA-1
    print("\nComputing SHA-1 hash...")
    sha1_hash = compute_sha1(final_zip_path)
    print(f"SHA-1: {sha1_hash}")

    # Generate UUID
    pack_uuid = generate_uuid()
    print(f"Generated UUID: {pack_uuid}")

    # Update server.properties
    print(f"\nUpdating server.properties at: {SERVER_PROPERTIES_PATH}")
    update_server_properties(SERVER_PROPERTIES_PATH, zip_name, sha1_hash, pack_uuid)

    print("\n" + "=" * 50)
    print("Resource pack build complete!")
    print(f"  File: {final_zip_path}")
    print(f"  Size: {final_zip_path.stat().st_size:,} bytes")
    print(f"  SHA-1: {sha1_hash}")
    print(f"  UUID: {pack_uuid}")
    print("=" * 50)


if __name__ == "__main__":
    main()
