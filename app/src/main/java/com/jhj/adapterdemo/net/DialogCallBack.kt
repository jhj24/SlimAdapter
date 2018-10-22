package com.jhj.adapterdemo.net

import android.app.Activity

import com.jhj.httplibrary.callback.HttpDialogCallback

/**
 * Created by jhj on 18-10-22.
 */

open class DialogCallback<T>(activity: Activity, msg: String) : HttpDialogCallback<T>(activity, msg) {
    override fun onStringResponse(str: String?) {
        val result: DataResult<T>
        try {
            result = DataResult<T>().parseJson(str, getTClazz())
        } catch (e: Exception) {
            hanlder.post { callFailure(4) }
            return
        }

        if (result.result != 1) {
            hanlder.post { callFailure(5, result.msg) }
            return
        }

        hanlder.post {
            onSuccess(result.data)
            onFinish()
        }
    }
}
