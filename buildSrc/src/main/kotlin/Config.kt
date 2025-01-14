import model.Credential
import model.Maven
import model.MavenPublish
import utils.PropertiesReadUtil
import java.net.URI

object Config {

    // 阿里Maven的repositories配置
    val MAVEN = Maven(
        releaseUrl = URI.create("https://packages.aliyun.com/maven/repository/2087120-release-unRi7v/"),
        snapshotUrl = URI.create("https://packages.aliyun.com/maven/repository/2087120-snapshot-n8shdu/"),
        credentials = Credential(
            // 修改project.rootDir下的local.properties要重启AS才能生效
            PropertiesReadUtil.readProperties("local.properties", "USER_NAME"),
            PropertiesReadUtil.readProperties("local.properties", "PASS_WORD")
        )
    )

    // 根据settings.gradle.ktx里的module来确定（除了app模块）
    val MODULES_MAVEN_CONFIG = mapOf(
        "okdownload" to MavenPublish(artifactId = "okdownload", version = MavenPublishVersions.OKDOWNLOAD),
        "sqlite" to MavenPublish(artifactId = "sqlite", version = MavenPublishVersions.SQLITE),
        "okhttp" to MavenPublish(artifactId = "okhttp", version = MavenPublishVersions.OKHTTP),
    )
}

object MavenPublishVersions {
    const val OKDOWNLOAD = "1.0.9-SNAPSHOT"
    const val SQLITE = "1.0.9-SNAPSHOT"
    const val OKHTTP = "1.0.9-SNAPSHOT"
}