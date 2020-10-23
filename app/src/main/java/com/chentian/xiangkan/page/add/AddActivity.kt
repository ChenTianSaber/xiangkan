package com.chentian.xiangkan.page.add

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chentian.xiangkan.MyEventBus
import com.chentian.xiangkan.R
import com.chentian.xiangkan.page.main.RSSRepository
import com.chentian.xiangkan.utils.RSSInfoUtils
import com.githang.statusbar.StatusBarCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var back: ImageView
    private lateinit var bilibiliUp:LinearLayout
    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add)
        StatusBarCompat.setStatusBarColor(this,  resources.getColor(R.color.white_3),true)

        context = this
        initView()
    }

    private fun initView(){
        back = findViewById(R.id.back)
        back.setOnClickListener {
            finish()
        }
        bilibiliUp = findViewById(R.id.bilibili_up)
        bilibiliUp.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.dialog_edit_text, null)
            val uid = view.findViewById<EditText>(R.id.uid)
            builder.setView(view)
                .setPositiveButton("确定",
                    DialogInterface.OnClickListener { dialog, id ->
                        if(uid.text.isNullOrEmpty()){
                            Toast.makeText(context,"请先输入uid~",Toast.LENGTH_SHORT).show()
                        }else{
                            // 先请求检测一下，可以的话就加入订阅源里
                            Toast.makeText(context,"正在检测链接~",Toast.LENGTH_SHORT).show()
                            GlobalScope.launch(Dispatchers.IO) {
                                val result = RSSInfoUtils.checkRSSData(
                                    link = "https://rsshub.ioiox.com/bilibili/user/dynamic/${uid.text}",
                                    showWeb = false
                                )
                                GlobalScope.launch(Dispatchers.Main) {
                                    if(result != null){
                                        Toast.makeText(context,"添加订阅成功~",Toast.LENGTH_SHORT).show()
                                        RSSInfoUtils.RSSLinkList.add(result)
                                        MyEventBus.post(result)
                                    }else{
                                        Toast.makeText(context,"添加订阅失败...请确认uid是否正确",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    })
                .setNegativeButton("取消",
                    DialogInterface.OnClickListener { dialog, id -> })
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        }
    }
}