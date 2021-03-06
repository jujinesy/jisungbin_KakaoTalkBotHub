package com.sungbin.autoreply.bot.three.view.hub.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.rarepebble.colorpicker.ColorPickerView
import com.rustamg.filedialogs.FileDialog
import com.rustamg.filedialogs.OpenFileDialog
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.hub.BoardDataItem
import com.sungbin.autoreply.bot.three.utils.FirebaseUtils
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.sungbintool.LayoutUtils
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import jp.wasabeef.richeditor.RichEditor
import java.io.File

@Suppress("DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")
class PostActivity : AppCompatActivity(), FileDialog.OnFileSelectedListener {

    private val storageRef = FirebaseStorage.getInstance().reference.child("Board Script")
    private val reference = FirebaseDatabase.getInstance().reference.child("Board")
    private var inputTitle: TextInputEditText? = null
    private var inputDesc: TextInputEditText? = null
    private var mEditor: RichEditor? = null
    private var alert: AlertDialog? = null
    private var mActionMode: ActionMode? = null
    private var upload: Button? = null
    private var script: File? = null
    private var version = "000"
    private var nickname: String? = null

    private var mActionCallback = @SuppressLint("NewApi")
    object : ActionMode.Callback2() {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                mActionMode = null
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.add(0, 1, 0, "H1")
                menu.add(0, 2, 0, "H2")
                menu.add(0, 3, 0, "H3")
                menu.add(0, 4, 0, "H4")
                menu.add(0, 5, 0, "H5")
                menu.add(0, 6, 0, "H6")
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                val id = item.itemId
                mEditor!!.setHeading(id)
                return false
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        nickname = ChatModuleUtils.getUser(ChatModuleUtils.getDeviceId(applicationContext))!!.name

        upload = findViewById(R.id.uploadScript)

        inputTitle = findViewById(R.id.inputTitleText)
        inputDesc = findViewById(R.id.inputDescText)

        mEditor = findViewById(R.id.editor)
        mEditor!!.setEditorFontSize(17)
        mEditor!!.setPadding(10, 10, 10, 10)

        mEditor!!.setOnTouchListener { _, _ ->
            if (mActionMode == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActionMode = startActionMode(mActionCallback, ActionMode.TYPE_FLOATING)
                }
            }
            false
        }

        upload!!.setOnClickListener { v ->
            ToastUtils.show(applicationContext,
                resources.getString(R.string.select_upload_script),
                ToastUtils.LONG, ToastUtils.SUCCESS)
            val dialog = OpenFileDialog()
            val args = Bundle()
            args.putString(FileDialog.EXTENSION, "js")
            dialog.arguments = args
            dialog.show(supportFragmentManager, OpenFileDialog::class.java.name)
        }

        findViewById<View>(R.id.action_undo).setOnClickListener { v -> mEditor!!.undo() }

        findViewById<View>(R.id.action_redo).setOnClickListener { v -> mEditor!!.redo() }

        findViewById<View>(R.id.action_bold).setOnClickListener { v -> mEditor!!.setBold() }

        findViewById<View>(R.id.action_italic).setOnClickListener { v -> mEditor!!.setItalic() }

        findViewById<View>(R.id.action_strikethrough).setOnClickListener { v -> mEditor!!.setStrikeThrough() }

        findViewById<View>(R.id.action_underline).setOnClickListener { v -> mEditor!!.setUnderline() }

        findViewById<View>(R.id.action_txt_color).setOnClickListener { v -> textColorSet() }

        findViewById<View>(R.id.action_indent).setOnClickListener { v -> mEditor!!.setIndent() }

        findViewById<View>(R.id.action_outdent).setOnClickListener { v -> mEditor!!.setOutdent() }

        findViewById<View>(R.id.action_align_left).setOnClickListener { v -> mEditor!!.setAlignLeft() }

        findViewById<View>(R.id.action_align_center).setOnClickListener { v -> mEditor!!.setAlignCenter() }

        findViewById<View>(R.id.action_align_right).setOnClickListener { v -> mEditor!!.setAlignRight() }

        findViewById<View>(R.id.action_blockquote).setOnClickListener { v -> mEditor!!.setBlockquote() }

        findViewById<View>(R.id.action_insert_bullets).setOnClickListener { v -> mEditor!!.setBullets() }

