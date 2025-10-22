package io.github.pingpongboss.explodedlayers.samples.common.fonts

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import compose.exploded.layers.samples.common.generated.resources.Res
import compose.exploded.layers.samples.common.generated.resources.bungee_regular
import compose.exploded.layers.samples.common.generated.resources.creepster_extrabold
import compose.exploded.layers.samples.common.generated.resources.league_spartan_regular
import compose.exploded.layers.samples.common.generated.resources.montserrat_regular
import compose.exploded.layers.samples.common.generated.resources.pixelify_sans_regular
import org.jetbrains.compose.resources.Font

@Composable
fun pixelifySansRegular() = FontFamily(Font(Res.font.pixelify_sans_regular, FontWeight.Normal))

@Composable
fun montserratRegular() = FontFamily(Font(Res.font.montserrat_regular, FontWeight.Normal))

@Composable
fun creepsterExtrabold() = FontFamily(Font(Res.font.creepster_extrabold, FontWeight.ExtraBold))

@Composable fun bungeeRegular() = FontFamily(Font(Res.font.bungee_regular, FontWeight.Normal))

@Composable
fun leagueSpartanRegular() = FontFamily(Font(Res.font.league_spartan_regular, FontWeight.Normal))
