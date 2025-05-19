# Mystical Customization

<p align="left">
    <a href="https://blakesmods.com/mystical-automation" alt="Downloads">
        <img src="https://img.shields.io/endpoint?url=https://api.blakesmods.com/v2/badges/mysticalautomation/downloads&style=for-the-badge" />
    </a>
    <a href="https://blakesmods.com/mystical-automation" alt="Latest Version">
        <img src="https://img.shields.io/endpoint?url=https://api.blakesmods.com/v2/badges/mysticalautomation/version&style=for-the-badge" />
    </a>
    <a href="https://blakesmods.com/mystical-automation" alt="Minecraft Version">
        <img src="https://img.shields.io/endpoint?url=https://api.blakesmods.com/v2/badges/mysticalautomation/mc_version&style=for-the-badge" />
    </a>
    <a href="https://blakesmods.com/docs/mysticalautomation" alt="Docs">
        <img src="https://img.shields.io/static/v1?label=docs&message=view&color=brightgreen&style=for-the-badge" />
    </a>
</p>

Allows modpack creators to add new content and modify existing content in Mystical Agriculture.

## Download

The official release builds can be downloaded from the following websites.

- [Blake's Mods](https://blakesmods.com/mystical-automation/download)
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mystical-automation)
- [Modrinth](https://modrinth.com/mod/mystical-automation)

## Development

To use this mod in a development environment, you will need to add the following to your `build.gradle`.

```groovy
repositories {
    maven {
        url 'https://maven.blakesmods.com'
    }
}

dependencies {
    implementation 'com.blakebr0.cucumber:Cucumber:<minecraft_version>-<mod_version>'
    implementation 'com.blakebr0.mysticalautomation:MysticalAutomation:<minecraft_version>-<mod_version>'
}
```

## License

[MIT License](./LICENSE)
