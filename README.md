# 供第三方使用，示例如下

```
    11
```
## 导入方式
你的build.gradle要有jitpack.io，大致如下
```
allprojects {
    repositories {
        jcenter()
        maven {
            url "https://maven.google.com"
        }
        maven {
            url "https://jitpack.io"
        }
    }
}
```
然后导入
`implementation（或api） 'com.github.weimingjue:'`
