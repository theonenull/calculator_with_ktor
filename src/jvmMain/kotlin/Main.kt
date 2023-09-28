import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import data.ViewModelState
import ui.Calculator
import ui.Chart

@Composable
@Preview
fun App(viewModel: ViewModelState) {
    var text by remember { mutableStateOf("Hello, World!") }
    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ){
            Calculator(
                viewModel = viewModel,
                modifier = Modifier.width(350.dp).fillMaxHeight()
            )
            Chart(
                viewModel = viewModel,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

fun main() = application {
    val viewModel  = ViewModelState()
    val isExpand = viewModel.withX.collectAsState()
    val state = rememberWindowState(
        size = DpSize(700.dp,700.dp),
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
