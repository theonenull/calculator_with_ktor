package ui

import androidx.compose.animation.Crossfade
import data.ViewModelState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import data.Result
import data.charList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun Calculator() {
    Box(modifier = Modifier.fillMaxSize()){
        val viewModel  = ViewModelState()
        Column (
            modifier = Modifier
                .fillMaxSize()
        ){
            rememberScrollState()
            val scope = rememberCoroutineScope()
            Column(
                modifier = Modifier
                    .verticalScroll(viewModel.scrollableState)
                    .height(200.dp)
            ) {
                InputArea(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    stringForCalculator = viewModel.stringForCalculator,
                    addString = { new ->
                        if(new.length<50){
                            viewModel.stringForCalculator.update {
                                new
                            }
                            scope.launch {
                                viewModel.scrollableState.animateScrollTo(viewModel.scrollableState.maxValue)
                            }
                        }
                    },
                    compute = {
                        viewModel.compute()
                    }
                )
            }
            ResultArea(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                state = viewModel.result
            )
            ButtonArea(
                modifier = Modifier
                    .weight(1f),
                addString = { new ->
                    viewModel.addSting(new)
                },
                subString = {
                    viewModel.subString()
                }
            )

        }
    }
}

@Composable
fun InputArea(
    modifier: Modifier,
    stringForCalculator : MutableStateFlow<String>,
    addString: (String)->Unit,
    compute : ()->Unit
){
    val value = stringForCalculator.collectAsState()
    LaunchedEffect(value.value){
        delay(3000)
        compute.invoke()
    }
    TextField(
        value = value.value,
        onValueChange = {
            addString.invoke(it)
        },
        modifier = modifier,
    )

}

@Composable
fun ButtonArea(
    modifier: Modifier,
    addString: (String) -> Unit,
    subString: () -> Unit
){
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(5)
    ){
        items(10) {
            Button(
                onClick = {
                    addString.invoke((it).toString())
                },
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(0.7f)
                    .padding(3.dp)
            ){
                Text((it).toString())
            }
        }

        items(charList.size) {
            Button(
                onClick = {
                    when(charList[it]){
                        '←'->{
                            subString.invoke()
                        }
                        else->{
                            addString.invoke(charList[it].toString())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(0.7f)
                    .padding(3.dp),
                colors = if( charList[it] == '←' ) ButtonDefaults.buttonColors( backgroundColor = Color.Red) else ButtonDefaults.buttonColors(),
            ){
                Text(
                    charList[it].toString()
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ResultArea(
    modifier: Modifier = Modifier,
    state : MutableStateFlow<Result>
){
    val result by state.collectAsState()
    Row(modifier){
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .wrapContentSize(align = Alignment.Center)
                .fillMaxSize(0.6f)
        ){
            Crossfade(result){
                when(it){
                    is Result.Success ->{
                        Icon(
                            painter = painterResource("check.svg"),
                            contentDescription = null,
                            tint = Color.Green
                        )
                    }
                    is Result.Error ->{
                        Icon(
                            painter = painterResource("error.svg"),
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                    is Result.Loading ->{
                        CircularProgressIndicator(
                            modifier = Modifier
                        )
                    }
                    is Result.Null -> {
                        Icon(
                            painter = painterResource("circle.svg"),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


