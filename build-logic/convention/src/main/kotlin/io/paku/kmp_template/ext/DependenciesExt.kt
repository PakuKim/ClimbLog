package io.paku.kmp_template.ext

import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.implementations(vararg notations: Any) {
    notations.forEach { notation ->
        add("implementation", notation)
    }
}

fun DependencyHandlerScope.debugImplementations(vararg notations: Any) {
    notations.forEach { notation ->
        add("debugImplementation", notation)
    }
}

fun DependencyHandlerScope.testImplementations(vararg notations: Any) {
    notations.forEach { notation ->
        add("testImplementation", notation)
    }
}