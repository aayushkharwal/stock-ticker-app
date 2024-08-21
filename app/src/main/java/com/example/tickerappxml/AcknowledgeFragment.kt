package com.example.tickerappxml

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.example.tickerappxml.databinding.FragmentAcknowledgeBinding
import models.TransactionDetails


class AcknowledgeFragment(
    context: Context,
    private val item: TransactionDetails,
    private val action: String
): Dialog(context) {

    private lateinit var binding: FragmentAcknowledgeBinding

    override fun onStart() {
        super.onStart()
        window?.apply {
            // Ensure the dialog is as wide as the parent
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Apply a transparent background to allow custom rounded corners
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window!!.setLayout(context.resources.displayMetrics.widthPixels - 100 * 2, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAcknowledgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.acknowledgeInfo.text = "You have successfully ${action} ${item.quantity} shares of ${item.ticker}"
        binding.acknowledgeDone.setOnClickListener {
            dismiss()
        }
    }



}