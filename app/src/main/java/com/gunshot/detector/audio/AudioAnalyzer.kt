package com.gunshot.detector.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

private const val SAMPLE_RATE = 16_000
private const val WINDOW_SAMPLES = 15_600
private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
private const val GUNSHOT_CLASS_INDEX = 427
const val DETECTION_THRESHOLD = 0.80f

data class DetectionResult(val isGunshot: Boolean, val confidence: Float, val timestamp: Long = System.currentTimeMillis(), val estimatedDirection: Float? = null, val weaponType: String? = null, val weaponCaliberEstimate: String? = null)

class AudioAnalyzer(private val context: Context) {
    private val _detectionFlow = MutableSharedFlow<DetectionResult>(replay = 0)
    val detectionFlow: SharedFlow<DetectionResult> = _detectionFlow
    private var tfliteInterpreter: Interpreter? = null
    private var audioRecord: AudioRecord? = null
    private var analysisJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    init { loadModel() }
    private fun loadModel() { try { val modelBuffer = loadModelFile("yamnet.tflite"); val options = Interpreter.Options().apply { numThreads = 2 }; tfliteInterpreter = Interpreter(modelBuffer, options) } catch (e: Exception) { throw RuntimeException("Cannot load yamnet.tflite", e) } }
    private fun loadModelFile(filename: String): MappedByteBuffer { val fileDescriptor = context.assets.openFd(filename); val inputStream = FileInputStream(fileDescriptor.fileDescriptor); return inputStream.channel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength) }
    fun startListening() { if (analysisJob?.isActive == true) return; val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT); val bufferSize = maxOf(minBufferSize, WINDOW_SAMPLES * 2); audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize); audioRecord?.startRecording(); analysisJob = scope.launch { val shortBuffer = ShortArray(WINDOW_SAMPLES); val floatBuffer = FloatArray(WINDOW_SAMPLES); while (isActive) { val samplesRead = audioRecord?.read(shortBuffer, 0, WINDOW_SAMPLES) ?: 0; val maxAmplitude = shortBuffer.take(samplesRead).maxOrNull() ?: 0; if (maxAmplitude < 1000) continue; for (i in 0 until samplesRead) { floatBuffer[i] = shortBuffer[i].toFloat() / Short.MAX_VALUE }; val confidence = runInference(floatBuffer); _detectionFlow.emit(DetectionResult(isGunshot = confidence > DETECTION_THRESHOLD, confidence = confidence)) } } }
    private fun runInference(audioData: FloatArray): Float { val interpreter = tfliteInterpreter ?: return 0f; val inputBuffer = ByteBuffer.allocateDirect(WINDOW_SAMPLES * 4).order(ByteOrder.nativeOrder()); for (sample in audioData) { inputBuffer.putFloat(sample) }; inputBuffer.rewind(); val outputArray = Array(1) { FloatArray(521) }; interpreter.run(inputBuffer, outputArray); return outputArray[0][GUNSHOT_CLASS_INDEX] }
    fun stopListening() { analysisJob?.cancel(); audioRecord?.stop(); audioRecord?.release(); audioRecord = null }
    fun release() { stopListening(); tfliteInterpreter?.close(); scope.cancel() }
}