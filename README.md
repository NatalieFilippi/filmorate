# java-filmorate
Template repository for Filmorate project.
[Diagram](src/main/resources/Filmorate.jpg)
### Примеры запросов к таблице user.
- findAll()
```
SELECT *
FROM user
```

- findById
```
SELECT *
FROM user
WHERE user_id='$id'
```

- getFriends
```
SELECT *
FROM user
WHERE user_id IN (SELECT friend_id
                FROM friends
                WHERE user_id='$id') 
```

- getCommonFriends
```
SELECT *
FROM user
WHERE user_id IN(SELECT *
               FROM (SELECT friend_id
                       FROM friends
                       WHERE user_id='$id') AS u
               WHERE friend_id IN (SELECT friend_id
                                   FROM friends
                                   WHERE user_id='$other_id'))
```


### Примеры запросов к таблице film.
- findAll()
```
SELECT f.name,
       f.decription,
       f.release_date,
       f.duration,
       r.name
FROM film AS f
LEFT JOIN rating AS r ON f.rating_id=r.rating_id
```

- findById
```
SELECT f.name,
       f.decription,
       f.release_date,
       f.duration,
       r.name
FROM film AS f
LEFT JOIN rating AS r ON f.rating_id=r.rating_id
WHERE film_id='$id'
```

- getPopularFilms
```
SELECT f.name,
       f.decription,
       f.release_date,
       f.duration,
       r.name
FROM film AS f
LEFT JOIN rating AS r ON f.rating_id=r.rating_id
WHERE film_id IN (SELECT film_id,
                        COUNT(user_id)
                   FROM like
                   GROUP BY film_id
                   ORDER BY COUNT(user_id) DESC)
```