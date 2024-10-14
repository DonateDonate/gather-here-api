package gather.here.api.Utils;


import gather.here.api.domain.entities.Member;

import java.util.Random;

public class Utils {
    public static String randomMemberId(){
        Random random = new Random();
        StringBuilder numberBuilder = new StringBuilder();
        numberBuilder.append("010");

        for (int i = 0; i < 8; i++) {
            int digit = random.nextInt(10);
            numberBuilder.append(digit);
        }

        // Convert StringBuilder to a String
        return numberBuilder.toString();
    }

    public static Long randomMemberSeq(){
        Random random = new Random();
        long upperBound = 10000L;
        return random.nextLong(upperBound);
    }

    public static Member createRandomMember(String password) {
        String id = Utils.randomMemberId();
        return Member.create(id,password);
    }
}
