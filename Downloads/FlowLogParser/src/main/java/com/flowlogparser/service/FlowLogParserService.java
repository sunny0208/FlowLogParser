package com.flowlogparser.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class FlowLogParserService {

    protected Map<String, String> lookupTable;

    public FlowLogParserService() throws IOException {
        lookupTable = new HashMap<>();
        loadLookupTable();
    }

    protected void loadLookupTable() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("lookup.csv")) {
            if (inputStream == null) {
                throw new IOException("Lookup file not found: lookup.csv");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if(parts.length >= 3){
                    String key = parts[0] + "," + parts[1].toLowerCase();
                    lookupTable.put(key, parts[2]);
                }else{
                    throw new IOException("Malformed line in lookup file: " + line);
                }
                }
            }
        }
    }

    public String getTag(String dstport, String protocol) {
        return lookupTable.getOrDefault(dstport + "," + protocol.toLowerCase(), "Untagged");
    }

    public void parseFlowLog(InputStream flowLogInputStream, OutputStreamWriter outputFileWriter) throws IOException {
        Map<String, Integer> tagCounts = new HashMap<>();
        Map<String, Integer> portProtocolCounts = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(flowLogInputStream));
             BufferedWriter writer = new BufferedWriter(outputFileWriter)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {  // Check if the data is malformed
                    throw new IOException("Malformed data in flow log");
                }
                String dstport = parts[0];
                String protocol = parts[1].toLowerCase();

                String tag = getTag(dstport, protocol);
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);

                String portProtocolKey = dstport + "," + protocol;
                portProtocolCounts.put(portProtocolKey, portProtocolCounts.getOrDefault(portProtocolKey, 0) + 1);
            }

            writer.write("Tag Counts:\n");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }

            writer.write("\nPort/Protocol Combination Counts:\n");
            for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
                writer.write(entry.getKey().replace(",", " ") + ": " + entry.getValue() + "\n");
            }
        }
    }
}

