package com.example.gokulahealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Cattle(
    val earTagId: String,
    val milkYield: String,
    val vaccineDate: String,
    val breed: String,
    val breedColor: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainNavigation()
        }
    }
}

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf("home") }
    val cattleList = remember { mutableStateListOf<Cattle>() }

    when (currentScreen) {
        "home" -> HomeScreen(onAddClick = { currentScreen = "add" }, onViewClick = { currentScreen = "view" })
        "add" -> AddCattleScreen(onBack = { currentScreen = "home" }, onSave = { cattleList.add(it); currentScreen = "home" })
        "view" -> ViewRecordsScreen(cattleList = cattleList, onBack = { currentScreen = "home" })
    }
}

@Composable
fun HomeScreen(onAddClick: () -> Unit, onViewClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🐄", fontSize = 80.sp) // Big visual cow icon
        Text("Gokula Health", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onAddClick, modifier = Modifier.width(250.dp).height(56.dp)) { Text("Add Cow", fontSize = 18.sp) }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onViewClick, modifier = Modifier.width(250.dp).height(56.dp)) { Text("View Records", fontSize = 18.sp) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCattleScreen(onBack: () -> Unit, onSave: (Cattle) -> Unit) {
    var tag by remember { mutableStateOf("") }
    var yield by remember { mutableStateOf("") }
    var vDate by remember { mutableStateOf("") }
    var selectedBreed by remember { mutableStateOf("Jersey") }

    val breeds = listOf("Jersey", "Holstein", "Gir")
    val breedColors = listOf(Color(0xFF795548), Color.Black, Color(0xFFD32F2F))

    Scaffold(topBar = { TopAppBar(title = { Text("Add New Cow Record") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Ear Tag ID") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = yield, onValueChange = { yield = it }, label = { Text("Daily Milk Yield (Liters)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = vDate, onValueChange = { vDate = it }, label = { Text("Next Vaccine Date (DD/MM/YYYY)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))
            Text("Select Breed Category:", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                breeds.forEachIndexed { index, breed ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedBreed = breed }.padding(8.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = if (selectedBreed == breed) breedColors[index] else Color.LightGray, modifier = Modifier.size(40.dp))
                        Text(breed, color = if (selectedBreed == breed) breedColors[index] else Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                if(tag.isNotBlank() && yield.isNotBlank()){
                    val color = if(selectedBreed == "Jersey") breedColors[0] else if(selectedBreed == "Holstein") breedColors[1] else breedColors[2]
                    onSave(Cattle(tag, yield, vDate, selectedBreed, color))
                }
            }, modifier = Modifier.fillMaxWidth().height(50.dp)) { Text("Save Record") }
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Cancel") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewRecordsScreen(cattleList: List<Cattle>, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Current Herd Records") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (cattleList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No records found.", color = Color.Gray)
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cattleList) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            // Using a visual placeholder since we are skipping real photos for the deadline
                            Text("🐄", fontSize = 32.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("ID: ${item.earTagId}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Breed: ${item.breed}", fontSize = 14.sp, color = item.breedColor)
                                Text("Yield: ${item.milkYield}L | Vaccine: ${item.vaccineDate}", fontSize = 13.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            }
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) { Text("Back to Home") }
        }
    }
}
