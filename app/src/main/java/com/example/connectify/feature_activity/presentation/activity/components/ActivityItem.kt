package com.example.connectify.feature_activity.presentation.activity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.connectify.R
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.feature_activity.presentation.util.ActivityAction
import com.example.connectify.core.presentation.ui.theme.SpaceSmall

@Composable
fun ActivityItem(
    activity: Activity,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .padding(
                start = SpaceSmall,
                end = SpaceSmall
            ),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val fillerText = when(activity.actionType) {
                is ActivityAction.LikedPost ->
                    stringResource(id = R.string.liked)
                is ActivityAction.CommentedOnPost ->
                    stringResource(id = R.string.commented_on)
                is ActivityAction.FollowedYou ->
                    stringResource(id = R.string.followed_you)
            }
            val actionText = when(activity.actionType) {
                is ActivityAction.LikedPost ->
                    stringResource(id = R.string.your_post)
                is ActivityAction.CommentedOnPost ->
                    stringResource(id = R.string.your_post)
                is ActivityAction.FollowedYou -> ""
            }
            Text(
                text = buildAnnotatedString {
                    val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
                    withStyle(boldStyle) {
                        append(activity.username)
                    }
                    append(" $fillerText ")
                    withStyle(boldStyle) {
                        append(actionText)
                    }
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = activity.formattedTime,
                textAlign = TextAlign.Right,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}