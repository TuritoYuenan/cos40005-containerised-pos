package containerised.pos.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import containerised.pos.models.Promotion
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant


private data class EditPromotionFormState(
    var name: String = "",
    var startDate: String? = null,
    var endDate: String? = null,
    var isActive: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPromotionPage(
    navController: NavController,
    promotionId: String?
) {
    var formState by remember { mutableStateOf(EditPromotionFormState()) }
    var promotion by remember { mutableStateOf<Promotion?>(null) }
    LaunchedEffect(Unit)
    {
        try {
            if (promotionId != null) {promotion = Promotion.fetchById(promotionId)}
            promotion?.let {
                formState = EditPromotionFormState(
                    name = it.name,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    isActive = it.isActive,
                )
            }
        }
        catch (e: Exception) {
            println("Error fetching data: ${e.message}")
        }
    }
    LazyColumn {
        item{
            TopBar { navController.popBackStack() }
        }
        item{
            FormSection(
                formState = formState,
                onFormChange = { formState = it }
            )
        }
        item {ConditionSection()}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        title = { Text("Promotion Edit") },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSection(
    formState: EditPromotionFormState,
    onFormChange: (EditPromotionFormState) -> Unit
) {

    var showDialog by remember { mutableStateOf(false) }
    var editingStart by remember { mutableStateOf(true) }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = formState.name,
                onValueChange = {onFormChange(formState.copy(name = it))},
                label = { Text("Name") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                DateField(
                    label = "Start Date",
                    date = startDate,
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f)
                )

                DateField(
                    label = "End Date",
                    date = endDate,
                    onClick = { showDialog = true },
                    modifier = Modifier.weight(1f)
                )
            }
            Row {
                Text("Active")
                Switch(
                    checked = formState.isActive,
                    onCheckedChange = {
                        onFormChange(formState.copy(isActive = it))
                    }
                )
            }
        }
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = millisConverter(datePickerState.selectedDateMillis)
                        println(millis)
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }

        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun DateField(
    label: String,
    date: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall

        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(4.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(date.ifEmpty { "Select date" })

            Icon(Icons.Default.EditCalendar, contentDescription = null)
        }
    }
}

fun timestampConverter(date: String?): Long? {
    if (date.isNullOrBlank()) return null

    val localDateTime = LocalDateTime.parse(date)

    val instant = localDateTime.toInstant(TimeZone.UTC)

    return instant.toEpochMilliseconds()
}

fun millisConverter(millis: Long?): String? {
    if (millis == null) return null

    val instant = Instant.fromEpochMilliseconds(millis)
    val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

    return localDateTime.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Box(
                modifier = Modifier
                    .clickable { println("clicked") }
                    .padding(4.dp)
            ) {
                Text("New Condition +")
            }

        }
    }
}

