pluginManagement.repositories {
    gradlePluginPortal()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "NeoForge"
        url = uri("https://maven.neoforged.net/releases")
    }
    maven {
        name = "Forge"
        url = uri("https://maven.minecraftforge.net")
    }
    maven {
        name = "Sponge Snapshots"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

rootProject.name = "TerraBlender"
include("Common", "Fabric", "Forge")