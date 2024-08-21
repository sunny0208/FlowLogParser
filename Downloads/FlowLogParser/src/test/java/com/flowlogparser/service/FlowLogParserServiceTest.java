package com.flowlogparser.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FlowLogParserServiceTest {

    @InjectMocks
    private FlowLogParserService flowLogParserService;

    @BeforeEach
    void setUp() throws Exception {
        // Load the test-specific lookup table
        InputStream lookupStream = getClass().getClassLoader().getResourceAsStream("lookuptest.csv");
        flowLogParserService = new FlowLogParserService() {
            @Override
            protected void loadLookupTable() throws IOException {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(lookupStream))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        String key = parts[0] + "," + parts[1].toLowerCase();
                        lookupTable.put(key, parts[2]);
                    }
                }
            }
        };
    }


    @Test
    void testGetTag_ExistingKey() {
        // Test for existing keys in the lookup table
        assertEquals("sv_p1", flowLogParserService.getTag("23", "tcp")); // Entry from lookuptest.csv
        assertEquals("sv_p1", flowLogParserService.getTag("23", "udp")); // Entry from lookuptest.csv
        assertEquals("sv_p3", flowLogParserService.getTag("25", "udp")); // Entry from lookuptest.csv
        assertEquals("sv_p2", flowLogParserService.getTag("443", "udp")); // Entry from lookuptest.csv
    }

    @Test
    void testGetTag_NonExistingKey() {
        // Test for a non-existing key in the lookup table
        assertEquals("Untagged", flowLogParserService.getTag("80", "tcp"));
        assertEquals("Untagged", flowLogParserService.getTag("22", "udp")); // Protocol not in lookup
    }

    @Test
    void testParseFlowLog() throws Exception {
        // Load flow log data from test resources
        InputStream flowLogStream = getClass().getClassLoader().getResourceAsStream("flowlogtest.txt");

        // Capture the output in a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        // Parse the flow log
        flowLogParserService.parseFlowLog(flowLogStream, writer);

        // Convert the output stream to a string and assert the expected output
        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("sv_p1: 1"));
        assertTrue(output.contains("sv_p2: 1"));
        assertTrue(output.contains("sv_p3: 3"));
    }
}
