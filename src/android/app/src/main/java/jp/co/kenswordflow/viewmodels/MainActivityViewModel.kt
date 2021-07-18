package jp.co.kenswordflow.viewmodels

import android.bluetooth.BluetoothAdapter
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import jp.co.kenswordflow.models.Connection
import jp.co.kenswordflow.models.Model

class MainActivityViewModel : ViewModel() {

    val status: LiveData<String> = Transformations.map(
        Model.getInstance().connection.isConnected
    ) {
        if(it == true) {
            "connected"
        } else {
            "NOT connected"
        }
    }

    val slash: LiveData<String> = Transformations.map(
        Model.getInstance().connection.data
    ) {
        when(it) {
            Connection.Companion.SlashType.None -> ""
            Connection.Companion.SlashType.Men -> "Men"
            Connection.Companion.SlashType.Dou -> "Dou"
            Connection.Companion.SlashType.Kote -> "Cote"
            Connection.Companion.SlashType.Issen -> "Issen"
        }
    }

    fun onConnectClicked(context: Context, bleAdapter: BluetoothAdapter) {
        Model.getInstance().connection.connect(context, bleAdapter)
    }
}