package com.stitchvpn.core

import android.net.Uri
import java.net.URLDecoder

data class AdvancedVlessConfig(
    val address: String,
    val port: Int,
    val uuid: String,
    val flow: String?,
    val encryption: String?,
    
    // Security & TLS & uTLS
    val security: String,
    val sni: String?,
    val fingerprint: String?,
    val alpn: String?,
    val allowInsecure: Boolean,
    
    // REALITY Specifics
    val publicKey: String?,
    val shortId: String?,
    val spiderX: String?,
    
    // Network / Transport Layer
    val network: String,
    val path: String?,
    val host: String?,
    
    // gRPC Specifics
    val serviceName: String?,
    val multiMode: Boolean,
    
    // XHTTP Specifics
    val xhttpMode: String?,
    val xhttpExtra: String?
)

object VlessAdvancedParser {

    fun parse(uriStr: String): AdvancedVlessConfig? {
        if (!uriStr.startsWith("vless://")) return null
        
        val uri = Uri.parse(uriStr)
        val address = uri.host ?: return null
        val port = uri.port ?: return null
        val uuid = uri.userInfo ?: return null
        
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) ?: "" }
        
        val security = params["security"]?.lowercase() ?: "none"
        val network = params["type"]?.lowercase() ?: "tcp"
        
        val sni = params["sni"]?.ifEmpty { params["peer"] }?.let { URLDecoder.decode(it, "UTF-8") }
        val alpn = params["alpn"]?.let { URLDecoder.decode(it, "UTF-8") }
        val fingerprint = params["fp"]?.ifEmpty { "chrome" }
        val allowInsecure = params["allowInsecure"]?.toBoolean() ?: false

        val publicKey = params["pbk"]
        val shortId = params["sid"]
        val spiderX = params["spx"]?.let { URLDecoder.decode(it, "UTF-8") }

        val flow = params["flow"]?.takeIf { it.isNotEmpty() }

        val path = params["path"]?.let { URLDecoder.decode(it, "UTF-8") }
        val host = params["host"]?.let { URLDecoder.decode(it, "UTF-8") }
        val serviceName = params["serviceName"]
        val multiMode = params["mode"]?.equals("multi", ignoreCase = true) ?: false
        
        val xhttpMode = params["mode"]?.takeIf { network == "xhttp" }
        val xhttpExtra = params["extra"]?.let { URLDecoder.decode(it, "UTF-8") }

        return AdvancedVlessConfig(
            address = address, port = port, uuid = uuid, flow = flow,
            encryption = params["encryption"] ?: "none",
            security = security, sni = sni, fingerprint = fingerprint,
            alpn = alpn, allowInsecure = allowInsecure,
            publicKey = publicKey, shortId = shortId, spiderX = spiderX,
            network = network, path = path, host = host,
            serviceName = serviceName, multiMode = multiMode,
            xhttpMode = xhttpMode, xhttpExtra = xhttpExtra
        )
    }
}
