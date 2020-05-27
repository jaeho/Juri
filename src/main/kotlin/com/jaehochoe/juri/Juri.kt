package com.jaehochoe.juri

import com.jaehochoe.juri.annotation.JuriField
import com.jaehochoe.juri.annotation.JuriIgnore
import com.jaehochoe.juri.annotation.JuriModel
import java.lang.reflect.Field
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.jvm.internal.Reflection
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaType

object Juri {

    fun toUri(model: Any): String {
        val clazz = model.javaClass
        clazz.getAnnotation(JuriModel::class.java)?.let {
            val fields = validFields(clazz).associateBy(
                { field ->
                    fieldName(field)
                },
                { field ->
                    field.isAccessible = true
                    field.get(model)
                }
            ).toMutableMap()
            val fieldAttributes = validFields(clazz).associateBy(
                { field ->
                    fieldName(field)
                },
                { field ->
                    uriField(field)
                }
            )
            val uri = StringBuilder(it.scheme.plus("://").plus(it.host))
            var closingBracketIndex = -1
            it.path.forEachIndexed { i, c ->
                if (i == 0 && c != '/')
                    uri.append('/')

                when (c) {
                    '{' -> {
                        closingBracketIndex = it.path.indexOf("}", i)
                        if (closingBracketIndex != -1) {
                            val key = it.path.substring(i + 1, it.path.indexOf("}", i))
                            uri.append(fields[key])
                            fields.remove(key)
                        }
                    }
                }

                if (closingBracketIndex == -1)
                    uri.append(c)
                else if (i == closingBracketIndex)
                    closingBracketIndex = -1

                when (c) {
                    '}' -> closingBracketIndex = -1
                }
            }

            if (fields.isNotEmpty()) {
                uri.append("?")
                fields.keys.forEach { key ->
                    fields[key]?.let { value ->
                        val isNeedUrlEncoding = fieldAttributes[key]?.isNeedUrlEncoding == true
                        uri.append(key).append('=')
                        when (value) {
                            is Array<*> -> {
                                uri.append(value.joinToString(",") { segment ->
                                    if (isNeedUrlEncoding)
                                        URLEncoder.encode(segment.toString(), "UTF-8")
                                    else
                                        segment.toString()
                                })
                            }
                            is Iterable<*> -> {
                                uri.append(value.joinToString(",") { segment ->
                                    if (isNeedUrlEncoding)
                                        URLEncoder.encode(segment.toString(), "UTF-8")
                                    else
                                        segment.toString()
                                })
                            }
                            else -> uri.append(if (isNeedUrlEncoding) URLEncoder.encode(value.toString(), "UTF-8") else value.toString())
                        }
                        uri.append("&")
                    }
                }
            }
            return uri.toString().removeSuffix("&")
        }
        throw Exception("Required @JuriModel annotation.")
    }

