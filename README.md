Erengine
========

Introduction
============
A book reader engine
采用开源代码FBReader和Adobe RMSDK作为插件。
支持文件格式有txt、epub、pdf、html等。

ChangeLog
==========

0.5.4
-----
1. 优化FBReader的遍历列表的处理方式.
2. 修正部分EPUB书段落第一行有多余空格导致显示的问题.

0.5.3
-----
1. 修改旋屏的接口方法，添加視圖的寬度和高度作為參數.(setScreenLandMode(boolean land, int viewWidth, int viewHeight))
2. 調整FBReader對于<tr>、<td>標識的處理.(XHTMLTagTrAction.java, XHTMLTagTdAction.java, FBTextKind.java)
3. 調整FBReader對于圖片元素的處理.(XHTMLTagImageAction.java)
4. 調整FBReader處理文字的行間距.(assets/default/styles.xml)
5. 添加處理CSS文件的庫和類.
 
0.5.2
-----
1. 修正Adobe翻上一章節無法正確跳轉的問題.
2. 添加根據字體等級進行縮放的接口pageZoomLevel(int lv).
3. FBreader添加獲取當前頁文字內容的接口doFetchText();

0.5.1
-----
1. 修改錯誤判斷的邏輯.
2. 修正Adobe字體最大或最小時，翻頁會調用字體最大或最小的回調問題.
3. 修正了部分PDF頁碼計算錯誤的問題.

0.5.0
-----
1. 添加數據庫支持的Provider.
2. 添加數據庫操作的接口IDatabaseService.
3. 添加IRecorder的數據庫實現類SqliteRecorder.
4. 添加書籍大小的屬性.
5. 修改了部分Book的屬性相關方法.
6. 修改了書簽，高亮信息的部分代碼.

0.4.7r2
-------
1. 可配置支持加载本地共享库.

0.4.7
-----
1. 修正部分注釋.
2. 修正部分代碼結構.
3. 修正FBReader畫詞不准確的Bug.

0.4.6
-----
1. EngineConfig添加設置library目錄配置.
2. EngineConfig添加自動讀取數據庫選項配置.
3. FBReader修正字體縮小等級計算錯誤的Bug.
4. Render类添加获取Bitmap.Config的方法.

0.4.5r2
-------
1. 修改Action類名為ReaderWrapper，并獨立IAction接口
2. 修改個別類結構

0.4.5
-----
1. FBReader添加画重点功能
2. Book添加ID属性
3. 添加注释并导出javadoc 

0.4.4
-----
1. Adobe添加画重点功能
2. 独立BookMark和BookSearch的类结构
3. TODO: FBReader添加画重点功能. (Finished in 0.4.5)
4. TODO: Book添加ID属性. (Finished in 0.4.5)
5. TODO: 添加注释并导出javadoc. (Finished in 0.4.5)

0.4.3
-----
1. 修正Adobe旋屏bug
2. 添加FBreader旋屏功能
3. 添加FBReaderopenLink功能

0.4.2r2
-------
1. 修正讀取Meta信息引起的crash問題
2. 修正Adobe無法打開600*800以上分辨率的BUG
3. 修正TaskManager退出調用cancelProcess可能引起的crash
4. Adobe添加openLink功能
5. Adobe添加旋屏功能

0.4.2
-----
1. 添加手势操作功能

0.4.1
-----
1. 移除HYF支持模块
2. FBReader支持打开TXT和HTML文档
3. TXT支持GBK、UTF-8、UTF-16等编码
4. HTML通过content标识分辨编码格式
5. 修改Book的Meta信息结构

0.4.0
-----
1. 更新FBReader代码为1.1.0版本
2. 加入FBReader fb2格式处理模块
3. 修改判断上下页机制，修正最后一页向前翻页问题
4. 修正上一页为章节头时，向上翻页后再向下翻页，导致内容错位
5. 修改预读上一页和下一页的机制，消除上下翻页内容不正确的问题

