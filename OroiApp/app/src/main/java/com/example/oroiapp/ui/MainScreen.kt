package com.example.oroiapp.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            CostCarousel(uiState = uiState)

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CostCarousel(uiState: MainUiState) {
    // 1. Pager-aren egoera gogoratzen du (zein orritan gauden jakiteko)
    val pagerState = rememberPagerState(pageCount = { 3 })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // 2. HorizontalPager osagaiak karrusela sortzen du
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            // `page` aldagaiak uneko orria adierazten du (0, 1, edo 2)
            when (page) {
                0 -> CostCard(title = "Hileko Gastua", amount = uiState.totalMonthlyCost)
                1 -> CostCard(title = "Urteko Gastua", amount = uiState.totalAnnualCost)
                2 -> CostCard(title = "Eguneko Gastua", amount = uiState.totalDailyCost)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 3. Adierazleak (puntutxoak)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { index ->
                val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun CostCard(title: String, amount: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                // Zenbatekoa bi dezimalekin formateatzen dugu
                text = "€${"%.2f".format(amount)}",
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
    val nextPaymentDate = calculateNextPaymentDate(subscription)
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    // 1. Box bat erabiltzen dugu txartela eta etiketa gainjartzeko
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                // Eskuinean padding gehigarria, etiketak testua estali ez dezan
                .padding(end = 8.dp, top = 8.dp)
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
        // 2. Etiketa txartelaren gainean kokatzen dugu, goiko eskuineko izkinan
        BillingCycleBadge(
            cycle = subscription.billingCycle,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

// 3. FUNTZIO LAGUNTZAILE BERRIA ETİKETA SORTZEKO
@Composable
fun BillingCycleBadge(cycle: BillingCycle, modifier: Modifier = Modifier) {
    val (text, color) = when (cycle) {
        BillingCycle.WEEKLY -> "A" to Color(0xFFE57373) // Gorria
        BillingCycle.MONTHLY -> "H" to Color(0xFFFFB74D) // Laranja
        BillingCycle.ANNUAL -> "U" to Color(0xFF64B5F6) // Urdina
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

// Hurrengo ordainketa-data kalkulatzeko laguntzailea (logika garrantzitsua!)
// MainScreen.kt fitxategiaren barruan

private fun calculateNextPaymentDate(subscription: Subscription): java.util.Date {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()

    // Egunaren ordua, minutuak eta segundoak garbitzen ditugu
    // data-konparaketa zuzena izan dadin.
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    calendar.time = subscription.firstPaymentDate

    // 1. KASUA: Lehen ordainketa-data etorkizunean bada
    if (calendar.time.after(today.time)) {
        return calendar.time

    }

    // 2. KASUA: Lehen ordainketa iraganean edo gaur bada
    // Data gaur baino beranduago izan arte gehitzen jarraitzen dugu
    while (calendar.time.before(today.time)) {
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
fun CostCarouselPreview() {
    val sampleUiState = MainUiState(
        totalMonthlyCost = 21.98,
        totalAnnualCost = 263.76,
        totalDailyCost = 0.73
    )
    OroiTheme {
        CostCarousel(uiState = sampleUiState)
    }
}

@Preview(showBackground = true)
@Composable
fun SubscriptionItemPreview() {
    val sampleWeekly = Subscription(1, "Clase de Yoga", 15.0, "EUR", BillingCycle.WEEKLY, Date())
    val sampleMonthly = Subscription(2, "Netflix", 12.99, "EUR", BillingCycle.MONTHLY, Date())
    val sampleAnnual = Subscription(3, "Amazon Prime", 49.90, "EUR", BillingCycle.ANNUAL, Date())

    OroiTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
            SubscriptionItem(subscription = sampleWeekly)
            SubscriptionItem(subscription = sampleMonthly)
            SubscriptionItem(subscription = sampleAnnual)
        }
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