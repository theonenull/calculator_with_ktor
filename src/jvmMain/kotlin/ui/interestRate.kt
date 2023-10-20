package ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.HistoryResponse
import data.ViewModelState
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.launch
import java.awt.Dialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun interestRate(
    modifier: Modifier = Modifier,
    viewModelState: ViewModelState,
    dialog:(String?)->Unit
){
    var data = remember {
        mutableStateMapOf<Int,String>()
    }
    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState(
        initialPage = 0
    ) {
        Page.values().size
    }
    Column(modifier){
        BottomNavigation(
            modifier = Modifier
                .padding(bottom = 10.dp)
        ) {
            BottomNavigationItem(
                icon = {
                    Icon(Icons.Filled.Email, null)
                },
                selected = pageState.currentPage == 0,
                onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(0)
                    }
                }
            )
            BottomNavigationItem(
                icon = {
                    Icon(Icons.Filled.Email, null)
                },
                selected = pageState.currentPage == 1,
                onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(1)
                    }
                }
            )
        }
        HorizontalPager(
            state = pageState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = false
        ) {
            when (it) {
                0 -> {
                    Deposit(
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                1 -> {
                    History(
                        modifier = Modifier
                            .fillMaxSize(),
                        dialog
                    )
                }
            }
        }
    }
}
enum class Page{
    Deposit,
    Loan
}

@Composable
fun Deposit(
    modifier: Modifier = Modifier
){
    val map = remember {
        mutableStateMapOf<Int,Double>()
    }
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            jackson()
        }
    }
    val scope = rememberCoroutineScope()
    var text by remember {
        mutableStateOf("")
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
    ){
        var currentProcy = remember {
            mutableStateOf<Pair<Int,Double>?>(null)
        }
        var month by remember {
            mutableStateOf("")
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            LaunchedEffect(Unit){
                scope.launch {
                    try {
                        val response:Map<Int,Double> = client.get("http://127.0.0.1:8082/interestRate").body()
                        response.forEach{
                            map[it.key] = it.value
                        }
                        println(response)
                    }catch (e:Exception){

                    }
                }
            }
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(20))
                    .background(Color(217, 217, 238))
                    .padding(10.dp)
                    .animateContentSize()
            ){
                Text(
                    text = text,
                )
            }
            map.toList().sortedBy {
                it.first
            }.forEach {
                Button(
                    onClick = {
                        text = if(it.first == 0) "活期"
                        else "死期 ${it.first}个月"
                        currentProcy.value = it
                    },
                ){
                    Text(
                        text = if(it.first == 0) "活期"
                        else "死期 ${it.first}月"
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            var principal by remember {
                mutableStateOf("")
            }
            var sumData by remember {
                mutableStateOf("")
            }
            LaunchedEffect(principal,currentProcy.value,month){
                println(currentProcy.value)
                if(currentProcy.value == null){
                    sumData = "未选择策略"
                    return@LaunchedEffect
                }
                if(principal == ""){
                    sumData = ""
                    return@LaunchedEffect
                }
                if(currentProcy.value!!.first == 0){
                    if(month.isEmpty()){
                        return@LaunchedEffect
                    }
                    sumData = (month.toInt() * principal.toInt() * currentProcy.value!!.second).toString()
                    return@LaunchedEffect
                }
                if(currentProcy.value!!.first != 0){
                    sumData = currentProcy.let {
                        val data = it.value!!
                        return@let (principal.toInt()+principal.toInt()* data.second* data.first).toString()
                    }
                    return@LaunchedEffect
                }
            }
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentSize()
                    .clip(RoundedCornerShape(20))
                    .background(Color(217, 217, 238))
                    .padding(10.dp)
                    .animateContentSize()
            ){
                Text(
                    text = sumData.toString(),
                )
            }
            TextField(
                value = principal,
                onValueChange = { it->
                    principal = it
                },
                label = {
                    Text("本金")
                },
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )
            if((currentProcy.value?.first ?: -1) == 0){
                TextField(
                    value = month,
                    onValueChange = { it->
                        month = it
                    },
                    label = {
                        Text("活期月份")
                    },
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
            }
        }
    }
}

@Composable
fun History(
    modifier: Modifier = Modifier,
    dialog: (String?) -> Unit
){
    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            jackson()
        }
    }
    val scope = rememberCoroutineScope()
    var list = remember {
        mutableStateListOf<Pair<String,String>>()
    }
    LaunchedEffect(Unit){
        val response : HistoryResponse = client.get("http://127.0.0.1:8082/history").body()
        list.clear()
        response.list.forEach {
            list.add(Pair(it.calculationFormula,it.result))
        }
    }
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Button(
                onClick = {
                    scope.launch {
                        val response : HistoryResponse = client.get("http://127.0.0.1:8082/history").body()
                        list.clear()
                        response.list.forEach {
                            list.add(Pair(it.calculationFormula,it.result))
                        }
                    }
                },
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 10.dp)
            ){
                Text("刷新")
            }
        }
        items(list.size){
            Card (
                modifier = Modifier
                    .fillParentMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                RoundedCornerShape(10.dp),
                backgroundColor = Color(216, 216, 237)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ){
                    Text(
                        list[it].first,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                    )
                    Text(
                        list[it].first,
                    )
                }
            }
        }
    }
}