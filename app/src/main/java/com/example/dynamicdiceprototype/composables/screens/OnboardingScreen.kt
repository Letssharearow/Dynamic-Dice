package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {

  // Display 10 items
  val textHeader = arrayOf("Getting Started", "Getting Started", "Getting Started", "done")
  val textDescription =
      arrayOf(
          "make dice with custom images, roll them and create your own game ideas",
          "Try holding actions to edit, duplicate or delete dice and dice groups",
          "Customize your group with states",
          "")
  val pagerState = rememberPagerState(pageCount = { textHeader.size })
  HorizontalPager(state = pagerState) { page ->
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()) {
          Text(text = textHeader[page])
          Text(text = textDescription[page])
          if (pagerState.currentPage == textHeader.size - 1) {
            Button(
                onClick = {
                  PreferenceManager.saveData(PreferenceKey.hasOnboardingCompleted, true)
                }) {
                  Text(text = "Let's start rolling")
                }
          }
        }
  }
}
