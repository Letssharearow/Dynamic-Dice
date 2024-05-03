package com.example.dynamicdiceprototype.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.dynamicdiceprototype.services.DiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceGroupsScreen(
    viewModel: DiceViewModel,
    onSelectGroup: () -> Unit,
    onCreateNewGroup: (number: Int) -> Unit
) {}
