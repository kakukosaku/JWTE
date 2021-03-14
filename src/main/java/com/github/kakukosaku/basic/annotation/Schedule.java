package com.github.kakukosaku.basic.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Arrays;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Schedules.class)
public @interface Schedule {
    String dayOfMonth() default "Jan";

    String dayOfWeek() default "Mon";

    int hour() default 12;
}


@Retention(RetentionPolicy.RUNTIME)
@interface Schedules {
    Schedule[] value();
}


class Job {

    @Schedule(dayOfMonth = "Feb", dayOfWeek = "Tuesday, Thursday")
    @Schedule(dayOfMonth = "Mar", dayOfWeek = "Tuesday, Thursday")
    public void doPeriodicTask() {
    }

    @Schedule(dayOfMonth = "Apr", dayOfWeek = "Sunday, Monday")
    public void doPeriodicTask2() {
    }

    public static void main(String[] args) {
        Job j = new Job();
        Class c = j.getClass();
        try {
            Method m = c.getMethod("doPeriodicTask");
            Schedules schedules = m.getAnnotation(Schedules.class);
            System.out.println(Arrays.toString(schedules.value()));

            Method m2 = Job.class.getMethod("doPeriodicTask2");
            Schedule schedule = m2.getAnnotation(Schedule.class);
            if (schedule != null) {
                System.out.println(schedule.dayOfMonth() + schedule.dayOfWeek() + schedule.hour());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}