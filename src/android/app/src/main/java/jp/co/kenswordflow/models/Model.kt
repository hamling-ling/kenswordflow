package jp.co.kenswordflow.models

import android.bluetooth.BluetoothAdapter

class Model private constructor() {
    companion object {
        @Volatile
        private var INSTANCE : Model? = null

        fun getInstance(): Model {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Model()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    val connection = Connection();
    val nano: Nano = Nano()
}
