package com.sungbin.autoreply.bot.three.view.bot.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.airbnb.lottie.LottieAnimationView
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardActivity
import com.sungbin.autoreply.bot.three.view.ui.bottombar.SmoothBottomBar
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils

class AddBotFragment constructor(
    private val fragmentManage: FragmentManager,
    private val view: Int,
    private val bottombar: SmoothBottomBar,
    private val textview: TextView,
    private val isTutorial: Boolean
) : Fragment() {

    var botTypeTs: ToggleButtonLayout? = null
    var inputBotNameEt: EditText? = null
    var addBotBtn: Button? = null
    var isJsBot = true

    private lateinit var viewLine: View
    private lateinit var tvWelcome: TextView
    private lateinit var btnNext: Button
    private lateinit var lavWelcome: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        botTypeTs = view.findViewById(R.id.tbl_langauge)
        inputBotNameEt = view.findViewById(R.id.et_input_bot_name)
        addBotBtn = view.findViewById(R.id.btn_bot_add)

        viewLine = view.findViewById(R.id.view_line)
        tvWelcome = view.findViewById(R.id.tv_welcome)
        btnNext = view.findViewById(R.id.btn_next)
        lavWelcome = view.findViewById(R.id.lav_welcome)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (isTutorial) {
            botTypeTs!!.alpha = 0.1f
            inputBotNameEt!!.alpha = 0.1f
            addBotBtn!!.alpha = 0.1f
            viewLine.alpha = 0.1f

            botTypeTs!!.isEnabled = false
            inputBotNameEt!!.isEnabled = false
            addBotBtn!!.isEnabled = false

            lavWelcome.visibility = View.VISIBLE
            tvWelcome.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE

            lavWelcome.playAnimation()
        }

        btnNext.setOnClickListener {
            textview.text = getString(R.string.setting)
            val fragmentTransaction = fragmentManage.beginTransaction()
            fragmentTransaction.replace(
                view,
                SettingFragment(fragmentManage, view, bottombar, textview, true)
            ).commit()
            bottombar.setActiveItem(4)
            DashboardActivity.bottomBarIndex = 4
        }

        val favoriteFanguage = DataUtils.readData(context!!, "FavoriteLanguage", "null")
        if (favoriteFanguage != "null") {
            if (favoriteFanguage == "??????????????????") {
                botTypeTs!!.setToggled(R.id.javascript, true)
            } else {
                botTypeTs!!.setToggled(R.id.simple, true)
                isJsBot = false
            }
        }

        botTypeTs!!.onToggledListener = { _, toggle, selected ->
            if (toggle.id == R.id.simple && selected) isJsBot = false
            else if (toggle.id == R.id.javascript && selected) isJsBot = true
        }
        inputBotNameEt!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().isNotBlank()) {
                    addBotBtn!!.backgroundTintList = ContextCompat.getColorStateList(
                        context!!,
                        R.color.colorPrimary
                    )
                    addBotBtn!!.isEnabled = true
                } else {
                    addBotBtn!!.backgroundTintList = ContextCompat.getColorStateList(
                        context!!,
                        R.color.colorAccent
                    )
                    addBotBtn!!.isEnabled = false
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        addBotBtn!!.setOnClickListener {
            if (botTypeTs!!.selectedToggles().isEmpty() && addBotBtn!!.isClickable) {
                ToastUtils.show(
                    context!!, getString(R.string.choose_bot_type),
                    ToastUtils.SHORT, ToastUtils.WARNING
                )
            } else {
                val botNameText = inputBotNameEt!!.text.toString()
                val fragmentTransaction = fragmentManage.beginTransaction()
                textview.text = getString(R.string.dashboard)
                fragmentTransaction.replace(
                    view,
                    DashboardFragment(
                        fragmentManage,
                        view,
                        bottombar,
                        textview,
                        false
                    )
                ).commit()
                bottombar.setActiveItem(0)
                if (isJsBot) {
                    StorageUtils.save(
                        "${BotPathManager.JS}/$botNameText.js",
                        """
                        function response(room, msg, sender, isGroupChat, replier, ImageDB, packageName) {
                            /*
                            @String room : ???????????? ?????? ??? ?????? ??????
                            @String sender : ???????????? ?????? ????????? ?????? ??????
                            @Boolean isGroupChat : ???????????? ?????? ?????? ???????????????(?????????????????? ??????????????? ??????) ?????? ??????
                            @Object replier : ???????????? ?????? ?????? Action??? ?????? Object ??????
                            @Object ImageDB : ????????? ?????? ???????????? ?????? Object ??????
                            @String package : ???????????? ?????? ????????? ???????????? ??????
                            */
                        }    
                    """.trimIndent()
                    )
                } else {
                    StorageUtils.createFolder("${BotPathManager.SIMPLE}/$botNameText")
                }
                ToastUtils.show(
                    context!!,
                    getString(R.string.added_new_bot),
                    ToastUtils.SHORT, ToastUtils.SUCCESS
                )
            }
        }
    }
}
