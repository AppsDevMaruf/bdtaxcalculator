package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        ProfileHeader()
        
        Spacer(modifier = Modifier.size(32.dp))
        
        ProfileSection(title = "অ্যাকাউন্ট সেটিংস") {
            ProfileMenuItem(icon = Icons.Default.Person, label = "ব্যক্তিগত তথ্য")
            ProfileMenuItem(icon = Icons.Default.Lock, label = "নিরাপত্তা")
            ProfileMenuItem(icon = Icons.Default.Settings, label = "অ্যাপ সেটিংস")
        }
        
        Spacer(modifier = Modifier.size(24.dp))
        
        ProfileSection(title = "সহায়তা ও তথ্য") {
            ProfileMenuItem(icon = Icons.Default.Info, label = "আমাদের সম্পর্কে")
            ProfileMenuItem(icon = Icons.Default.Description, label = "শর্তাবলী")
            ProfileMenuItem(icon = Icons.Default.Email, label = "যোগাযোগ")
        }
        
        Spacer(modifier = Modifier.size(24.dp))
        
        ProfileSection(title = "অন্যান্য") {
            ProfileMenuItem(icon = Icons.Default.Share, label = "শেয়ার করুন")
            ProfileMenuItem(icon = Icons.Default.Star, label = "রেট দিন")
        }
        
        Spacer(modifier = Modifier.size(32.dp))
        
        Text(
            "সংস্করণ ১.০.৫",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 12.sp,
            color = HomeTextMuted,
            fontFamily = TiroBanglaFontFamily
        )
        
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = HomeSoftBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = HomeActionBlue
                )
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "মারুফ আলম",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = HomeActionBlue,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                "maruf@example.com",
                fontSize = 14.sp,
                color = HomeTextMuted,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HomeTextPrimary,
            fontFamily = TiroBanglaFontFamily,
            modifier = Modifier.padding(start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HomeBorder)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            color = HomeSoftBlue,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(8.dp).size(20.dp),
                tint = HomeActionBlue
            )
        }
        Text(
            label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = HomeTextPrimary,
            fontFamily = TiroBanglaFontFamily
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = HomeTextMuted
        )
    }
}
