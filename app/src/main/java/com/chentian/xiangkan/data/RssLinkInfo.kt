package com.chentian.xiangkan.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 这个类是订阅源的数据
 */
@Entity
data class RssLinkInfo(
        var url: String = "", //订阅请求的链接
        var channelLink: String = "", //主站的链接
        var channelTitle: String = "", //主站名称
        var channelDescription: String = "", // 主站的描述
        var state: Boolean = false, // 是否开启订阅
        var icon: String = "" // 图标
) {
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0
        var latsedTitle: String = "" // 最新
        var latestPubDate: Long = 0
        @Ignore
        var isRefreshing: Boolean = false
        @Ignore
        var isChoosed: Boolean = false
}

object RssLinkInfoFactory {

    /**
     * API域名
     */
    const val RSSHUB_DOMAIN = "https://datatube.dev/api/rss"
//    const val RSSHUB_DOMAIN_BACK = "https://datatube.dev/api/rss"

    /**
     * BiliBili UP的动态订阅链接
     */
    const val BILIBILI_UP = "$RSSHUB_DOMAIN/bilibili/user/dynamic/"
    const val BILIBILI_API = "https://api.bilibili.com/x/space/acc/info?mid="

    /**
     * 全部TAB的占位数据
     */
    const val ALLDATA = "-1"

    /**
     * 构造默认的订阅源数据
     */
    fun getDefaultRssLinkInfo(): MutableList<RssLinkInfo> {
        return mutableListOf(
                RssLinkInfo( // 少数派
                        url = "https://sspai.com/feed",
                        channelLink = "https://sspai.com",
                        channelTitle = "少数派",
                        channelDescription = "少数派致力于更好地运用数字产品或科学方法，帮助用户提升工作效率和生活品质",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/9141cd005566788b0bde.png/icon_sspai.png"
                ),
                RssLinkInfo( // 知乎热榜
                        url = "$RSSHUB_DOMAIN/zhihu/hotlist",
                        channelLink = "https://www.zhihu.com/billboard",
                        channelTitle = "知乎热榜",
                        channelDescription = "知乎热榜",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/1af55f939856dde8bff1.png/icon_zhihu.png"
                ),
                RssLinkInfo( // 开眼
                        url = "$RSSHUB_DOMAIN/kaiyan/index",
                        channelLink = "https://www.kaiyanapp.com/",
                        channelTitle = "开眼",
                        channelDescription = "开眼每日精选",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/7a673bbf7068781c8bfe.png/icon_kaiyan.png"
                ),
                RssLinkInfo( // 品玩
                        url = "$RSSHUB_DOMAIN/pingwest/status",
                        channelLink = "https://www.pingwest.com/status",
                        channelTitle = "品玩",
                        channelDescription = "品玩 - 实时要闻",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/8e86a367ee659cf342d3.jpg/icon_pinwan.jpg"
                ),
                RssLinkInfo( // 36K
                        url = "$RSSHUB_DOMAIN/36kr/newsflashes",
                        channelLink = "https://36kr.com/newsflashes",
                        channelTitle = "36氪",
                        channelDescription = "快讯 - 36氪",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/83ec172460dbf30ba932.png/logo_36k.png"
                ),
                RssLinkInfo( // 好奇心日报
                        url = "$RSSHUB_DOMAIN/qdaily/tag/29",
                        channelLink = "http://www.qdaily.com/tags/29.html",
                        channelTitle = "好奇心日报",
                        channelDescription = "Top 15_好奇心日报",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/f35d3d1faf199c1acfd9.jpg/icon_haoqixinribao.jpg"
                ),
                RssLinkInfo( // Bilibili每周必看
                        url = "$RSSHUB_DOMAIN/bilibili/weekly",
                        channelLink = "https://www.bilibili.com/h5/weekly-recommend",
                        channelTitle = "B站每周必看",
                        channelDescription = "B站每周必看",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/780d46690997834f8827.png/bilibili%20%282%29.png"
                ),
                RssLinkInfo( // 3DM新闻中心
                        url = "$RSSHUB_DOMAIN/3dm/news",
                        channelLink = "http://www.3dmgame.com/news/",
                        channelTitle = "3DM新闻中心",
                        channelDescription = "3DM - 新闻中心",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/1a3728e2c785034c6938.jpg/icon_3dm.jpg"
                ),
                RssLinkInfo( // 虎嗅
                        url = "$RSSHUB_DOMAIN/huxiu/article",
                        channelLink = "https://www.huxiu.com/article",
                        channelTitle = "虎嗅",
                        channelDescription = "虎嗅网 - 首页资讯",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/5b3bdfc4f9c22549ab3f.png/icon_huxiu.png"
                ),
                RssLinkInfo( // 机核
                        url = "$RSSHUB_DOMAIN/gcores/category/articles",
                        channelLink = "https://www.gcores.com/articles",
                        channelTitle = "机核GCORES",
                        channelDescription = "文章 | 机核 GCORES",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/b7b0facd9da6eb3fba68.png/icon_jihe.png"
                ),
                RssLinkInfo( // UI中国
                        url = "$RSSHUB_DOMAIN/ui-cn/article",
                        channelLink = "https://www.ui.cn/",
                        channelTitle = "UI中国",
                        channelDescription = "推荐文章 - UI 中国",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/18ecd4bd99ebba61466a.jpg/icon_uichina.jpg"
                ),
                RssLinkInfo( // 站酷
                        url = "$RSSHUB_DOMAIN/zcool/recommend/edit",
                        channelLink = "https://www.zcool.com.cn/discover/0!0!0!0!0!!!!2!-1!1",
                        channelTitle = "站酷",
                        channelDescription = "站酷 - 编辑推荐",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/b9feca1ab08a238e2893.jpg/icon_zcool.jpg"
                ),
                RssLinkInfo( // 什么值得买
                        url = "$RSSHUB_DOMAIN/smzdm/haowen/1",
                        channelLink = "https://post.smzdm.com/p/aqnd9pdp/",
                        channelTitle = "什么值得买",
                        channelDescription = "什么值得买的值客原创频道，由晒物广场和经验盒子合并而来，新值客原创频道分为开箱晒物、使用评测、购物攻略、消费知识、摄影旅游、生活记录等六个板块，是众多值友分享消费主张、获取消费经验知识的互动频道。",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/b3757558bd3bef9c76ee.jpg/icon_shenmezhidemai.jpg"
                ),
                RssLinkInfo( // 差评
                        url = "$RSSHUB_DOMAIN/chaping/news",
                        channelLink = "https://chaping.cn/news?cate=",
                        channelTitle = "差评",
                        channelDescription = "差评资讯 - 全部",
                        state = false,
                        icon = "http://leancloudfile.chentiansaber.top/a11b12759e435f6ba59e.jpg/icon_chaping.jpg"
                )
        )
    }

}