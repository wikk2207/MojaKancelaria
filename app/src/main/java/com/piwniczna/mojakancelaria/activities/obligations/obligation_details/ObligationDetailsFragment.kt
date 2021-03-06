package com.piwniczna.mojakancelaria.activities.obligations.obligation_details

import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.piwniczna.mojakancelaria.DB.DataService
import com.piwniczna.mojakancelaria.models.CaseEntity
import com.piwniczna.mojakancelaria.models.ClientEntity
import com.piwniczna.mojakancelaria.models.ObligationEntity
import com.piwniczna.mojakancelaria.R
import com.piwniczna.mojakancelaria.activities.cases.ObligationsFragment
import com.piwniczna.mojakancelaria.utils.ObligationHelper
import com.piwniczna.mojakancelaria.activities.obligations.update_obligation.UpdateObligationFragment
import com.piwniczna.mojakancelaria.utils.SpannedText

class ObligationDetailsFragment(var client: ClientEntity, val case: CaseEntity, var obligation: ObligationEntity) : Fragment() {
    lateinit var dbService: DataService

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_obligation_details, container, false)
        dbService = DataService(this.context!!)

        val nameTextView = view.findViewById(R.id.obligation_name_value) as TextView
        val typeTextView = view.findViewById(R.id.obligation_type_value) as TextView
        val amountTextView = view.findViewById(R.id.obligation_amount_value) as TextView
        val payedAmountTextView = view.findViewById(R.id.obligation_payed_amount_value) as TextView
        val creationDateTextView = view.findViewById(R.id.obligation_creation_date_value) as TextView
        val paymentDateTextView = view.findViewById(R.id.oligation_payment_date_value) as TextView


        nameTextView.text = obligation.name
        typeTextView.text = ObligationHelper.getTypeLongString(obligation.type, this.context!!)
        amountTextView.text = getString(R.string.amount_with_currency, obligation.amount.setScale(2).toString())
        payedAmountTextView.text =  getString(R.string.amount_with_currency, obligation.payed.setScale(2).toString())
        creationDateTextView.text = obligation.convertDate()
        paymentDateTextView.text = obligation.convertPaymentDate()

        val editButton = view.findViewById(R.id.obligation_edit_button) as Button
        editButton.setOnClickListener { openEditObligationsFragment() }

        val deleteButton = view.findViewById(R.id.obligation_delete_button) as Button
        deleteButton.setOnClickListener { deleteObligation() }

        val caseNameTextView = view.findViewById<TextView>(R.id.case_name_title)
        caseNameTextView.text = case.name

        return view
    }

    private fun openEditObligationsFragment() {
        fragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                UpdateObligationFragment(client, case, obligation)
        )?.commit()
    }

    private fun deleteObligation() {
        val builder = AlertDialog.Builder(this.context)

        val message = SpannedText.getSpannedText(getString(R.string.delete_obligation, obligation.name))

        builder.setTitle(R.string.warning)
        builder.setMessage(message)

        builder.setPositiveButton(R.string.delete) { dialog, which ->

            builder.setTitle(R.string.deleting_obligation)
            builder.setMessage(R.string.are_you_sure)

            builder.setPositiveButton(R.string.yes) { dialog, which -> deleteObligationFromDB() }

            builder.setNegativeButton(R.string.no) { dialog, which -> }

            builder.show()

        }

        builder.setNegativeButton(R.string.cancel) { dialog, which -> }

        builder.show()
    }

    private fun deleteObligationFromDB() {
        AsyncTask.execute {
            dbService.deleteObligation(obligation)
            fragmentManager?.beginTransaction()?.replace(
                    R.id.fragment_container,
                    ObligationsFragment(client, case)
            )?.commit()
        }
    }

    fun onBackPressed() {
        fragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                ObligationsFragment(client, case)
        )?.commit()
    }
}