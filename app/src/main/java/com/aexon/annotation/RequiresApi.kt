package com.aexon.annotation

import kotlin.annotation.AnnotationTarget

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
annotation class RequiresApi(val value: Int = 1, val api: Int = 1) {
	companion object {
		const val GINGERBREAD = 9
		const val HONEYCOMB = 11
		const val ICE_CREAM_SANDWICH = 14
		const val JELLY_BEAN = 16
		const val KITKAT = 19
		const val LOLLIPOP = 21
		const val MARSHMALLOW = 23
		const val NOUGAT = 24
		const val OREO = 26
		const val OREO_MR1 = 27
		const val PIE = 28
		const val Q = 29
		const val R = 30
		const val S = 31
		const val S_V2 = 32
		const val TIRAMISU = 33
		const val UPSIDE_DOWN_CAKE = 34
		const val VANILLA_ICE_CREAM = 35
		const val BAKLAVA = 36
	}
}