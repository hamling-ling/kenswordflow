package jp.co.kenswordflow.models

import android.bluetooth.BluetoothGatt
import java.util.*

class GattReceiver(private var gatt: BluetoothGatt) {
    companion object {
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2
    }
}