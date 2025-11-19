package com.gcgenome.lims

import com.greencross.lims.jandiwebhook.JandiWebhook
import com.greencross.lims.jandiwebhook.Webhook
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebhookConfig {
    @Value("\${webhook.url}")
    private val webhook: String? = null
    @Bean("w")
    fun webhook(): Webhook = JandiWebhook(webhook)
}