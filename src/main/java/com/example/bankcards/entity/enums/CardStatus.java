package com.example.bankcards.entity.enums;

public enum CardStatus {
    ACTIVE,
    BLOCKED,
    /*
    Требование - все 3 статуса. Решение - Вычисляемое в Entity значение. Максимальный приоритет. В бд только ACTIVE и BLOCKED.
    TODO: Рассмотреть другие варианты, может через view в бд
     */
    EXPIRED //
}
