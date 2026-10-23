package ru.mfa.lab3.entity;

/** WAITING - стоит в очереди, READY - за читателем закреплён экземпляр, FULFILLED - книга выдана, CANCELLED - отменена. */
public enum WaitingStatus { WAITING, READY, FULFILLED, CANCELLED }
