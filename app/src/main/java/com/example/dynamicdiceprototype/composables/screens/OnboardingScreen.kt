package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import com.example.dynamicdiceprototype.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {

  val textHeader =
      arrayOf(
          "Customize", "Create Die", "Create Dice Groups", "Configure", "Roll", "Manage", "Done")
  val textDescription =
      arrayOf(
          "Add images from your device for your dice\nTag images that you want to use together",
          "Create a die with images or numbers\nSet values for each face of the die",
          "Combine different dice to a group of dice",
          "Configure dice with weights or colors",
          "Roll and interact with the dice",
          "Manage dice and dice groups",
          "")
  val images =
      arrayOf(
          R.drawable.onboarding_add_images,
          R.drawable.onboarding_create_die,
          R.drawable.onboarding_create_dice_group,
          R.drawable.onboarding_add_weights_and_colors,
          R.drawable.onboarding_roll_dice,
          R.drawable.onboarding_manage_die,
          R.drawable.onboarding_roll_dice_2,
      )
  val pagerState = rememberPagerState(pageCount = { textHeader.size })
  HorizontalPager(state = pagerState) { page ->
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier =
            Modifier.fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Color(0x85FFAF72))) {
          Text(text = textHeader[page], style = Typography.headlineMedium)
          Image(
              painter = painterResource(id = images[page]),
              contentDescription = "Add images",
              modifier = Modifier.fillMaxSize(0.75f))
          Text(text = textDescription[page])
          if (pagerState.currentPage == textHeader.size - 1) {
            Button(
                onClick = {
                  PreferenceManager.saveData(PreferenceKey.HasOnboardingCompleted, true)
                }) {
                  Text(text = "Let's start rolling")
                }
          }
          PagerIndicator(pagerState = pagerState)
        }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
  Row(
      Modifier.wrapContentHeight().fillMaxWidth().padding(bottom = 8.dp),
      horizontalArrangement = Arrangement.Center) {
        repeat(pagerState.pageCount) { iteration ->
          val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
          Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(16.dp))
        }
      }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
  DynamicDicePrototypeTheme { OnboardingScreen() }
}
