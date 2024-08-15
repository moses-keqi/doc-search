# ytds

## 项目介绍

云图文档搜索,一款文档内全文搜索工具，上传pdf，word，excel，ppt等文件，通过tika解析文本内容。使用elasticsearch索引文档内容，调用elasticsearch的api实现文档的全文搜索。使用LibreOffice将文档转换成pdf实现在线预览功能。同时包含个人文档统计，管理等功能，每个人上传的文档都是以个人账户为基本单位相互隔离的，是隐私安全可靠的。

> **ytds解释:** yt **云图（yuntu）** ds **文档搜索（document search）** 


## 相关技术和框架

> springboot mysql elasticsearch spring vue element-ui LibreOffice

## 功能介绍
1. 用户登录，用户邮箱注册
2. 文档全文搜索，全文搜索和数据库like查找是完全不一样的概念，请勿混淆。同时支持多种条件组合搜索，时间范围，文档类型，文档大小等条件
3. 文档上传，支持word文档，pdf文档，excel文档，ppt文档，txt文档
4. 支持从印象笔记的导出enex文件导入到文档库中搜索
5. 我的文档，管理和删除文档，统计文档类型
6. 可在线预览几乎所有上传的文档，包括从印象笔记导入的文档，原理是使用libreoffice转换成pdf，使用pdf.js实现在线预览
7. 提供原文档下载

## 后续开发计划
1. 支持富文本编辑器添加文档
2. 新增子项目ytds-mobile，在手机端或者小程序端访问
3. 新增子项目ytds-electron，桌面客户端，主要是监听磁盘目录，实现自动同步上传索引文档

## 安装教程

#### ytds-backend安装教程

> 需要对springboot有所了解

1. 下载elasticsearch  
https://www.elastic.co/downloads/elasticsearch
2. 安装elasticsearch ik分词器  
https://github.com/medcl/elasticsearch-analysis-ik
3. 安装LibreOffice  
https://zh-cn.libreoffice.org/download/libreoffice-still/  
linux安装方法  
https://zh-cn.libreoffice.org/get-help/install-howto/linux/
4. 打开sql文件夹，导入ytds.sql文件到mysql中
5. 修改配置文件application.xml  
```yaml
    // elasticsearch配置,端口和ip地址
    es:
      port: 9300
      ip: 127.0.0.1
    // 文档存储路径和soffice路径
    base:
      upload-dir: /Users/iamdev/project/springboot/ytds/upload
      soffice-path: /Applications/LibreOffice.app/Contents/MacOS/soffice
      
    // smtp邮箱地址和账号密码
    mail:
      smtp: smtp.163.com
      password: 520ytds
      name: your_account@163.com
      
    //jdbc路径和mysql账号密码
      datasource:
        url: jdbc:mysql://127.0.0.1:3306/ytds?useUnicode=true&characterEncoding=utf-8&useSSL=false
        username: root
        password: root
        driver-class-name: com.mysql.jdbc.Driver
```
6. 启动elasticsearch和mysql
7. 运行YTDSApplication

#### ytds-fontend安装教程

1. 进入ytds-fontend项目，然后npm install安装依赖包，也可以使用cnpm
2. npm run dev即可
3. 修改代理端口  
config/index.js
```javascript
module.exports = {
  dev: {
    proxyTable: {
      '/ytds':{
          target: 'http://127.0.0.1:9090',
          changeOrigin: true
      }
    },
  }
}
```
4. 编译，npm run build  
打开dist文件夹即为编译结果  
根据dist的文件目录修改ytds-fontend的application.yml
```yaml
//静态文件路径
spring:
  resources:
    static-locations: file:/Users/iamdev/project/springboot/ytds/ytds-fontend/dist
```

## 使用教程
1. 启动ytds-backend无报错之后，启动ydts-fontend，使用cnpm run dev
2. 然后打开浏览器->localhost:8080
3. sql里面默认登录账号 admin/yt520
4. 选择文档上传，上传文档之后即可搜索文档

#### 页面展示

1. 搜索页面
![](https://gitee.com/Moriarty/ytds/raw/master/snapshots/%E6%96%87%E6%A1%A3%E6%90%9C%E7%B4%A2.jpeg "文档搜索")
![](https://gitee.com/Moriarty/ytds/raw/master/snapshots/%E9%AB%98%E7%BA%A7%E6%90%9C%E7%B4%A2.jpeg "高级搜索")
2. 文档上传
![](https://gitee.com/Moriarty/ytds/raw/master/snapshots/%E4%B8%8A%E4%BC%A0%E6%96%87%E6%A1%A3.jpeg "文档上传")
3. 我的文档
![](https://gitee.com/Moriarty/ytds/raw/master/snapshots/%E6%88%91%E7%9A%84%E6%96%87%E6%A1%A3.jpeg "我的文档")
4. 印象笔记导入
![](https://gitee.com/Moriarty/ytds/raw/master/snapshots/印象笔记上传.png "印象笔记")
#### 参与贡献

1. Fork 本项目
2. 新建 dev 分支
3. 提交代码
4. 新建 Pull Request

