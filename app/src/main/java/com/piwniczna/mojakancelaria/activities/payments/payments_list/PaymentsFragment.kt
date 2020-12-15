package com.piwniczna.mojakancelaria.activities.payments.payments_list

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.piwniczna.mojakancelaria.DB.DataService
import com.piwniczna.mojakancelaria.Models.CaseEntity
import com.piwniczna.mojakancelaria.Models.ClientEntity
import com.piwniczna.mojakancelaria.Models.PaymentEntity
import com.piwniczna.mojakancelaria.R
import com.piwniczna.mojakancelaria.activities.payments.add_payment.AddPaymentFragment
import com.piwniczna.mojakancelaria.activities.cases.case_details.CaseDetailsFragment
import com.piwniczna.mojakancelaria.activities.payments.payment_details.PaymentDetailsFragment

import kotlin.collections.ArrayList


class PaymentsFragment(var client: ClientEntity, val case: CaseEntity)  : Fragment() {
    lateinit var paymentsListView: ListView
    lateinit var paymentsList: ArrayList<PaymentEntity>
    lateinit var paymentsListAdapter: PaymentsListAdapter
    lateinit var dbService: DataService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_payments, container, false)
        dbService = DataService(this.context!!)

        paymentsListView = view.findViewById(R.id.payments_list_view) as ListView
        paymentsList = arrayListOf()
        paymentsListAdapter = PaymentsListAdapter(this.context!!, paymentsList)
        paymentsListView.adapter = paymentsListAdapter

        paymentsListView.setOnItemClickListener { _, _, position, _ ->
            openPaymentDetailsFragment(position)
        }


        val addButton = view.findViewById<Button>(R.id.add_payment_button)
        addButton.setOnClickListener { handleAddPayment(it) }

        getPaymentsFromDB()

        return view
    }

    fun onBackPressed() {
        fragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                CaseDetailsFragment(client, case)
        )?.commit()
    }

    private fun getPaymentsFromDB() {
        AsyncTask.execute {
            val payments = dbService.getPayments(client.id)
            paymentsList.clear()
            paymentsList.addAll(payments)
            activity?.runOnUiThread {
                paymentsListAdapter.notifyDataSetChanged()
            }
        }
    }


    private fun handleAddPayment(view: View) {
        fragmentManager?.beginTransaction()?.replace(
            R.id.fragment_container,
            AddPaymentFragment(client, case)
        )?.commit()
    }

    private fun openPaymentDetailsFragment(paymentPosition: Int) {
        fragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                PaymentDetailsFragment(client, case, paymentsList[paymentPosition])
        )?.commit()
    }

}
