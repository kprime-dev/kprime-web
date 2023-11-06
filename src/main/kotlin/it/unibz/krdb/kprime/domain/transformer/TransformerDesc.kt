package it.unibz.krdb.kprime.domain.transformer

data class TransformerDesc(
        var name:String,
        val composeMatcher:String,
        val composeTemplate:String,
        val decomposeMatcher:String,
        val decomposeTemplate:String)