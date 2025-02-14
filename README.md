# OkDownload

原[OkDownload](https://github.com/lingochamp/okdownload)库已多年不维护，
主要改动如下：

- 依赖库升级到Androidx
- Gradle插件升级到8.x.x（Android Studio Koala | 2024.1.1 Patch 2）
- groovy切换到kts，并添加了Catalogs的支持
- 在`RemitSyncExecutor`类中，在读写sql的时候catch`SQLiteFullException`
- 在`DownloadDispatcher`类中，设置`maxParallelRunningCount`的默认值为1

后续改动会在issue里提问并做相应的回复

### Maven 上传

阿里云效`maven`上传支持：

- 在`buildSrc/main/kotlin/Config.kt`里替换你的releaseUrl和snapshotUrl
- 在`local.properties`里替换你的mavenUsername和mavenPassword

类似以下这种：

```
USER_NAME=XXX
PASS_WORD=XXX
```

然后在`gradle`面板的相应模块下找到`publishReleasePublicationToMavenRepository`这个Task，双击即可