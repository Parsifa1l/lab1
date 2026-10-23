package ru.mfa.lab3.model;

public record Copy(Long id, Long editionId, String inventoryNumber, CopyCondition condition) {}
