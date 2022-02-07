import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.net.URL

/*
 * @created 29. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/

class DataManager {
    lateinit var url: String
    var previews = mutableListOf<Preview>()
    var history = mutableListOf<mossQuery>()
    val results = mutableMapOf<String, Student>()

    fun addPreview(preview: Preview) {
        previews.add(preview)
    }

    fun addHistory(mossQuery: mossQuery) {
        history.add(mossQuery)
    }

    fun parseMossResult(resultUrl: URL) {
        val document: Document = Jsoup.connect(resultUrl.toString()).get()
        val rows = document.select("tr")
        rows.drop(1).forEach {

            val first = it.select("td")[0]
            val second = it.select("td")[1]

            val submissionA = first.select("A").html();
            val linkA: String = first.select("A").attr("href")
            val similarityA: Int = submissionA.substring(submissionA.indexOf("(") + 1).replace("%)", "").toInt()

            val submissionB: String = second.select("A").html()
            val linkB: String = second.select("A").attr("href")
            val similarityB: Int = submissionB.substring(submissionB.indexOf("(") + 1).replace("%)", "").toInt()

            val linesMatched: Int = it.select("td")[2].html().toInt()

            val keyA = sha256(submissionA).asHex
            val keyB = sha256(submissionB).asHex

            val studentA  = results[keyA] ?: Student(keyA, submissionA, linkA, mutableMapOf())
            val studentB  = results[keyB] ?: Student(keyB, submissionB, linkB, mutableMapOf())
            val result = Result(linkA, linkB, similarityA, similarityB, linesMatched)

            studentA.resultMap[studentB] = result
            studentB.resultMap[studentA] = result
            results[studentA.id] = studentA
            results[studentB.id] = studentB
        }
    }
}

data class mossQuery(val timestamp: Long, val resultUrl: String)

data class Student(
    val id: String,
    val name: String,
    val url: String,
    val resultMap: MutableMap<Student, Result>
)

data class Result(
    val resultLinkA: String,
    val resultLinkB: String,
    val similarityA: Int,
    val similarityB: Int,
    val linesMatched: Int,
)

fun getResults(A: Student, B: Student) {
    //val result = A.resultMap[B]?.sortedBy { it.similarity }
}

data class Preview(
    val id: Int,
    val name: String,
    val url: String,
    val error: String,
    val files: MutableList<File>
)