        findViewById<View>(R.id.action_insert_numbers).setOnClickListener { v -> mEditor!!.setNumbers() }

        findViewById<View>(R.id.action_insert_image).setOnClickListener {
            val ctx = this@PostActivity
            val dialog = AlertDialog.Builder(ctx)
            dialog.setTitle(R.string.insert_image_title)

            val layout = LinearLayout(ctx)
            layout.orientation = LinearLayout.VERTICAL

            val title = EditText(ctx)
            title.setHint(R.string.image_title)
            layout.addView(title)

            val adress = EditText(ctx)
            adress.setHint(R.string.input_image_address)
            layout.addView(adress)

            dialog.setView(layout)
            dialog.setNegativeButton("??????", null)
            dialog.setPositiveButton("??????") { dialogInterface, i ->
                val titleStr = title.text.toString()
                val addressStr = adress.text.toString()

                if (titleStr.isBlank() || addressStr.isBlank()) {
                    ToastUtils.show(ctx, getString(R.string.please_input_all),
                        ToastUtils.SHORT, ToastUtils.WARNING)
                } else {
                    mEditor!!.insertImage(addressStr, titleStr)
                    ToastUtils.show(ctx, getString(R.string.success_insert),
                        ToastUtils.SHORT, ToastUtils.SUCCESS)
                }
            }
            dialog.show()
        }

