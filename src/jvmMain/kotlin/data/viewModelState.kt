package data

import androidx.compose.foundation.ScrollState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ViewModelState {
    val stringForCalculator = MutableStateFlow("")
    val scrollableState = ScrollState(initial = 0)
    val result = MutableStateFlow<Result>(Result.Null())

    fun subString(){
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

    fun addSting(new:String){
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
    fun compute(){
        if(result.value !is Result.Null){
            result.value = Result.Error(Throwable())
        }
    }
}