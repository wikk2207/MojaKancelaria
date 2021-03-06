package com.piwniczna.mojakancelaria.utils

import android.content.Context
import android.content.res.ColorStateList
import com.piwniczna.mojakancelaria.models.ObligationEntity
import com.piwniczna.mojakancelaria.models.ObligationType
import com.piwniczna.mojakancelaria.R
import java.math.BigDecimal
import java.time.LocalDate

class ObligationHelper {
    companion object {
        fun getTypeString(type: ObligationType, context: Context) : String {
            when (type) {
                ObligationType.HEARING -> return context.resources.getString(R.string.hearing)
                ObligationType.STAMP -> return context.resources.getString(R.string.stamps)
                ObligationType.CONTRACT -> return context.resources.getString(R.string.contract)
                ObligationType.COURT -> return context.resources.getString(R.string.court)
                ObligationType.ROOT -> return "-"
                else -> return ""
            }
        }

        fun getTypeLongString(type: ObligationType, context: Context) : String {
            when (type) {
                ObligationType.HEARING -> return context.resources.getString(R.string.hearing)
                ObligationType.STAMP -> return context.resources.getString(R.string.stamps_long)
                ObligationType.CONTRACT -> return context.resources.getString(R.string.contract)
                ObligationType.COURT -> return context.resources.getString(R.string.court)
                ObligationType.ROOT -> return "-"
                else -> return ""
            }
        }
        fun getColor(obligation: ObligationEntity, context: Context) : ColorStateList {

            if (obligation.payed >= obligation.amount) {
                return context.resources.getColorStateList(R.color.payed)
            }
            if (obligation.paymentDate < LocalDate.now()) {
                return context.resources.getColorStateList(R.color.overdue)
            }
            if (obligation.payed > BigDecimal.ZERO) {
                return context.resources.getColorStateList(R.color.inprogress)
            }
            return context.resources.getColorStateList(R.color.topay)
        }
    }
}