0.3.3r2
-------
1. 修正FBReader放大缩小字体后导致无法跳转的问题
2. 修正FileRecorder无法保存书签信息的问题

0.3.3
-----
1. 添加FBreader书签功能
2. 添加Adobe书签功能
3. [UI]:添加书签相关布局

0.3.2r2
-------
1. 调整搜索栏布局
2. 添加对话框风格

0.3.2
-----
1. 添加Adobe搜索功能
2. 添加FBreader搜索功能
3. 独立标题栏和底栏视图类

0.3.1
-----
1. 添加水平和垂直移动机制
2. [UI]:添加触摸缩放字体大小功能

0.3.0
-----
1. 修改页码计算方法，修正跳页引起的页码问题。
2. [UI]:添加控制面板，完成目录和跳转功能。
3. [UI]:添加进度条跳转功能。
4. 添加手势移动的处理模块。

0.2.3
-----
1. 修正載入後字符重疊問題，并解決定位不准的問題。
2. 改進TaskManager的性能，保証任務隊列中最多只有五個任務。
3. FbreaderAdapter添加定位相關方法。
4. 修改字體縮放的策略，FbreaderAdapter添加獲取字體大小的接口。
5. Adobe打開epub格式支持DRM。

0.2.2r2
-------
1. 優化isEndParagraph()方法調用，提高翻下頁的速度

0.2.2
-----
1. 修正載入最後頁碼內容時，前幾個文字會疊加的問題，但是不能准確定位。
2. 修改Action.closeBook()方法，將該代碼放在UI線程做處理以避免程序關閉後無法執行的問題

0.2.1r2
-------
1. 修正FBReader字體縮放的問題

0.2.1
-----
1. FBReader支持目錄瀏覽、目錄跳轉功能

0.2.0r2
-------
1.Modify: 優化翻頁機制，基本解決最後一頁和第一頁的問題

0.2.0
-----
1.Add: 添加FBReader顯示，翻頁，放大，縮小功能支持
2.Modify: 修正FBReader翻页错乱问题。
3.TODO: 添加翻页缓存机制。(FIXED in 0.2.3)

0.1.4
-----
1.Modify: 改進Book類中的文件類型識別方法		
2.Add: FBReader插件
3.TODO: FBReader顯示，翻頁，放大，縮小功能支持 (FIXED in 0.2.0)	

0.1.3
-----
1.Add: 添加批注功能。(TODO:未完善，保存、删除、加载功能)

0.1.2
-----
1.Modify: 修改Action的todo、predo機制，使用開關控制todo、perdo的執行，并能夠在子類中手動調用。
2.Add: HYF支持上下翻頁、字體放大和縮小功能。
3.TODO: HyfAdapter的臨時文件路徑暫時為/sdcard/.hyf_temp。(HYF暂时停止更新)			

0.1.1
-----
1.Modify: 修改Action的todo、predo機制
2.Add: 支持HYF打開并顯示txt文檔，暫不支持翻頁，字體放大等功能

0.1.0
-----
1.Modify: 线程模型的任务分配机制
2.Fixed: 任务管理可能没有退出
3.TODO: 添加清除记录文件功能

0.0.9r3
-------
1.Fixed: 向下翻页调用错误
2.Fixed: 执行错误回调后会将错误代码变更为0 
3.TODO: 任务管理可能没有退出 (FIXED in 0.1.0)
4.Fixed: 恢復0.0.9r2的變更，Adobe必须固定包的格式

0.0.9r2
-------
1.Modify: Change package's name "com.foxconn.ebook.jni" to "com.foxconn.ebook.reader.lib.adobe" 

0.0.9
-----
1. Add: 添加支持密码验证.
2. Modify: 修改书籍信息装载机制.
3. Modify: 修改错误处理机制，避免最后的错误代码可能被覆盖.
