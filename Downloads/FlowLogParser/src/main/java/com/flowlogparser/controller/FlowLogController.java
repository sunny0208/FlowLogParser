package com.flowlogparser.controller;

import com.flowlogparser.service.FlowLogParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/flowlog")

public class FlowLogController {

    private final FlowLogParserService flowLogParserService;

    @Autowired
    public FlowLogController(FlowLogParserService flowLogParserService) {
        this.flowLogParserService = flowLogParserService;
    }

    @PostMapping("/parse")
    public String parseFlowLog(@RequestParam("file") MultipartFile file) {
        try {
            String outputFilePath = "output.txt";  // This can be configured to a different path as needed.
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFilePath))) {
                flowLogParserService.parseFlowLog(file.getInputStream(), writer);
            }
            return "Flow log parsed successfully. Results saved to " + outputFilePath;
        } catch (IOException e) {
            return "Failed to parse flow log: " + e.getMessage();
        } catch (Exception e) {
            return "An error occurred: " + e.getMessage();
        }
    }

    @GetMapping("/tag")
    public String getTag(@RequestParam String dstport, @RequestParam String protocol) {
        return flowLogParserService.getTag(dstport, protocol);
    }
}
