package com.soenmez.profilecardlayout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.soenmez.profilecardlayout.ui.theme.ProfileCardLayoutTheme
import com.soenmez.profilecardlayout.ui.theme.lightGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileCardLayoutTheme {
                UsersApplication()
            }
        }
    }
}

@Composable
fun UsersApplication(userProfiles: List<UserProfile> = userProfileList) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "user_list") {
        composable("user_list") { UserListScreen(userProfiles, navController) }
        composable(
            "user_profile_detail/{userId}",
            arguments = listOf(navArgument("userId") {
                type = NavType.IntType
            }
            )) { navBackStackEntry ->
            UserProfileDetailScreen(navBackStackEntry.arguments!!.getInt("userId"), navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(userProfiles: List<UserProfile>, navController: NavHostController?) {
    Scaffold(topBar = {
        AppBar(
            title = "Users list",
            icon = Icons.Default.Home
        ) {}
    }) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(items = userProfiles) { userProfile ->
                    ProfileCard(userProfile) {
                        navController?.navigate("user_profile_detail/${userProfile.id}")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDetailScreen(userId: Int, navController: NavHostController?) {
    val userProfile = userProfileList.firstOrNull { it.id == userId } ?: return

    Scaffold(topBar = {
        AppBar(
            title = "Users profile detail",
            icon = Icons.Default.ArrowBack
        ) {
            navController?.navigateUp()
        }
    }) {
        Surface(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ProfilePicture(userProfile.pictureUrl, userProfile.status, 240.dp)
                ProfileContent(userProfile.name, userProfile.status, Alignment.CenterHorizontally)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(title: String, icon: ImageVector, clickAction: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            Icon(
                imageVector = icon,
                contentDescription = "Content Description",
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .clickable { clickAction.invoke() }
            )
        },
        title = { Text(text = title) },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun ProfileCard(userProfile: UserProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .padding(top = 8.dp, bottom = 4.dp, start = 16.dp, end = 16.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            ProfilePicture(userProfile.pictureUrl, userProfile.status, 72.dp)
            ProfileContent(userProfile.name, userProfile.status, Alignment.Start)
        }
    }
}

@Composable
fun ProfilePicture(pictureUrl: String, onlineStatus: Boolean, imageSize: Dp) {
    Card(
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = if (onlineStatus) MaterialTheme.colorScheme.lightGreen else Color.Red
        ),
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        val requestBuilder = ImageRequest.Builder(LocalContext.current)
            .data(pictureUrl)
            .crossfade(true)
            .build()

        AsyncImage(
            model = requestBuilder,
            contentDescription = "Content Description",
            modifier = Modifier.size(imageSize),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun ProfileContent(userName: String, onlineStatus: Boolean, alignment: Alignment.Horizontal) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = alignment
    ) {
        CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = if (onlineStatus) 1f else 0.6f)) {
            Text(text = userName, style = MaterialTheme.typography.headlineMedium)
        }
        CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = if (onlineStatus) 1f else 0.6f)) {
            Text(
                text = if (onlineStatus) "Active now" else "Offline",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileDetailPreview() {
    ProfileCardLayoutTheme {
        UserProfileDetailScreen(0, null)
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    ProfileCardLayoutTheme {
        UserListScreen(userProfiles = userProfileList, null)
    }
}
