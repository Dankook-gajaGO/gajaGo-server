// 사용자가 입력한 첫번째 채팅 전체 문장(React -> Spring)
package com.example.gazago.gazago.transport.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatInitialRequest {
    private String message; //사용자 첫 메시지
}
