package com.montessoricopilot.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.montessoricopilot.app.ui.navigation.MontessoriNavGraph
import com.montessoricopilot.app.ui.theme.MontessoriCopilotTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MontessoriApp

        setContent {
            MontessoriCopilotTheme {
                MontessoriNavGraph(
                    contentDatabase = app.contentDatabase,
                    userDatabase = app.userDatabase,
                )
            }
        }
    }
}
