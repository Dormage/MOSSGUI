/*
 * @created 29. 01. 2022
 * @project IntelliJ IDEA
 * @author Dormage
*/

class DataManager {
    var students = mutableListOf<Student>()

    fun addStudent(student: Student){
        students.add(student)
    }
}