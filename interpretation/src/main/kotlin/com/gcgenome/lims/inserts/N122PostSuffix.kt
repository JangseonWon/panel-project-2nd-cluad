package com.gcgenome.lims.inserts

class N122PostSuffix : Insert(){
    private val services = setOf("N122")
    override fun weight(): Int = 0

    override fun text(something: Any?): String = "본 검사는 Mitochondrial DNA (mtDNA) 및 Mitochondrial disease와 관련된 Nuclear DNA 유전자를 분석하여 Mitochondrial disease 관련 변이를 검출합니다. 단, Mitochondrial DNA의 중복과 대규모 결실은 검출할 수 없습니다. 검사의 기술적 한계에 대한 자세한 내용은 검사의 한계를 참조하십시오."

    override fun services(): Set<String> = services

    override fun position(): Position = Position.POSTSUFFIX
}