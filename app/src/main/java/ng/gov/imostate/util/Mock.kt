package ng.gov.imostate.util

import ng.gov.imostate.model.Transaction
import ng.gov.imostate.model.Update

object Mock {

    fun getTransactions(): List<Transaction> {
        return listOf(
            Transaction("1", "Credit: NURTW", 100000.00, "17/12/2022"),
            Transaction("2", "Credit: NURTW",2100800.00, "15/12/2021"),
            Transaction("3", "Credit: NURTW",100000.00, "09/11/2021"),
            Transaction("4", "Credit: NURTW",45000.00, "02/10/2021"),
            Transaction("5", "Credit: NURTW",2100800.00, "15/12/2021"),
            Transaction("6", "Credit: NURTW",300.00, "09/11/2021"),
            Transaction("7", "Credit: NURTW",6000.00, "02/10/2021"),
            Transaction("8", "Credit: NURTW",29000.00, "15/12/2021"),
            Transaction("9", "Credit: NURTW",180000.00, "09/11/2021"),
            Transaction("10", "Credit: NURTW",9000.00, "02/10/2021")
        )
    }

    fun getUpdates(): List<Update> {
        return listOf(
            Update("1", "Daily Prices Updated! - Check Now"),
            Update("2", "Route Exempted: World Bank - Orji"),
            Update("3", "Route Exempted: World Bank - Orji, Amakohia - Warehouse, Orlu - Orji")
        )
    }
}