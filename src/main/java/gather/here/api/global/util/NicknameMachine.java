package gather.here.api.global.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NicknameMachine {
    private static final List<String> nicknames = Arrays.asList(
            "떡볶이 마법사",
            "콩나물 파이터",
            "잠자는 사자",
            "구름 속 고양이",
            "달콤한 핫도그",
            "햇살 요정",
            "춤추는 용",
            "달밤의 탐험가",
            "비밀의 소풍",
            "버섯 왕국의 왕자",
            "바다 속 유령",
            "초코송이",
            "해피 쥐포",
            "웃는 토끼",
            "핑크 고래",
            "별빛 우주인",
            "다람쥐 스파이",
            "우주에서 온 외계인",
            "방울토마토 요정",
            "하늘을 나는 피자"
    );

    public static String getRandomNickname() {
        int index = new Random().nextInt(nicknames.size());
        return nicknames.get(index);
    }

}
