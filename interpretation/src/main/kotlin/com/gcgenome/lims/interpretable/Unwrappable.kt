package com.gcgenome.lims.interpretable

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.data.elasticsearch.core.document.Document
import java.util.*
import java.util.stream.Collectors

interface Unwrappable {
    companion object {
        val om = ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .registerModule(JavaTimeModule())
            .registerModule(
                KotlinModule.Builder()
                    .withReflectionCacheSize(512)
                    .configure(KotlinFeature.NullToEmptyCollection, false)
                    .configure(KotlinFeature.NullToEmptyMap, false)
                    .configure(KotlinFeature.NullIsSameAsDefault, false)
                    .configure(KotlinFeature.SingletonSupport, false)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            ).setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)!!
    }
    fun Document.getStringUnwrap(key: String): String? {
        this.getStringOrDefault(key, "")
        val str =  this.getStringOrDefault(key, "")
        if(str.isEmpty()|| "." == str || "0"== str) return null
        val unwrapped = str.trim()
        if (!unwrapped.startsWith("[") || !unwrapped.endsWith("]")) return unwrapped
        val arr = om.readValue(unwrapped, Array<String>::class.java)
        return Arrays.stream(arr).collect(Collectors.joining(","))
    }
}