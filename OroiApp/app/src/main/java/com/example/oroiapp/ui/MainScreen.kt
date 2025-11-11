package com.example.oroiapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.model.Subscription
import com.example.oroiapp.ui.theme.OroiTheme
import com.example.oroiapp.viewmodel.MainUiState
import com.example.oroiapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAddSubscription: () -> Unit,
    onEditSubscription: (Int) -> Unit // ZUZENDUTA: Parametro berria onartzen du
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
                onEdit = onEditSubscription // ZUZENDUTA: Funtzioa pasatzen du
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CostCarousel(uiState: MainUiState) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> CostCard(title = "Hileko Gastua", amount = uiState.totalMonthlyCost)
                1 -> CostCard(title = "Urteko Gastua", amount = uiState.totalAnnualCost)
                2 -> CostCard(title = "Eguneko Gastua", amount = uiState.totalDailyCost)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = "€${"%.2f".format(amount)}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionList(
    subscriptions: List<Subscription>,
    onEdit: (Int) -> Unit
) {
    LazyColumn(
        // ZUZENDUTA: Padding-a elementu bakoitzari emango diogu, ez zerrendari
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = subscriptions, key = { it.id }) { subscription ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = {
                    if (it == SwipeToDismissBoxValue.EndToStart) {
                        onEdit(subscription.id)
                        return@rememberSwipeToDismissBoxState false
                    }
                    false
                }
            )
            SwipeToDismissBox(
                state = dismissState,
                modifier = Modifier.padding(vertical = 4.dp), // ZUZENDUTA: Tartea hemen
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    val color = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Color(0xFF4CAF50) // Berdea
                        else -> Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, shape = RoundedCornerShape(12.dp)) // ZUZENDUTA: Forma biribildua
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editatu ikonoa",
                            tint = Color.White
                        )
                    }
                }
            ) {
                // ORAIN SubscriptionItem da irristatzen den elementu osoa
                SubscriptionItem(subscription = subscription)
            }
        }
    }
}

@Composable
fun SubscriptionItem(subscription: Subscription) {
    val nextPaymentDate = calculateNextPaymentDate(subscription)
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    // ZUZENDUTA: Box-a kendu eta Card-a da elementu nagusia
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp) // ZUZENDUTA: Forma biribildua
    ) {
        Box { // Box hau badge-a kokatzeko da
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
                Text(text = "${subscription.amount} ${subscription.currency}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            BillingCycleBadge(
                cycle = subscription.billingCycle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp) // Padding-a badge-ari
            )
        }
    }
}

@Composable
fun BillingCycleBadge(cycle: BillingCycle, modifier: Modifier = Modifier) {
    val (text, color) = when (cycle) {
        BillingCycle.WEEKLY -> "A" to Color(0xFFE57373)
        BillingCycle.MONTHLY -> "H" to Color(0xFFFFB74D)
        BillingCycle.ANNUAL -> "U" to Color(0xFF64B5F6)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

private fun calculateNextPaymentDate(subscription: Subscription): Date {
    val calendar = Calendar.getInstance()
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    calendar.time = subscription.firstPaymentDate
    if (calendar.time.after(today.time)) {
        return calendar.time
    }
    while (calendar.time.before(today.time)) {
        when (subscription.billingCycle) {
            BillingCycle.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            BillingCycle.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            BillingCycle.ANNUAL -> calendar.add(Calendar.YEAR, 1)
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
    val sampleSubscriptions = listOf(
        Subscription(1, "Spotify", 9.99, "EUR", BillingCycle.MONTHLY, Date()),
        Subscription(2, "HBO Max", 8.99, "EUR", BillingCycle.MONTHLY, Date()),
        Subscription(3, "Amazon Prime", 49.90, "EUR", BillingCycle.ANNUAL, Date())
    )
    val sampleUiState = MainUiState(
        subscriptions = sampleSubscriptions,
        totalMonthlyCost = 19.84,
        totalAnnualCost = 238.08,
        totalDailyCost = 0.66
    )

    OroiTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Oroi - Nire Harpidetzak") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                CostCarousel(uiState = sampleUiState)
                Spacer(modifier = Modifier.height(16.dp))
                // ALDAKETA HEMEN DAGO:
                SubscriptionList(
                    subscriptions = sampleUiState.subscriptions,
                    onEdit = {}
                )
            }
        }
    }
}