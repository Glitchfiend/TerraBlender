<p align="center"><img src="https://i.imgur.com/8VBgnKN.png"></p>

<p align="center"><img src="https://i.imgur.com/CYxKg5M.png"></p>

<p align="center">https://discord.gg/GyyzU6T</p>

**TerraBlender** is a **library mod** for adding biomes in a simple and compatible manner with Minecraft's new biome/terrain system.

-----------------

### Using TerraBlender

To configure your ``build.gradle`` to use **TerraBlender** you should:

1. Ensure you have the Forge Maven in your `buildscript -> repositories` block:

```groovy
buildscript {
    repositories {
        maven { url = 'https://maven.minecraftforge.net/' }
    }
    dependencies {
        ...
    }
}
```

2. Add **TerraBlender** to your dependencies block, ensuring you replace ``x.x.x`` with your **Minecraft version** and ``y.y.y.y`` with your mod version as appropriate:

**Forge**
```groovy
dependencies {
    implementation fg.deobf('com.github.glitchfiend:TerraBlender-forge:x.x.x-y.y.y.y')
}
```

**Fabric**
```groovy
dependencies {
    modImplementation 'com.github.glitchfiend:TerraBlender-fabric:x.x.x-y.y.y.y'
}
```

-----------------

 [<img src="https://licensebuttons.net/l/by-nc-nd/4.0/88x31.png">](http://creativecommons.org/licenses/by-nc-nd/4.0/deed.en_US)

TerraBlender is licensed under a [Creative Commons Attribution-NonCommercial-NoDerivs 4.0 Unported License](http://creativecommons.org/licenses/by-nc-nd/4.0/deed.en_US).