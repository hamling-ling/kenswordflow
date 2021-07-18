package jp.co.kenswordflow.views

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import jp.co.kenswordflow.R
import jp.co.kenswordflow.databinding.ActivityMainBinding
import jp.co.kenswordflow.viewmodels.MainActivityViewModel
import jp.co.kenswordflow.viewmodels.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory()
        ).get(MainActivityViewModel::class.java)

        binding.connectButton.setOnClickListener() {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = bluetoothManager.adapter
            if(!adapter.isEnabled) {
                return@setOnClickListener
            }
            viewModel.onConnectClicked(applicationContext, adapter)
        }
    }
}
