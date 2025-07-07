@file:OptIn(ExperimentalMaterial3Api::class)

package com.xhhold.winlator.ui.screens.home

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.CombinedModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.winlator.container.Container
import com.xhhold.winlator.LocalMainModel
import com.xhhold.winlator.R
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel()
) {
    val activity = LocalActivity.current
    val mainViewModel = LocalMainModel.current

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val containers by mainViewModel.containers.collectAsState()

    CompositionLocalProvider(
        LocalHomeViewModel provides homeViewModel
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState, drawerContent = { HomeDrawer() }) {
            Scaffold(
                topBar = {
                    HomeTopBar(onMenuClick = { scope.launch { drawerState.open() } })
                }) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    HomeContainerGrid(
                        containers = containers,
                        onItemClick = { container ->
                            homeViewModel.openEditor(activity, container.id)
                        },
                        onActionClick = { container, action ->
                            when (action) {
                                ContainerCardAction.Run -> homeViewModel.runContainer(activity, container.id)
                                ContainerCardAction.Edit -> homeViewModel.openEditor(activity, container.id)
                                ContainerCardAction.Copy -> { /* TODO: 实现复制逻辑 */ }
                                ContainerCardAction.Remove -> { /* TODO: 实现删除逻辑 */ }
                                ContainerCardAction.Info -> homeViewModel.showInfo(activity, container.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeDrawer() {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .width(240.dp)
            .fillMaxHeight()
            .padding(16.dp)
    ) {

    }
}

@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit = {},
) {
    TopAppBar(title = { Text(stringResource(R.string.app_name)) }, navigationIcon = {
        IconButton(onClick = onMenuClick) {
            Icon(
                Icons.Default.Menu, contentDescription = stringResource(R.string.menu)
            )
        }
    }, actions = {
        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Add, contentDescription = stringResource(R.string.add)
            )
        }
    })
}

@Composable
fun HomeContainerGrid(
    modifier: Modifier = Modifier,
    containers: List<Container>,
    onItemClick: (Container) -> Unit = {},
    onActionClick: (Container, ContainerCardAction) -> Unit = { _, _ -> },
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(containers) { container ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clickable { onItemClick(container) },
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Default.Dns,
                        contentDescription = null,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)
                            .alpha(0.25f)
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                            .padding(start = 12.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = container.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        ContainerCardActions(
                            onActionClick = { action ->
                                onActionClick(container, action)
                            },
                        )
                    }
                }
            }
        }
    }
}

enum class ContainerCardAction {
    Run, Edit, Copy, Remove, Info
}

@Composable
fun ContainerCardActions(
    modifier: Modifier = Modifier,
    onActionClick: (ContainerCardAction) -> Unit = {},
) {
    val menuExpanded = remember { mutableStateOf(false) }
    IconButton(
        onClick = { menuExpanded.value = true },
        modifier = CombinedModifier(outer = modifier, inner = Modifier.size(24.dp))
    ) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.more),
            modifier = Modifier.size(16.dp)
        )
        DropdownMenu(
            expanded = menuExpanded.value, onDismissRequest = { menuExpanded.value = false }) {
            ContainerMenuItem(
                icon = Icons.Default.PlayArrow,
                iconTint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.run),
                text = stringResource(R.string.run),
                onClick = {
                    menuExpanded.value = false
                    onActionClick(ContainerCardAction.Run)
                })
            ContainerMenuItem(
                icon = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                text = stringResource(R.string.edit),
                onClick = {
                    menuExpanded.value = false
                    onActionClick(ContainerCardAction.Edit)
                })
            ContainerMenuItem(
                icon = Icons.Default.ContentCopy,
                contentDescription = stringResource(R.string.duplicate),
                text = stringResource(R.string.duplicate),
                onClick = {
                    menuExpanded.value = false
                    onActionClick(ContainerCardAction.Copy)
                })
            ContainerMenuItem(
                icon = Icons.Default.Delete,
                iconTint = MaterialTheme.colorScheme.error,
                contentDescription = stringResource(R.string.remove),
                text = stringResource(R.string.remove),
                onClick = {
                    menuExpanded.value = false
                    onActionClick(ContainerCardAction.Remove)
                })
            ContainerMenuItem(
                icon = Icons.Default.Info,
                contentDescription = stringResource(R.string.storage_info),
                text = stringResource(R.string.storage_info),
                onClick = {
                    menuExpanded.value = false
                    onActionClick(ContainerCardAction.Info)
                })
        }
    }
}

@Composable
private fun ContainerMenuItem(
    icon: ImageVector,
    iconTint: Color = LocalContentColor.current,
    contentDescription: String,
    text: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                icon, tint = iconTint, contentDescription = contentDescription
            )
        }, text = { Text(text) }, onClick = onClick
    )
}
