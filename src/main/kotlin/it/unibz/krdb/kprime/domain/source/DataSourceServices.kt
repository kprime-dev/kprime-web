package it.unibz.krdb.kprime.domain.source

import it.unibz.krdb.kprime.domain.project.PrjContext
import unibz.cs.semint.kprime.domain.datasource.DataSource

interface DataSourceServices {

    fun newWorkingDataSource(datasourceName: String): DataSource?
    fun newContextWorkingDataSource(prjContext: PrjContext, datasourceName: String): DataSource?
    fun newWorkingDataSourceOrH2(datasourceName: String): DataSource
}