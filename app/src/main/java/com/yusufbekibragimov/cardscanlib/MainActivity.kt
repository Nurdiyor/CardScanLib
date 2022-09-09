package com.yusufbekibragimov.cardscanlib

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.yusufbekibragimov.cardscanlib.databinding.ActivityMainBinding
import uz.scan_card.cardscan.CreditCard
import uz.scan_card.cardscan.ScanActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardData.setOnClickListener {
//            startScanActivity()
            val intent = ScanActivity.buildIntent(
                this,
                true,
                null,
                "Поместите карту в рамку",
                AppCompatResources.getDrawable(this,R.drawable.ic_flash_on),
                AppCompatResources.getDrawable(this,R.drawable.ic_flash_on),
            )
            startActivityForResult(intent,ScanActivity.SCAN_REQUEST_CODE)
        }
    }

    private fun startScanActivity() {

        val intent = ScanActivity.buildIntent(
            this,
            true,
            null,
            "Поместите карту в рамку",
            null, //    AppCompatResources.getDrawable(this,R.drawable.ic_flash_on),
            null, //    AppCompatResources.getDrawable(this,R.drawable.ic_flash_off),
        )

        activityLauncher.launch(intent)

    }

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val scanResult : CreditCard?= ScanActivity.creditCardFromResult(intent)
                binding.cardData.text = scanResult.toString()
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (ScanActivity.isScanResult(requestCode)) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val scanResult = ScanActivity.creditCardFromResult(data)
                val resultMap = mutableMapOf<String, String?>()
                resultMap["card_number"] = scanResult?.number
                resultMap["expiry_month"] = scanResult?.expiryMonth
                resultMap["expiry_year"] = scanResult?.expiryYear
                binding.cardData.text = scanResult.toString()
            }
        }
    }

}