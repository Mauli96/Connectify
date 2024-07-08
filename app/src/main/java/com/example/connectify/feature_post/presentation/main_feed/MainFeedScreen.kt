package com.example.connectify.feature_post.presentation.main_feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.connectify.R
import com.example.connectify.core.presentation.components.Post
import com.example.connectify.core.presentation.components.StandardToolbar
import com.example.connectify.core.util.Screen

@Composable
fun MainFeedScreen(
    navController: NavController,
    viewModel: MainFeedViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        StandardToolbar(
            navController = navController,
            title = {
                Text(
                    text = stringResource(id = R.string.your_feed),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            modifier = Modifier.fillMaxWidth(),
            navActions = {
                IconButton(onClick = {
                    navController.navigate(Screen.SearchScreen.route)
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        )
        Post(
            post = com.example.connectify.core.domain.models.Post(
                username = "mauli.waghmore",
                imageUrl = "",
                profilePictureUrl = "",
                description = "Are you ready to take control of your financial future? Discover proven strategies to grow your wealth, manage your expenses, and invest wisely. Whether you're just starting out or looking to optimize your current financial plan, our expert tips and advice will help you unlock your financial potential. From budgeting techniques to investment insights, we've got everything you need to make your money work for you. Start your journey towards financial freedom today!",
                likeCount = 70,
                commentCount = 19
            ),
            onPostClick = {
                navController.navigate(Screen.PostDetailScreen.route)
            },
            onLikeByClick = {
                navController.navigate(Screen.PersonListScreen.route)
            }
        )
    }
}