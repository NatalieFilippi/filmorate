package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Event {
    private long id;            //primary key
    private long userId;
    private String eventType;   // одно из значениий LIKE, REVIEW или FRIEND
    private String operation;   // одно из значениий REMOVE, ADD, UPDATE
    private long entityId;      // идентификатор сущности, с которой произошло событие
    private LocalDateTime timeStamp;
}
