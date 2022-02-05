import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.util.*

/*
 * @created 29. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/

class DataManager {
    lateinit var url:String
    var students = mutableListOf<Student>()
    var history = mutableListOf<mossQuery>()

    fun addStudent(student: Student){
        students.add(student)
    }
    fun addHistory(mossQuery: mossQuery){
        history.add(mossQuery)
    }

    fun parseMossResult(resultUrl: String){
        val document: Document =Jsoup.connect(resultUrl).get()
    }
}

data class mossQuery(val timestamp:Long, val resultUrl:String)
