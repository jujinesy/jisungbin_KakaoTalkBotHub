package com.sungbin.autoreply.bot.three.utils.bot

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.receiver.NotificationActionClickReceiver
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardActivity
import com.sungbin.sungbintool.DataUtils

object BotNotificationManager {

    private val smallIcon: Int
        get() = R.drawable.icon

    private fun getAppName(context: Context): String {
        return context.getString(R.string.app_name)
    }

    private fun getTitle(context: Context): String {
        return context.getString(R.string.kakaotalk_bot)
    }

    private fun getBotOnContent(context: Context): String {
        return context.getString(R.string.now_bot_working)
    }

    private fun getBotOffContent(context: Context): String {
        return context.getString(R.string.now_bot_stop)
    }

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val group = NotificationChannelGroup(
                getAppName(context),
                getAppName(context)
            )
            getManager(context).createNotificationChannelGroup(group)

            val channelMessage = NotificationChannel(
                getAppName(context),
                getAppName(context),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelMessage.description = getAppName(context)
            channelMessage.group = context.getString(R.string.app_name)
            channelMessage.lightColor = R.color.colorAccent
            channelMessage.enableVibration(true)
            channelMessage.vibrationPattern = longArrayOf(0, 0)
            getManager(context).createNotificationChannel(channelMessage)
        }
    }

    private fun getManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun setBuilder(context: Context, builder: NotificationCompat.Builder) {
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, NotificationActionClickReceiver::class.java)
                .putExtra("value", "start"),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val startAction = NotificationCompat.Action(
            R.drawable.icon,
            context.getString(R.string.bot_start),
            startPendingIntent
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, NotificationActionClickReceiver::class.java)
                .putExtra("value", "stop"),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopAction = NotificationCompat.Action(
            R.drawable.icon,
            context.getString(R.string.bot_off),
            stopPendingIntent
        )

        val deletePendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, NotificationActionClickReceiver::class.java)
                .putExtra("value", "delete"),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val deleteAction = NotificationCompat.Action(
            R.drawable.icon,
            context.getString(R.string.close),
            deletePendingIntent
        )

        val reloadPendingIntent = PendingIntent.getBroadcast(
            context,
            3,
            Intent(context, NotificationActionClickReceiver::class.java)
                .putExtra("value", "reload"),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val reloadAction = NotificationCompat.Action(
            R.drawable.icon,
            context.getString(R.string.bot_reload),
            reloadPendingIntent
        )

        val contentIntent =
            PendingIntent.getActivity(
                context,
                4,
                Intent(context, DashboardActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val botPowerOn = DataUtils.readData(context, "BotOn", "true").toBoolean()
        if (botPowerOn) { //??? ?????? -> ??? ??????
            builder.addAction(stopAction)
            builder.setContentText(getBotOnContent(context))
        } else { //??? ?????? -> ??? ??????, ?????? ??????
            builder.addAction(startAction)
            builder.addAction(deleteAction)
            builder.setContentText(getBotOffContent(context))
        }

        builder.addAction(reloadAction)
        builder.setContentIntent(contentIntent)
    }

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = NotificationCompat.Builder(
                context,
                context.getString(R.string.app_name)
            )
                .setContentTitle(getTitle(context))
                .setSmallIcon(smallIcon)
                .setAutoCancel(false)
                .setOngoing(true)

            setBuilder(context, builder)

            getManager(context).notify(1000, builder.build())
        } else {
            val builder = NotificationCompat.Builder(context)
                .setContentTitle(getTitle(context))
                .setSmallIcon(smallIcon)
                .setAutoCancel(false)
                .setOngoing(true)

            setBuilder(context, builder)

            getManager(context).notify(1000, builder.build())
        }
    }

    fun delete(context: Context) {
        NotificationManagerCompat.from(context).cancel(1000)
    }

}