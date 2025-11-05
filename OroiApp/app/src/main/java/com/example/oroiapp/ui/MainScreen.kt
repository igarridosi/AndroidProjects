package com.example.oroiapp.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oroiapp.model.Subscription
import com.example.oroiapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.ui.theme.OroiTheme
import com.example.oroiapp.viewmodel.MainUiState
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAddSubscription: () -> Unit // Gehitzeko pantailara nabigatzeko lambda
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oroi - Nire Harpidetzak") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSubscription) {
                Icon(Icons.Filled.Add, contentDescription = "Harpidetza Gehitu")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            TotalMonthlyCost(uiState.totalMonthlyCost)
            Spacer(modifier = Modifier.height(16.dp))
            SubscriptionList(
                subscriptions = uiState.subscriptions,
                onDelete = { subscription ->
                    viewModel.deleteSubscription(subscription)
                }
            )
        }
    }
}

@Composable
fun TotalMonthlyCost(total: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hileko Gastu Osoa",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "€${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionList(subscriptions: List<Subscription>, onDelete: (Subscription) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = subscriptions,
            key = { it.id } // Elementu bakoitzarentzat gako bakarra
        ) { subscription ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(subscription)
                        true // Baieztatu balio aldaketa (ezabatzeko)
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                // Ezkerretara irristatzea bakarrik gaitu
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    val color by animateColorAsState(
                        targetValue = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.8f)
                            else -> Color.Transparent
                        },
                        label = "background color"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Ezabatu ikonoa",
                            modifier = Modifier.scale(1.2f),
                            tint = Color.White
                        )
                    }
                }
            ) {
                SubscriptionItem(subscription = subscription)
            }
        }
    }
}


@Composable
fun SubscriptionItem(subscription: Subscription) {
    // Hurrengo ordainketa-data kalkulatzeko logika
    val nextPaymentDate = calculateNextPaymentDate(subscription)
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(subscription.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Hurrengo ordainketa: ${dateFormat.format(nextPaymentDate)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${subscription.amount} ${subscription.currency}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

// Hurrengo ordainketa-data kalkulatzeko laguntzailea (logika garrantzitsua!)
private fun calculateNextPaymentDate(subscription: Subscription): java.util.Date {
    val calendar = Calendar.getInstance()
    calendar.time = subscription.firstPaymentDate
    val today = Calendar.getInstance()

    // Egutegiko data gaur baino beranduago izan arte aurreratzen du
    while (calendar.before(today)) {
        when (subscription.billingCycle) {
            com.example.oroiapp.model.BillingCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            com.example.oroiapp.model.BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            com.example.oroiapp.model.BillingCycle.ANNUAL -> calendar.add(Calendar.YEAR, 1)
        }
    }
    return calendar.time
}

@Preview(showBackground = true)
@Composable
fun SubscriptionItemPreview() {
    // 1. Sortu harpidetza datu faltsuekin aurrebistarako
    val sampleSubscription = Subscription(
        id = 1,
        name = "Netflix",
        amount = 12.99,
        currency = "EUR",
        billingCycle = BillingCycle.MONTHLY,
        firstPaymentDate = Date()
    )

    // 2. Erabili gure aplikazioaren gaia itxura koherentea izateko
    OroiTheme {
        SubscriptionItem(subscription = sampleSubscription)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun MainScreenPreview() {
    // 1. Sortu datu-zerrenda faltsu bat pantaila osoa ikusteko
    val sampleSubscriptions = listOf(
        Subscription(1, "Spotify", 9.99, "EUR", BillingCycle.MONTHLY, Date()),
        Subscription(2, "HBO Max", 8.99, "EUR", BillingCycle.MONTHLY, Date()),
        Subscription(3, "Amazon Prime", 49.90, "EUR", BillingCycle.ANNUAL, Date())
    )
    val sampleUiState = MainUiState(
        subscriptions = sampleSubscriptions,
        totalMonthlyCost = 19.84 // Eskuz kalkulatua adibiderako
    )

    OroiTheme {
        // 2. Sortu pantaila, baina ViewModel-ik gabe.
        //    Horren ordez, datu faltsuak eta funtzio hutsak pasatzen dizkiogu.
        Scaffold(
            topBar = { TopAppBar(title = { Text("Oroi - Nire Harpidetzak") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                TotalMonthlyCost(total = sampleUiState.totalMonthlyCost)
                Spacer(modifier = Modifier.height(16.dp))
                // onDelete funtzioak ez du ezer egingo aurrebistan
                SubscriptionList(
                    subscriptions = sampleUiState.subscriptions,
                    onDelete = {}
                )
            }
        }
    }
}