        findViewById<View>(R.id.action_insert_link).setOnClickListener {
            val ctx = this@PostActivity
            val dialog = AlertDialog.Builder(ctx)
            dialog.setTitle(R.string.insert_link)

            val layout = LinearLayout(ctx)
            layout.orientation = LinearLayout.VERTICAL

            val title = EditText(ctx)
            title.setHint(R.string.address_title)
            layout.addView(title)

            val adress = EditText(ctx)
            adress.setHint(R.string.address_link)
            layout.addView(adress)

            dialog.setView(
                LayoutUtils.putMargin(
                    layout
                )
            )
            dialog.setNegativeButton("??????", null)
            dialog.setPositiveButton("??????") { _, _ ->
                val titleStr = title.text.toString()
                val addressStr = adress.text.toString()
                if (titleStr.isBlank() || addressStr.isBlank()) {
                    ToastUtils.show(ctx, getString(R.string.please_input_all),
                        ToastUtils.SHORT, ToastUtils.WARNING)
                } else {
                    mEditor!!.insertImage(addressStr, titleStr)
                    ToastUtils.show(ctx, getString(R.string.success_insert),
                        ToastUtils.SHORT, ToastUtils.SUCCESS)
                }
            }
            dialog.show()
        }
    }

    override fun onFileSelected(dialog: FileDialog, file: File) {
        upload!!.text = (file.name.toString()).replace(".JS", ".js")
        script = file
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, resources.getString(R.string.upload)).setIcon(R.drawable.ic_save_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == 1) {
            if (!inputTitle!!.text!!.toString().isBlank()
                && !inputDesc!!.text!!.toString().isBlank()
                && !mEditor!!.html.isBlank()) {

                val ctx: Context = this@PostActivity
                var key = Utils.makeRandomUUID()
                if (script == null) {
                    val root = reference.child(key)
                    val boardDataItem =
                        BoardDataItem(
                            inputTitle!!.text!!.toString(),
                            inputDesc!!.text!!.toString(),
                            0,
                            0,
                            upload!!.text.toString() + ":V." + version,
                            version,
                            key,
                            nickname,
                            mEditor!!.html
                        )
                    root.setValue(boardDataItem)
                    ToastUtils.show(ctx, getString(R.string.success_request),
                        ToastUtils.SHORT, ToastUtils.SUCCESS
                    )
                    FirebaseUtils.showNoti(applicationContext,
                        "?????? ??????", inputTitle!!.text!!.toString(),
                        "NewPostNoti")
                    finish()
                } else {
                    val dialog = AlertDialog.Builder(ctx)
                    dialog.setTitle(getString(R.string.description_script_version))

                    val layout = LinearLayout(ctx)
                    layout.orientation = LinearLayout.VERTICAL

                    val textview = TextView(ctx)
                    textview.text = getString(R.string.same_version_notice)
                    textview.gravity = Gravity.CENTER
                    layout.addView(textview)

                    val input = EditText(ctx)
                    input.hint = getString(R.string.value_script_version)
                    input.inputType = 0x00000002
                    input.filters = arrayOf(InputFilter.LengthFilter(3))
                    layout.addView(input)

                    dialog.setView(LayoutUtils.putMargin(
                        layout
                    ))
                    dialog.setNeutralButton(getString(R.string.cancel_script_upload)) { _, _ ->
                        val root = reference.child(key)
                        val boardDataItem =
                            BoardDataItem(
                                inputTitle!!.text!!.toString(),
                                inputDesc!!.text!!.toString(), 0, 0, "null",
                                version, key, nickname, mEditor!!.html
                            )
                        root.setValue(boardDataItem)
                        ToastUtils.show(applicationContext,
                            getString(R.string.upload_post_cancel_script),
                            ToastUtils.SHORT, ToastUtils.SUCCESS
                        )
                        finish()
                    }
                    dialog.setPositiveButton(getString(R.string.upload)) { _, _ ->
                        version = input.text.toString()
                        if (version.isBlank()) {
                            ToastUtils.show(ctx,
                                getString(R.string.please_input_script_version),
                                ToastUtils.SHORT, ToastUtils.WARNING)
                        } else {
                            val pDialog = SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE)
                            pDialog.progressHelper.barColor = resources.getColor(R.color.colorPrimary)
                            pDialog.setTitle(resources.getString(R.string.script_uploading))
                            pDialog.setCancelable(false)
                            pDialog.show()
                            key = upload!!.text.toString().replace(".", "") + ":V:" + version
                            val file = Uri.fromFile(script)
                            val riversRef = storageRef.child(key)
                            riversRef.putFile(file).addOnFailureListener { exception ->
                                pDialog.dismissWithAnimation()
                                ToastUtils.show(ctx,
                                    "???????????? ???????????? ??????????????????.\n\n" + exception.message,
                                    ToastUtils.SHORT, ToastUtils.ERROR
                                )
                            }.addOnSuccessListener { taskSnapshot ->
                                pDialog.dismissWithAnimation()
                               ToastUtils.show(ctx,
                                   resources.getString(R.string.script_uploaded),
                                ToastUtils.SHORT, ToastUtils.SUCCESS
                               )
                                val root = reference.child(key)
                                val boardDataItem =
                                    BoardDataItem(
                                        inputTitle!!.text!!.toString(),
                                        inputDesc!!.text!!.toString(), 0, 0,
                                        upload!!.text.toString().replace(".", ""),
                                        version, key, nickname, mEditor!!.html
                                    )
                                root.setValue(boardDataItem)
                                finish()
                            }
                        }
                    }
                    dialog.setCancelable(false)
                    dialog.show()
                }
            } else {
                ToastUtils.show(applicationContext,
                    getString(R.string.please_input_all),
                    ToastUtils.SHORT, ToastUtils.WARNING
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun textColorSet() {
        alert = null
        val ctx = this@PostActivity
        val dialog = AlertDialog.Builder(ctx)

        val layout = LinearLayout(applicationContext)
        layout.setBackgroundColor(Color.parseColor("#ffffff"))
        layout.orientation = LinearLayout.VERTICAL

        val picker = ColorPickerView(ctx)
        picker.showAlpha(true)
        picker.showHex(true)
        picker.showPreview(true)
        picker.color = Color.parseColor("#00000000")
        picker.setOriginalColor(Color.parseColor("#00000000"))
        picker.addColorObserver { observableColor -> picker.setOriginalColor(observableColor.color) }
        layout.addView(picker)

        val save = Button(ctx)
        save.text = "?????? ??????"
        save.setOnClickListener { view ->
            val color = picker.color
            mEditor!!.setTextColor(color)
            ToastUtils.show(ctx, getString(R.string.color_selected),
                ToastUtils.SHORT, ToastUtils.SUCCESS
            )
            alert!!.cancel()
        }
        layout.addView(save)
        dialog.setView(
            LayoutUtils.putMargin(
                layout
            )
        )

        alert = dialog.create()
        alert!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            ToastUtils.show(applicationContext, fileUri!!.toString(),
                ToastUtils.SHORT, ToastUtils.INFO
            )
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }
}
