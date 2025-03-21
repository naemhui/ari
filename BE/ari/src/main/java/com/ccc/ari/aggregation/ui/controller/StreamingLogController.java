package com.ccc.ari.aggregation.ui.controller;

import com.ccc.ari.aggregation.application.service.StreamingLogQueryService;
import com.ccc.ari.aggregation.domain.vo.StreamingLog;
import com.ccc.ari.aggregation.ui.response.StreamingLogResponse;
import com.ccc.ari.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StreamingLogController {

    private final StreamingLogQueryService streamingLogQueryService;

    @GetMapping("/albums/{albumId}/tracks/{trackId}/logs")
    public ApiUtils.ApiResponse<List<StreamingLogResponse>> getStreamingLogByTrackId(@PathVariable Integer albumId,
                                                                                     @PathVariable Integer trackId) {

        List<StreamingLog> streamingLogList = streamingLogQueryService.findStreamingLogByTrackId(trackId);

        List<StreamingLogResponse> streamingLogs = streamingLogList.stream()
                .map(streamingLog -> StreamingLogResponse.builder()
                        .nickname(streamingLog.getMemberNickname())
                        .datetime(streamingLog.timestampToString())
                        .build())
                .toList();

        return ApiUtils.success(streamingLogs);
    }
}
