package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        List<UserMealWithExcess> userMealWithExcesses = filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        userMealWithExcesses.forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO return filtered list with excess. Implement by cycles
        List<UserMealWithExcess> userMealWithExcesses = new ArrayList<>();
        List<UserMeal> resultUserMeals = new ArrayList<>();
        Map<LocalDate, Integer> map = new HashMap<>();

        for (UserMeal userMeal : meals) {
            LocalDate date = userMeal.getDateTime().toLocalDate();
            map.put(date, map.getOrDefault(date, 0) + userMeal.getCalories());
            LocalTime time = userMeal.getDateTime().toLocalTime();

            if (TimeUtil.isBetweenHalfOpen(time, startTime, endTime)) {
                resultUserMeals.add(new UserMeal(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories()
                ));
            }
        }
        for (UserMeal userMeal : resultUserMeals) {
            LocalDate date = LocalDate.of(userMeal.getDateTime().getYear(), userMeal.getDateTime().getMonth(), userMeal.getDateTime().getDayOfMonth());
            if (map.get(date) > caloriesPerDay) {
                userMealWithExcesses.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true));
            } else {
                userMealWithExcesses.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false));
            }
        }
        return userMealWithExcesses;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // TODO Implement by streams
        Map<LocalDate, Integer> mapCalories = new HashMap<>();
        List<UserMeal> collect = meals.stream().peek(userMeal -> {
            mapCalories.merge(userMeal.getDateTime().toLocalDate(), userMeal.getCalories(), Integer::sum);
        }).filter(userMeal -> {
            return TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime);
        }).map(userMeal -> {
            return new UserMeal(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories());
        }).collect(Collectors.toList());

        return collect.stream().map(userMeal -> {
            if (mapCalories.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay)
                return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), true);
            else
                return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), false);
        }).collect(Collectors.toList());
    }
}
