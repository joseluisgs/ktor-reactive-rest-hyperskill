package joseluisgs.dev.data

import joseluisgs.dev.models.Racquet
import java.time.LocalDateTime

fun racquetsDemoData(): MutableMap<Long, Racquet> = mutableMapOf(
    1L to Racquet(
        1L,
        "Babolat",
        "Pure Drive",
        200.0,
        10,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    2L to Racquet(
        2L,
        "Babolat",
        "Pure Aero",
        225.0,
        8,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    3L to Racquet(
        3L,
        "Head",
        "Speed",
        250.25,
        15,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
    4L to Racquet(
        4L,
        "Wilson",
        "Pro Staff",
        300.0,
        12,
        LocalDateTime.parse("2021-05-05T00:00:00"),
        LocalDateTime.parse("2021-05-05T00:00:00")
    ),
)