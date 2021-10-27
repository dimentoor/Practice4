import java.util.*

data class Subject(var title: String, 
                   var grade: Int)

data class Student(val name: String?, 
                   val birthYear: Int, 
                   val subjects: List<Subject>) {

    val averageGrade
        get() = subjects.average { it.grade.toFloat() }
    val age
        get()= Calendar.getInstance().get(Calendar.YEAR) - birthYear

    override fun toString(): String =
        "\n\tName: $name, Year of birth: $birthYear, Subjects: $subjects, AVG grade: $averageGrade "

}

fun <T> Iterable<T>.average(block: (T) -> Float): Float {
    var sum = 0.0
    var count = 0
    for (element in this) {
        sum += block(element)
        ++count
    }
    return (sum / count).toFloat()
}

data class University(val title: String, val students: MutableList<Student>) {

    val average
        get() = students.filter{ it.age in 17..20 }.average { it.averageGrade }

    val courses
        get() = students.groupBy { it.age }.mapKeys {
            when (it.key) {
                17 -> 1
                18 -> 2
                19 -> 3
                20 -> 4
                else -> throw StudentTooYoungException()
            }
        }
}

class StudentTooYoungException : Exception("The student is too young")

enum class StudyProgram(private val title: String) {
    DISCIPLINE1("History"), 
    DISCIPLINE2("English"),
    DISCIPLINE3("Math"), 
    DISCIPLINE4("Physics"),
    DISCIPLINE5("Philosophy");

    infix fun withGrade(grade: Int): Subject = Subject(title, grade)
}

typealias StudentListener = ((Student) -> Unit)

val students = mutableListOf(
    Student("Baraboshkin Dmitry", 2002, listOf(StudyProgram.DISCIPLINE1 withGrade 4, 
                                               StudyProgram.DISCIPLINE2 withGrade 4)),
    Student("Tremaskin Kirill", 2002, listOf(StudyProgram.DISCIPLINE2 withGrade 5, 
                                             StudyProgram.DISCIPLINE3 withGrade 4)),
    Student("Balyaikin Ilia", 2003, listOf(StudyProgram.DISCIPLINE4 withGrade 3, 
                                           StudyProgram.DISCIPLINE5 withGrade 5)),
    Student("Karpov Mikhail", 2001, listOf(StudyProgram.DISCIPLINE3 withGrade 5, 
                                           StudyProgram.DISCIPLINE5 withGrade 4)),
    Student("Korzhov Alexander", 2003, listOf(StudyProgram.DISCIPLINE1 withGrade 4, 
                                              StudyProgram.DISCIPLINE2 withGrade 5)),
    Student("Cherapkin Alexander", 2002, listOf(StudyProgram.DISCIPLINE1 withGrade 5, 
                                                StudyProgram.DISCIPLINE3 withGrade 3)),
)

object DataSource {
    val university: University by lazy {
        University("MRSU", students)
    }

    var onNewStudentListener: StudentListener? = null


    fun addStudent(students: MutableList<Student>) {
        println("Adding a student")
        println("Enter student's name: ")
        val name = readLine()
        println("Enter the year of birth: ")
        val year = Scanner(System.`in`)
        val birthYear: Int = year.nextInt()
        students.add(Student(name, birthYear, listOf(StudyProgram.DISCIPLINE3 withGrade 4, StudyProgram.DISCIPLINE1 withGrade 3)))
        val addedStudent = students.last()
        onNewStudentListener?.invoke( addedStudent)
    }

}

fun main() {

    println("University: " + DataSource.university.title)
    println("\t" + DataSource.university.students.joinToString(separator = "\t"))
    println("Division by course = " + DataSource.university.courses)
    println("AVG university grade = " + DataSource.university.average)

    DataSource.onNewStudentListener = {
        println("New student: $it \n" + "AVG university grade: ${DataSource.university.average}")
    }
    DataSource.addStudent(students)

    println("Division by course = " + DataSource.university.courses)
}