package com.example.bankcards.controller.user;

import com.example.bankcards.dto.user.in.TransferRequest;
import com.example.bankcards.dto.user.out.UserCardView;
import com.example.bankcards.dto.mappers.UserDtoMappers;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.controller.querydsl.UserCardsFilterCustomizer;
import com.example.bankcards.util.security.AuthenticatedUserId;
import com.example.bankcards.util.security.PreAuthorizeRole;
import com.example.bankcards.service.user.UserCardService;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/card")
@PreAuthorizeRole("USER")
@Validated
public class UserCardController {

    private final UserCardService userCardService;
    private final UserDtoMappers userDtoMappers;

    @GetMapping
    public PagedModel<UserCardView> getMyCards(
        @ParameterObject
        @QuerydslPredicate(root = Card.class, bindings = UserCardsFilterCustomizer.class)
        Predicate predicate,

        @ParameterObject @PageableDefault Pageable pageable,
        @AuthenticatedUserId Long userId) {

        return new PagedModel<>(userCardService.getUserCards(pageable, predicate, userId).map(userDtoMappers::userCardView));
    }

    @PostMapping("/block")
    public void requestBlock(@RequestParam @NotNull Long cardId, @AuthenticatedUserId Long userId) {
        userCardService.requestBlock(cardId, userId);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody @Valid TransferRequest transferRequest, @AuthenticatedUserId Long userId) {
        userCardService.transfer(transferRequest, userId);
    }

    @GetMapping("/balance")
    public BigDecimal getBalance(@RequestParam @NotNull Long cardId, @AuthenticatedUserId Long userId) {
        return userCardService.getBalance(cardId, userId);
    }
}
