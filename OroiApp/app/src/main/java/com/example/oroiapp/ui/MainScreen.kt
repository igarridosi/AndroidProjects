package com.example.oroiapp.ui

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.OroiTheme
import com.example.oroiapp.model.BillingCycle
import com.example.oroiapp.model.Subscription
import com.example.oroiapp.viewmodel.MainUiState
import com.example.oroiapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainHeader(username: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "oroi",
            fontSize = 32.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Ongi Etorri, ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAddSubscription: () -> Unit,
    onEditSubscription: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogInput by viewModel.dialogUsernameInput.collectAsState()

    if (uiState.showUsernameDialog) {
        UsernamePromptDialog(
            currentInput = dialogInput,
            onInputChange = viewModel::onDialogUsernameChange,
            onSave = viewModel::onUsernameSave
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSubscription,
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Harpidetza Gehitu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Usamos el nuevo header
            MainHeader(username = uiState.username)
            Spacer(modifier = Modifier.height(24.dp))
            CostCarousel(uiState = uiState)
            Spacer(modifier = Modifier.height(24.dp))
            SubscriptionList(
                subscriptions = uiState.subscriptions,
                onEdit = onEditSubscription
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
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 16.dp,
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
                val color = if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface

            )
            Text(
                text = "€${"%.2f".format(amount)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
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
        // Padding-a elementu bakoitzari emango diogu, ez zerrendari
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
                modifier = Modifier.padding(vertical = 4.dp),
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    val color = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surface
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color, shape = RoundedCornerShape(12.dp))
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
                // Orain SubscriptionItem da irristatzen den elementu osoa
                SubscriptionItem(subscription = subscription)
            }
        }
    }
}

@Composable
fun SubscriptionItem(subscription: Subscription) {
    val nextPaymentDate = calculateNextPaymentDate(subscription)
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    // Box-a kendu eta Card-a da elementu nagusia
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
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
                    Text(subscription.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Hurrengo ordainketa: ${dateFormat.format(nextPaymentDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val (text, color, textColor) = when (cycle) {
        BillingCycle.WEEKLY -> Triple("A", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        BillingCycle.MONTHLY -> Triple("H", MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
        BillingCycle.ANNUAL -> Triple("U", MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(text = text, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Oroi - Nire Harpidetzak") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)

            ) {
                CostCarousel(uiState = sampleUiState)
                Spacer(modifier = Modifier.height(16.dp))
                SubscriptionList(
                    subscriptions = sampleUiState.subscriptions,
                    onEdit = {}
                )
            }
        }
    }
}