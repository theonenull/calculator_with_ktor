package ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import data.ViewModelState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
fun Calculator(
    viewModel: ViewModelState,
    modifier: Modifier,
    dialog: (String?) -> Unit,
    expand: () -> Unit
) {
    Box(modifier = modifier){
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
                        viewModel.compute(dialog)
                    },
                    viewModel = viewModel
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
                },
                compute = {
                    viewModel.saveResult(
                        dialog
                    )
                },
                clear = {
                    viewModel.stringForCalculator.value = ""
                    println("ssss")
                },
                expand = expand,
                viewModel = viewModel
            )

        }
    }
}

@Composable
fun InputArea(
    modifier: Modifier,
    stringForCalculator: MutableStateFlow<String>,
    addString: (String) -> Unit,
    compute: () -> Unit,
    viewModel: ViewModelState
){
    val value = stringForCalculator.collectAsState()
    LaunchedEffect(value.value){
        delay(500)
        compute.invoke()
    }
    TextField(
        value = value.value,
        onValueChange = {
            addString.invoke(it)
        },
        modifier = modifier,
        readOnly = true
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ButtonArea(
    modifier: Modifier,
    addString: (String) -> Unit,
    subString: () -> Unit,
    compute: () -> Unit,
    expand: () -> Unit,
    viewModel: ViewModelState,
    clear: () -> Unit
){
    val isExpand = viewModel.withX.collectAsState()
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
                        "💾" ->{
                            compute.invoke()
                        }
                        "\uD83D\uDCD6" ->{
                            expand.invoke()
                        }
                        "\uD83D\uDCD9" ->{
                            expand.invoke()
                        }
                        else->{
                            addString.invoke(charList[it].toString())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(0.7f)
                    .padding(3.dp)
                    .apply {
                        if(charList[it] == '←'){
                            this.combinedClickable(
                                onLongClick = {
                                    clear.invoke()
                                },
                                onClickLabel = "",
                                onClick = {
                                    subString.invoke()
                                }
                            )
                        }
                    },
                colors =
                    when(charList[it]){
                        '←'-> ButtonDefaults.buttonColors( backgroundColor = Color.Red)
                        '='-> ButtonDefaults.buttonColors( backgroundColor = Color.Gray)
                        'x'-> ButtonDefaults.buttonColors( backgroundColor = Color(215, 215, 237))
                        else-> ButtonDefaults.buttonColors()
                    }
                ){

                if(charList[it]=="📖"){
                    if(isExpand.value){
                        Text(
                            "\uD83D\uDCD6"
                        )
                    }
                    else{
                        Text(
                            "\uD83D\uDCD9"
                        )
                    }
                }
                else{
                    Text(
                        charList[it].toString()
                    )
                }
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
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
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
                    is Result.WithX ->{
                        Icon(
                            painter = painterResource("xml.svg"),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .verticalScroll(scrollState)
        ){
            Crossfade(
                result,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ){
                when(it){
                    is Result.Success ->{
                        Text(
                            it.data,
                            modifier = Modifier
                        )
                    }
                    is Result.Error ->{
                        Text(
                            it.error.message.toString(),
                            modifier = Modifier
                        )
                    }
                    is Result.Loading ->{
                        Text(
                            "加载中",
                            modifier = Modifier
                        )
                    }
                    is Result.Null -> {
                        Text(
                            "无输入",
                            modifier = Modifier
                        )
                    }

                    is Result.WithX ->{
                        Text(
                            "已进入功能模式",
                            modifier = Modifier
                        )
                    }
                }
            }
        }
        VerticalScrollbar(
            adapter = ScrollbarAdapter(scrollState),
            modifier = Modifier
                .fillMaxHeight(1f)
                .width(8.dp)
                .padding(horizontal = 2.dp)
        )
    }
}


