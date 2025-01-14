package model

data class MavenPublish(
    val artifactId: String,
    val version: String, //X.Y.Z; X = Major, Y = minor, Z = Patch (endsWith('-SNAPSHOT') ? "Debug" : "Release")
    val productFlavor:String = "release",
    val groupId: String = "com.yunqinglai.okdownload"
)