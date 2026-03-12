package com.example.taskflow

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background

data class ShopItem(val id: Int, val name: String, val price: Int, val icon: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    flowCoinsBalance: Int,
    purchasedItems : Set<String>,
    onBack: () -> Unit,
    onBuyItem: (ShopItem) -> Unit
) {
    val shopItems = listOf(
        ShopItem(1, "Confettis", 50, "🎉"),
        ShopItem(2, "Feu d'artifice", 150, "🎆"),
        ShopItem(3, "Explosion dorée", 300, "🌟"),
        ShopItem(4, "Pluie d'étoiles", 500, "🌠")
    )

    val context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Boutique", "Ma Collection", "Zone Secrète ")

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Boutique", color = MaterialTheme.colorScheme.onPrimary) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Retour",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "FlowCoins",
                                tint = MaterialTheme.colorScheme.tertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$flowCoinsBalance",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        val displayedItems = if (selectedTabIndex == 0){
            shopItems
        } else{
            shopItems.filter {purchasedItems.contains(it.id.toString())}
        }

        if (selectedTabIndex == 1 && displayedItems.isEmpty()){
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Votre collection est vide !", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aller dans la boutique pour débloquer des animations ! ", color = Color.Gray)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            )  {
                items(displayedItems.size){ index ->
                    val currentItem = displayedItems[index]
                    val isItemOwned = purchasedItems.contains(currentItem.id.toString())

                    ShopItemCard(
                        item = currentItem,
                        isOwned = isItemOwned,
                        isCollectionTab = selectedTabIndex == 1,
                        onBuyClick = {
                            if (flowCoinsBalance >= currentItem.price) {
                                onBuyItem(currentItem)
                            } else {
                                Toast.makeText(context, "Vous n'avez pas assez d'étoiles !", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            if (selectedTabIndex == 2) {
                var isGameStarted by remember { mutableStateOf(false) }
                var snake by remember { mutableStateOf(listOf(Pair(10, 10))) }
                var food by remember { mutableStateOf(Pair(5, 5)) }
                var direction by remember { mutableStateOf(Pair(0, -1)) }
                var isGameOver by remember { mutableStateOf(false) }
                var score by remember { mutableStateOf(0) }

                LaunchedEffect(isGameStarted, isGameOver) {
                    if (isGameStarted && !isGameOver) {
                        while (!isGameOver) {
                            kotlinx.coroutines.delay(200)
                            val head = snake.first()
                            val newHead = Pair(head.first + direction.first, head.second + direction.second)

                            if (newHead.first !in 0..19 || newHead.second !in 0..19 || snake.contains(newHead)) {
                                isGameOver = true
                            } else {
                                val newSnake = snake.toMutableList()
                                newSnake.add(0, newHead)

                                if (newHead == food) {
                                    food = Pair((0..19).random(), (0..19).random())
                                    score++
                                } else {
                                    if (newSnake.isNotEmpty()) {
                                        newSnake.removeAt(newSnake.lastIndex)
                                    }
                                }
                                snake = newSnake
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isGameStarted) {
                        Text("Mini-Jeu : Snake \ud83d\udc0d", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { isGameStarted = true },
                            modifier = Modifier.size(width = 200.dp, height = 50.dp)
                        ) {
                            Text("Commencer", fontSize = MaterialTheme.typography.titleMedium.fontSize)
                        }
                    } else if (isGameOver) {
                        Text("Game Over ! Score: $score", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            snake = listOf(Pair(10, 10))
                            direction = Pair(0, -1)
                            isGameOver = false
                            score = 0
                        }) { Text("Rejouer") }
                    } else {
                        Text("Score : $score", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.LightGray)
                        ) {
                            Box(modifier = Modifier.offset(x = (food.first * 10).dp, y = (food.second * 10).dp).size(10.dp).background(Color.Red))
                            snake.forEach { pos ->
                                Box(modifier = Modifier.offset(x = (pos.first * 10).dp, y = (pos.second * 10).dp).size(10.dp).background(MaterialTheme.colorScheme.primary))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Button(onClick = { if (direction != Pair(0, 1)) direction = Pair(0, -1) }) { Text("Haut") }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Button(onClick = { if (direction != Pair(1, 0)) direction = Pair(-1, 0) }) { Text("Gauche") }
                                Button(onClick = { if (direction != Pair(-1, 0)) direction = Pair(1, 0) }) { Text("Droite") }
                            }
                            Button(onClick = { if (direction != Pair(0, -1)) direction = Pair(0, 1) }) { Text("Bas") }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun ShopItemCard(item: ShopItem, isOwned: Boolean, isCollectionTab : Boolean, onBuyClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwned && !isCollectionTab) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, style = MaterialTheme.typography.headlineLarge)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            if (isCollectionTab) {
                OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("Débloqué ✨")
                }
            } else if (isOwned) {
                OutlinedButton(onClick = { }, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("Possédé")
                }
            } else {
                Button(onClick = onBuyClick, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${item.price}")
                }
            }
        }
    }
}