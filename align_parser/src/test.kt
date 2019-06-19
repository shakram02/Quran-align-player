import align_parsing.AnnotationFileWriter
import align_parsing.UnresolvableAnnotationExtractor
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val pathToFile = Paths.get("assets/json_filename.txt")
    val config = Files.readAllLines(pathToFile)
    val alignFilePath = config[0]
    val quranTextFilePath = config[1]
    val annotationFilePath = config[2]

    val itemFilter = UnresolvableAnnotationExtractor(alignFilePath, quranTextFilePath, annotationFilePath)
    val unresolvableAnnotations = itemFilter.extractNonResolvableAnnotations()

    val annotationWriter = AnnotationFileWriter(annotationFilePath)
    annotationWriter.writeAnnotations(unresolvableAnnotations)
}