    fun <T> fromUri(uri: String, clazz: Class<T>): T? {
        clazz.getAnnotation(JuriModel::class.java)?.let { uriModel ->
            val baseUri = uriModel.scheme.plus("://").plus(uriModel.host)
            if (uri.startsWith(baseUri).not())
                throw Exception("Not matched scheme or host.")

            val fieldAttributes = validFields(clazz).associateBy(
                { field ->
                    fieldName(field)
                },
                { field ->
                    uriField(field)
                }
            )
            val fields = mutableMapOf<String, String?>()
            val pathSegment = uri.substring(baseUri.length + 1).split("/")
            val pathFormat = uriModel.path.split("/")
            val startWithSlash = uriModel.path.startsWith("/")
            pathFormat.forEachIndexed { index, s ->
                val i = index - if(startWithSlash) 1 else 0
                if (s.startsWith("{") && s.endsWith("}")) {
                    val value = pathSegment[if(i >= pathSegment.size) pathSegment.size - 1 else i]
                    fields[s.substring(1, s.length - 1)] = if (value.contains("?", false)) value.substring(0, value.indexOfFirst { c -> c == '?' }) else value
                }
            }
            val paramSegment = uri.split("?")
            if (paramSegment.size > 1) {
                paramSegment[1].split("&").forEach { s ->
                    s.split("=").apply {
                        if (size > 1)
                            fields[get(0)] = get(1)
                    }
                }
            }

            val fieldMap = fieldMap(clazz)
            val model: T? = try {
                clazz.newInstance()
            } catch (e: Exception) {
                val kClass = Reflection.createKotlinClass(clazz)
                kClass.primaryConstructor?.parameters?.associateBy(
                    { parameter ->
                        parameter
                    },
                    { parameter ->
                        val key = fieldMap[parameter.name] ?: parameter.name
                        val isNeedUrlEncoding = fieldAttributes[key]?.isNeedUrlEncoding == true
                        fields[key]?.let { value ->
                            when (parameter.type.javaType) {
                                Int::class.java -> value.toInt()
                                Float::class.java -> value.toFloat()
                                Double::class.java -> value.toDouble()
                                Long::class.java -> value.toLong()
                                Boolean::class.java -> value.toBoolean()
                                Array<Int>::class.java -> value.split(",").map { entry -> entry.toInt() }.toTypedArray()
                                Array<Float>::class.java -> value.split(",").map { entry -> entry.toFloat() }.toTypedArray()
                                Array<Double>::class.java -> value.split(",").map { entry -> entry.toDouble() }.toTypedArray()
                                Array<Long>::class.java -> value.split(",").map { entry -> entry.toLong() }.toTypedArray()
                                Array<String>::class.java -> value.split(",").map { entry -> if(isNeedUrlEncoding) URLDecoder.decode(entry, "UTF-8") else entry }.toTypedArray()
                                Array<Boolean>::class.java -> value.split(",").map { entry -> entry.toBoolean() }.toTypedArray()
                                String::class.java -> if(isNeedUrlEncoding) URLDecoder.decode(value, "UTF-8") else value
                                else -> null
                            }
                        }
                    }
                )?.let { map ->
                    kClass.primaryConstructor?.callBy(map) as? T
                }
            }

            validFields(clazz).forEach { field ->
                field.isAccessible = true
                val key = if (field.isAnnotationPresent(JuriField::class.java)) field.getAnnotation(JuriField::class.java).key else field.name
                val isNeedUrlEncoding = fieldAttributes[key]?.isNeedUrlEncoding == true
                fields[key]?.let { value ->
                    when (field.type) {
                        Int::class.java, Integer::class.java -> field.set(model, value.toInt())
                        Float::class.java -> field.set(model, value.toFloat())
                        Double::class.java -> field.set(model, value.toDouble())
                        Long::class.java -> field.set(model, value.toLong())
                        Boolean::class.java -> field.set(model, value.toBoolean())
                        Array<Int>::class.java -> field.set(model, value.split(",").map { entry -> entry.toInt() }.toTypedArray())
                        Array<Float>::class.java -> field.set(model, value.split(",").map { entry -> entry.toFloat() }.toTypedArray())
                        Array<Double>::class.java -> field.set(model, value.split(",").map { entry -> entry.toDouble() }.toTypedArray())
                        Array<Long>::class.java -> field.set(model, value.split(",").map { entry -> entry.toLong() }.toTypedArray())
                        Array<String>::class.java -> field.set(model, value.split(",").map { entry -> if(isNeedUrlEncoding) URLDecoder.decode(entry, "UTF-8") else entry }.toTypedArray())
                        Array<Boolean>::class.java -> field.set(model, value.split(",").map { entry -> entry.toBoolean() }.toTypedArray())
                        String::class.java -> field.set(model, if(isNeedUrlEncoding) URLDecoder.decode(value, "UTF-8") else value)
                    }
                }
            }

            try {
                clazz.getDeclaredMethod("onRestoreJuriModel", clazz)
            } catch (e: Exception) {
                null
            }?.let { method ->
                model?.let {
                    method.invoke(it, it)
                }
            }

            return model
        }
        throw Exception("Required @JuriModel annotation.")
    }

    fun <T> values(uri: String, clazz: Class<T>) : Map<String, Any> {
        val value = fromUri(uri, clazz)
        return mutableMapOf<String, Any>().apply {
            validFields(clazz).forEach { field ->
                field.isAccessible = true
                val key = if (field.isAnnotationPresent(JuriField::class.java)) field.getAnnotation(JuriField::class.java).key else field.name
                val isNeedUrlEncoding = field.isAnnotationPresent(JuriField::class.java) && field.getAnnotation(JuriField::class.java).isNeedUrlEncoding
                put(key, if(isNeedUrlEncoding) URLEncoder.encode(field.get(value).toString(), "UTF-8") else field.get(value))
            }
        }
    }

    private fun <T> validFields(clazz: Class<T>): List<Field> {
        return clazz.declaredFields.filter { field ->
            field.isAnnotationPresent(JuriIgnore::class.java).not()
        }.filter { field ->
            field.type.isAnonymousClass.not() && field.type.isLocalClass.not() && field.type.isMemberClass.not()
        }
    }

    private fun fieldName(field: Field): String {
        return when (val uriField = uriField(field)) {
            null -> field.name
            else -> if(uriField.key.isNullOrBlank()) field.name else uriField.key
        }
    }

    private fun uriField(field: Field): JuriField? {
        return when {
            field.isAnnotationPresent(JuriField::class.java) -> field.getAnnotation(JuriField::class.java)
            else -> null
        }
    }

    private fun <T> fieldMap(clazz: Class<T>): Map<String, String?> {
        return validFields(clazz).associateBy(
            { it.name },
            { field ->
                when {
                    field.isAnnotationPresent(JuriField::class.java) -> {
                        when (field.getAnnotation(JuriField::class.java).key) {
                            "" -> null
                            else -> field.getAnnotation(JuriField::class.java).key
                        }
                    }
                    else -> null
                }
            }
        )
    }

}