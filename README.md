# java-filmorate
Template repository for Filmorate project.
[Diagram](src/main/resources/schema.png)
### Примеры запросов к таблице user.
- findAll()
```
select * from USERS;
```

- findById
```
select * from USERS where USER_ID = ?;
```

- getFriends
```
select * from USERS where USER_ID in 
(select FRIEND_ID from FRIENDS where USER_ID = ? AND STATUS = TRUE); 
```

- getCommonFriends
```
select * from USERS where USER_ID in 
(select * from (select FRIEND_ID from FRIENDS where USER_ID=? AND STATUS = TRUE)
 where FRIEND_ID in (select FRIEND_ID from FRIENDS where USER_ID=? AND STATUS = TRUE))
```

-addFriend
```
insert into FRIENDS(USER_ID, FRIEND_ID, STATUS) + values (?, ?, ?);
```

-deleteFriend
```
delete from FRIENDS where USER_ID = ? AND FRIEND_ID = ?;
```

### Примеры запросов к таблице film.
- findAll()
```
select * from FILMS left join MPA M on FILMS.MPA_ID = M.MPA_ID;
```

- findById
```
select * from FILMS left join MPA M on FILMS.MPA_ID = M.MPA_ID where FILM_ID = ?
```

- getPopularFilms
```
select F.*, M.MPA_NAME, COUNT(L.USER_ID) from FILMS F
    left join LIKES L on F.film_id = L.film_id
    left join MPA M on F.MPA_ID = M.MPA_ID
group by F.film_id, film_name, description, duration, f.mpa_id, m.mpa_id, mpa_name, release_date
order by COUNT(L.USER_ID) desc limit ?;
```

-create
```
insert into FILMS(FILM_NAME, DESCRIPTION, DURATION, MPA_ID, RELEASE_DATE)  +
values (?, ?, ?, ?, ?);
```

-addLike
```
insert into LIKES(USER_ID, FILM_ID)  + values (?, ?);
```

-deleteLike
```
delete from LIKES where USER_ID = ? AND FILM_ID = ?;
```