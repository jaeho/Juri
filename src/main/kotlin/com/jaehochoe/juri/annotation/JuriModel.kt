package com.jaehochoe.juri.annotation

/**
 * Created by jaehochoe on 2020/03/31.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JuriModel(val scheme: String, val host: String, val path: String = "")