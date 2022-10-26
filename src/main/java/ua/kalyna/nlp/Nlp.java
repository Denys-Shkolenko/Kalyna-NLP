package ua.kalyna.nlp;

// Imports the Google Cloud client library

import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Nlp {

    public static String stt(String fileName) throws Exception {
        String res = "не розпізнано, спробуйте ще раз";
        try (SpeechClient speech = SpeechClient.create()) {

            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            // Configure request with local raw PCM audio
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(AudioEncoding.OGG_OPUS)
                            .setLanguageCode("uk-UA")
                            .setSampleRateHertz(16000)
                            .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

            // Use blocking call to get audio transcript
            RecognizeResponse response = speech.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                res = alternative.getTranscript();
            }
        }
        return res;
    }

    public static String tts(String inputText) throws Exception {
        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder().setText(inputText).build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("uk-UA")
                            .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                            .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig =
                    AudioConfig.newBuilder().setAudioEncoding(com.google.cloud.texttospeech.v1.AudioEncoding.OGG_OPUS).build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("src/main/resources/output.ogg")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.ogg\"");
            }
            return "src/main/resources/output.ogg";
        }
    }
}