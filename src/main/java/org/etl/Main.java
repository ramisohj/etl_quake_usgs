package org.etl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.ETL();
    }

    public void ETL(){
        // URL of the GET request
        String urlString = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=1900-01-01&endtime=2025-10-10&minmagnitude=2.5&latitude=-16.5&longitude=-64.5&maxradiuskm=500";
        // File path to save the GeoJSON response
        String filePath = "earthquake_data.geojson";

        try {
            // Create a URL object
            URL url = new URL(urlString);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check if the response code is OK (200)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder geoJsonBuilder = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    geoJsonBuilder.append(inputLine);
                }
                in.close();

                // Write the formatted JSON to a file
                File file = new File(filePath);

                // Convert the StringBuilder to a String
                String geoJsonString = geoJsonBuilder.toString();

                // Format the GeoJSON string with indentation
                String formattedGeoJson = formatJson(geoJsonString);

                // Save the formatted GeoJSON to a file
                if (formattedGeoJson != null) {
                    saveToFile(formattedGeoJson, filePath);
                    System.out.println("Formatted GeoJSON saved to: " + filePath);
                } else {
                    System.out.println("Failed to format GeoJSON.");
                }

                System.out.println("GeoJSON data saved to: " + file.getAbsolutePath());
            } else {
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }

            // Disconnect the connection
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Formats a JSON string with indentation.
     *
     * @param jsonString The JSON string to format.
     * @return The formatted JSON string, or null if formatting fails.
     */
    private static String formatJson(String jsonString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Parse the JSON string into a JsonNode
            JsonNode jsonNode = mapper.readValue(jsonString, JsonNode.class);
            // Convert the JsonNode back to a formatted JSON string
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Saves a string to a file.
     *
     * @param content  The content to save.
     * @param filePath The path to the file.
     */
    private static void saveToFile(String content, String filePath) {
        try {
            File file = new File(filePath);
            // Write the content to the file
            java.nio.file.Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}