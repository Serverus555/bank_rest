package com.example.bankcards.dto.mappers;

import com.example.bankcards.dto.admin.out.AdminCardView;
import com.example.bankcards.dto.admin.out.AdminUserView;
import com.example.bankcards.dto.admin.out.PendingBlockCardView;
import com.example.bankcards.entity.BlockCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.business.CardNumberCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminDtoMappers {

    private final CardNumberCrypt cardNumberCrypt;

    public AdminCardView adminCardView(Card card) {
        return new AdminCardView(
            card.getId(),
            cardNumberCrypt.generateMaskedNumber(card.getLast4Digits()),
            card.getOwnerId(),
            card.getOwnerText(),
            card.getExpiration(),
            card.calculateStatus());
    }

    public AdminUserView adminUserView(User user) {
        return new AdminUserView(user.getId(), user.getUsername());
    }

    public PendingBlockCardView pendingBlockCardView(BlockCardRequest request) {
        return new PendingBlockCardView(request.getId(), this.adminCardView(request.getCard()));
    }
}
