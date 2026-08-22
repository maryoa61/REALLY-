package com.stitchvpn.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.stitchvpn.core.AdvancedXrayConfigBuilder
import com.stitchvpn.core.VlessAdvancedParser

class XrayVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private val socksPort = 10808
    private var xrayProcess: Process? = null

    companion object {
        const val EXTRA_VLESS_LINK = "vless_link"
        private const val NOTIFICATION_CHANNEL_ID = "stitch_vpn_channel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val vlessLink = intent?.getStringExtra(EXTRA_VLESS_LINK)

        if (vlessLink != null) {
            startForeground()
            startVpn(vlessLink)
        }
        return START_STICKY
    }

    private fun startVpn(vlessLink: String) {
        val config = VlessAdvancedParser.parse(vlessLink) ?: return
        val xrayJsonConfig = AdvancedXrayConfigBuilder.build(config, socksPort)

        startCore(xrayJsonConfig)
        setupVpnTunnel()
        startTun2Socks()
    }

    private fun setupVpnTunnel() {
        val builder = Builder()
        builder.setSession("StitchVPN")
        builder.addAddress("172.19.0.1", 30)
        builder.addDnsServer("1.1.1.1")
        builder.addDnsServer("8.8.8.8")
        builder.addRoute("0.0.0.0", 0)
        
        vpnInterface = builder.establish()
    }

    private fun startTun2Socks() {
        // فراخوانی کتابخانه NDK hev-socks5-tunnel
        // HevSocks5Tunnel.start(vpnInterface?.detachFd(), "127.0.0.1", socksPort)
    }

    private fun startCore(configJson: String) {
        // فراخوانی کتابخانه libxray.so از طریق gomobile
        // XrayCore.start(configJson, this)
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "StitchVPN Service",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val notificationIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("StitchVPN")
            .setContentText("Connection is active")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        // XrayCore.stop()
        // HevSocks5Tunnel.stop()
        vpnInterface?.close()
        vpnInterface = null
    }
}
