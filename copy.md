            .isWin(
                winnerId != null &&
                player.getId().equals(winnerId)
            )
            .isLoss(
                loserId != null &&
                player.getId().equals(loserId)
            )
            .isSave(
                saveId != null &&
                player.getId().equals(saveId)
            )  
            ==> 박스 스코어 데이터 삭제 후 투수 관련에 해당 메소드 이 코드로 수정

TRUNCATE TABLE box_score;
