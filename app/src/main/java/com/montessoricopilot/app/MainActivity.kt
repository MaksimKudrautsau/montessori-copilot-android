package com.montessoricopilot.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.montessoricopilot.app.logic.withAppLanguage
import com.montessoricopilot.app.ui.navigation.MontessoriNavGraph
import com.montessoricopilot.app.ui.theme.MontessoriCopilotTheme

class MainActivity : ComponentActivity() {

    /**
     * Applies the user's language choice before any resources are resolved.
     * This is the earliest hook available, and it is why the preference is
     * stored in SharedPreferences — it must be readable synchronously here.
     */
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.withAppLanguage())
    }

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
