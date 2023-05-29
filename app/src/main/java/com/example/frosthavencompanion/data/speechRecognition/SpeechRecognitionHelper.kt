package com.example.frosthavencompanion.data.speechRecognition

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SpeechRecognitionHelper(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null

    private val _resultsFlow = MutableStateFlow<List<String>?>(null)
    val resultsFlow: StateFlow<List<String>?> = _resultsFlow

    private val _errorFlow = MutableStateFlow<Int?>(null)
    val errorFlow: StateFlow<Int?> = _errorFlow

    init {
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            speechRecognizerIntent!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        } else {
            // Speech recognition is not supported on this device.
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                // Ready for speech
            }

            override fun onBeginningOfSpeech() {
                // User started speaking
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Input level changed
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // More sound has been received
            }

            override fun onEndOfSpeech() {
                // User finished speaking
            }

            override fun onError(error: Int) {
                _errorFlow.value = error
            }

            override fun onResults(results: Bundle) {
                // A final set of recognition results is ready
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    _resultsFlow.value = matches
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // Intermediate recognition results are ready
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // A reserved event occurred
            }
        }

        speechRecognizer?.setRecognitionListener(listener)
    }

    fun startListening() {
        speechRecognizer?.startListening(speechRecognizerIntent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

}
