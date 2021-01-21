# Spring-boot-starter Demo

这是一个demo项目。此项目在本地经测试成功后打包。

你需要修改 `resource/application.yml` 文件中的 `simbot.bots` 配置为自己要登陆的QQ账号密码信息。

demo项目只做最简单的项目实例，且不会对各项功能做介绍。详细内容需要参阅文档：https://www.yuque.com/simpler-robot/simpler-robot-doc


## 账号问题

如果是首次登录、异地登录、新号/不用的旧号登录，首先要从本地电脑登录并挂机一段时间，以让账号熟悉当前网络环境，
然后从本地电脑使用代码登录并挂机一段时间，以让账号熟悉mirai设备信息，然后再考虑部署到服务器。

## 依赖问题

如果出现类似于 `Cannot resolve love.forte.simple-robot:xxx:xxx.xxx.xxx`等依赖无法下载的问题，
尝试清理本地maven目录下的 `xxx.lastUpdated`文件（网上有很多一键清理脚本，或者可以自己写一个），然后通过idea刷新maven并尝试重新下载。

有时候如果依赖下载全部下载完成没有出现上述问题但是maven工具栏中依然有红线，则可能是idea的显示bug，尝试重启idea。




