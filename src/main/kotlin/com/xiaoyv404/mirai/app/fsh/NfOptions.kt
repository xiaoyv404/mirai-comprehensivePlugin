package com.xiaoyv404.mirai.app.fsh

import org.apache.commons.cli.*

class NfOptions : Options() {
    init {
        addOption("h", "help", false, "查看使用说明")
    }
}