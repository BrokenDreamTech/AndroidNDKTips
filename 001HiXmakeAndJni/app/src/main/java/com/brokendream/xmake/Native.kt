package com.brokendream.xmake

object Native {
    init {
        System.loadLibrary("hi")
    }
    external fun hiString(): String
}