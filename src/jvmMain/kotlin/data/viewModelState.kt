package data

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logic.evaluateExpression
import javax.script.ScriptEngineManager

class ViewModelState {
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            jackson()
        }
    }
    private val scope = CoroutineScope(Job())
    private val scope2 = CoroutineScope(Job())
    val stringForCalculator = MutableStateFlow("")
    val scrollableState = ScrollState(initial = 0)
    val result = MutableStateFlow<Result>(Result.Null())
    val withX = MutableStateFlow(false)
    val chartStart = MutableStateFlow("")
    val chartEnd = MutableStateFlow("")
    val charResult = chartEnd
        .combine(chartStart){ end , start ->
            if( end.isEmpty() || start.isEmpty() ){
                return@combine "范围不能为空"
            }
            try {
                val endInt = chartEnd.value.toInt()
                val startInt = chartStart.value.toInt()
                if(startInt > endInt){
                    return@combine "结束要大于开始"
                }
                if(endInt-startInt > 100){
                    return@combine "范围过大，范围在100以内"
                }
            }catch (e:Exception){
                return@combine "格式错误，必须为整数"
            }
            return@combine "格式正确"
        }.stateIn(
            scope,
            SharingStarted.Eagerly,
            ""
        )
    val dataForDraw = MutableStateFlow<MutableList<ChartItem>>(mutableStateListOf())
    val dialogState = MutableStateFlow<String?>(null)
    fun subString(){
        scope.launch {
            stringForCalculator.let { stringMutableStateFlow ->
                if (stringMutableStateFlow.value.isNotEmpty()) {
                    stringMutableStateFlow.update {
                        val data = it
                        if(data.substring(0, data.length - 1).isEmpty()){
                            result.value = Result.Null()
                        }
                        it.substring(0, it.length - 1)
                    }
                }
            }
        }
    }

    fun addSting(new:String){
        scope.launch {
            stringForCalculator.let { stringMutableStateFlow ->
                if((stringMutableStateFlow.value+new).length<50){
                    stringMutableStateFlow.update {
                        it + new
                    }
                    if(result.value !is Result.Loading){
                        result.value = Result.Loading()
                    }
                }
            }
        }
    }
    fun compute(
        dialog: (String?) -> Unit
    ){
        if(stringForCalculator.value.isEmpty() ){
            result.value = Result.Null()
            return
        }
        if(stringForCalculator.value.indexOf("x")!=-1){
            result.value = Result.WithX()
            return
        }
        val sanitizedExpression = stringForCalculator.value.replace(" ", "") // 删除空格
        try {
            val data = evaluateExpression(sanitizedExpression)
            result.value = Result.Success(data.toString())
        } catch (e: Exception) {
            println(e)
            result.value = Result.Error(Throwable(e))
        }
    }

    fun updateChartStart(string:String){
        scope.launch(Dispatchers.IO) {
            chartStart.emit(string)
//            chartEnd.emit(chartEnd.value)
        }
    }

    fun updateChartEnd(string:String){
        scope.launch(Dispatchers.IO) {
            chartEnd.emit(string)
//            chartStart.emit(chartStart.value)
        }
    }

    fun updateDataForCharMain(){
        scope.launch {
            if(charResult.value!="格式正确"){
                dialogState.value = "范围有误"
                return@launch
            }
            val range = (chartStart.value.toInt()..chartEnd.value.toInt())
            val list = mutableListOf<Double>()
            for( item in range){
                try {
                    val sanitizedExpression =
                        if(item<0){
                            stringForCalculator.value.replace("x","(0${item.toString()})")
                        }else{
                            stringForCalculator.value.replace("x", item.toString())
                        }
                    val data = evaluateExpression(sanitizedExpression)
                    list.add(data)
                }
                catch (e:Exception){
                    dialogState.value = "表达式有误"
                    return@launch
                }
            }
            val ymax = list.max()
            val ymin = list.min()
            val xmax = range.toList().max().toDouble()
            val xmin = range.toList().min().toDouble()
            dataForDraw.value = list.mapIndexed { index, d ->
                ChartItem(
                    x = range.elementAt(index),
                    y = d,
                    xPercent = (range.elementAt(index).toDouble()-xmin)/(xmax - xmin),
                    yPercent = (d.toDouble()-ymin)/(ymax - ymin)
                )
            }.toMutableStateList()
        }
    }

    fun saveResult(
        dialog: (String?) -> Unit
    ){
        scope.launch(Dispatchers.Default) {
            if(result.value !is Result.Success){
                dialog.invoke("正确的结果才可保存")

                return@launch
            }
            val data:String = client.submitForm(
                "http://127.0.0.1:8082/calculate",
//                    contentType(ContentType.Application.Json),
//                    setBody(HelloWorld("123","455"))
                formParameters = parameters {
                    append("result",(result.value as Result.Success).data)
                    append("calculationFormula",stringForCalculator.value)
                }
            ).body()
            dialog.invoke("保存成功")
        }
    }

}

data class ChartItem(
    val x : Int,
    val y :Double,
    val xPercent : Double,
    val yPercent : Double
)