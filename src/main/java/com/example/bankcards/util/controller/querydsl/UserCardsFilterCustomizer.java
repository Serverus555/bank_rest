package com.example.bankcards.util.controller.querydsl;

import com.example.bankcards.entity.QCard;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public class UserCardsFilterCustomizer implements QuerydslBinderCustomizer<QCard> {

    @Override
    public void customize(QuerydslBindings bindings, QCard root) {
        // Регистро-независимый поиск
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));

        bindings.excluding(root.id);
        bindings.excluding(root.encryptedNumber);
        bindings.excluding(root.ownerId);

    }

}
