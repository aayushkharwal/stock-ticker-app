package com.example.tickerappxml

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import com.example.tickerappxml.databinding.FragmentTransactionBinding
import com.plcoding.animatedsplashscreen.StockViewModel
import models.TransactionDetails
import java.lang.Double
import java.math.BigDecimal
import java.math.RoundingMode

class TransactionFragment(context: Context, private val viewModel: StockViewModel, private val item: TransactionDetails) : Dialog(context) {

    private lateinit var binding: FragmentTransactionBinding

    override fun onStart() {
        super.onStart()
        window?.apply {
            // Ensure the dialog is as wide as the parent
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Apply a transparent background to allow custom rounded corners
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            decorView.setPadding(0,50, 0, 50)
            window!!.setLayout(context.resources.displayMetrics.widthPixels - 100 * 2, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.transactionTitle.text = "Trade ${item.name} shares"
        binding.transactionBalance.text = "$${BigDecimal(item.balance).setScale(2, RoundingMode.HALF_EVEN).toString()} to buy ${item.ticker}"

        binding.transactionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("Before TextChange: ", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("On TextChange: ", s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

                if (s.isNullOrEmpty()) {
                    binding.transactionTotal.text = "0*\$0.00/share = \$0.00"
                } else {
                    var shares = Double.valueOf(s.toString())
                    var totalPrice = shares*item.currentPrice
                    binding.transactionTotal.text = "${s.toString()}*$${BigDecimal(item.currentPrice).setScale(2, RoundingMode.HALF_EVEN).toString()} " +
                            "= ${BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_EVEN).toString()}"
                }

            }
        })


        binding.transactionBuy.setOnClickListener{
            val inputText = binding.transactionInput.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_LONG).show()
            } else {

                val sharesToBuy = inputText.toInt()
                val totalPrice = sharesToBuy * item.currentPrice

                if (totalPrice > item.balance) {
                    Toast.makeText(context, "Not enough balance to buy", Toast.LENGTH_LONG).show()
                } else {
                    // Handle buy logic here
                    val details = TransactionDetails(
                        ticker = item.ticker,
                        name = item.name,
                        quantity = sharesToBuy,
                        totalCost = totalPrice,
                        balance = item.balance,
                        currentPrice = item.currentPrice
                    )
                    viewModel.executeTransaction(details, "Buy")

                    Log.d("BBBBB", details.toString())

                    dismiss()
                    val acknowledgeDialog = AcknowledgeFragment(context, details, "bought")
                    acknowledgeDialog.show()

//                    Toast.makeText(context, "Purchase successful", Toast.LENGTH_LONG).show()
                }

            }
        }

        binding.transactionSell.setOnClickListener{
            val inputText = binding.transactionInput.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_LONG).show()
            } else {
                val sharesToSell = inputText.toInt()
                val totalPrice = sharesToSell * item.currentPrice

                if (sharesToSell > item.quantity) {
                    Toast.makeText(context, "Not enough shares to sell", Toast.LENGTH_LONG).show()
                } else {
                    val details = TransactionDetails(
                        ticker = item.ticker,
                        name = item.name,
                        quantity = sharesToSell,
                        totalCost = totalPrice,
                        balance = item.balance,
                        currentPrice = item.currentPrice
                    )
                    viewModel.executeTransaction(details, "Sell")

                    dismiss()
                    val acknowledgeDialog = AcknowledgeFragment(context, details, "sold")
                    acknowledgeDialog.show()

//                    Toast.makeText(context, "Sale successful", Toast.LENGTH_LONG).show()
                }
            }


        }

    }

}