### 使用方法：
#### 1. 代码仓库新增，添加依赖
```
  maven { url=uri("https://www.jitpack.io")}

  implementation("com.github.Larissa-x:LogPrivider:v1.0.0")
```
#### 2. 使用方法
  在Applicatyion中注册
```
  // 第一个参数application、第二个参数当前环境是否debug，false下 不打印日志，会输出到文件中，第三个参数是否需要定期清理过期文件，目前是只保留一周内的文件，超出七天，每次初始化的时候，会进行清理。
  TimberProvider.init(this, debug, true)

   // 需要打印日志时调用,级别可自选，Tag也可自定义传入，不传默认会是当前类名
  Timber.d("日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志")
```

注意需要注册权限，
```
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
```
  
