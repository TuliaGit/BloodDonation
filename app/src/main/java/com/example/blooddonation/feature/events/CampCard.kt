package com.example.blooddonation.feature.events


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.blooddonation.R
import com.example.blooddonation.feature.theme.BloodBankTheme
import java.io.File

@Composable
fun CampCard(
    modifier: Modifier = Modifier,
    name: String,
    location: String,
    date: String,
    description: String,
    imagePath: String = "",
    actionContent: @Composable RowScope.() -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val model = when {
                imagePath.isNotEmpty() && File(imagePath).exists() -> File(imagePath)
                imagePath.isNotEmpty() -> imagePath
                else -> null
            }

            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_image),
                error = painterResource(id = R.drawable.ic_image),
                fallback = painterResource(id = R.drawable.ic_image),
                alignment = Alignment.Center
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Location: $location", color = MaterialTheme.colorScheme.onBackground)
                Text("Date: $date", color = MaterialTheme.colorScheme.onBackground)
                Text(description, color = MaterialTheme.colorScheme.onBackground, maxLines = 3)

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    content = actionContent
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCampCard() {
    BloodBankTheme {
        CampCard(
            name = "Downtown Blood Donation Camp",
            location = "Community Hall, Main Street",
            date = "June 20, 2025",
            description = "Join us to donate blood and save lives. Refreshments and certificates for all donors!",
            imagePath = "",
            actionContent = {
                Button(onClick = { }) {
                    Text("Register")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
