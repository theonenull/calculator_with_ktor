package ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import data.ChartItem
import data.ViewModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Chart(
    modifier : Modifier,
    viewModel: ViewModelState,
){
    val chartStart = viewModel.chartStart.collectAsState()
    val chartEnd = viewModel.chartEnd.collectAsState()
    val charResult = viewModel.charResult.collectAsState()
    val dialogState = viewModel.dialogState.collectAsState()
    val dataForDraw = viewModel.dataForDraw.collectAsState()
    Column(
        modifier = modifier
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 20.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 2.dp,
                        color = Color.Gray,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp),
                text = charResult.value
            )
            TextField(
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .weight(0.4f),
                value = chartStart.value,
                onValueChange = {
                    viewModel.updateChartStart(it)
                    println("start")
                }
            )
            Icon(
                painter = painterResource("arrow.svg"),
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .aspectRatio(1f),
                contentDescription = null
            )
            TextField(
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .weight(0.4f),
                value = chartEnd.value,
                onValueChange = {
                    viewModel.updateChartEnd(it)
                    println("end")
                }
            )
            Button(
                onClick = {
                    viewModel.updateDataForCharMain()
                },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(10.dp),
                contentPadding = PaddingValues()
            ){
                Text(
                    maxLines = 1,
                    modifier = Modifier,
                    text = "º”‘ÿ"
                )
            }
        }
        ChartMain(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            dataForDraw
        )
    }
    if(dialogState.value!=null){
        AlertDialog(
            onDismissRequest = {
                viewModel.dialogState.value = null
            },
            buttons = {
                Row (
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp)
                ){
                    Button(
                        onClick = {
                            viewModel.dialogState.value = null
                        }
                    ){
                        Text("»∑∂®")
                    }
                }
            },
            text = {
                viewModel.dialogState.value?.let { Text(it) }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .padding(10.dp)
                .width(200.dp)
                .wrapContentHeight()
        )
    }
}

@Composable
fun ChartMain(
    modifier: Modifier,
    dataForDraw : State<List<ChartItem>>
){
    val scope = rememberCoroutineScope()
    val clipState = Animatable(0f)
    LaunchedEffect(dataForDraw.value){
        clipState.animateTo(
            0f,
            animationSpec = tween(50)
        )
    }
    if(dataForDraw.value.isNotEmpty()){
        Canvas(modifier = modifier){
            val path = Path()
            val list = dataForDraw.value
            list.forEachIndexed { _, it ->
                path.lineTo(size.width*( it.xPercent ).toFloat(),size.height*(1f - it.yPercent).toFloat())
            }
            path.lineTo(size.width*( list.last().xPercent ).toFloat(),size.height)
            path.lineTo(size.width*( list.first().xPercent ).toFloat(),size.height)
            path.close()
            clipRect(right = clipState.value*size.width){
//                dataForDraw.value.forEachIndexed { index,it ->
//                    if(index!=dataForDraw.value.size-1){
//                        val next = dataForDraw.value[index+1]
//                        drawLine(
//                            start = Offset(size.width*( it.xPercent ).toFloat(),size.height*(1f - it.yPercent).toFloat()),
//                            end = Offset(size.width*( next.xPercent ).toFloat(),size.height*(1f - next.yPercent).toFloat()),
//                            color = Color.Gray,
//                            strokeWidth = 3f
//                        )
//                    }
//                }
                drawPoints(
                    list.map { Offset(size.width*( it.xPercent ).toFloat(),size.height*(1f - it.yPercent).toFloat()) },
                    pointMode = PointMode.Points,
                    Color.Blue,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                drawPath(path, Brush.linearGradient(colors = listOf(Color.Green,Color.Transparent)))
                scope.launch(Dispatchers.Default){
                    clipState.animateTo(
                        1f,
                        animationSpec = tween(65)
                    )
                }
            }
        }
    }
}

fun randomColor() = Color(Random.nextInt(0,256),Random.nextInt(0,256),Random.nextInt(0,256))