import androidx.compose.animation.Crossfade
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import data.ViewModelState
import ui.Calculator
import ui.Chart
import ui.interestRate


@Composable
@Preview
fun App(viewModel: ViewModelState) {
    var text by remember { mutableStateOf<String?>(null) }
    val isExpand = viewModel.withX.collectAsState()
    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(700.dp)
        ){
            Calculator(
                viewModel = viewModel,
                modifier = Modifier.width(350.dp).fillMaxHeight(),
                dialog = {
                    text = it
                }
            ) {
                viewModel.withX.value = !viewModel.withX.value
            }
//            Chart(
//                viewModel = viewModel,
//                modifier = Modifier.weight(1f).fillMaxHeight()
//            )
            interestRate(
                viewModelState = viewModel,
                modifier = Modifier.width(if(isExpand.value) 700.dp else 0.dp).height(700.dp),
                dialog = {
                    text = it
                }
            )
        }
        if(text!=null){
            Dialog(
                onDismissRequest = {
                    text = null
                }
            ){
                Card (
                    modifier = Modifier,
                    shape = RoundedCornerShape(10.dp)
                ){
                    Column(
                        modifier = Modifier
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(text = text!!)
                        Row (
                            modifier = Modifier.padding(top = 20.dp)
                        ){
                            Spacer(modifier = Modifier.weight(1f))
                            Button(onClick = { text = null }){
                                Text("È·¶¨")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    val viewModel  = ViewModelState()
    val isExpand = viewModel.withX.collectAsState()
    val state = rememberWindowState(
        size = DpSize(350.dp,700.dp),
    )
    val icon = painterResource("calculator.svg")
    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        state = state,
        icon = icon
    ) {
        LaunchedEffect(isExpand.value){
            if(isExpand.value){
                state.size =  DpSize(1050.dp,700.dp)
            }else{
                state.size =  DpSize(350.dp,700.dp)
            }
        }
        App(
            viewModel  = viewModel
        )
    }
}
