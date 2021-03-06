package com.sungbin.autoreply.bot.three.view.bot.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.fsn.cauly.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.License
import com.marcoscg.licenser.LicenserDialog
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.apps.AppListAdapter
import com.sungbin.autoreply.bot.three.api.Black
import com.sungbin.autoreply.bot.three.dto.apps.AppInfo
import com.sungbin.autoreply.bot.three.utils.bot.BotNotificationManager
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardActivity
import com.sungbin.autoreply.bot.three.view.ui.bottombar.SmoothBottomBar
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StringUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.fragment_setting.*
import java.text.Collator
import java.util.*
import kotlin.collections.ArrayList


class SettingFragment constructor(
    private val fragmentManage: FragmentManager,
    private val view: Int,
    private val bottombar: SmoothBottomBar,
    private val textview: TextView,
    private val isTutorial: Boolean
) : Fragment(), CaulyAdViewListener, CaulyInterstitialAdListener {

    private var showInterstitial = false

    private var swBotOnoff: Switch? = null
    private var swAutoSave: Switch? = null
    private var swKeepScope: Switch? = null
    private var swNotHighting: Switch? = null
    private var swErrorBotOff: Switch? = null
    private var swNotErrorHighting: Switch? = null

    private var etTextSize: EditText? = null
    private var etPackages: EditText? = null
    private var etBlackRoom: EditText? = null
    private var etBlackSender: EditText? = null
    private var etHtmlLimitTime: EditText? = null

    private var sbTextSize: SeekBar? = null
    private var sbHtmlLimitTime: SeekBar? = null

    private var btnShowAd: Button? = null
    private var btnRemoveAd: Button? = null
    private var btnSelectApp: Button? = null
    private var btnShowLicense: Button? = null

    private var llAd: LinearLayout? = null
    private var svLayout: ScrollView? = null
    private var tvPreviewSize: TextView? = null
    private var fabSave: FloatingActionButton? = null
    private var tblFavorateLanguage: ToggleButtonLayout? = null

    private lateinit var lavWelcome: LottieAnimationView
    private lateinit var tvWelcome: TextView
    private lateinit var btnDone: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        swAutoSave = view.findViewById(R.id.sw_auto_save)
        swBotOnoff = view.findViewById(R.id.sw_bot_onoff)
        swKeepScope = view.findViewById(R.id.sw_keep_scope)
        swNotHighting = view.findViewById(R.id.sw_not_highting)
        swErrorBotOff = view.findViewById(R.id.sw_error_bot_off)
        swNotErrorHighting = view.findViewById(R.id.sw_not_error_highting)

        etPackages = view.findViewById(R.id.et_packages)
        etTextSize = view.findViewById(R.id.et_text_size)
        etBlackRoom = view.findViewById(R.id.et_black_room)
        etBlackSender = view.findViewById(R.id.et_black_sender)
        etHtmlLimitTime = view.findViewById(R.id.et_html_limit_time)

        sbTextSize = view.findViewById(R.id.sb_text_size)
        sbHtmlLimitTime = view.findViewById(R.id.sb_html_limit_time)

        btnShowAd = view.findViewById(R.id.btn_show_ad)
        btnRemoveAd = view.findViewById(R.id.btn_remove_ad)
        btnSelectApp = view.findViewById(R.id.btn_select_app)
        tvPreviewSize = view.findViewById(R.id.tv_preview_size)
        btnShowLicense = view.findViewById(R.id.btn_show_license)

        llAd = view.findViewById(R.id.ll_ad)
        fabSave = view.findViewById(R.id.fab_save)
        svLayout = view.findViewById(R.id.sv_layout)
        tblFavorateLanguage = view.findViewById(R.id.tbl_favorite_langauge)

        lavWelcome = view.findViewById(R.id.lav_welcome)
        btnDone = view.findViewById(R.id.btn_done)
        tvWelcome = view.findViewById(R.id.tv_welcome)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Black.init(context!!)

        val adInfo = CaulyAdInfoBuilder(getString(R.string.cauly_ad_id))
            .effect("TopSlide")
            .bannerHeight("Fixed_50").build()

        val adView = CaulyAdView(context)
        adView.setAdInfo(adInfo)
        adView.setAdViewListener(this)
        llAd!!.addView(adView)

        fabSave!!.hide()

        if (isTutorial) {
            lavWelcome.visibility = View.VISIBLE
            lavWelcome.playAnimation()
            tvWelcome.visibility = View.VISIBLE
            btnDone.visibility = View.VISIBLE
            btnDone.setOnClickListener {
                textview.text = getString(R.string.dashboard)
                val fragmentTransaction = fragmentManage.beginTransaction()
                fragmentTransaction.replace(
                    view,
                    DashboardFragment(fragmentManage, view, bottombar, textview, true)
                ).commit()
                bottombar.setActiveItem(0)
                DashboardActivity.bottomBarIndex = 0
            }

            swBotOnoff!!.alpha = 0.1f
            swAutoSave!!.alpha = 0.1f
            swKeepScope!!.alpha = 0.1f
            swNotHighting!!.alpha = 0.1f
            swErrorBotOff!!.alpha = 0.1f
            swNotErrorHighting!!.alpha = 0.1f
            etTextSize!!.alpha = 0.1f
            etPackages!!.alpha = 0.1f
            etBlackRoom!!.alpha = 0.1f
            etBlackSender!!.alpha = 0.1f
            etHtmlLimitTime!!.alpha = 0.1f
            sbTextSize!!.alpha = 0.1f
            sbHtmlLimitTime!!.alpha = 0.1f
            btnShowAd!!.alpha = 0.1f
            btnRemoveAd!!.alpha = 0.1f
            btnSelectApp!!.alpha = 0.1f
            btnShowLicense!!.alpha = 0.1f
            llAd!!.alpha = 0.1f
            svLayout!!.alpha = 0.1f
            tvPreviewSize!!.alpha = 0.1f
            fabSave!!.alpha = 0.1f
            tblFavorateLanguage!!.alpha = 0.1f

            swBotOnoff!!.isEnabled = false
            swAutoSave!!.isEnabled = false
            swKeepScope!!.isEnabled = false
            swNotHighting!!.isEnabled = false
            swErrorBotOff!!.isEnabled = false
            swNotErrorHighting!!.isEnabled = false
            etTextSize!!.isEnabled = false
            etPackages!!.isEnabled = false
            etBlackRoom!!.isEnabled = false
            etBlackSender!!.isEnabled = false
            etHtmlLimitTime!!.isEnabled = false
            sbTextSize!!.isEnabled = false
            sbHtmlLimitTime!!.isEnabled = false
            btnShowAd!!.isEnabled = false
            btnRemoveAd!!.isEnabled = false
            btnSelectApp!!.isEnabled = false
            btnShowLicense!!.isEnabled = false
            llAd!!.isEnabled = false
            svLayout!!.isEnabled = false
            tvPreviewSize!!.isEnabled = false
            fabSave!!.isEnabled = false
            tblFavorateLanguage!!.isEnabled = false
        }

        val textSize = DataUtils.readData(context!!, "TextSize", "17")
        val htmlLimitTime = DataUtils.readData(context!!, "HtmlLimitTime", "5")
        val packages = DataUtils.readData(context!!, "packages", "com.kakao.talk")
        val autoSave = DataUtils.readData(context!!, "AutoSave", "true").toBoolean()
        val keepScope = DataUtils.readData(context!!, "KeepScope", "false").toBoolean()
        val favoriteFanguage = DataUtils.readData(context!!, "FavoriteLanguage", "null")
        val notHighting = DataUtils.readData(context!!, "NotHighting", "false").toBoolean()
        val errorBotOff = DataUtils.readData(context!!, "ErrorBotOff", "false").toBoolean()
        val notErrorHighting =
            DataUtils.readData(context!!, "NotErrorHighting", "false").toBoolean()

        swAutoSave!!.isChecked = autoSave
        swKeepScope!!.isChecked = keepScope
        swNotHighting!!.isChecked = notHighting
        swErrorBotOff!!.isChecked = errorBotOff
        tvPreviewSize!!.textSize = textSize.toFloat()
        swNotErrorHighting!!.isChecked = notErrorHighting
        etTextSize!!.text = StringUtils.toEditable(textSize)
        etPackages!!.text = StringUtils.toEditable(packages)
        etHtmlLimitTime!!.text = StringUtils.toEditable(htmlLimitTime)

        etBlackRoom!!.text = StringUtils.toEditable(Black.readRoom().trim())
        etBlackSender!!.text = StringUtils.toEditable(Black.readSender().trim())

        if (favoriteFanguage != "null") {
            if (favoriteFanguage == "??????????????????") {
                tblFavorateLanguage!!.setToggled(R.id.javascript, true)
            } else {
                tblFavorateLanguage!!.setToggled(R.id.simple, true)
            }
        }

        swBotOnoff!!.isChecked = DataUtils.readData(context!!, "BotOn", "false").toBoolean()
        swBotOnoff!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "BotOn", boolean.toString())
            if (boolean) {
                BotNotificationManager.create(context!!)
            } else {
                BotNotificationManager.delete(context!!)
            }
        }

        swAutoSave!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "AutoSave", boolean.toString())
        }

        swKeepScope!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "KeepScope", boolean.toString())
        }

        swNotHighting!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "NotHighting", boolean.toString())
        }

        swErrorBotOff!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "ErrorBotOff", boolean.toString())
        }

        swNotErrorHighting!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "NotErrorHighting", boolean.toString())
        }

        sbTextSize!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                etTextSize!!.setText(i.toString())
                tvPreviewSize!!.textSize = i.toFloat()
                DataUtils.saveData(context!!, "TextSize", i.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        sbHtmlLimitTime!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                etHtmlLimitTime!!.setText(i.toString())
                DataUtils.saveData(context!!, "HtmlLimitTime", i.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        etTextSize!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                try {
                    etTextSize!!.setSelection(etTextSize!!.text.length)
                    val i = editable.toString().toInt()
                    if (i < 1 || i > 30) {
                        ToastUtils.show(
                            context!!,
                            getString(R.string.can_set_one_thirty),
                            ToastUtils.SHORT,
                            ToastUtils.WARNING
                        )
                    } else {
                        sbTextSize!!.progress = i
                        tvPreviewSize!!.textSize = i.toFloat()
                        DataUtils.saveData(context!!, "TextSize", i.toString())
                    }
                } catch (e: Exception) {
                    ToastUtils.show(
                        context!!,
                        getString(R.string.can_set_one_thirty),
                        ToastUtils.SHORT,
                        ToastUtils.WARNING
                    )
                }
            }
        })

        etHtmlLimitTime!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                try {
                    etHtmlLimitTime!!.setSelection(etHtmlLimitTime!!.text.length)
                    val i = editable.toString().toInt()
                    if (i < 1 || i > 10) {
                        ToastUtils.show(
                            context!!,
                            getString(R.string.can_set_one_ten),
                            ToastUtils.SHORT,
                            ToastUtils.WARNING
                        )
                    } else {
                        sbHtmlLimitTime!!.progress = i
                        DataUtils.saveData(context!!, "HtmlLimitTime", i.toString())
                    }
                } catch (e: Exception) {
                    ToastUtils.show(
                        context!!,
                        getString(R.string.can_set_one_ten),
                        ToastUtils.SHORT,
                        ToastUtils.WARNING
                    )
                }
            }
        })

        btnShowAd!!.setOnClickListener {
            ToastUtils.show(
                context!!,
                getString(R.string.thanks),
                ToastUtils.LONG,
                ToastUtils.SUCCESS
            )
            @Suppress("NAME_SHADOWING")
            val adInfo = CaulyAdInfoBuilder(getString(R.string.cauly_ad_id)).build()
            val interstial = CaulyInterstitialAd()
            interstial.setAdInfo(adInfo)
            interstial.requestInterstitialAd(activity)
            interstial.setInterstialAdListener(this)
            showInterstitial = true
        }

        btnShowLicense!!.setOnClickListener {
            showLicenseDialog()
        }

        btnSelectApp!!.setOnClickListener {
            showAppSelectDialog()
        }

        tblFavorateLanguage!!.onToggledListener = { _, toggle, select ->
            if (select) {
                DataUtils.saveData(
                    context!!,
                    "FavoriteLanguage",
                    toggle.title.toString()
                )
            }
        }

        fabSave!!.setOnClickListener {
            @Suppress("NAME_SHADOWING")
            val packages = etPackages!!.text.toString()
            val blackRoom = etBlackRoom!!.text.toString()
            val blackSender = etBlackSender!!.text.toString()

            DataUtils.saveData(context!!, "packages", packages)
            DataUtils.saveData(context!!, "RoomBlackList", blackRoom)
            DataUtils.saveData(context!!, "SenderBlackList", blackSender)

            ToastUtils.show(
                context!!,
                getString(R.string.saved),
                ToastUtils.SHORT,
                ToastUtils.SUCCESS
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            svLayout!!.setOnScrollChangeListener { _, _, y, _, oldY ->
                if (!isTutorial) {
                    if (y > oldY) { //Down
                        fabSave!!.show()
                    }
                    if (y < oldY) { //Up
                        fabSave!!.hide()
                    }
                }
            }
        }

    }

    /*----- ?????? ?????? -----*/
    override fun onReceiveInterstitialAd(
        ad: CaulyInterstitialAd,
        isChargeableAd: Boolean
    ) {
        // ?????? ?????? ?????? ??????
        if (showInterstitial) {
            ad.show()
            showInterstitial = false
        } else ad.cancel()
    }

    override fun onFailedToReceiveInterstitialAd(
        ad: CaulyInterstitialAd?,
        errorCode: Int,
        errorMsg: String?
    ) {
        // ?????? ?????? ?????? ????????? ?????? ?????????.
    }

    override fun onClosedInterstitialAd(ad: CaulyInterstitialAd?) {
        // ?????? ????????? ?????? ???????????? ?????? ?????? ?????????.
    }

    override fun onLeaveInterstitialAd(arg: CaulyInterstitialAd?) {
        // ?????? ????????? ???????????? ???????????? ?????? ?????? ?????????
    }
    /*----- ?????? ?????? ??? -----*/

    /*----- ?????? ?????? -----*/
    override fun onReceiveAd(adView: CaulyAdView?, isChargeableAd: Boolean) {
        // ?????? ?????? ?????? & ????????? ?????? ?????????.
        // ????????? ????????? ?????? ????????? ?????? isChargeableAd ?????? false ???.
    }

    override fun onFailedToReceiveAd(
        adView: CaulyAdView?,
        errorCode: Int,
        errorMsg: String?
    ) {
        // ?????? ?????? ?????? ????????? ?????? ?????????.
    }

    override fun onShowLandingScreen(adView: CaulyAdView?) {
        // ?????? ????????? ???????????? ?????? ???????????? ?????? ?????? ?????????.
    }

    override fun onCloseLandingScreen(adView: CaulyAdView?) {
        // ?????? ????????? ???????????? ?????? ???????????? ?????? ?????? ?????????.
    }
    /*----- ?????? ?????? ??? -----*/

    @Suppress("DEPRECATION")
    @SuppressLint("WrongConstant")
    fun getAppList(): ArrayList<AppInfo> {
        val pm = context!!.packageManager

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val appList = pm.queryIntentActivities(intent, 0)

        val appInfoList = ArrayList<AppInfo>()
        for (app in appList) {
            val name = app.loadLabel(pm).toString()
            val icon = app.loadIcon(pm)
            val packageString = app.activityInfo.packageName
            appInfoList.add(AppInfo(name, icon, packageString))
        }
        Collections.sort(
            appInfoList.toList()
        ) { object1, object2 ->
            Collator.getInstance().compare(object1!!.name, object2!!.name)
        }
        return appInfoList
    }

    @SuppressLint("InflateParams")
    private fun showAppSelectDialog() {
        ToastUtils.show(
            context!!,
            context!!.getString(R.string.load_app_list),
            ToastUtils.SHORT,
            ToastUtils.INFO
        )

        Thread {
            val appInfoList = getAppList()

            val layout = LayoutInflater
                .from(context)
                .inflate(
                    R.layout.layout_app_list_dialog,
                    null,
                    false
                )

            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("?????????????????? ??????")

            val recyclerView = layout.findViewById<RecyclerView>(R.id.rv_apps)
            val editText = layout.findViewById<EditText>(R.id.et_search)
            val adapter = AppListAdapter(appInfoList)

            recyclerView.layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    adapter.search(editable.toString())
                    recyclerView.smoothScrollToPosition(0)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

            dialog.setView(layout)
            var alert: AlertDialog? = null

            activity!!.runOnUiThread {
                alert = dialog.create()
                alert!!.show()
            }

            adapter.setOnAppClickListener {
                activity!!.runOnUiThread {
                    et_packages.text = StringUtils.toEditable("${et_packages.text}\n$it")
                    alert!!.cancel()
                }
            }


        }.start()
    }

    private fun showLicenseDialog() {
        LicenserDialog(activity)
            .setTitle("Opensource Licenses")
            .setCustomNoticeTitle("Licenses for Libraries: ")
            .setLibrary(
                Library(
                    "Kotlin",
                    "https://github.com/JetBrains/kotlin",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Android Support Libraries",
                    "https://developer.android.com/topic/libraries/support-library/index.html",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Firebase",
                    "https://github.com/firebase/quickstart-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Google Play Service",
                    "play-services-plugins",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "ChatKit",
                    "https://github.com/stfalcon-studio/ChatKit",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "file-dialogs",
                    "https://github.com/RustamG/file-dialogs",
                    License.MIT

                )
            )
            .setLibrary(
                Library(
                    "Licenser",
                    "https://github.com/marcoscgdev/Licenser",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "Glide",
                    "https://github.com/bumptech/glide",
                    License.BSD3
                )
            )
            .setLibrary(
                Library(
                    "SweetAlertDialog",
                    "https://github.com/F0RIS/sweet-alert-dialog",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "AndroidUtils",
                    "https://github.com/sungbin5304/AndroidUtils",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "SmoothBottomBar",
                    "https://github.com/ibrahimsn98/SmoothBottomBar",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "ToggleButtonLayout",
                    "https://github.com/savvyapps/ToggleButtonLayout",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "MaterialPopupMenu",
                    "https://github.com/zawadz88/MaterialPopupMenu",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Jsoup",
                    "https://jsoup.org/",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "commons io",
                    "https://github.com/apache/commons-io",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "hsv alph color picker android",
                    "https://github.com/martin-stone/hsv-alpha-color-picker-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "rhino android",
                    "https://github.com/F43nd1r/rhino-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "CrashReporter",
                    "https://github.com/MindorksOpenSource/CrashReporter",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "SmartTabLayout",
                    "https://github.com/ogaclejapan/SmartTabLayout",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "richeditor android",
                    "https://github.com/wasabeef/richeditor-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "TedImagePicker",
                    "https://github.com/ParkSangGwon/TedImagePicker",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Kakao SDK",
                    "https://developers.kakao.com/docs/latest/ko/sdk-download/android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Lottie",
                    "https://github.com/airbnb/lottie-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "HttpComponents",
                    "https://github.com/apache/httpcomponents-client",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "material components android",
                    "https://github.com/material-components/material-components-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "MaterialSpinner",
                    "https://github.com/jaredrummler/MaterialSpinner",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Android View Animations",
                    "https://github.com/daimajia/AndroidViewAnimations",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "SimpleCodeEditor",
                    "https://github.com/sungbin5304/SimpleCodeEditor/blob/master/README.md",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Cauly SDK",
                    "https://github.com/cauly/Android-SDK",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "Logger",
                    "https://github.com/orhanobut/logger",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "android json view",
                    "https://github.com/pvarry/android-json-viewer",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Markdown",
                    "https://github.com/tiagohm/MarkdownView",
                    License.APACHE2
                )
            )
            .show()
    }

}