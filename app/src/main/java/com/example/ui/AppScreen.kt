package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChartOutlined
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ActivityEntity

val Orange500 = Color(0xFFF97316)
val Orange400 = Color(0xFFFB923C)
val Orange600 = Color(0xFFEA580C)
val Orange50 = Color(0xFFFFF7ED)
val Gray800 = Color(0xFF1F2937)
val Gray600 = Color(0xFF4B5563)
val Gray500 = Color(0xFF6B7280)
val Gray400 = Color(0xFF9CA3AF)
val Gray300 = Color(0xFFD1D5DB)
val Gray200 = Color(0xFFE5E7EB)
val Gray100 = Color(0xFFF3F4F6)
val Gray50 = Color(0xFFF9FAFB)
val Blue500 = Color(0xFF3B82F6)
val Blue50 = Color(0xFFEFF6FF)

@Composable
fun AppScreen(viewModel: MainViewModel) {
    val activities by viewModel.uiState.collectAsStateWithLifecycle()
    var activeTab by remember { mutableStateOf("home") }
    var isModalOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for bottom nav
        ) {
            if (activeTab == "home") {
                HomeView(activities = activities, onOpenModal = { isModalOpen = true })
            } else if (activeTab == "stats") {
                StatsView(activities = activities)
            }
        }

        BottomNav(
            activeTab = activeTab,
            onTabSelected = { activeTab = it },
            onFabClick = { isModalOpen = true },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        if (isModalOpen) {
            AddDataModal(
                onClose = { isModalOpen = false },
                onSave = { text ->
                    viewModel.parseAndSaveData(text)
                    isModalOpen = false
                }
            )
        }
    }
}

@Composable
fun HomeView(activities: List<ActivityEntity>, onOpenModal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Aktivitas Saya",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (activities.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.ListAlt,
                    contentDescription = null,
                    tint = Gray300,
                    modifier = Modifier
                        .size(64.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = "Belum ada data",
                    fontWeight = FontWeight.Medium,
                    color = Gray500
                )
                Text(
                    text = "Tekan tombol + di bawah untuk menambah.",
                    fontSize = 12.sp,
                    color = Gray400,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(activities) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(1.dp, Gray100, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.activity,
                                fontWeight = FontWeight.Bold,
                                color = Gray800,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "${item.date} • ${item.startTime} - ${item.endTime}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Gray400,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Orange50, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.durationText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Orange600
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsView(activities: List<ActivityEntity>) {
    val totalMinutesAll = activities.sumOf { it.rawMinutes }
    val totalHours = totalMinutesAll / 60
    val totalMins = totalMinutesAll % 60
    val totalDurationText = if (totalHours > 0) "${totalHours} Jam ${totalMins} Menit" else "${totalMins} Menit"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Statistik",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Gray800,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Orange500, Orange400)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Total Waktu Aktivitas",
                    color = Orange50,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = totalDurationText,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(1.dp, Gray100, RoundedCornerShape(24.dp))
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Sesi Aktivitas",
                    color = Gray400,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${activities.size} Sesi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Gray800,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Blue50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.InsertChartOutlined,
                    contentDescription = null,
                    tint = Blue500
                )
            }
        }
    }
}

@Composable
fun BottomNav(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    onFabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(64.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected("home") }
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Beranda",
                    tint = if (activeTab == "home") Orange500 else Gray400,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Beranda",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeTab == "home") Orange500 else Gray400,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Dummy box to take space for FAB
            Box(modifier = Modifier.width(56.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(64.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected("stats") }
            ) {
                Icon(
                    imageVector = Icons.Default.InsertChartOutlined,
                    contentDescription = "Statistik",
                    tint = if (activeTab == "stats") Orange500 else Gray400,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Statistik",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeTab == "stats") Orange500 else Gray400,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // FAB
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 40.dp), // Elevated above the nav bar
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(elevation = 12.dp, shape = CircleShape, spotColor = Orange500)
                .background(Orange500, CircleShape)
                .clickable { onFabClick() }
                .testTag("add_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Tambah",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AddDataModal(
    onClose: () -> Unit,
    onSave: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66111827)) // bg-gray-900/40
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .background(Color.White, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {} // consume clicks so they don't close the modal
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tambah Data",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Gray800
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Gray100, CircleShape)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Gray500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Tempel (Paste) Chat WhatsApp",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Gray600,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Gray50, RoundedCornerShape(16.dp))
                    .testTag("chat_input"),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                placeholder = {
                    Text(
                        text = "[6/5, 09.36] Me: Jalan\n[6/5, 10.07] Me: .",
                        color = Gray300,
                        fontFamily = FontFamily.Monospace
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Gray200,
                    focusedBorderColor = Orange500,
                    cursorColor = Orange500
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSave(inputText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("save_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange500)
            ) {
                Text(
                    text = "Proses & Simpan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
