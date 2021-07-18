package jp.co.kenswordflow.models

import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class Connection() {
    companion object {
        const val SCAN_PERIOD = 10000L
        const val DEVICE_NAME = "Kenswordflow"
        val UUID_SERVICE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
        val UUID_CHARA   = UUID.fromString("00001143-0000-1000-8000-00805f9b34fb")
        val UUID_CHDESC  = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        enum class SlashType {
            None,
            Men,
            Dou,
            Kote,
            Issen
        }

        fun valueToSlashType(value: Byte) : SlashType {
            return if(value.compareTo(0x00) == 0) {
                SlashType.None
            } else if(value.compareTo(0x01) == 0) {
                SlashType.Men
            } else if(value.compareTo(0x02) == 0) {
                SlashType.Dou
            } else if(value.compareTo(0x03) == 0) {
                SlashType.Kote
            } else {
                SlashType.None
            }
        }
    }

    private val _isScanning = MutableLiveData(false)
    val isScanning : LiveData<Boolean>
        get() = _isScanning

    private val _isConnected = MutableLiveData(false);
    val isConnected : LiveData<Boolean>
        get() = _isConnected;

    private var context: Context? = null
    private var bleAdapter: BluetoothAdapter? = null
    private var handler: Handler = Handler(Looper.getMainLooper());
    private val lock = ReentrantLock()
    private var foundDevice: BluetoothDevice? = null;

    private val _data = MutableLiveData<SlashType>(SlashType.None)
    val data: LiveData<SlashType>
        get() = _data

    fun connect(ctx: Context, adapter: BluetoothAdapter) {
        if(isScanning.value == true) {
            return;
        }
        lock.withLock {
            context = ctx
            bleAdapter = adapter
            foundDevice = null
            _isScanning.postValue(true);
            scanLeDevice(true);
        }
    }

    private fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                handler!!.postDelayed({
                    bleAdapter!!.stopLeScan(leScanCallback)
                    _isScanning.postValue(false)
                }, SCAN_PERIOD)
                bleAdapter!!.startLeScan(leScanCallback)
            }
            else -> {
                bleAdapter!!.stopLeScan(leScanCallback)
                _isScanning.postValue(false);
            }
        }
    }

    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        if(device.name != DEVICE_NAME) {
            return@LeScanCallback;
        }

        lock.withLock {
            if(foundDevice == null) {
                // add one and finish
                foundDevice = device
                scanLeDevice(false)
                device.connectGatt(context, false, godCallback)
            }
        }
    }

    private val godCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                }
            }
        }
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    val chara = gatt.getService(UUID_SERVICE).getCharacteristic(UUID_CHARA)
                    gatt.setCharacteristicNotification(chara, true)

                    val desclist = chara.descriptors.toList()
                    val desc: BluetoothGattDescriptor = chara.getDescriptor(UUID_CHDESC)
                    desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    if (!gatt.writeDescriptor(desc)) {
                        return
                    }
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            if(characteristic.uuid == UUID_CHARA && characteristic.value.isNotEmpty()) {
                val value:Byte = characteristic.value[0]
                Log.i("Connection", "received ${value.toString()}")

                val st = valueToSlashType(value)
                _data.postValue(st)
            }
        }
    }
}
