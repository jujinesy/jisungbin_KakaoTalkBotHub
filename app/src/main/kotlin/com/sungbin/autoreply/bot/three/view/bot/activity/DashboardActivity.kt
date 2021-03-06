package com.sungbin.autoreply.bot.three.view.bot.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.bot.BotNotificationManager
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.autoreply.bot.three.view.bot.fragment.AddBotFragment
import com.sungbin.autoreply.bot.three.view.bot.fragment.DashboardFragment
import com.sungbin.autoreply.bot.three.view.bot.fragment.SandboxFragment
import com.sungbin.autoreply.bot.three.view.bot.fragment.SettingFragment
import com.sungbin.autoreply.bot.three.view.hub.activity.MainActivity
import com.sungbin.sungbintool.*
import kotlinx.android.synthetic.main.content_dashboard.bottombar
import kotlinx.android.synthetic.main.content_dashboard_new.*


@Suppress("DEPRECATION")
class DashboardActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = this.supportFragmentManager

    companion object {
        var bottomBarIndex = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.content_dashboard_new)
            Logger.addLogAdapter(AndroidLogAdapter())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor =
                    ContextCompat.getColor(applicationContext, R.color.colorWhite)
            }

            fab_add.setOnClickListener {
                //TODO: ???????????? ?????? ?????????????????????
            }

            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val configSettings = FirebaseRemoteConfigSettings
                .Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
            remoteConfig.setConfigSettings(configSettings)
            remoteConfig.fetch(60)
                .addOnCompleteListener { task: Task<Void?> ->
                    if (task.isSuccessful) {
                        remoteConfig.activateFetched()
                    } else {
                        ToastUtils.show(
                            this,
                            getString(R.string.error_get_data),
                            ToastUtils.SHORT,
                            ToastUtils.ERROR
                        )
                    }
                    checkAppVersion(remoteConfig)
                }

            StorageUtils.createFolder(BotPathManager.JS)
            StorageUtils.createFolder(BotPathManager.LOG)
            StorageUtils.createFolder(BotPathManager.ROOM)
            StorageUtils.createFolder(BotPathManager.SENDER)
            StorageUtils.createFolder(BotPathManager.SIMPLE)
            StorageUtils.createFolder(BotPathManager.DATABASE)
            BotNotificationManager.createChannel(applicationContext)

            if (DataUtils.readData(applicationContext, "BotOn", "true").toBoolean()) {
                BotNotificationManager.create(applicationContext)
            }

            val title = findViewById<TextView>(R.id.tv_dashboard)
            fragmentManager.beginTransaction().add(
                R.id.framelayout,
                DashboardFragment(
                    fragmentManager,
                    R.id.framelayout,
                    bottombar,
                    title,
                    false
                )
            ).commit()

            bottombar.ignoredIndex = 2
            bottombar.alwaysWhiteTintIndex = 2
            bottombar.onItemSelected = {
                if (DataUtils.readData(applicationContext, "isTutorial", "true").toBoolean()) {
                    bottombar.setActiveItem(bottomBarIndex)
                } else {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    when (it) {
                        0 -> { //????????????
                            title.text = getString(R.string.dashboard)
                            fragmentTransaction.replace(
                                R.id.framelayout,
                                DashboardFragment(
                                    fragmentManager,
                                    R.id.framelayout,
                                    bottombar,
                                    title,
                                    false
                                )
                            ).commit()
                        }
                        1 -> { //????????????
                            title.text = getString(R.string.sandbox)
                            fragmentTransaction.replace(
                                R.id.framelayout,
                                SandboxFragment(
                                    fragmentManager,
                                    R.id.framelayout,
                                    bottombar,
                                    title,
                                    false
                                )
                            ).commit()
                        }
                        2 -> { //???????????? ??????
                            title.text = getString(R.string.add_bot)
                            fragmentTransaction.replace(
                                R.id.framelayout,
                                AddBotFragment(
                                    fragmentManager,
                                    R.id.framelayout,
                                    bottombar,
                                    title,
                                    false
                                )
                            ).commit()
                        }
                        3 -> { //????????? ??????
                            fragmentTransaction.replace(
                                R.id.framelayout,
                                DashboardFragment(
                                    fragmentManager,
                                    R.id.framelayout,
                                    bottombar,
                                    title,
                                    false
                                )
                            ).commit()
                            bottombar.setActiveItem(0)
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        4 -> { //??????
                            title.text = getString(R.string.setting)
                            fragmentTransaction.replace(
                                R.id.framelayout,
                                SettingFragment(
                                    fragmentManager,
                                    R.id.framelayout,
                                    bottombar,
                                    title,
                                    false
                                )
                            ).commit()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Error", e.toString())
        }
    }

    fun checkAppVersion(config: FirebaseRemoteConfig) {
        val nowVersion = Utils.getAppVersionName(this)
        val lastVersion = config.getString("last_version").replace("\"", "")
        if (lastVersion != nowVersion) {
            DialogUtils.show(
                this, getString(R.string.need_app_update),
                "??????????????? KakaoTalkBotHub??? ????????? ????????? ??? ?????? ???????????? ??? ????????????." +
                        "\n???????????? ?????????????????? ??????????????? ??? ?????????.\n\n" +
                        "?????? ?????? : $nowVersion\n" +
                        "?????? ?????? : $lastVersion",
                DialogInterface.OnClickListener { _, _ -> finish() }, false
            )
        }
    }
}