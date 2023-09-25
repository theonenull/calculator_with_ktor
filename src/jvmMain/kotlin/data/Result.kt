package data

interface Result {
    data class Success(
        val data:String
    ):Result
    data class Error(
        val error : Throwable
    ):Result
    class Loading():Result
    class Null():Result

    class WithX():Result
}