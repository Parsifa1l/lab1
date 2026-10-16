package ru.mfa.lab2.model;

public record Copy(Long id, Long editionId, String inventoryNumber, CopyCondition condition) {}
