package com.example.dynamicdiceprototype.composables.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dynamicdiceprototype.R
import com.example.dynamicdiceprototype.services.PreferenceKey
import com.example.dynamicdiceprototype.services.PreferenceManager
import com.example.dynamicdiceprototype.ui.theme.DynamicDicePrototypeTheme
import com.example.dynamicdiceprototype.ui.theme.Typography

data class OnboardingPage(val header: String, val description: String, val image: Int)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier) {

  val onboardingPages =
      listOf(
          OnboardingPage(
              "Welcome\nto Dynamic Dice",
              "Discover the flexible dice game tool",
              R.drawable.applogo_toolbox),
          OnboardingPage(
              "Customize",
              "Add images from your device for your dice\nTag images that you want to use together",
              R.drawable.onboarding_add_images),
          OnboardingPage(
              "Create Die",
              "Create a die with images or numbers\nSet values for each face of the die",
              R.drawable.onboarding_create_die),
          OnboardingPage(
              "Create Dice Groups",
              "Combine different dice to a group of dice",
              R.drawable.onboarding_create_dice_group),
          OnboardingPage(
              "Configure",
              "Configure dice with weights or colors",
              R.drawable.onboarding_add_weights_and_colors),
          OnboardingPage(
              "Roll", "Roll and interact with the dice", R.drawable.onboarding_roll_dice),
          OnboardingPage("Manage", "Manage dice and dice groups", R.drawable.onboarding_manage_die),
          OnboardingPage("Begin", "", R.drawable.onboarding_roll_dice_2))

  val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
  HorizontalPager(state = pagerState) { page ->
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier =
            Modifier.fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Color(0x85FFAF72))) {
          Text(
              text = onboardingPages[page].header,
              style = Typography.headlineMedium,
              textAlign = TextAlign.Center)
          Box(
              modifier = Modifier.clip(RoundedCornerShape(16.dp)),
              contentAlignment = Alignment.Center) {
                if (page == 0)
                    Image(
                        painter = painterResource(id = onboardingPages[page].image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(0.75f).aspectRatio(1f),
                    )
                else
                    Image(
                        painter = painterResource(id = onboardingPages[page].image),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.75f),
                    )
              }
          Text(text = onboardingPages[page].description, textAlign = TextAlign.Center)
          if (pagerState.currentPage == onboardingPages.size - 1) {
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
