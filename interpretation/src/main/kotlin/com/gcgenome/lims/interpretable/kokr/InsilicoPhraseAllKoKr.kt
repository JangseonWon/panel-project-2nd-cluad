package com.gcgenome.lims.interpretable.kokr

import com.gcgenome.lims.interpretable.InsilicoPhrase
import com.gcgenome.lims.interpretable.variant.Snv
import java.util.*
import java.util.stream.Collectors

internal class InsilicoPhraseAllKoKr: InsilicoPhrase {
    override fun insilico(variant: Snv): String? {
        val sift = variant.sift()
        val polyphen = variant.polyphen()
        val polyphen2 = variant.polyphen2()
        val mutationtaster = variant.mutationtaster()
        return if(sift==null|| (polyphen==null && polyphen2==null) || mutationtaster==null) null
        else {
            val results = LinkedList<Insilico>()
            if(sift!=null) results.add(Insilico("SIFT", "T".equals(sift.toString(), true).not()))
            if(polyphen!=null) results.add(Insilico("PolyPhen", "B".equals(polyphen.toString(), true).not()))
            if(polyphen2!=null) results.add(Insilico("PolyPhen-2", "B".equals(polyphen2.toString(), true).not()))
            if(mutationtaster!=null) results.add(Insilico("MutationTaster", mutationtaster.toString().startsWith("D").or(mutationtaster.toString().startsWith("A"))))

            if(results.stream().allMatch(Insilico::isDeleterious)) "in-silico prediction(${results.stream().map(Insilico::tester).collect(Collectors.joining(", "))})에서는 Deleterious로 예측되었습니다."
            else if(results.stream().noneMatch(Insilico::isDeleterious)) "in-silico prediction(${results.stream().map(Insilico::tester).collect(Collectors.joining(", "))})에서는 Tolerated로 예측되었습니다."
            else "in-silico prediction에서는 서로 다르게 예측되었습니다(" +
                    "${results.stream().filter(Insilico::isDeleterious).map(Insilico::tester).collect(Collectors.joining(", "))}: Deleterious, " +
                    "${results.stream().filter{ i -> i.isDeleterious().not()}.map(Insilico::tester).collect(Collectors.joining(", "))}: Tolerated)."
        }
    }
    companion object {
        data class Insilico(val tester: String, val result: Boolean) {
            fun isDeleterious(): Boolean = result
        }
    }
}