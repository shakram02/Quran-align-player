package align_parsing


import annotation_parsing.TextEntry

class ParseItemFilter(
    private val parsedAlignFile: HashMap<Int, ParsedSurah>, private val annotationList: List<TextEntry>
) {

    fun extractNonResolvableAnnotations(): HashMap<Int, List<TextEntry>> {
        val alignFileResult = transformAlignFile()
        System.err.println(alignFileResult.size)

        val annotationFileResult = transformAnnotationFile()
        System.err.println(annotationFileResult.size)

        val resultSet = annotationFileResult.intersect(alignFileResult)
        System.err.println(resultSet.size)

        val unresolvableEntries = annotationList.filter {
            val key = "${it.chapterNumber},${it.sectionNumber}"
            key in resultSet
        }

        val result = HashMap<Int, ArrayList<TextEntry>>()
        unresolvableEntries.map {
            val key = it.chapterNumber.toInt()
            if (!result.containsKey(key)) {
                result[key] = arrayListOf()
            }

            result[key]!!.add(it)
        }

        val toMap = HashMap<Int, List<TextEntry>>()
        result.map { toMap.put(it.key, it.value.toList()) }

        return toMap
    }

    private fun transformAnnotationFile(): Set<String> {
        return annotationList.map {
            "${it.chapterNumber},${it.sectionNumber}"
        }.toSet()
    }

    private fun transformAlignFile(): Set<String> {
        val alignFileResult = HashSet<String>()
        for (entry in parsedAlignFile.values) {

            val chapterNumber = entry.surahNumber.toString().padStart(3, '0')

            for (i in 1 until entry.ayahCount + 1) {
                val ayah = entry.getAyah(i)

                if (ayah.deletions < 2 && ayah.insertions < 2) continue
                val sectionNumber = ayah.number.toString().padStart(3, '0')
                alignFileResult.add("$chapterNumber,$sectionNumber")
            }

        }

        return alignFileResult
    }

}
