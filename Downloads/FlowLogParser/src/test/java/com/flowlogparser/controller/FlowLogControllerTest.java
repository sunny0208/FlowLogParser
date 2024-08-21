package com.flowlogparser.controller;

import com.flowlogparser.service.FlowLogParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.io.OutputStreamWriter;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlowLogController.class) // Specifies that we're testing only the FlowLogController
public class FlowLogControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc allows us to simulate HTTP requests

    @MockBean
    private FlowLogParserService flowLogParserService; // Mocking the FlowLogParserService

    @Test
    void testParseFlowLog() throws Exception {
        // Prepare a mock file to simulate the file upload
        MockMultipartFile file = new MockMultipartFile("file", "flowlogtest.txt",
                "text/plain", "23,tcp\n25,udp\n".getBytes());

        // Simulate the behavior of the service method
        doNothing().when(flowLogParserService).parseFlowLog(any(InputStream.class), any(OutputStreamWriter.class));

        // Perform the POST request to /api/flowlog/parse
        mockMvc.perform(multipart("/api/flowlog/parse")
                        .file(file))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(content().string(containsString("Flow log parsed successfully.")));
    }


    @Test
    void testParseFlowLogWithEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "flowlogtest.txt",
                "text/plain", new byte[0]);

        mockMvc.perform(multipart("/api/flowlog/parse")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Flow log parsed successfully.")));
    }



    @Test
    void testGetTag() throws Exception {
        // Mock the behavior of getTag method in the service
        when(flowLogParserService.getTag("23", "tcp")).thenReturn("sv_p1");

        // Perform the GET request to /api/flowlog/tag
        mockMvc.perform(get("/api/flowlog/tag")
                        .param("dstport", "23")
                        .param("protocol", "tcp"))
                .andExpect(status().isOk()) // Expecting HTTP 200 OK
                .andExpect(content().string("sv_p1")); // Expecting "sv_p1" in the response
    }
}
