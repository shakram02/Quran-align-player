package align_parsing

import annotation_parsing.AnnotationParser
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val pathToFile = Paths.get("assets/json_filename.txt")
    val config = Files.readAllLines(pathToFile)
    val alignFileName = config[0]
    val textFileName = config[1]

    val surahs = AlignFileParser.parseFile(alignFileName, textFileName)
    val annotationParser = AnnotationParser(File("annotation-file.txt"))
    val itemFilter = ParseItemFilter(surahs, annotationParser.getAllFileSections())
    val unresolvableAnnotations = itemFilter.extractNonResolvableAnnotations()


    val annotationWriter = AnnotationFileWriter("intersected_annotations.txt")
    annotationWriter.writeAnnotations(unresolvableAnnotations)
}