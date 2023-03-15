package org.voice;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class SpeechToText {
    private final SpeechSettings settings;
    private String text;
    private final RecognitionConfig config;

    public SpeechToText() throws IOException {
        // Load the Google Cloud credentials from a JSON file
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new FileInputStream("response-gen-2c889d8e0017.json")
        );

        // Build the SpeechSettings object with the credentials
        settings = SpeechSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // Builds the recognition request
        config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setLanguageCode("en-US")
                .setSampleRateHertz(16000)
                .build();
    }

    public void recognize() {
        // Create the SpeechClient using the SpeechSettings object
        try (SpeechClient speechClient = SpeechClient.create(settings)) {
            // The path to the audio file to transcribe
            String filename = "audio.wav";
            Path path = Paths.get(filename);
            byte[] data = Files.readAllBytes(path);
            ByteString audioBytes = ByteString.copyFrom(data);

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            System.out.println("Printing the transcribed text");
            // Prints the transcribed text
            for (SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                text = alternative.getTranscript();
                System.out.println(text);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
