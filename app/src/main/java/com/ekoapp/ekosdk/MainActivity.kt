package com.ekoapp.ekosdk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ekoapp.ekosdk.uikit.chat.messages.EkoMessageListActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, EkoMessageListActivity::class.java)
        startActivity(intent)

    }
}