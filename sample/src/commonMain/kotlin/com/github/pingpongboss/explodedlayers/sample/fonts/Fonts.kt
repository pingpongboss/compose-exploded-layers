package com.github.pingpongboss.explodedlayers.sample.fonts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.github.pingpongboss.explodedlayers.sample.resources.Res
import com.github.pingpongboss.explodedlayers.sample.resources.bungee
import com.github.pingpongboss.explodedlayers.sample.resources.creepster
import com.github.pingpongboss.explodedlayers.sample.resources.league_spartan
import com.github.pingpongboss.explodedlayers.sample.resources.montserrat
import com.github.pingpongboss.explodedlayers.sample.resources.pixelify_sans_regular
import org.jetbrains.compose.resources.Font

@Composable
@Stable
fun pixelifySans() = FontFamily(Font(Res.font.pixelify_sans_regular, FontWeight.Normal))

@Composable
@Stable
fun montserrat() = FontFamily(Font(Res.font.montserrat, FontWeight.Normal))

@Composable
@Stable
fun creepster() = FontFamily(Font(Res.font.creepster, FontWeight.ExtraBold))

@Composable
@Stable
fun bungee() = FontFamily(Font(Res.font.bungee, FontWeight.Normal))

@Composable
@Stable
fun leagueSpartan() = FontFamily(Font(Res.font.league_spartan, FontWeight.Normal))
