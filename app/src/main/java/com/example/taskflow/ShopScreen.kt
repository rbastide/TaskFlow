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

    var selectedTabIndex by remember { mutableStateOf(0) }
    var tabs = listOf("Boutique","Ma Collection")

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
                        onBuyClick = {onBuyItem(currentItem)}
                    )
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