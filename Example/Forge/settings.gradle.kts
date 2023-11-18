pluginManagement.repositories {
    gradlePluginPortal()
    maven {
        name = "Forge"
        url = uri("https://maven.minecraftforge.net")
    }
    maven {
        name = "Sponge Snapshots"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
}

rootProject.name = "Example"
