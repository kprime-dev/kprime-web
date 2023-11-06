package it.unibz.krdb.kprime.domain.search

typealias FileName = String
typealias FilePosition = String
enum class SearchResultType { GOAL, TERM, STORY, ERROR }

data class SearchResult(val fileName: FileName, val position: FilePosition, val type: SearchResultType)
