package io.github.wulkanowy.sdk.hebe.register

import kotlinx.serialization.SerialName
import java.util.UUID

internal data class RegisterRequest(

    @SerialName("Certificate")
    val certificate: String,

    @SerialName("CertificateThumbprint")
    val certificateThumbprint: String,

    @SerialName("CertificateType")
    val certificateType: String = "X509",

    @SerialName("DeviceModel")
    val deviceModel: String,

    @SerialName("OS")
    val os: String = "Android",

    @SerialName("PIN")
    val pin: String,

    @SerialName("SecurityToken")
    val securityToken: String,

    @SerialName("SelfIdentifier")
    val selfIdentifier: String = UUID.randomUUID().toString(),
)
