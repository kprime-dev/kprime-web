package it.unibz.krdb.kprime.mock

import it.unibz.krdb.kprime.domain.Repository
import it.unibz.krdb.kprime.domain.project.PrjContext
import it.unibz.krdb.kprime.domain.project.PrjContextRepositoryBuilder

class PrjRepository : RamRepository<PrjContext>() {

    val mockPrjContext = PrjContext("mock","")

    override fun findFirstBy(criteria: (PrjContext) -> Boolean): PrjContext? {
        return mockPrjContext
    }

    override fun findByCriteria(criteria: (PrjContext) -> Boolean): List<PrjContext> {
        return listOf(mockPrjContext)
    }


}

class PrjContextRepositoryMock: PrjContextRepositoryBuilder {
    private val repo = PrjRepository()
    override fun build(location: String): Repository<PrjContext> {
        return repo
    }

}