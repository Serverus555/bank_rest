package com.example.bankcards.dto.mappers;

import com.example.bankcards.dto.user.out.UserCardView;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.business.CardNumberCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDtoMappers {

    private final CardNumberCrypt cardNumberCrypt;

    public UserCardView userCardView(Card card) {
        return new UserCardView(
            card.getId(),
            cardNumberCrypt.generateMaskedNumber(card.getLast4Digits()),
            card.getExpiration(),
            card.calculateStatus()
        );
    }
}
